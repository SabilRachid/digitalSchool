package com.digital.school.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.digital.school.model.SubscriptionPlan;
import com.digital.school.repository.SubscriptionPlanRepository;
import com.digital.school.service.SubscriptionPlanService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class SubscriptionPlanServiceImpl implements SubscriptionPlanService {

    @Autowired
    private SubscriptionPlanRepository subscriptionPlanRepository;

    @Override
    public List<Map<String, Object>> findAllAsMap() {
        return subscriptionPlanRepository.findAll().stream()
            .map(plan -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", plan.getId());
                map.put("name", plan.getName());
                map.put("code", plan.getCode());
                map.put("description", plan.getDescription());
                map.put("price", plan.getPrice());
                map.put("durationMonths", plan.getDurationMonths());
                map.put("maxStudents", plan.getMaxStudents());
                map.put("maxTeachers", plan.getMaxTeachers());
                map.put("features", plan.getFeatures());
                map.put("active", plan.isActive());
                return map;
            })
            .collect(Collectors.toList());
    }

    @Override
    public List<SubscriptionPlan> findAllActive() {
        return subscriptionPlanRepository.findAllActiveOrderByPrice();
    }

    @Override
    public Optional<SubscriptionPlan> findById(Long id) {
        return subscriptionPlanRepository.findById(id);
    }

    @Override
    public Optional<SubscriptionPlan> findByCode(String code) {
        return subscriptionPlanRepository.findByCode(code);
    }

    @Override
    @Transactional
    public SubscriptionPlan save(SubscriptionPlan plan) {
        plan.setUpdatedAt(LocalDateTime.now());
        return subscriptionPlanRepository.save(plan);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        subscriptionPlanRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return subscriptionPlanRepository.existsById(id);
    }
}
