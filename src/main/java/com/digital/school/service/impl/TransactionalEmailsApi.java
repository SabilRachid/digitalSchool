package com.digital.school.service.impl;

import com.digital.school.config.SendSmtpEmail;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

public class TransactionalEmailsApi {
    private final String apiUrl = "https://api.sendinblue.com/v3";
    private final RestTemplate restTemplate;
    private final String apiKey;
    private final ObjectMapper objectMapper;

    public TransactionalEmailsApi(String apiKey) {
        this.apiKey = apiKey;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public void sendTransacEmail(SendSmtpEmail email) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", apiKey);

        try {
            String jsonBody = objectMapper.writeValueAsString(email);
            HttpEntity<String> request = new HttpEntity<>(jsonBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                apiUrl + "/smtp/email",
                HttpMethod.POST,
                request,
                String.class
            );

            if (response.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException("Failed to send email: " + response.getBody());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
