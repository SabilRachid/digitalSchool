package com.digital.school.controller.rest.professor;

import com.digital.school.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.digital.school.model.Professor;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/professor/api/courses")
public class ProfessorCourseRestController {

    @Autowired
    private CourseService courseService;

    /**
     * Retourne la liste des cours pour le professeur connecté, sous forme de Map,
     * adaptée à l'affichage dans la DataTable.
     */
    @GetMapping("/data")
    public ResponseEntity<List<Map<String, Object>>> getCoursesData(@AuthenticationPrincipal Professor professor) {
        List<Map<String, Object>> coursesData = courseService.findByProfessor(professor);
        return ResponseEntity.ok(coursesData);
    }

    // Vous pouvez ajouter ici d'autres endpoints (GET par ID, PUT, DELETE) si nécessaire.
}
