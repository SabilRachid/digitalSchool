package com.digital.school.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.digital.school.model.Level;
import com.digital.school.repository.LevelRepository;
import com.digital.school.service.LevelService;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LevelServiceImpl implements LevelService {

    @Autowired
    private LevelRepository levelRepository;

    @Override
    public List<Map<String, Object>> findAllAsMap() {
        return levelRepository.findAllByOrderByOrderAsc().stream()
            .map(level -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", level.getId());
                map.put("code", level.getCode());
                map.put("name", level.getName());
                map.put("levelName", level.getLevelName());
                map.put("cycle", level.getCycle());
                map.put("order", level.getOrder());
                map.put("classCount", countClasses(level.getId()));
                return map;
            })
            .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> findAllBasicInfo() {
        return levelRepository.findAllByOrderByOrderAsc().stream()
            .map(level -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", level.getId());
                map.put("name", level.getName());
                return map;
            })
            .collect(Collectors.toList());
    }

    @Override
    public Optional<Level> findById(Long id) {
        return levelRepository.findById(id);
    }

    @Override
    @Transactional
    public Level save(Level level) {
        // Si pas d'ordre spécifié, on calcule automatiquement
        if (level.getOrder() == null && level.getCycle() != null) {
            Integer maxOrder = findMaxOrderByCycle(level.getCycle());
            level.setOrder(maxOrder != null ? maxOrder + 1 : 1);
        }
        return levelRepository.save(level);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        levelRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return levelRepository.existsById(id);
    }

    @Override
    public boolean existsByCode(String code) {
        return levelRepository.existsByCode(code);
    }

    @Override
    public List<Level> findAllOrdered() {
        return levelRepository.findAllByOrderByOrderAsc();
    }

    @Override
    public long countClasses(Long levelId) {
        return levelRepository.countClasses(levelId);
    }

    @Override
    public Integer findMaxOrderByCycle(String cycle) {
        return levelRepository.findMaxOrderByCycle(cycle);
    }
}