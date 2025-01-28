package com.digital.school.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "invoice_items")
public class InvoiceItem extends AuditableEntity {

    @ManyToOne
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;
    
    private String description;
    private String type; // TUITION, REGISTRATION, ACTIVITY, OTHER
    
    @Column(precision = 10, scale = 2)
    private BigDecimal amount;
    
    private Integer quantity;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal total;
    
    

	public InvoiceItem() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}
    
    // Getters and setters
    
    
}
