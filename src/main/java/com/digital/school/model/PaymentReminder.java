package com.digital.school.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_reminders")
public class PaymentReminder extends AuditableEntity {

    @ManyToOne
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;
    
    private String type; // EMAIL, SMS
    private String status; // SENT, FAILED
    private LocalDateTime sentAt;
    private String content;
    
    
    public PaymentReminder() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Invoice getInvoice() {
		return invoice;
	}
	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public LocalDateTime getSentAt() {
		return sentAt;
	}
	public void setSentAt(LocalDateTime sentAt) {
		this.sentAt = sentAt;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
    
    // Getters and setters
    
    
    
}
