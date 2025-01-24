package com.digital.school.service;

import java.math.BigDecimal;
import java.util.Map;

import com.digital.school.model.Payment;

public interface PaymentGatewayService {
    String initializePayment(String orderId, BigDecimal amount, String description, String returnUrl);
    boolean verifyPayment(String orderId, String transactionId, Map<String, String> params);
    Map<String, String> getPaymentStatus(String orderId);
	String initiatePayment(Payment payment);
}
