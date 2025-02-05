package com.digital.school.controller;

import com.digital.school.service.PaymentService;
import com.digital.school.config.CMICallback;
import com.digital.school.model.User;
import com.digital.school.config.PaymentRequest;
import com.digital.school.service.EmailService;
import com.digital.school.service.SMSService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.Map;

@Controller
@RequestMapping("/payments")
public class PaymentController {

    @Qualifier("CMIPaymentServiceImpl")
    @Autowired
    private PaymentService paymentService;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private SMSService smsService;

    @GetMapping
    public String showPayments(HttpServletRequest request, Model model, @AuthenticationPrincipal User user) {
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    	if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            model.addAttribute("payments", paymentService.findAllPayments());
        } else {
            model.addAttribute("payments", paymentService.findPaymentsByUser(user));
        }
        model.addAttribute("currentURI", request.getRequestURI());
        return "payments/index";
    }

    @PostMapping("/initiate")
    @ResponseBody
    public ResponseEntity<?> initiatePayment(@RequestBody PaymentRequest request) {
        try {
            String paymentUrl = paymentService.initiatePayment(
                //request.getOrderId(),
            	"1",
                request.getAmount(),
                request.getDescription(),
                //request.getEmail() 
                ""
            );
            return ResponseEntity.ok(Map.of("paymentUrl", paymentUrl));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/callback")
    @ResponseBody
    public ResponseEntity<?> paymentCallback(@RequestBody CMICallback callback) {
        try {
            boolean isValid = paymentService.verifyPayment(
                callback.getOrderId(),
                callback.getTransactionId()
            );
            
            if (isValid) {
                // Envoyer confirmation par email et SMS
                emailService.sendPaymentConfirmation(
                    callback.getEmail(),
                    callback.getOrderId(),
                    callback.getAmount().toString()
                );
                
                smsService.sendPaymentConfirmation(
                    callback.getPhone(),
                    callback.getAmount().toString()
                );
                
                return ResponseEntity.ok(Map.of("status", "success"));
            } else {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid payment signature"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/receipt/{orderId}")
    public ResponseEntity<?> downloadReceipt(@PathVariable String orderId) {
        try {
            byte[] receipt = paymentService.getReceipt(orderId);
            return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition", "attachment; filename=\"receipt-" + orderId + ".pdf\"")
                .body(receipt);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
}
