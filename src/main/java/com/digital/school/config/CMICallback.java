package com.digital.school.config;


import lombok.Data;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

@Data
public class CMICallback {
    private String merchantId;
    private String orderId;
    private String transactionId;
    private String responseCode;
    private String transactionStatus;
    private BigDecimal amount;
    private String currency;
    private String hash;
    private String paymentMethod;
    private String cardNumber; // Derniers 4 chiffres uniquement
    private String authorizationId;
    private LocalDateTime transactionDate;
    private String email;
    private String phone;
    
    // Méthodes de validation
    public boolean isValid(String expectedMerchantId, String secretKey) {
        if (!merchantId.equals(expectedMerchantId)) {
            return false;
        }
        
        return validateHash(secretKey);
    }
    
    public boolean isSuccessful() {
        return "00".equals(responseCode) && "APPROVED".equals(transactionStatus);
    }
    
    private boolean validateHash(String secretKey) {
        String dataToHash = String.join("|",
            merchantId,
            orderId,
            transactionId,
            responseCode,
            transactionStatus,
            amount.toString(),
            currency
        );
        
        String calculatedHash = generateHash(dataToHash, secretKey);
        return hash.equals(calculatedHash);
    }
    
    private String generateHash(String data, String secretKey) {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            
            byte[] hash = sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du hash", e);
        }
    }

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String getTransactionStatus() {
		return transactionStatus;
	}

	public void setTransactionStatus(String transactionStatus) {
		this.transactionStatus = transactionStatus;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public String getAuthorizationId() {
		return authorizationId;
	}

	public void setAuthorizationId(String authorizationId) {
		this.authorizationId = authorizationId;
	}

	public LocalDateTime getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(LocalDateTime transactionDate) {
		this.transactionDate = transactionDate;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
    
    
    
    
}
