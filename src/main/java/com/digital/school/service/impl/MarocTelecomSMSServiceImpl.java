package com.digital.school.service.impl;

import com.digital.school.model.Invoice;
import com.digital.school.service.SMSService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Service
public class MarocTelecomSMSServiceImpl implements SMSService {

    @Value("${maroctelecom.sms.api-url}")
    private String apiUrl;
    
    @Value("${maroctelecom.sms.api-key}")
    private String apiKey;
    
    @Value("${maroctelecom.sms.sender-id}")
    private String senderId;

    private final RestTemplate restTemplate;

    public MarocTelecomSMSServiceImpl() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public void sendSMS(String to, String message) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        Map<String, Object> request = Map.of(
            "sender", senderId,
            "recipient", formatPhoneNumber(to),
            "content", message
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
        
        try {
            restTemplate.postForEntity(apiUrl + "/send", entity, String.class);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'envoi du SMS", e);
        }
    }

    @Override
    public void sendPaymentConfirmation(String to, String amount) {
        String message = String.format(
            "Votre paiement de %s MAD a été confirmé. Merci de votre confiance.",
            amount
        );
        sendSMS(to, message);
    }

    @Override
    public void sendPaymentReminder(String to, String amount, String dueDate) {
        String message = String.format(
            "Rappel: Un paiement de %s MAD est attendu pour le %s. Merci de régulariser votre situation.",
            amount, dueDate
        );
        sendSMS(to, message);
    }

    private String formatPhoneNumber(String phone) {
        // Formatage du numéro de téléphone au format marocain
        phone = phone.replaceAll("[^0-9]", "");
        if (phone.startsWith("0")) {
            phone = "212" + phone.substring(1);
        }
        return phone;
    }

	@Override
	public void sendPaymentReminder(Invoice invoice) {
		// TODO Auto-generated method stub
		
	}
    
    
    
}
