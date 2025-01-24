package com.digital.school.service;

import com.digital.school.model.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface BillingService {
    Invoice generateInvoice(User student, List<InvoiceItem> items);
    Invoice getInvoice(Long id);
    List<Invoice> getParentInvoices(User parent);
    byte[] generateInvoicePdf(Invoice invoice);
    void sendInvoiceReminders();
    Map<String, Object> getBillingStats();
	List<Map<String, Object>> getAllInvoicesAsMap();
	void sendInvoiceReminder(Long id);
}
