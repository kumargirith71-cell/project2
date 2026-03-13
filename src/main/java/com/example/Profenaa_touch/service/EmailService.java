package com.example.Profenaa_touch.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // ================= EXISTING OTP METHOD (UNCHANGED) =================
    public void sendOtpEmail(String to, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Your OTP - Amcurio");
            message.setText("Your OTP is: " + otp);

            mailSender.send(message);

            System.out.println("✅ OTP email sent to " + to);

        } catch (Exception e) {
            System.out.println("EMAIL FAILED");
            e.printStackTrace();
            throw e;
        }
    }

    // ================= NEW METHOD FOR INVOICE EMAIL =================
    public void sendInvoiceEmail(
            String to,
            byte[] pdfBytes,
            String invoiceId
    ) {
        try {

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject("Your Invoice - Amcurio");

            helper.setText("""
                    Hello,

                    Thank you for your purchase.

                    Please find your invoice attached.

                    Regards,
                    Profenaa Team
                    """);

            helper.addAttachment(
                    "Invoice_" + invoiceId + ".pdf",
                    new ByteArrayResource(pdfBytes)
            );

            mailSender.send(message);

            System.out.println("✅ Invoice email sent to " + to);

        } catch (Exception e) {
            System.out.println("INVOICE EMAIL FAILED");
            e.printStackTrace();
            throw new RuntimeException("Invoice email failed");
        }
    }
}