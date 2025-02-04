package com.digital.school.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.digital.school.model.*;
import com.digital.school.repository.*;
import com.digital.school.service.PaymentGatewayService;
import com.digital.school.service.PaymentService;
import com.digital.school.service.StorageService;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private InvoiceRepository invoiceRepository;
    
    @Autowired
    private PaymentGatewayService paymentGateway;

    @Autowired StorageService storageService;
    
    @Value("${app.storage.receipts.path}")
    private String receiptsPath;

    
    @Override
    public Payment processPayment(Invoice invoice, BigDecimal amount, String paymentMethod) {
        Payment payment = new Payment();
        payment.setInvoice(invoice);
        payment.setAmount(amount);
        payment.setPaymentMethod(paymentMethod);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setStatus("PENDING");
        
        payment = paymentRepository.save(payment);
        
        // Process payment through gateway
        String transactionId = paymentGateway.initiatePayment(payment);
        payment.setTransactionId(transactionId);
        
        return paymentRepository.save(payment);
    }

    @Override
    public void handlePaymentCallback(String transactionId, String status) {
        Payment payment = paymentRepository.findByTransactionId(transactionId)
            .orElseThrow(() -> new RuntimeException("Payment not found"));
            
        payment.setStatus(status);
        
        if ("COMPLETED".equals(status)) {
            payment.setPaymentDate(LocalDateTime.now());
            
            // Generate receipt
            byte[] receiptContent = generateReceipt(payment);
            String receiptPath = saveReceiptToStorage(receiptContent, payment.getId());
            payment.setReceiptPath(receiptPath);
            
            // Update invoice status if fully paid
            Invoice invoice = payment.getInvoice();
            BigDecimal totalPaid = paymentRepository.getTotalPaidAmount(invoice.getId());
            if (totalPaid.compareTo(invoice.getTotalAmount()) >= 0) {
                invoice.setStatus("PAID");
                invoiceRepository.save(invoice);
            }
        }
        
        paymentRepository.save(payment);
    }

	@Override
	public Payment getPayment(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] generateReceipt(Payment payment) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String initiatePayment(String orderId, BigDecimal amount, String description, String customerEmail) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean verifyPayment(String orderId, String transactionId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void savePaymentReceipt(String orderId, byte[] receipt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public byte[] getReceipt(String orderId) {
		// TODO Auto-generated method stub
		return null;
	}
    
    
	@Override
    public String saveReceiptToStorage(byte[] receiptContent, Long paymentId) {
        try {
            // Générer un nom de fichier unique
            String fileName = generateReceiptFileName(paymentId);
            
            // Créer le chemin complet
            String fullPath = generateReceiptPath(fileName);

            // Sauvegarder le fichier
            storageService.store(receiptContent, fileName);
            
            // Mettre à jour le paiement avec le chemin du reçu
            Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Paiement non trouvé"));
            payment.setReceiptPath(fullPath);
            paymentRepository.save(payment);
            
            return fullPath;
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la sauvegarde du reçu", e);
        }
    }
    
    private String generateReceiptFileName(Long paymentId) {
        return String.format("receipt-%d-%s.pdf", 
            paymentId,
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
        );
    }
    
    private String generateReceiptPath(String fileName) {
        return String.format("%s/%s/%s",
            receiptsPath,
            LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM")),
            fileName
        );
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

}
