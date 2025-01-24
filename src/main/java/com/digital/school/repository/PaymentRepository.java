package com.digital.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.digital.school.model.Payment;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByInvoiceId(Long invoiceId);
    
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = 'COMPLETED' AND p.paymentDate BETWEEN ?1 AND ?2")
    BigDecimal calculateRevenue(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT p.paymentMethod, COUNT(p) FROM Payment p WHERE p.status = 'COMPLETED' GROUP BY p.paymentMethod")
    List<Object[]> getPaymentMethodStats();

    Optional<Payment> findByTransactionId(String transactionId);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.invoice.id = :invoiceId AND p.status = 'COMPLETED'")
    BigDecimal getTotalPaidAmount(@Param("invoiceId") Long invoiceId);

}
