package com.digital.school.controller.rest.student;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.digital.school.model.Course;
import com.digital.school.model.User;
import com.digital.school.service.CourseService;
import com.digital.school.service.SubjectService;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/student/courses")
public class StudentCourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private SubjectService subjectService;

    @GetMapping
    public String showCourses(HttpServletRequest request, @AuthenticationPrincipal User student, Model model) {

        model.addAttribute("courses", courseService.findByStudent(student));
        model.addAttribute("subjects", subjectService.findByStudent(student));
        model.addAttribute("currentURI", request.getRequestURI());
        return "student/courses";
    }

    @GetMapping("/{id}")
    public String showCourseDetails(@PathVariable Long id, Model model) {
        Course course = courseService.findById(id)
                .orElseThrow(() -> new RuntimeException("Cours non trouvé"));
        model.addAttribute("course", course);
        return "student/course-details";
    }

    @GetMapping("/{id}/resources")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getCourseResources(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseResourcesAsMap(id));
    }

    @GetMapping("/{id}/join")
    @ResponseBody
    public ResponseEntity<Map<String, String>> joinOnlineCourse(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.generateMeetingLink(id));
    }
}