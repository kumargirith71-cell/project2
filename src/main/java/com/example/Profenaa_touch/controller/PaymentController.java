package com.example.Profenaa_touch.controller;

import com.example.Profenaa_touch.Repository.*;
import com.example.Profenaa_touch.dto.PaymentVerifyRequest;
import com.example.Profenaa_touch.entity.*;
import com.example.Profenaa_touch.entity.Payment;
import com.example.Profenaa_touch.service.EmailService;
import com.example.Profenaa_touch.service.InvoiceService;
import com.razorpay.*;
import jakarta.transaction.Transactional;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Value("${razorpay.key}")
    private String key;

    @Value("${razorpay.secret}")
    private String secret;

    private final PaymentRepository paymentRepo;
    private final UserRepository userRepo;
    private final CourseRepository courseRepo;
    private final EnrollmentRepository enrollRepo;
    private final CartRepository cartRepo;

    // 🔥 NEW SERVICES
    private final InvoiceService invoiceService;
    private final EmailService emailService;

    public PaymentController(
            PaymentRepository paymentRepo,
            UserRepository userRepo,
            CourseRepository courseRepo,
            EnrollmentRepository enrollRepo,
            CartRepository cartRepo,
            InvoiceService invoiceService,
            EmailService emailService
    ) {
        this.paymentRepo = paymentRepo;
        this.userRepo = userRepo;
        this.courseRepo = courseRepo;
        this.enrollRepo = enrollRepo;
        this.cartRepo = cartRepo;
        this.invoiceService = invoiceService;
        this.emailService = emailService;
    }

    // ================= CREATE ORDER (UNCHANGED) =================
    @PostMapping("/create-order/{courseId}")
    public Map<String, Object> createOrder(
            @PathVariable Long courseId,
            Authentication auth
    ) throws Exception {

        User user = userRepo.findByEmail(auth.getName()).orElseThrow();
        Course course = courseRepo.findById(courseId).orElseThrow();
        if(enrollRepo.existsByUserAndCourse(user, course)){
            throw new RuntimeException("You already purchased this course");
        }

        RazorpayClient razorpay = new RazorpayClient(key, secret);

        JSONObject options = new JSONObject();
        options.put("amount", course.getPrice() * 100);
        options.put("currency", "INR");
        options.put("receipt", "course_" + courseId);

        Order order = razorpay.orders.create(options);

        Payment payment = new Payment();
        payment.setRazorpayOrderId(order.get("id"));
        payment.setStatus("CREATED");
        payment.setUser(user);
        payment.setCourse(course);
        payment.setAmount(course.getPrice());
        payment.setCreatedAt(LocalDateTime.now());

        paymentRepo.save(payment);

        return Map.of(
                "orderId", order.get("id"),
                "amount", order.get("amount"),
                "key", key
        );
    }

    // ================= VERIFY PAYMENT =================
    // ================= VERIFY PAYMENT =================
    @PostMapping("/verify")
    @Transactional
    public ResponseEntity<?> verifyPayment(
            @RequestBody PaymentVerifyRequest request
    ) throws Exception {

        Payment payment = paymentRepo
                .findByRazorpayOrderId(request.getOrderId())
                .orElseThrow();

        try {

            JSONObject attributes = new JSONObject();
            attributes.put("razorpay_order_id", request.getOrderId());
            attributes.put("razorpay_payment_id", request.getPaymentId());
            attributes.put("razorpay_signature", request.getSignature());

            Utils.verifyPaymentSignature(attributes, secret);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Invalid payment signature");
        }

        payment.setRazorpayPaymentId(request.getPaymentId());
        payment.setStatus("SUCCESS");

        paymentRepo.save(payment);

        // 🔥 Reload user & course (already in your code)
        User user = userRepo.findById(payment.getUser().getId())
                .orElseThrow();

        Course course = courseRepo.findById(payment.getCourse().getId())
                .orElseThrow();

        // 🔥 Enrollment logic (unchanged)
        if (!enrollRepo.existsByUserAndCourse(user, course)) {

            Enrollment enrollment = new Enrollment();
            enrollment.setUser(user);
            enrollment.setCourse(course);
            enrollment.setSource(EnrollmentSource.ONLINE);
            enrollment.setAssignedAt(LocalDateTime.now());

            enrollRepo.save(enrollment);
        }

        // ==========================================
        // 🔥 NEW FEATURE: GENERATE & SEND INVOICE
        // ==========================================

        byte[] invoicePdf = invoiceService.generateInvoice(payment);

        emailService.sendInvoiceEmail(
                user.getEmail(),
                invoicePdf,
                payment.getRazorpayPaymentId()
        );

        return ResponseEntity.ok("Payment successful, enrolled & invoice sent");
    }
    @GetMapping("/invoice/{paymentId}")
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable Long paymentId) {

        Payment payment = paymentRepo.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        byte[] invoice = invoiceService.generateInvoice(payment);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=invoice.pdf")
                .header("Content-Type", "application/pdf")
                .body(invoice);
    }
}