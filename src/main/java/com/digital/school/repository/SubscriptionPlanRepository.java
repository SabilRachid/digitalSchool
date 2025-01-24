package com.digital.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.digital.school.model.SubscriptionPlan;
import java.util.List;
import java.util.Optional;

public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {
    Optional<SubscriptionPlan> findByCode(String code);
    List<SubscriptionPlan> findByActiveTrue();
    
    @Query("SELECT p FROM SubscriptionPlan p WHERE p.active = true ORDER BY p.price ASC")
    List<SubscriptionPlan> findAllActiveOrderByPrice();
}
