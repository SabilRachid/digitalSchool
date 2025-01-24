package com.digital.school.config;


import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Data
public class PaymentRequest {
    private Long studentId;
    private Long parentId;
    private Long invoiceId;
    
    private BigDecimal amount;
    private String currency = "MAD"; // Dirham marocain par défaut
    private String description;
    private String paymentMethod = "CARD"; // Carte bancaire par défaut
    private LocalDateTime requestDate = LocalDateTime.now();
    private String language = "fr"; // Langue par défaut
    private String returnUrl;
    private String cancelUrl;
    private String notifyUrl;
    
    private Map<String, String> metadata; // Données supplémentaires
    
    // Méthode pour générer l'ID de commande unique
    public String generateOrderId() {
        return String.format("ORD-%d-%d-%s", 
            this.studentId, 
            this.invoiceId, 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
        );
    }
    
    // Méthode pour valider la requête
    public void validate() {
        if (studentId == null) {
            throw new IllegalArgumentException("L'ID de l'étudiant est requis");
        }
        if (parentId == null) {
            throw new IllegalArgumentException("L'ID du parent est requis");
        }
        if (invoiceId == null) {
            throw new IllegalArgumentException("L'ID de la facture est requis");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant doit être supérieur à 0");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("La description est requise");
        }
    }

	public Long getStudentId() {
		return studentId;
	}

	public void setStudentId(Long studentId) {
		this.studentId = studentId;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public Long getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(Long invoiceId) {
		this.invoiceId = invoiceId;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public LocalDateTime getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(LocalDateTime requestDate) {
		this.requestDate = requestDate;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getReturnUrl() {
		return returnUrl;
	}

	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}

	public String getCancelUrl() {
		return cancelUrl;
	}

	public void setCancelUrl(String cancelUrl) {
		this.cancelUrl = cancelUrl;
	}

	public String getNotifyUrl() {
		return notifyUrl;
	}

	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}

	public Map<String, String> getMetadata() {
		return metadata;
	}

	public void setMetadata(Map<String, String> metadata) {
		this.metadata = metadata;
	}
    
    
}
