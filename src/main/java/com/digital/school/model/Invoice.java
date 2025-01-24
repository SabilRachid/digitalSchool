package com.digital.school.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "invoices")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    private String invoiceNumber;
    
    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student;
    
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private User parent;
    
    private LocalDateTime issueDate;
    private LocalDateTime dueDate;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal totalAmount;
    
    private String status; // PENDING, PAID, OVERDUE, CANCELLED
    
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL)
    private Set<InvoiceItem> items = new HashSet<>();
    
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL)
    private Set<Payment> payments = new HashSet<>();
    
    private String notes;
    private boolean installmentEnabled;
    private Integer installmentCount;
    
    @Column(columnDefinition = "TEXT")
    private String pdfPath;
    
    private LocalDateTime lastReminderSent;
    private Integer reminderCount;
    
    
    public Invoice() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getInvoiceNumber() {
		return invoiceNumber;
	}
	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}
	public User getStudent() {
		return student;
	}
	public void setStudent(User student) {
		this.student = student;
	}
	public User getParent() {
		return parent;
	}
	public void setParent(User parent) {
		this.parent = parent;
	}
	public LocalDateTime getIssueDate() {
		return issueDate;
	}
	public void setIssueDate(LocalDateTime issueDate) {
		this.issueDate = issueDate;
	}
	public LocalDateTime getDueDate() {
		return dueDate;
	}
	public void setDueDate(LocalDateTime dueDate) {
		this.dueDate = dueDate;
	}
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Set<InvoiceItem> getItems() {
		return items;
	}
	public void setItems(Set<InvoiceItem> items) {
		this.items = items;
	}
	public Set<Payment> getPayments() {
		return payments;
	}
	public void setPayments(Set<Payment> payments) {
		this.payments = payments;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	public boolean isInstallmentEnabled() {
		return installmentEnabled;
	}
	public void setInstallmentEnabled(boolean installmentEnabled) {
		this.installmentEnabled = installmentEnabled;
	}
	public Integer getInstallmentCount() {
		return installmentCount;
	}
	public void setInstallmentCount(Integer installmentCount) {
		this.installmentCount = installmentCount;
	}
	public String getPdfPath() {
		return pdfPath;
	}
	public void setPdfPath(String pdfPath) {
		this.pdfPath = pdfPath;
	}
	public LocalDateTime getLastReminderSent() {
		return lastReminderSent;
	}
	public void setLastReminderSent(LocalDateTime lastReminderSent) {
		this.lastReminderSent = lastReminderSent;
	}
	public Integer getReminderCount() {
		return reminderCount;
	}
	public void setReminderCount(Integer reminderCount) {
		this.reminderCount = reminderCount;
	}
    
    // Getters and setters
    
    
}
