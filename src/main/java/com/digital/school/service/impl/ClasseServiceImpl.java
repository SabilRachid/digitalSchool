package com.digital.school.service.impl;

import com.digital.school.model.Professor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(ClasseServiceImpl.class);


    @Autowired
    private ClasseRepository classeRepository;

    @Override
    public List<Map<String, Object>> findAllAsMap() {
        LOGGER.debug("findAllAsMap ClasseServiceImpl" + getClass().getName());
        return classeRepository.findAll().stream()
            .map(classe -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", classe.getId());
                map.put("name", classe.getName());
                map.put("level", Map.of(
                    "id", classe.getLevel().getId(),
                    "name", classe.getLevel().getName()
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
        LOGGER.debug("findAllBasicInfo ClasseServiceImpl"+ getClass().getName());
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

    @Override
    public List<Classe> getAll() {
        return classeRepository.findAll();
    }

    @Override
    public List<Classe> findByProfessor(Professor professor) {
        return classeRepository.findByProfessor(professor);
    }
}