package com.digital.school.service.impl;

import com.digital.school.model.Payment;
import com.digital.school.service.PaymentGatewayService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Service
public class CMIPaymentGatewayServiceImpl implements PaymentGatewayService {

    @Value("${cmi.merchant.id}")
    private String merchantId;
    
    @Value("${cmi.secret.key}")
    private String secretKey;
    
    @Value("${cmi.gateway.url}")
    private String gatewayUrl;

    @Override
    public String initializePayment(String orderId, BigDecimal amount, String description, String returnUrl) {
        Map<String, String> params = new TreeMap<>();
        params.put("merchantId", merchantId);
        params.put("orderId", orderId);
        params.put("amount", amount.toString());
        params.put("currency", "504"); // Code pour MAD
        params.put("description", description);
        params.put("language", "fr");
        params.put("returnUrl", returnUrl);
        
        String signature = generateSignature(params);
        params.put("signature", signature);
        
        return buildPaymentUrl(params);
    }

    @Override
    public boolean verifyPayment(String orderId, String transactionId, Map<String, String> params) {
        String receivedSignature = params.get("signature");
        params.remove("signature");
        
        String calculatedSignature = generateSignature(params);
        
        return receivedSignature.equals(calculatedSignature);
    }

    @Override
    public Map<String, String> getPaymentStatus(String orderId) {
        Map<String, String> params = new TreeMap<>();
        params.put("merchantId", merchantId);
        params.put("orderId", orderId);
        
        String signature = generateSignature(params);
        params.put("signature", signature);
        
        // Appel à l'API CMI pour vérifier le statut
        // Implémentation à adapter selon l'API CMI
        
        return new HashMap<>();
    }

    private String generateSignature(Map<String, String> params) {
        try {
            String data = String.join("|", params.values());
            
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            
            byte[] hash = sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Error generating signature", e);
        }
    }

    private String buildPaymentUrl(Map<String, String> params) {
        StringBuilder url = new StringBuilder(gatewayUrl + "?");
        params.forEach((key, value) -> url.append(key).append("=").append(value).append("&"));
        return url.substring(0, url.length() - 1);
    }

	@Override
	public String initiatePayment(Payment payment) {
		// TODO Auto-generated method stub
		return null;
	}
}