package com.digital.school.service.impl;

import com.digital.school.config.CMIConfig;
import com.digital.school.model.Invoice;
import com.digital.school.model.Payment;
import com.digital.school.model.User;
import com.digital.school.service.PaymentService;
import com.digital.school.service.StorageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.util.Base64;

@Service
public class CMIPaymentServiceImpl implements PaymentService {
    
    @Autowired
    private CMIConfig cmiConfig;
    
    @Autowired
    private StorageService storageService;

    @Override
    public String initiatePayment(String orderId, BigDecimal amount, String description, String customerEmail) {
        // Préparation des paramètres pour CMI
        String amountStr = amount.multiply(new BigDecimal(100)).toString(); // Conversion en centimes
        
        // Construction des paramètres de paiement
        StringBuilder data = new StringBuilder();
        data.append("merchantId=").append(cmiConfig.getMerchantId())
            .append("&amount=").append(amountStr)
            .append("&currency=").append(cmiConfig.getCurrency())
            .append("&language=").append(cmiConfig.getLanguage())
            .append("&orderid=").append(orderId)
            .append("&customeremail=").append(customerEmail)
            .append("&description=").append(description);

        // Génération de la signature
        String signature = generateSignature(data.toString());
        
        // Construction de l'URL de redirection CMI
        return cmiConfig.getApiUrl() + "?" + data.toString() + "&signature=" + signature;
    }

    @Override
    public boolean verifyPayment(String orderId, String transactionId) {
        // Vérification de la réponse CMI
        // Implémentation de la vérification de la signature de retour
        return true; // À implémenter selon la documentation CMI
    }

    @Override
    public void savePaymentReceipt(String orderId, byte[] receipt) {
        String path = "receipts/" + orderId + ".pdf";
        //storageService.store(path, receipt);
        storageService.store(null, path);
    }

    private String generateSignature(String data) {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(cmiConfig.getStoreKey().getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            return Base64.getEncoder().encodeToString(sha256_HMAC.doFinal(data.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération de la signature", e);
        }
    }

	@Override
	public Payment processPayment(Invoice invoice, BigDecimal amount, String paymentMethod) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Payment getPayment(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void handlePaymentCallback(String transactionId, String status) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public byte[] generateReceipt(Payment payment) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] getReceipt(String orderId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object findAllPayments() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object findPaymentsByUser(User user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String saveReceiptToStorage(byte[] receiptContent, Long paymentId) {
		// TODO Auto-generated method stub
		return null;
	}
}
