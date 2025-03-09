package com.digital.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.digital.school.model.Invoice;
import com.digital.school.model.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByParentOrderByIssueDateDesc(User parent);
    List<Invoice> findByStudentOrderByIssueDateDesc(User student);
    List<Invoice> findByStatusAndDueDateBefore(String status, LocalDateTime date);
    
    @Query("SELECT i FROM Invoice i WHERE i.status = 'PENDING' AND i.dueDate < CURRENT_TIMESTAMP")
    List<Invoice> findOverdueInvoices();
    
    @Query("SELECT i FROM Invoice i WHERE i.lastReminderSent < ?1 OR i.lastReminderSent IS NULL")
    List<Invoice> findInvoicesNeedingReminder(LocalDateTime cutoffDate);

    @Query("SELECT new map(" +
            "i.id as id, " +
            "i.invoiceNumber as invoiceNumber, " +
            "i.issueDate as issueDate, " +
            "i.dueDate as dueDate, " +
            "i.totalAmount as totalAmount, " +
            "i.status as status, " +
            "i.pdfPath as pdfPath" +
            ") FROM Invoice i")
    List<Map<String, Object>> findAllInvoicesAsMap();

    @Query("SELECT COALESCE(SUM(i.totalAmount), 0) FROM Invoice i WHERE i.status = 'PENDING'")
    BigDecimal sumTotalAmountDue();

}
