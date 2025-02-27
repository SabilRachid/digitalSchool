package com.digital.school.controller.rest;

import com.digital.school.model.Classe;
import com.digital.school.model.Professor;
import com.digital.school.service.ClasseService;
import com.digital.school.service.ProfessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    @Autowired
    private ProfessorService professorService;

    @Autowired
    private ClasseService classeService;

    /**
     * Retourne la liste de tous les professeurs sous forme de DTO.
     */
    @GetMapping("/professors")
    public ResponseEntity<List<ProfessorDTO>> getAllProfessors() {
        List<Professor> professors = professorService.findAll();
        List<ProfessorDTO> result = professors.stream()
                .map(prof -> new ProfessorDTO(prof.getId(), prof.getFirstName() + " " + prof.getLastName()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    /**
     * Retourne la liste de toutes les classes sous forme de DTO.
     */
    @GetMapping("/classes")
    public ResponseEntity<List<ClasseDTO>> getAllClasses() {
        List<Classe> classes = classeService.findAll();
        List<ClasseDTO> result = classes.stream()
                .map(classe -> new ClasseDTO(classe.getId(), classe.getName()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    /**
     * Retourne la liste des groupes d'étudiants.
     * Ici, on considère que les groupes correspondent aux classes.
     */
    @GetMapping("/student-groups")
    public ResponseEntity<List<ClasseDTO>> getStudentGroups() {
        List<Classe> classes = classeService.findAll();
        List<ClasseDTO> result = classes.stream()
                .map(classe -> new ClasseDTO(classe.getId(), classe.getName()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    // DTO internes pour retourner uniquement les informations nécessaires

    public static class ProfessorDTO {
        private Long id;
        private String name;

        public ProfessorDTO(Long id, String name) {
            this.id = id;
            this.name = name;
        }
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    public static class ClasseDTO {
        private Long id;
        private String name;

        public ClasseDTO(Long id, String name) {
            this.id = id;
            this.name = name;
        }
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }
}
