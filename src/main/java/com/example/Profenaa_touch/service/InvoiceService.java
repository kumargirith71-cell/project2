package com.example.Profenaa_touch.service;

import com.example.Profenaa_touch.entity.Payment;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class InvoiceService {

    public byte[] generateInvoice(Payment payment) {

        try {

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document, out);

            document.open();

            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
            Font normalFont = new Font(Font.HELVETICA, 12);

            document.add(new Paragraph("AMCURIO - INVOICE", titleFont));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Invoice ID: " + payment.getRazorpayPaymentId(), normalFont));
            document.add(new Paragraph("Order ID: " + payment.getRazorpayOrderId(), normalFont));
            document.add(new Paragraph("Date: " +
                    payment.getCreatedAt().format(
                            DateTimeFormatter.ofPattern("dd MMM yyyy")
                    ), normalFont));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Customer Email: " + payment.getUser().getEmail(), normalFont));
            document.add(new Paragraph("Course Purchased: " + payment.getCourse().getName(), normalFont));
            document.add(new Paragraph("Amount Paid: ₹" + payment.getAmount(), normalFont));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Thank you for your purchase!", normalFont));

            document.close();

            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Invoice generation failed");
        }
    }
}