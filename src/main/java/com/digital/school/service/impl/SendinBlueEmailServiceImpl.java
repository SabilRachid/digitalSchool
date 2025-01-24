package com.digital.school.service.impl;

import com.digital.school.model.Invoice;
import com.digital.school.service.EmailService;
import sendinblue.ApiClient;
import sendinblue.Configuration;
import sendinblue.auth.ApiKeyAuth;
import sibApi.TransactionalEmailsApi;
import sibModel.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.Arrays;

@Service
public class SendinBlueEmailServiceImpl implements EmailService {

    private final TransactionalEmailsApi api;
    
    @Value("${sendinblue.api-key}")
    private String apiKey;
    
    @Value("${sendinblue.sender.email}")
    private String senderEmail;
    
    @Value("${sendinblue.sender.name}")
    private String senderName;

    public SendinBlueEmailServiceImpl() {
        ApiClient client = Configuration.getDefaultApiClient();
        api = new TransactionalEmailsApi();
        ApiKeyAuth apiKeyAuth = (ApiKeyAuth) client.getAuthentication("api-key");
        apiKeyAuth.setApiKey(apiKey);
    }

    @Override
    public void sendEmail(String to, String subject, String template, Map<String, Object> variables) {
        try {
            SendSmtpEmail email = new SendSmtpEmail();
            email.setSender(new SendSmtpEmailSender().email(senderEmail).name(senderName));
            email.setTo(Arrays.asList(new SendSmtpEmailTo().email(to)));
            email.setSubject(subject);
            email.setTemplateId(Long.parseLong(template));
            email.setParams(variables);
            
            api.sendTransacEmail(email);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'envoi de l'email", e);
        }
    }

    @Override
    public void sendPaymentConfirmation(String to, String orderRef, String amount) {
        Map<String, Object> variables = Map.of(
            "orderRef", orderRef,
            "amount", amount,
            "date", java.time.LocalDateTime.now().toString()
        );
        
        sendEmail(to, "Confirmation de paiement", "payment-confirmation-template", variables);
    }

    @Override
    public void sendPaymentReminder(String to, String invoiceRef, String amount, String dueDate) {
        Map<String, Object> variables = Map.of(
            "invoiceRef", invoiceRef,
            "amount", amount,
            "dueDate", dueDate
        );
        
        sendEmail(to, "Rappel de paiement", "payment-reminder-template", variables);
    }

	@Override
	public void sendPaymentReminder(Invoice invoice) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendInvoiceNotification(Invoice invoice) {
		// TODO Auto-generated method stub
		
	}
}
