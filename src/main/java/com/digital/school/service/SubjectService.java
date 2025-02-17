package com.digital.school.service;

import com.digital.school.model.Professor;
import com.digital.school.model.Student;
import com.digital.school.model.Subject;
import com.digital.school.model.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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
    List<Subject> findByStudent(Student student);
    List<Subject> findByProfessor(Professor professor);

    Set<Subject> findSubjectsByIds(Set<Long> subjectIds);
}