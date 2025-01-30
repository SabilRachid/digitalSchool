package com.digital.school.service;

import com.digital.school.model.Subject;
import com.digital.school.model.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface SubjectService {
    List<Map<String, Object>> findAllAsMap();
    List<Map<String, Object>> findAllBasicInfo();
    Optional<Subject> findById(Long id);
    Subject save(Subject subject);
    void deleteById(Long id);
    boolean existsById(Long id);
    boolean existsByName(String name);
    List<Subject> findByClasseId(Long classeId);
    long countCourses(Long subjectId);
    Object findByStudent(User student);
}