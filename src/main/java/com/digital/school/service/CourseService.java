package com.digital.school.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.digital.school.model.Course;
import com.digital.school.model.Classe;
import com.digital.school.model.Subject;
import com.digital.school.model.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface CourseService {
    Page<Course> findAll(Pageable pageable);
    Course findById(Long id);
    Course save(Course course);
    void deleteById(Long id);
    boolean existsById(Long id);
    List<Course> findByClasse(Classe classe);
    List<Course> findByProfessor(User professor);
    List<Course> findBySubject(Subject subject);
    List<Course> findByDateRange(LocalDateTime start, LocalDateTime end);
    List<Course> findUpcomingCourses(User user);
    Map<String, Object> getCourseStatistics(Course course);
    List<Map<String, Object>> findAllAsMap();
    List<Map<String, Object>> findAllBasicInfo();
    Object findByStudent(User student);

    List<Map<String, Object>> getCourseResourcesAsMap(Long id);
    Map<String, String> generateMeetingLink(Long id);

    //donne moi implementation de
    Object findTodaySchedule(User professor);

}