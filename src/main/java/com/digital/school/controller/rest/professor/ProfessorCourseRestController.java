package com.digital.school.controller.rest.professor;

import com.digital.school.service.CourseService;
import com.digital.school.service.ProfessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<List<Map<String, Object>>> getCoursesForProfessor(@AuthenticationPrincipal UserDetails userDetails) {
        // Récupérer l'ID du professeur connecté
        Professor professor = professorService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Professeur non trouvé"));

        // Récupérer les cours du professeur
        List<Map<String, Object>> courses = courseService.findByProfessorId(professor.getId())
                .stream()
                .map(course -> Map.of(
                        "id", course.getId(),
                        "name", course.getName(),
                        "subject", course.getSubject().getName(),
                        "class", course.getClasse().getName(),
                        "startTime", course.getStartTime().toString(),
                        "endTime", course.getEndTime().toString(),
                        "room", course.getRoom()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(courses);
    }
}

