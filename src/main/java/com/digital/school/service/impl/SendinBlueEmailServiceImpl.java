package com.digital.school.service.impl;

import com.digital.school.model.Invoice;
import com.digital.school.service.EmailService;
import sendinblue.ApiClient;
import sendinblue.auth.ApiKeyAuth;
import sibApi.TransactionalEmailsApi;
import sibModel.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class SendinBlueEmailServiceImpl implements EmailService {

   private final TransactionalEmailsApi api=null;

    @Value("${sendinblue.sender.email}")
    private String senderEmail;

    @Value("${sendinblue.sender.name}")
    private String senderName;


    public SendinBlueEmailServiceImpl() {
        // Pas de code ici
    }

/*
    public SendinBlueEmailServiceImpl(@Value("${sendinblue.api-key}") String apiKey) {
        // Configuration personnalisée d'ApiClient
        ApiClient client = new ApiClient();
        client.setBasePath("https://api.sendinblue.com/v3");

        // Configuration de la clé API
        ApiKeyAuth apiKeyAuth = (ApiKeyAuth) client.getAuthentication("api-key");
        apiKeyAuth.setApiKey(apiKey);

        this.api = new TransactionalEmailsApi(client);
    }
*/

    @Override
    public void sendEmail(String to, String subject, String template, Map<String, Object> variables) {
        try {
            // Création de l'email à envoyer
            SendSmtpEmail email = new SendSmtpEmail()
                    .sender(new SendSmtpEmailSender().email(senderEmail).name(senderName))
                    .to(Arrays.asList(new SendSmtpEmailTo().email(to)))
                    .subject(subject)
                    .templateId(Long.parseLong(template))
                    .params(variables);

            // Envoi de l'email
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
        // Exemple d'implémentation si vous utilisez des données spécifiques à l'objet `Invoice`
        Map<String, Object> variables = Map.of(
                "invoiceRef", invoice.getInvoiceNumber(),
                "amount", invoice.getTotalAmount().toString(),
                "dueDate", invoice.getDueDate().toString()
        );

        sendEmail(invoice.getParent().getEmail(), "Rappel de paiement", "payment-reminder-template", variables);
    }

    @Override
    public void sendInvoiceNotification(Invoice invoice) {
        // Exemple d'implémentation pour une notification de facture
        Map<String, Object> variables = Map.of(
                "invoiceRef", invoice.getInvoiceNumber(),
                "amount", invoice.getTotalAmount().toString(),
                "date", invoice.getIssueDate().toString()
        );

        sendEmail(invoice.getParent().getEmail(), "Notification de facture", "invoice-notification-template", variables);
    }
}
