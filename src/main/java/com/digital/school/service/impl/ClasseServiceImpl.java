package com.digital.school.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.digital.school.model.Classe;
import com.digital.school.repository.ClasseRepository;
import com.digital.school.service.ClasseService;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ClasseServiceImpl implements ClasseService {

    @Autowired
    private ClasseRepository classeRepository;

    @Override
    public List<Map<String, Object>> findAllAsMap() {
        return classeRepository.findAll().stream()
            .map(classe -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", classe.getId());
                map.put("name", classe.getName());
                map.put("level", Map.of(
                    "id", classe.getLevel(),
                    "name", classe.getLevel()
                ));
                map.put("maxStudents", classe.getMaxStudents());
                map.put("currentStudents", countStudents(classe.getId()));
                map.put("schoolYear", classe.getSchoolYear());
                return map;
            })
            .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> findAllBasicInfo() {
        return classeRepository.findAll().stream()
            .map(classe -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", classe.getId());
                map.put("name", classe.getName());
                return map;
            })
            .collect(Collectors.toList());
    }
    
    @Override
    public Optional<Classe> findById(Long id) {
        return classeRepository.findById(id);
    }

    @Override
    @Transactional
    public Classe save(Classe classe) {
        return classeRepository.save(classe);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        classeRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return classeRepository.existsById(id);
    }

    @Override
    public boolean existsByNameAndSchoolYear(String name, String schoolYear) {
        return classeRepository.existsByNameAndSchoolYear(name, schoolYear);
    }

    @Override
    public List<Classe> findByLevelId(Long levelId) {
        return classeRepository.findByLevelId(levelId);
    }

    @Override
    public long countStudents(Long classeId) {
        return classeRepository.countStudents(classeId);
    }
}