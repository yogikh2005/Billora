package com.gstbilling.controler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gstbilling.dto.InvoiceRequest;
import com.gstbilling.dto.InvoiceResponse;
import com.gstbilling.models.Invoice;
import com.gstbilling.service.InvoiceService;

@RestController
@RequestMapping("/invoices")
public class InvoiceController {
	
    @Autowired
    private InvoiceService invoiceService;
  

    @GetMapping
    public Page<InvoiceResponse> getAllInvoices(
    		@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        return invoiceService.getAllInvoices(page,size);
    }


    @GetMapping("/{id}")
    public InvoiceResponse getInvoice(@PathVariable Long id) {
    	System.out.println("normal");
        return invoiceService.getInvoiceById2(id);
    }

    @PostMapping
    public Invoice createInvoice(@RequestBody InvoiceRequest request) {
        return invoiceService.createInvoice(request);
    }

    
    @PutMapping("/{id}")
    public Invoice updateInvoice(@PathVariable Long id, @RequestBody InvoiceRequest request) {
        return invoiceService.updateInvoice(id, request);
    }

  
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteInvoice(@PathVariable Long id) {
        invoiceService.deleteInvoice(id);
        return ResponseEntity.ok("Invoice deleted successfully!");
    }
    
//
//    @PostMapping("/{id}/send-email")
//    public ResponseEntity<String> sendInvoiceEmail(@PathVariable Long id) {
//        invoiceService.sendInvoiceEmail(id);
//        return ResponseEntity.ok("Invoice sent via email!");	
//    }
    
    
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadInvoicePdf(
            @PathVariable Long id) {
    	System.out.println("cally");
        byte[] pdf =
                invoiceService.generateInvoicePdf(id);

        return ResponseEntity.ok()
                .header(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=invoice-" + id + ".pdf"
                )
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
    

}
