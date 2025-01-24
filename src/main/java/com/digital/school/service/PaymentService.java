package com.digital.school.service;

import com.digital.school.model.*;
import java.math.BigDecimal;

public interface PaymentService {
    Payment processPayment(Invoice invoice, BigDecimal amount, String paymentMethod);
    Payment getPayment(Long id);
    void handlePaymentCallback(String transactionId, String status);
    byte[] generateReceipt(Payment payment);
    
    String initiatePayment(String orderId, BigDecimal amount, String description, String customerEmail);
    boolean verifyPayment(String orderId, String transactionId);
    void savePaymentReceipt(String orderId, byte[] receipt);
	byte[] getReceipt(String orderId);
	Object findAllPayments();
	Object findPaymentsByUser(User user);
	String saveReceiptToStorage(byte[] receiptContent, Long paymentId);

    
}
