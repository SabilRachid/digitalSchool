package com.digital.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.digital.school.model.PaymentReminder;
import java.util.List;

public interface PaymentReminderRepository extends JpaRepository<PaymentReminder, Long> {
    List<PaymentReminder> findByInvoiceIdOrderBySentAtDesc(Long invoiceId);
}
