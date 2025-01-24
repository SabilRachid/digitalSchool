package com.digital.school.service;

import com.digital.school.model.Invoice;

public interface SMSService {
    void sendSMS(String to, String message);
    void sendPaymentConfirmation(String to, String amount);
    void sendPaymentReminder(String to, String amount, String dueDate);
	void sendPaymentReminder(Invoice invoice);
}
