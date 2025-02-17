package com.digital.school.service;

import com.digital.school.model.School;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface SchoolService {
    List<Map<String, Object>> findAllAsMap();
    Optional<School> findById(Long id);
    Optional<School> findByCode(String code);
    School save(School school);
    void deleteById(Long id);
    List<School> findRecentlyCreated();
    Map<String, Object> getSchoolStatistics();
    boolean existsById(Long id);
    School getSchools();
    School updateSchool(School school);
}