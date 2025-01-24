package com.digital.school.service;

import com.digital.school.model.Level;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface LevelService {
    List<Map<String, Object>> findAllAsMap();
    List<Map<String, Object>> findAllBasicInfo();
    Optional<Level> findById(Long id);
    Level save(Level level);
    void deleteById(Long id);
    boolean existsById(Long id);
    boolean existsByCode(String code);
    List<Level> findAllOrdered();
    long countClasses(Long levelId);
    Integer findMaxOrderByCycle(String cycle);
}