package com.digital.school.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.digital.school.model.*;
import com.digital.school.repository.*;
import com.digital.school.service.BillingService;
import com.digital.school.service.EmailService;
import com.digital.school.service.SMSService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class BillingServiceImpl implements BillingService {

    @Autowired
    private InvoiceRepository invoiceRepository;
    
    @Autowired
    private PaymentReminderRepository reminderRepository;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private SMSService smsService;

    @Override
    public Invoice generateInvoice(Student student, List<InvoiceItem> items) {
        Invoice invoice = new Invoice();
        invoice.setStudent(student);
        invoice.setParent(student.getParent());
        invoice.setInvoiceNumber(generateInvoiceNumber());
        invoice.setIssueDate(LocalDateTime.now());
        invoice.setDueDate(LocalDateTime.now().plusDays(30));
        invoice.setStatus("PENDING");
        
        BigDecimal total = BigDecimal.ZERO;
        for (InvoiceItem item : items) {
            item.setInvoice(invoice);
            item.setTotal(item.getAmount().multiply(new BigDecimal(item.getQuantity())));
            total = total.add(item.getTotal());
        }
        invoice.setTotalAmount(total);
        invoice.getItems().addAll(items);
        
        invoice = invoiceRepository.save(invoice);
        
        // Generate PDF
        byte[] pdfContent = generateInvoicePdf(invoice);
        String pdfPath = savePdfToStorage(pdfContent, invoice.getInvoiceNumber());
        invoice.setPdfPath(pdfPath);
        
        // Send notification
        emailService.sendInvoiceNotification(invoice);
        
        return invoice;
    }

    private String generateInvoiceNumber() {
		// TODO Auto-generated method stub
		return null;
	}

	private String savePdfToStorage(byte[] pdfContent, String invoiceNumber) {
		return invoiceNumber;
		// TODO Auto-generated method stub
		
	}

	@Override
    public void sendInvoiceReminders() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(7);
        List<Invoice> invoices = invoiceRepository.findInvoicesNeedingReminder(cutoffDate);
        
        for (Invoice invoice : invoices) {
            // Send email reminder
            PaymentReminder emailReminder = new PaymentReminder();
            emailReminder.setInvoice(invoice);
            emailReminder.setType("EMAIL");
            emailReminder.setSentAt(LocalDateTime.now());
            
            try {
                emailService.sendPaymentReminder(invoice);
                emailReminder.setStatus("SENT");
            } catch (Exception e) {
                emailReminder.setStatus("FAILED");
            }
            
            reminderRepository.save(emailReminder);
            
            // Send SMS reminder
            PaymentReminder smsReminder = new PaymentReminder();
            smsReminder.setInvoice(invoice);
            smsReminder.setType("SMS");
            smsReminder.setSentAt(LocalDateTime.now());
            
            try {
                smsService.sendPaymentReminder(invoice);
                smsReminder.setStatus("SENT");
            } catch (Exception e) {
                smsReminder.setStatus("FAILED");
            }
            
            reminderRepository.save(smsReminder);
            
            // Update invoice
            invoice.setLastReminderSent(LocalDateTime.now());
            invoice.setReminderCount(invoice.getReminderCount() + 1);
            invoiceRepository.save(invoice);
        }
    }

	@Override
	public Invoice getInvoice(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Invoice> getParentInvoices(User user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] generateInvoicePdf(Invoice invoice) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> getBillingStats() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Map<String, Object>> getAllInvoicesAsMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sendInvoiceReminder(Long id) {
		// TODO Auto-generated method stub
		
	}
    
    // Other methods implementation...
}
