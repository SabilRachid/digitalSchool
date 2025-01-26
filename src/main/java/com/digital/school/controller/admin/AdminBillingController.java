package com.digital.school.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.digital.school.model.*;
import com.digital.school.service.*;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/admin/billing")
public class AdminBillingController {

    @Autowired
    private BillingService billingService;
    
    @Autowired
    private PaymentService paymentService;
    
    @Autowired
    private UserService userService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("stats", billingService.getBillingStats());
        return "admin/billing/dashboard";
    }

    @GetMapping("/invoices")
    public String listInvoices(Model model) {
        return "admin/billing/invoices";
    }

    @GetMapping("/invoices/data")
    @ResponseBody
    public List<Map<String, Object>> getInvoicesData() {
        return billingService.getAllInvoicesAsMap();
    }

    @PostMapping("/invoices/generate")
    @ResponseBody
    public ResponseEntity<?> generateInvoice(@RequestBody Map<String, Object> data) {
        try {
            User student = userService.findById(Long.parseLong(data.get("studentId").toString()))
                .orElseThrow(() -> new RuntimeException("Student not found"));
                
            //List<InvoiceItem> items = parseInvoiceItems(data.get("items"));
            //rachid
            List<InvoiceItem> items=null;
            Invoice invoice = billingService.generateInvoice(student, items);
            
            return ResponseEntity.ok(invoice);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/invoices/{id}/remind")
    @ResponseBody
    public ResponseEntity<?> sendReminder(@PathVariable Long id) {
        try {
            billingService.sendInvoiceReminder(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
}
