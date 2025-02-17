package com.digital.school.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.digital.school.model.School;
import com.digital.school.repository.SchoolRepository;
import com.digital.school.service.SchoolService;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class SchoolServiceImpl implements SchoolService {

    @Autowired
    private SchoolRepository schoolRepository;




    @Override
    public School updateSchool(School school) {
        return schoolRepository.save(school);
    }

    @Override
    public List<Map<String, Object>> findAllAsMap() {
        return schoolRepository.findAll().stream()
                .map(school -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", school.getId());
                    map.put("name", school.getName());
                    map.put("code", school.getCode());
                    map.put("city", school.getCity());
                    map.put("status", "Active");
                    map.put("createdAt", school.getAuditable().getCreated());
                    return map;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Optional<School> findById(Long id) {
        return schoolRepository.findById(id);
    }

    @Override
    public Optional<School> findByCode(String code) {
        return schoolRepository.findByCode(code);
    }

    @Override
    @Transactional
    public School save(School school) {

        return schoolRepository.save(school);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        schoolRepository.deleteById(id);
    }



    @Override
    public List<School> findRecentlyCreated() {
        return schoolRepository.findRecentlyCreatedSchools();
    }

    @Override
    public Map<String, Object> getSchoolStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalSchools", schoolRepository.count());
        stats.put("activeSchools", schoolRepository.countActiveSchools());
        // Add more statistics as needed
        return stats;
    }

    @Override
    public boolean existsById(Long id) {
        return schoolRepository.existsById(id);
    }

    @Override
    public School getSchools() {
        return schoolRepository.findAll().get(0);
    }
}