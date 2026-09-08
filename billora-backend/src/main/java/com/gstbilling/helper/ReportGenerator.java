package com.gstbilling.helper;
import org.springframework.core.io.ClassPathResource;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Component;

import com.gstbilling.models.BusinessDetails;
import com.gstbilling.models.Customer;
import com.gstbilling.models.Invoice;
import com.gstbilling.models.InvoiceItem;

@Component
public class ReportGenerator {

    // =====================================================
    // LOAD UNICODE FONT
    // =====================================================

   private PDType0Font loadUnicodeFont(PDDocument document)
        throws IOException {

    ClassPathResource fontResource =
            new ClassPathResource("fonts/NotoSans-Regular.ttf");

    try (InputStream inputStream = fontResource.getInputStream()) {
        return PDType0Font.load(document, inputStream);
    }
}


    // =====================================================
    // LOAD LOGO
    // =====================================================
   
    private PDImageXObject loadLogo(
            PDDocument document,
            String logoPath) {

        try {

            File logoFile = new File(logoPath);

            if (logoFile.exists()) {

                return PDImageXObject.createFromFileByContent(
                        logoFile,
                        document
                );
            }

        } catch (IOException e) {

            System.err.println(
                    "Could not load logo: "
                    + e.getMessage()
            );
        }

        return null;
    }


    // =====================================================
    // SINGLE INVOICE PDF
    // =====================================================

    public byte[] generatePdf(Invoice invoice) {

        try (
                PDDocument document = new PDDocument();
                ByteArrayOutputStream baos =
                        new ByteArrayOutputStream()
        ) {

            PDPage page =
                    new PDPage(PDRectangle.A4);

            document.addPage(page);


            // Load font
            PDType0Font font =
                    loadUnicodeFont(document);


            // Load logo
            PDImageXObject logo =
                    loadLogo(
                            document,
                            invoice.getBusiness().getLogoPath()
                            
                    );


            try (
                    PDPageContentStream content =
                            new PDPageContentStream(
                                    document,
                                    page
                            )
            ) {

                // Page border
                drawPageBorder(content);


                float margin = 40;

                float yStart =
                        PDRectangle.A4.getHeight()
                        - margin;

                float y = yStart;


                // =====================================================
                // 1. HEADER
                // =====================================================

                y = addHeader(
                        content,
                        invoice,
                        y,
                        font,
                        logo
                );


                // =====================================================
                // 2. BUSINESS DETAILS
                // =====================================================

                y = addBusinessDetails(
                        content,
                        invoice.getBusiness(),
                        y - 20,
                        font
                );


                // =====================================================
                // 3. CUSTOMER DETAILS
                // =====================================================

                y = addCustomerDetails(
                        content,
                        invoice.getCustomer(),
                        y - 20,
                        font
                );


                // =====================================================
                // 4. ITEMS TABLE
                // =====================================================

                y = addItemsTable(
                        content,
                        invoice,
                        y - 20,
                        font
                );


                // =====================================================
                // 5. FOOTER
                // =====================================================

                addFooter(
                        content,
                        invoice.getBusiness(),
                        100,
                        font
                );
            }


            document.save(baos);

            return baos.toByteArray();

        } catch (IOException e) {

            e.printStackTrace();

            return null;
        }
    }


    // =====================================================
    // BUSINESS DETAILS
    // =====================================================

    private float addBusinessDetails(
            PDPageContentStream content,
            BusinessDetails business,
            float y,
            PDType0Font font
    ) throws IOException {

        float leftMargin = 40;

        y -= 20;


        writeText2(
                content,
                font,
                12,
                leftMargin,
                y,
                "From :"
        );

        y -= 15;


        // Business Name
        writeText2(
                content,
                font,
                10,
                leftMargin,
                y,
                business.getBusinessName()
        );

        y -= 12;


        // Address
        if (
                business.getAddress() != null
                && !business.getAddress().isEmpty()
        ) {

            y = writeMultilineText(
                    content,
                    font,
                    10,
                    leftMargin,
                    y,
                    business.getAddress(),
                    12
            );
        }


        // City / State / Pincode
        String location = String.join(
                ", ",
                business.getCity() != null
                        ? business.getCity()
                        : "",
                business.getState() != null
                        ? business.getState()
                        : "",
                business.getPincode() != null
                        ? business.getPincode()
                        : ""
        );


        if (!location.trim().isEmpty()) {

            writeText2(
                    content,
                    font,
                    10,
                    leftMargin,
                    y,
                    location
            );

            y -= 12;
        }


        // GSTIN
        if (
                business.getGstin() != null
                && !business.getGstin().isEmpty()
        ) {

            writeText2(
                    content,
                    font,
                    10,
                    leftMargin,
                    y,
                    "GSTIN: "
                    + business.getGstin()
            );

            y -= 12;
        }


        // Email
        if (
                business.getEmail() != null
                && !business.getEmail().isEmpty()
        ) {

            writeText2(
                    content,
                    font,
                    10,
                    leftMargin,
                    y,
                    "Email: "
                    + business.getEmail()
            );

            y -= 12;
        }


        // Mobile / Phone
        if (
                business.getMobileNo() != null
                && !business.getMobileNo().isEmpty()
        ) {

            writeText2(
                    content,
                    font,
                    10,
                    leftMargin,
                    y,
                    "Mobile: "
                    + business.getMobileNo()
            );

            y -= 12;

        } else if (
                business.getPhoneNo() != null
                && !business.getPhoneNo().isEmpty()
        ) {

            writeText2(
                    content,
                    font,
                    10,
                    leftMargin,
                    y,
                    "Phone: "
                    + business.getPhoneNo()
            );

            y -= 12;
        }


        // Website
        if (
                business.getWebsite() != null
                && !business.getWebsite().isEmpty()
        ) {

            writeText2(
                    content,
                    font,
                    10,
                    leftMargin,
                    y,
                    "Website: "
                    + business.getWebsite()
            );

            y -= 12;
        }


        return y;
    }


    // =====================================================
    // MULTIPLE INVOICE REPORT
    // =====================================================

    public byte[] generatePdf(
            List<Invoice> invoices,
            String type
    ) {

        if (
                invoices == null
                || invoices.isEmpty()
        ) {

            return null;
        }


        try (
                PDDocument document = new PDDocument();
                ByteArrayOutputStream baos =
                        new ByteArrayOutputStream()
        ) {

            PDPage page =
                    new PDPage(PDRectangle.A4);

            document.addPage(page);


            PDType0Font font =
                    loadUnicodeFont(document);


            PDImageXObject logo =
                    loadLogo(
                            document,
                            invoices.get(0).getBusiness().getLogoPath()
                    );


            BusinessDetails business =
                    invoices.get(0).getBusiness();


            try (
                    PDPageContentStream content =
                            new PDPageContentStream(
                                    document,
                                    page
                            )
            ) {

                drawPageBorder(content);


                float margin = 40;

                float yStart =
                        PDRectangle.A4.getHeight()
                        - margin;

                float y = yStart;


                // Report Header
                y = addReportHeader(
                        content,
                        business,
                        y,
                        font,
                        logo
                );


                // Report title
                y -= 25;

                float leftMargin = 40;

                content.setFont(
                        font,
                        14
                );


                String reportTitle =
                        type.substring(0, 1).toUpperCase()
                        + type.substring(1)
                        + " Sales Report";


                writeText(
                        content,
                        reportTitle,
                        leftMargin,
                        y,
                        font
                );


                y -= 14;


                content.setFont(
                        font,
                        9
                );


                DateTimeFormatter formatter =
                        DateTimeFormatter.ofPattern(
                                "dd-MMM-yyyy"
                        );


                String dateText =
                        "Generated on: "
                        + LocalDate.now().format(
                                formatter
                        );


                writeText(
                        content,
                        dateText,
                        leftMargin,
                        y,
                        font
                );


                // Summary
                y = addReportSummary(
                        content,
                        invoices,
                        y - 20,
                        font
                );


                // Invoice table
                y = addInvoiceListTable(
                        content,
                        invoices,
                        y - 30,
                        font
                );


                // Footer
                addReportFooter(
                        content,
                        business,
                        40,
                        font
                );
            }


            document.save(baos);

            return baos.toByteArray();

        } catch (IOException e) {

            e.printStackTrace();

            return null;
        }
    }


    // =====================================================
    // PAGE BORDER
    // =====================================================

    private void drawPageBorder(
            PDPageContentStream content
    ) throws IOException {

        float margin = 15;

        float pageWidth =
                PDRectangle.A4.getWidth();

        float pageHeight =
                PDRectangle.A4.getHeight();


        content.setLineWidth(2f);


        float x1 = margin;
        float y1 = margin;

        float x2 =
                pageWidth - margin;

        float y2 =
                pageHeight - margin;


        content.moveTo(x1, y1);

        content.lineTo(x2, y1);

        content.lineTo(x2, y2);

        content.lineTo(x1, y2);

        content.lineTo(x1, y1);

        content.stroke();
    }


    // =====================================================
    // REPORT HEADER
    // =====================================================

    private float addReportHeader(
            PDPageContentStream content,
            BusinessDetails business,
            float y,
            PDType0Font font,
            PDImageXObject logo
    ) throws IOException {

        float leftMargin = 40;

        float rightMargin = 555;


        float headerBottom =
                y - 85;


        // Logo
        if (logo != null) {

            content.drawImage(
                    logo,
                    35,
                    y - 65,
                    80,
                    80
            );
        }


        // GSTIN
        content.setFont(
                font,
                8
        );


        writeText(
                content,
                "GSTIN: "
                + safeText(business.getGstin()),
                leftMargin + 5,
                y - 5,
                font
        );


        y -= 38;


        // Business Name
        content.setFont(
                font,
                18
        );


        String businessName =
                safeText(
                        business.getBusinessName()
                );


        float textWidth =
                getTextWidth(
                        font,
                        18,
                        businessName
                );


        writeText(
                content,
                businessName,
                (
                        PDRectangle.A4.getWidth()
                        - textWidth
                ) / 2,
                y,
                font
        );


        y -= 15;


        // Address
        content.setFont(
                font,
                9
        );


        String address =
                String.format(
                        "%s, %s, %s - %s",
                        safeText(
                                business.getAddress()
                        ),
                        safeText(
                                business.getCity()
                        ),
                        safeText(
                                business.getState()
                        ),
                        safeText(
                                business.getPincode()
                        )
                );


        textWidth =
                getTextWidth(
                        font,
                        9,
                        address
                );


        writeText(
                content,
                address,
                (
                        PDRectangle.A4.getWidth()
                        - textWidth
                ) / 2,
                y,
                font
        );


        y -= 12;


        // Contact
        String contact =
                String.format(
                        "Mob No: %s     Email: %s     H/O Tel.: %s",
                        safeText(
                                business.getMobileNo()
                        ),
                        safeText(
                                business.getEmail()
                        ),
                        business.getPhoneNo() != null
                                ? safeText(
                                        business.getPhoneNo()
                                )
                                : "N/A"
                );


        textWidth =
                getTextWidth(
                        font,
                        9,
                        contact
                );


        writeText(
                content,
                contact,
                (
                        PDRectangle.A4.getWidth()
                        - textWidth
                ) / 2,
                y,
                font
        );


        return headerBottom;
    }


    // =====================================================
    // REPORT SUMMARY
    // =====================================================

    private float addReportSummary(
            PDPageContentStream content,
            List<Invoice> invoices,
            float y,
            PDType0Font font
    ) throws IOException {

        double totalRevenue =
                invoices.stream()
                        .mapToDouble(
                                invoice ->
                                        invoice.getTotalAmount()
                        )
                        .sum();


        double totalGst =
                invoices.stream()
                        .mapToDouble(
                                invoice ->
                                        invoice.getGstAmount()
                        )
                        .sum();


        int totalInvoices =
                invoices.size();


        content.setFont(
                font,
                12
        );


        writeText(
                content,
                "Summary",
                40,
                y,
                font
        );


        y -= 18;


        content.setFont(
                font,
                10
        );


        writeText(
                content,
                "Total Invoices: "
                + totalInvoices,
                40,
                y,
                font
        );


        writeText(
                content,
                "Total Revenue: ₹"
                + String.format(
                        "%.2f",
                        totalRevenue
                ),
                200,
                y,
                font
        );


        writeText(
                content,
                "Total GST: ₹"
                + String.format(
                        "%.2f",
                        totalGst
                ),
                400,
                y,
                font
        );


        y -= 15;


        drawLine(
                content,
                40,
                y,
                550,
                y
        );


        return y;
    }


    // =====================================================
    // INVOICE LIST TABLE
    // =====================================================

    private float addInvoiceListTable(
            PDPageContentStream content,
            List<Invoice> invoices,
            float yStart,
            PDType0Font font
    ) throws IOException {

        float tableWidth = 520;

        float x = 40;

        float y = yStart;


        content.setFont(
                font,
                10
        );


        String[] headers = {
                "Invoice No",
                "Date",
                "Customer",
                "Subtotal",
                "GST",
                "Total"
        };


        float[] colWidths = {
                80,
                80,
                150,
                70,
                70,
                70
        };


        float nextX = x;


        for (
                int i = 0;
                i < headers.length;
                i++
        ) {

            writeText(
                    content,
                    headers[i],
                    nextX + 2,
                    y,
                    font
            );


            nextX += colWidths[i];
        }


        y -= 15;


        drawLine(
                content,
                x,
                y,
                x + tableWidth,
                y
        );


        double totalSubtotal = 0;

        double totalGst = 0;

        double grandTotal = 0;


        content.setFont(
                font,
                9
        );


        for (Invoice invoice : invoices) {

            nextX = x;

            y -= 15;


            double subtotal =
                    invoice.getSubtotal();

            double gst =
                    invoice.getGstAmount();

            double total =
                    invoice.getTotalAmount();


            totalSubtotal += subtotal;

            totalGst += gst;

            grandTotal += total;


            writeText(
                    content,
                    safeText(
                            invoice.getInvoiceNumber()
                    ),
                    nextX + 2,
                    y,
                    font
            );


            nextX += colWidths[0];


            writeText(
                    content,
                    invoice.getInvoiceDate() != null
                            ? invoice.getInvoiceDate().toString()
                            : "",
                    nextX + 2,
                    y,
                    font
            );


            nextX += colWidths[1];


            String customerName =
                    invoice.getCustomer() != null
                            ? safeText(
                                    invoice
                                            .getCustomer()
                                            .getName()
                            )
                            : "";


            if (customerName.length() > 20) {

                customerName =
                        customerName.substring(
                                0,
                                17
                        )
                        + "...";
            }


            writeText(
                    content,
                    customerName,
                    nextX + 2,
                    y,
                    font
            );


            nextX += colWidths[2];


            writeText(
                    content,
                    String.format(
                            "%.2f",
                            subtotal
                    ),
                    nextX + 2,
                    y,
                    font
            );


            nextX += colWidths[3];


            writeText(
                    content,
                    String.format(
                            "%.2f",
                            gst
                    ),
                    nextX + 2,
                    y,
                    font
            );


            nextX += colWidths[4];


            writeText(
                    content,
                    String.format(
                            "%.2f",
                            total
                    ),
                    nextX + 2,
                    y,
                    font
            );
        }


        y -= 18;


        drawLine(
                content,
                x,
                y,
                x + tableWidth,
                y
        );


        y -= 18;


        content.setFont(
                font,
                10
        );


        nextX = x;


        writeText(
                content,
                "TOTAL",
                nextX + 2,
                y,
                font
        );


        nextX +=
                colWidths[0]
                + colWidths[1]
                + colWidths[2];


        writeText(
                content,
                String.format(
                        "%.2f",
                        totalSubtotal
                ),
                nextX + 2,
                y,
                font
        );


        nextX += colWidths[3];


        writeText(
                content,
                String.format(
                        "%.2f",
                        totalGst
                ),
                nextX + 2,
                y,
                font
        );


        nextX += colWidths[4];


        writeText(
                content,
                String.format(
                        "%.2f",
                        grandTotal
                ),
                nextX + 2,
                y,
                font
        );


        y -= 10;


        drawLine(
                content,
                x,
                y,
                x + tableWidth,
                y
        );


        return y;
    }


    // =====================================================
    // REPORT FOOTER
    // =====================================================

    private void addReportFooter(
            PDPageContentStream content,
            BusinessDetails business,
            float y,
            PDType0Font font
    ) throws IOException {

        content.setFont(
                font,
                8
        );


        writeText(
                content,
                "This is a system generated report",
                40,
                y,
                font
        );


        y -= 12;


        writeText(
                content,
                "For any queries, contact: "
                + safeText(
                        business.getEmail()
                )
                + " | "
                + safeText(
                        business.getMobileNo()
                ),
                40,
                y,
                font
        );
    }


    // =====================================================
    // SINGLE INVOICE HEADER
    // =====================================================

    private float addHeader(
            PDPageContentStream content,
            Invoice invoice,
            float y,
            PDType0Font font,
            PDImageXObject logo
    ) throws IOException {

        BusinessDetails business =
                invoice.getBusiness();


        float leftMargin = 40;

        float rightMargin =
                PDRectangle.A4.getWidth()
                - 40;


        // Logo
        if (logo != null) {

            content.drawImage(
                    logo,
                    35,
                    y - 65,
                    80,
                    80
            );
        }


        // GSTIN
        writeText2(
                content,
                font,
                9,
                leftMargin,
                y,
                "GSTIN: "
                + safeText(
                        business.getGstin()
                )
        );


        writeTextRight(
                content,
                font,
                9,
                rightMargin,
                y,
                "Original Copy"
        );


        y -= 15;


        // TAX INVOICE
        writeTextCenter(
                content,
                font,
                14,
                y,
                "TAX INVOICE"
        );


        y -= 22;


        // Business name
        writeTextCenter(
                content,
                font,
                18,
                y,
                business.getBusinessName()
        );


        y -= 15;


        // Address
        String address =
                String.format(
                        "%s, %s, %s - %s",
                        safeText(
                                business.getAddress()
                        ),
                        safeText(
                                business.getCity()
                        ),
                        safeText(
                                business.getState()
                        ),
                        safeText(
                                business.getPincode()
                        )
                );


        writeTextCenter(
                content,
                font,
                9,
                y,
                address
        );


        y -= 12;


        // Contact
        String contact =
                String.format(
                        "Mob No: %s | Email: %s | H/O Tel.: %s",
                        safeText(
                                business.getMobileNo()
                        ),
                        safeText(
                                business.getEmail()
                        ),
                        business.getPhoneNo() != null
                                ? safeText(
                                        business.getPhoneNo()
                                )
                                : "N/A"
                );


        writeTextCenter(
                content,
                font,
                9,
                y,
                contact
        );


        y -= 15;


        // Separator
        drawLine(
                content,
                leftMargin,
                y,
                rightMargin,
                y
        );


        y -= 15;


        // Invoice Number
        writeText2(
                content,
                font,
                10,
                leftMargin,
                y,
                "Invoice Number: "
                + safeText(
                        invoice.getInvoiceNumber()
                )
        );


        // Date
        writeTextCenter(
                content,
                font,
                10,
                y,
                "Date: "
                + (
                        invoice.getInvoiceDate() != null
                                ? invoice
                                        .getInvoiceDate()
                                        .toString()
                                : ""
                )
        );


        // Status
        writeTextRight(
                content,
                font,
                10,
                rightMargin,
                y,
                "Status: "
                + safeText(
                        invoice.getStatus()
                )
        );


        return y;
    }


    // =====================================================
    // CUSTOMER DETAILS
    // =====================================================

    private float addCustomerDetails(
            PDPageContentStream content,
            Customer customer,
            float y,
            PDType0Font font
    ) throws IOException {

        float leftMargin = 40;


        writeText2(
                content,
                font,
                12,
                leftMargin,
                y,
                "Billed To:"
        );


        y -= 15;


        if (customer == null) {

            writeText2(
                    content,
                    font,
                    10,
                    leftMargin,
                    y,
                    "Customer information not available"
            );

            return y - 15;
        }


        writeText2(
                content,
                font,
                10,
                leftMargin,
                y,
                customer.getName()
        );


        y -= 12;


        // Customer Address
        if (
                customer.getAddress() != null
                && !customer.getAddress().isEmpty()
        ) {

            y = writeMultilineText(
                    content,
                    font,
                    10,
                    leftMargin,
                    y,
                    customer.getAddress(),
                    12
            );
        }


        // Email
        if (
                customer.getEmail() != null
                && !customer.getEmail().isEmpty()
        ) {

            writeText2(
                    content,
                    font,
                    10,
                    leftMargin,
                    y,
                    "Email: "
                    + customer.getEmail()
            );

            y -= 12;
        }


        // GSTIN
        if (
                customer.getGstin() != null
                && !customer.getGstin().isEmpty()
        ) {

            writeText2(
                    content,
                    font,
                    10,
                    leftMargin,
                    y,
                    "GSTIN: "
                    + customer.getGstin()
            );

            y -= 12;
        }


        return y;
    }


    // =====================================================
    // ITEMS TABLE
    // =====================================================

    private float addItemsTable(
            PDPageContentStream content,
            Invoice invoice,
            float yStart,
            PDType0Font font
    ) throws IOException {

        float x = 40;


        float tableTopY =
                yStart - 20;


        float tableBottomY = 150;


        float tableWidth =
                PDRectangle.A4.getWidth()
                - (2 * x);


        String[] headers = {
                "Item",
                "Qty",
                "HSN",
                "Price",
                "GST Rate",
                "GST Amt",
                "Total"
        };


        float[] colWidths = {
                220,
                43,
                40,
                50,
                60,
                50,
                40
        };


        float y = tableTopY;

        float currentX = x;


        drawLine(
                content,
                x,
                y + 12,
                x + tableWidth,
                y + 12
        );


        // Table Header
        for (
                int i = 0;
                i < headers.length;
                i++
        ) {

            if (i == 0) {

                writeText2(
                        content,
                        font,
                        10,
                        currentX,
                        y,
                        headers[i]
                );

            } else {

                writeTextRight(
                        content,
                        font,
                        10,
                        currentX + colWidths[i],
                        y,
                        headers[i]
                );
            }


            currentX += colWidths[i];
        }


        y -= 8;


        drawLine(
                content,
                x,
                y,
                x + tableWidth,
                y
        );


        // Items
        if (invoice.getItems() != null) {

            for (
                    InvoiceItem item :
                    invoice.getItems()
            ) {

                y -= 15;

                currentX = x;


                // Item name
                writeText2(
                        content,
                        font,
                        9,
                        currentX,
                        y,
                        safeText(
                                item.getItemName()
                        )
                );


                currentX +=
                        colWidths[0];


                // Quantity
                writeTextRight(
                        content,
                        font,
                        9,
                        currentX + colWidths[1],
                        y,
                        String.valueOf(
                                item.getQuantity()
                        )
                );


                currentX +=
                        colWidths[1];


                // HSN
                writeTextRight(
                        content,
                        font,
                        9,
                        currentX + colWidths[2],
                        y,
                        String.valueOf(
                                item.getHSN()
                        )
                );


                currentX +=
                        colWidths[2];


                // Price
                writeTextRight(
                        content,
                        font,
                        9,
                        currentX + colWidths[3],
                        y,
                        String.format(
                                "%,.2f",
                                item.getPrice()
                        )
                );


                currentX +=
                        colWidths[3];


                // GST Rate
                writeTextRight(
                        content,
                        font,
                        9,
                        currentX + colWidths[4],
                        y,
                        String.format(
                                "%.0f%%",
                                item.getGstRate()
                        )
                );


                currentX +=
                        colWidths[4];


                // GST Amount
                writeTextRight(
                        content,
                        font,
                        9,
                        currentX + colWidths[5],
                        y,
                        String.format(
                                "%,.2f",
                                item.getGstAmount()
                        )
                );


                currentX +=
                        colWidths[5];


                // Total
                writeTextRight(
                        content,
                        font,
                        9,
                        currentX + colWidths[6],
                        y,
                        String.format(
                                "%,.2f",
                                item.getTotalWithGst()
                        )
                );
            }
        }


        // =====================================================
        // TOTALS
        // =====================================================

        float totalsX =
                x + tableWidth;


        float totalsLabelX =
                totalsX - 100;


        drawLine(
                content,
                x,
                y - 10,
                x + tableWidth,
                y - 10
        );


        y = tableBottomY;


        writeTextRight(
                content,
                font,
                10,
                totalsLabelX,
                y,
                "Subtotal:"
        );


        writeTextRight(
                content,
                font,
                10,
                totalsX,
                y,
                "₹"
                + String.format(
                        "%,.2f",
                        invoice.getSubtotal()
                )
        );


        y -= 15;


        writeTextRight(
                content,
                font,
                10,
                totalsLabelX,
                y,
                "Total GST:"
        );


        writeTextRight(
                content,
                font,
                10,
                totalsX,
                y,
                "₹"
                + String.format(
                        "%,.2f",
                        invoice.getGstAmount()
                )
        );


        y -= 15;


        writeTextRight(
                content,
                font,
                10,
                totalsLabelX,
                y,
                "Grand Total:"
        );


        writeTextRight(
                content,
                font,
                10,
                totalsX,
                y,
                "₹"
                + String.format(
                        "%,.2f",
                        invoice.getTotalAmount()
                )
        );


        return y;
    }


    // =====================================================
    // FOOTER
    // =====================================================

    private void addFooter(
            PDPageContentStream content,
            BusinessDetails business,
            float y,
            PDType0Font font
    ) throws IOException {

        float leftMargin = 40;


        y += 20;


        // Bank Details
        writeText2(
                content,
                font,
                10,
                leftMargin,
                y,
                "Bank Details"
        );


        y -= 12;


        writeText2(
                content,
                font,
                9,
                leftMargin,
                y,
                "Bank: "
                + safeText(
                        business.getBankName()
                )
                + ", A/C No: "
                + safeText(
                        business.getAccountNumber()
                )
        );


        y -= 12;


        writeText2(
                content,
                font,
                9,
                leftMargin,
                y,
                "IFSC: "
                + safeText(
                        business.getIfscCode()
                )
                + ", Branch: "
                + safeText(
                        business.getBranch()
                )
        );


        y -= 20;


        // Terms
        if (
                business.getTermsAndConditions() != null
                && !business
                        .getTermsAndConditions()
                        .isEmpty()
        ) {

            writeText2(
                    content,
                    font,
                    10,
                    leftMargin,
                    y,
                    "Terms & Conditions:"
            );


            y -= 12;


            String terms =
                    business.getTermsAndConditions();


            // Handle both:
            // 1. New lines
            // 2. Numbered terms such as 1. 2. 3.
            String[] lines =
                    terms.split(
                            "\\R|(?=\\s*[1-9][\\.|\\)])"
                    );


            float fontSize = 8;

            float leading =
                    1.4f * fontSize;


            float yPosition = y;


            for (String line : lines) {

                if (
                        line == null
                        || line.trim().isEmpty()
                ) {

                    continue;
                }


                writeText2(
                        content,
                        font,
                        fontSize,
                        leftMargin,
                        yPosition,
                        line.trim()
                );


                yPosition -= leading;
            }
        }
    }


    // =====================================================
    // SAFE TEXT
    // =====================================================

    private String safeText(String text) {

        if (text == null) {
            return "";
        }


        return text
                .replace("\r", " ")
                .replace("\n", " ")
                .replace("\t", " ");
    }


    // =====================================================
    // TEXT WIDTH
    // =====================================================

    private float getTextWidth(
            PDType0Font font,
            float fontSize,
            String text
    ) throws IOException {

        String safe =
                safeText(text);


        return font.getStringWidth(safe)
                / 1000
                * fontSize;
    }


    // =====================================================
    // WRITE NORMAL TEXT
    // =====================================================

    private void writeText(
            PDPageContentStream content,
            String text,
            float x,
            float y,
            PDType0Font font
    ) throws IOException {

        String safe =
                safeText(text);


        content.beginText();


        content.setFont(
                font,
                10
        );


        content.newLineAtOffset(
                x,
                y
        );


        content.showText(
                safe
        );


        content.endText();
    }


    // =====================================================
    // DRAW LINE
    // =====================================================

    private void drawLine(
            PDPageContentStream content,
            float xStart,
            float yStart,
            float xEnd,
            float yEnd
    ) throws IOException {

        content.moveTo(
                xStart,
                yStart
        );


        content.lineTo(
                xEnd,
                yEnd
        );


        content.stroke();
    }


    // =====================================================
    // WRITE TEXT 2
    // =====================================================

    private void writeText2(
            PDPageContentStream content,
            PDType0Font font,
            float fontSize,
            float x,
            float y,
            String text
    ) throws IOException {

        String safe =
                safeText(text);


        content.setFont(
                font,
                fontSize
        );


        content.beginText();


        content.newLineAtOffset(
                x,
                y
        );


        content.showText(
                safe
        );


        content.endText();
    }


    // =====================================================
    // WRITE RIGHT ALIGNED TEXT
    // =====================================================

    private void writeTextRight(
            PDPageContentStream content,
            PDType0Font font,
            float fontSize,
            float x,
            float y,
            String text
    ) throws IOException {

        String safe =
                safeText(text);


        float textWidth =
                getTextWidth(
                        font,
                        fontSize,
                        safe
                );


        writeText2(
                content,
                font,
                fontSize,
                x - textWidth,
                y,
                safe
        );
    }


    // =====================================================
    // WRITE CENTER ALIGNED TEXT
    // =====================================================

    private void writeTextCenter(
            PDPageContentStream content,
            PDType0Font font,
            float fontSize,
            float y,
            String text
    ) throws IOException {

        if (
                text == null
                || text.isEmpty()
        ) {

            return;
        }


        // Split multiline text safely
        String[] lines =
                text.split("\\R");


        float lineHeight =
                fontSize + 3;


        for (String line : lines) {

            line = safeText(line).trim();


            if (line.isEmpty()) {

                y -= lineHeight;

                continue;
            }


            float textWidth =
                    getTextWidth(
                            font,
                            fontSize,
                            line
                    );


            float x =
                    (
                            PDRectangle.A4.getWidth()
                            - textWidth
                    ) / 2;


            writeText2(
                    content,
                    font,
                    fontSize,
                    x,
                    y,
                    line
            );


            y -= lineHeight;
        }
    }


    // =====================================================
    // WRITE MULTILINE TEXT
    // =====================================================

    private float writeMultilineText(
            PDPageContentStream content,
            PDType0Font font,
            float fontSize,
            float x,
            float y,
            String text,
            float lineHeight
    ) throws IOException {

        if (
                text == null
                || text.isEmpty()
        ) {

            return y;
        }


        String[] lines =
                text.split("\\R");


        for (String line : lines) {

            line = safeText(line).trim();


            if (line.isEmpty()) {

                y -= lineHeight;

                continue;
            }


            writeText2(
                    content,
                    font,
                    fontSize,
                    x,
                    y,
                    line
            );


            y -= lineHeight;
        }


        return y;
    }
}