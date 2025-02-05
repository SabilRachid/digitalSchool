package com.digital.school.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.coyote.http11.Http11InputBuffer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.digital.school.model.*;
import com.digital.school.service.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/billing")
public class BillingController {

    @Autowired
    private BillingService billingService;

    @Qualifier("CMIPaymentServiceImpl")
    @Autowired
    private PaymentService paymentService;

    @GetMapping("/invoices")
    public String listInvoices(HttpServletRequest request, @AuthenticationPrincipal User user, Model model) {
        List<Invoice> invoices = billingService.getParentInvoices(user);
        model.addAttribute("invoices", invoices);
        model.addAttribute("currentURI", request.getRequestURI());
        return "billing/invoices";
    }

    @GetMapping("/invoices/{id}")
    public String viewInvoice(@PathVariable Long id, Model model) {
        Invoice invoice = billingService.getInvoice(id);
        model.addAttribute("invoice", invoice);
        return "billing/invoice-details";
    }

    @GetMapping("/invoices/{id}/pdf")
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable Long id) {
        Invoice invoice = billingService.getInvoice(id);
        byte[] pdf = billingService.generateInvoicePdf(invoice);
        
        return ResponseEntity.ok()
            .header("Content-Type", "application/pdf")
            .header("Content-Disposition", "attachment; filename=\"invoice-" + invoice.getInvoiceNumber() + ".pdf\"")
            .body(pdf);
    }

    @PostMapping("/invoices/{id}/pay")
    @ResponseBody
    public ResponseEntity<?> payInvoice(
            @PathVariable Long id,
            @RequestBody Map<String, Object> paymentDetails) {
        try {
            Invoice invoice = billingService.getInvoice(id);
            Payment payment = paymentService.processPayment(
                invoice,
                new BigDecimal(paymentDetails.get("amount").toString()),
                paymentDetails.get("method").toString()
            );
            return ResponseEntity.ok(payment);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/payment-callback")
    @ResponseBody
    public ResponseEntity<?> handlePaymentCallback(@RequestBody Map<String, String> callback) {
        try {
            paymentService.handlePaymentCallback(
                callback.get("transactionId"),
                callback.get("status")
            );
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
}

