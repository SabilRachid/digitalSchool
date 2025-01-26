package com.digital.school.service;

import com.digital.school.model.Course;
import com.digital.school.model.User;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CourseService {
    List<Course> findByStudent(User student);
    Optional<Course> findById(Long id);
    List<Map<String, Object>> getCourseResourcesAsMap(Long courseId);
    Map<String, String> generateMeetingLink(Long courseId);
    List<Course> findCurrentCourses(User student);
    List<Course> findUpcomingCourses(User student);
    Map<String, Object> getCourseStatistics(Long courseId);
}