package com.digital.school.service.impl;

import com.digital.school.model.Invoice;
import com.digital.school.model.InvoiceItem;
import com.digital.school.model.PaymentReminder;
import com.digital.school.model.Student;
import com.digital.school.model.User;
import com.digital.school.repository.InvoiceRepository;
import com.digital.school.repository.PaymentReminderRepository;
import com.digital.school.service.BillingService;
import com.digital.school.service.EmailService;
import com.digital.school.service.SMSService;
import com.digital.school.service.PDFService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Autowired
    private PDFService pdfService;

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

        // Génération du PDF et sauvegarde du chemin
        byte[] pdfContent = generateInvoicePdf(invoice);
        String pdfPath = savePdfToStorage(pdfContent, invoice.getInvoiceNumber());
        invoice.setPdfPath(pdfPath);

        // Envoi de la notification par e-mail
        emailService.sendInvoiceNotification(invoice);

        return invoice;
    }

    private String generateInvoiceNumber() {
        // Exemple simple, à remplacer par une logique métier plus robuste
        return "INV-" + System.currentTimeMillis();
    }

    private String savePdfToStorage(byte[] pdfContent, String invoiceNumber) {
        // Implémentez le stockage du PDF et retournez son chemin ou URL
        // Exemple : retourner l'invoiceNumber comme chemin
        return "/invoices/" + invoiceNumber + ".pdf";
    }

    @Override
    public void sendInvoiceReminders() {
        // Récupère les factures pour lesquelles un rappel est nécessaire (définissez cette méthode dans InvoiceRepository)
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(7);
        List<Invoice> invoices = invoiceRepository.findInvoicesNeedingReminder(cutoffDate);

        for (Invoice invoice : invoices) {
            // Envoi d'un rappel par email
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

            // Envoi d'un rappel par SMS
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

            // Mise à jour de la facture
            invoice.setLastReminderSent(LocalDateTime.now());
            invoice.setReminderCount(invoice.getReminderCount() + 1);
            invoiceRepository.save(invoice);
        }
    }

    @Override
    public Invoice getInvoice(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
    }

    @Override
    public List<Invoice> getParentInvoices(User user) {
        // Supposons que InvoiceRepository possède une méthode findByParentId
        return invoiceRepository.findByParentOrderByIssueDateDesc(user);
    }

    @Override
    public byte[] generateInvoicePdf(Invoice invoice) {
        // Délégation au service PDF, en passant les données nécessaires
        Map<String, Object> data = new HashMap<>();
        data.put("invoice", invoice);
        return pdfService.generateReport("invoice-template", data);
    }

    @Override
    public Map<String, Object> getBillingStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalInvoices", invoiceRepository.count());
        stats.put("totalAmountDue", invoiceRepository.sumTotalAmountDue());
        return stats;
    }

    @Override
    public List<Map<String, Object>> getAllInvoicesAsMap() {
        // Supposons que InvoiceRepository possède une méthode findAllInvoicesAsMap
        return invoiceRepository.findAllInvoicesAsMap();
    }

    @Override
    public void sendInvoiceReminder(Long id) {
        Invoice invoice = getInvoice(id);
        PaymentReminder reminder = new PaymentReminder();
        reminder.setInvoice(invoice);
        reminder.setType("EMAIL");
        reminder.setSentAt(LocalDateTime.now());
        try {
            emailService.sendPaymentReminder(invoice);
            reminder.setStatus("SENT");
        } catch (Exception e) {
            reminder.setStatus("FAILED");
        }
        reminderRepository.save(reminder);
    }
}
