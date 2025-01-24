package com.digital.school.service;

import com.digital.school.model.SubscriptionPlan;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface SubscriptionPlanService {
    List<Map<String, Object>> findAllAsMap();
    List<SubscriptionPlan> findAllActive();
    Optional<SubscriptionPlan> findById(Long id);
    Optional<SubscriptionPlan> findByCode(String code);
    SubscriptionPlan save(SubscriptionPlan plan);
    void deleteById(Long id);
    boolean existsById(Long id);
}
