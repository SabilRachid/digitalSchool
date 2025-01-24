package com.digital.school.service;

import java.util.Map;

import com.digital.school.model.Invoice;

public interface EmailService {
    void sendEmail(String to, String subject, String template, Map<String, Object> variables);
    void sendPaymentConfirmation(String to, String orderRef, String amount);
    void sendPaymentReminder(String to, String invoiceRef, String amount, String dueDate);
	void sendPaymentReminder(Invoice invoice);
	void sendInvoiceNotification(Invoice invoice);
}
