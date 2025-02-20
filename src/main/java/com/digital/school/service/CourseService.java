package com.digital.school.service;

import com.digital.school.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CourseService {
    Page<Course> findAll(Pageable pageable);
    List<Course> findAll();
    Optional<Course> findById(Long id);
    Course save(Course course);
    void deleteById(Long id);
    boolean existsById(Long id);
    List<Course> findTodaySchedule(Student student);
    List<Course> findByClasse(Classe classe);
    List<Map<String, Object>> findByProfessor(Professor professor);
    List<Map<String, Object>> findAllAsMap();
    List<Map<String, Object>> findAllBasicInfo();
    Object findByStudent(Student student);

    List<Map<String, Object>> getCourseResourcesAsMap(Long id);
    Map<String, String> generateMeetingLink(Long id);

    Object findTodaySchedule(Professor professor);

    Object findTodayCourses();

    Object findTodayCoursesForStudent(Long studentId);
}