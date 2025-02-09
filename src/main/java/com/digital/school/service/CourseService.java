package com.digital.school.service;

import com.digital.school.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.swing.text.html.Option;
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
    List<Course> findByClasse(Classe classe);
    List<Course> findByProfessor(Professor professor);
    List<Course> findBySubject(Subject subject);
    List<Course> findByDateRange(LocalDateTime start, LocalDateTime end);
    List<Course> findUpcomingCourses(User user);
    Map<String, Object> getCourseStatistics(Course course);
    List<Map<String, Object>> findAllAsMap();
    List<Map<String, Object>> findAllBasicInfo();
    Object findByStudent(Student student);

    List<Map<String, Object>> getCourseResourcesAsMap(Long id);
    Map<String, String> generateMeetingLink(Long id);

    Object findTodaySchedule(Professor professor);

    Object findTodayCourses();
}