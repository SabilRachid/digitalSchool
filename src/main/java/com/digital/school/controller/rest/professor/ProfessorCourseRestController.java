package com.digital.school.controller.rest.professor;

import com.digital.school.model.Course;
import com.digital.school.model.Professor;
import com.digital.school.service.CourseService;
import com.digital.school.service.ProfessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/professor/api/courses")
public class ProfessorCourseRestController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private ProfessorService professorService;

    @GetMapping("/my-courses")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getCoursesForProfessor(@AuthenticationPrincipal Professor professor) {

        List<Map<String, Object>> courses = courseService.findByProfessor(professor);
        return ResponseEntity.ok(courses);
    }
}

