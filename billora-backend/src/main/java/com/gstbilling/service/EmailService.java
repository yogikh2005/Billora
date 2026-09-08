package com.gstbilling.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.gstbilling.models.Invoice;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;


    public void sendInvoice(
            Invoice invoice,
            byte[] pdf) {

        if (invoice == null) {
            throw new IllegalArgumentException(
                    "Invoice cannot be null"
            );
        }

        if (pdf == null || pdf.length == 0) {
            throw new IllegalArgumentException(
                    "Invoice PDF cannot be empty"
            );
        }

        if (invoice.getCustomer() == null) {
            throw new IllegalArgumentException(
                    "Invoice customer is missing"
            );
        }

        String customerEmail =
                invoice.getCustomer().getEmail();

        if (customerEmail == null
                || customerEmail.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Customer email is missing"
            );
        }


        try {

            // =====================================
            // Create Email
            // =====================================

            MimeMessage message =
                    mailSender.createMimeMessage();


            MimeMessageHelper helper =
                    new MimeMessageHelper(
                            message,
                            true,
                            "UTF-8"
                    );


            // =====================================
            // Email Details
            // =====================================

            helper.setTo(customerEmail);

            helper.setSubject(
                    "GST Invoice - "
                    + invoice.getInvoiceNumber()
            );


            // =====================================
            // Email Body
            // =====================================

            String customerName =
                    invoice.getCustomer().getName();

            String businessName =
                    invoice.getBusiness() != null
                            ? invoice.getBusiness()
                                    .getBusinessName()
                            : "Billora";


            String htmlBody = """

                    <html>
                    <body style="
                        font-family: Arial, sans-serif;
                        background-color: #f5f7fb;
                        padding: 30px;
                    ">

                        <div style="
                            max-width: 600px;
                            margin: auto;
                            background: white;
                            padding: 30px;
                            border-radius: 10px;
                            border: 1px solid #e5e7eb;
                        ">

                            <h2 style="
                                color: #2563eb;
                                margin-bottom: 20px;
                            ">
                                %s
                            </h2>

                            <p>
                                Dear <strong>%s</strong>,
                            </p>

                            <p>
                                Thank you for your business.
                                Please find your GST invoice
                                attached to this email.
                            </p>

                            <div style="
                                background: #f8fafc;
                                padding: 15px;
                                border-radius: 8px;
                                margin: 20px 0;
                            ">

                                <p>
                                    <strong>Invoice Number:</strong>
                                    %s
                                </p>

                                <p>
                                    <strong>Invoice Date:</strong>
                                    %s
                                </p>

                                <p>
                                    <strong>Total Amount:</strong>
                                    ₹%.2f
                                </p>

                            </div>

                            <p>
                                Please find the invoice PDF
                                attached with this email.
                            </p>

                            <p style="
                                margin-top: 25px;
                                color: #6b7280;
                            ">
                                Regards,<br>
                                <strong>%s</strong>
                            </p>

                            <hr style="
                                border: none;
                                border-top: 1px solid #e5e7eb;
                                margin: 25px 0;
                            ">

                            <p style="
                                font-size: 12px;
                                color: #9ca3af;
                            ">
                                This is an automatically generated
                                email from Billora.
                                Please do not reply to this email.
                            </p>

                        </div>

                    </body>
                    </html>

                    """.formatted(
                            businessName,
                            customerName,
                            invoice.getInvoiceNumber(),
                            invoice.getInvoiceDate(),
                            invoice.getTotalAmount(),
                            businessName
                    );


            helper.setText(
                    htmlBody,
                    true
            );


            // =====================================
            // PDF Attachment
            // =====================================

            String fileName =
                    "Invoice-"
                    + invoice.getInvoiceNumber()
                    + ".pdf";


            helper.addAttachment(
                    fileName,
                    new ByteArrayResource(pdf)
            );


            // =====================================
            // SEND
            // =====================================

            mailSender.send(message);


        } catch (MessagingException | MailException e) {

            throw new RuntimeException(
                    "Failed to send invoice email to "
                    + customerEmail,
                    e
            );
        }
    }
}