package com.digital.school.service.impl;

import com.digital.school.model.Professor;
import com.digital.school.model.Student;
import com.digital.school.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.digital.school.model.Subject;
import com.digital.school.repository.SubjectRepository;
import com.digital.school.service.SubjectService;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class SubjectServiceImpl implements SubjectService {

    @Autowired
    private SubjectRepository subjectRepository;

    @Override
    public List<Map<String, Object>> findAllAsMap() {
        return subjectRepository.findAll().stream()
            .map(subject -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", subject.getId());
                map.put("code", subject.getCode());
                map.put("name", subject.getName());
                map.put("description", subject.getDescription());
                map.put("coefficient", subject.getCoefficient());
                map.put("optional", subject.isOptional());
                
                if (subject.getLevels() != null) {
                    map.put("levels", subject.getLevels().stream()
                        .map(level -> Map.of(
                            "id", level.getId(),
                            "name", level.getName()
                        ))
                        .collect(Collectors.toList()));
                } else {
                    map.put("levels", Collections.emptyList());
                }
                
                return map;
            })
            .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> findAllBasicInfo() {
        return subjectRepository.findAll().stream()
            .map(subject -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", subject.getId());
                map.put("name", subject.getName());
                return map;
            })
            .collect(Collectors.toList());
    }

    @Override
    public Optional<Subject> findById(Long id) {
        return subjectRepository.findById(id);
    }

    @Override
    @Transactional
    public Subject save(Subject subject) {
        return subjectRepository.save(subject);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        subjectRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return subjectRepository.existsById(id);
    }

    @Override
    public boolean existsByName(String name) {
        return subjectRepository.existsByName(name);
    }

    @Override
    public List<Subject> findByClasseId(Long classeId) {
        return subjectRepository.findByClasseId(classeId);
    }

    @Override
    public long countCourses(Long subjectId) {
        return subjectRepository.countCourses(subjectId);
    }

    @Override
    public List<Subject> findByStudent(Student student) {
        return subjectRepository.findByStudent(student);
    }

    @Override
    public List<Subject> findByProfessor(Professor professor) {
        return subjectRepository.findByProfessor(professor);
    }

    @Override
    public Set<Subject> findSubjectsByIds(Set<Long> ids) {
        Set<Subject> subjects = new HashSet<>();
        subjectRepository.findAllById(ids).forEach(subjects::add);
        return subjects;
    }
}