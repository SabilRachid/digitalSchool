package com.digital.school.controller.rest.admin;

import com.digital.school.dto.UserDTO;
import com.digital.school.model.*;
import com.digital.school.model.enumerated.RoleName;
import com.digital.school.service.ClasseService;
import com.digital.school.service.RoleService;
import com.digital.school.service.SubjectService;
import com.digital.school.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/api/users")
public class AdminUserRestController {

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(AdminUserRestController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ClasseService classeService;

    @Autowired
    private SubjectService subjectService;
    @Autowired
    private RoleService roleService;

    @GetMapping("/data")
    @ResponseBody
    public List<Map<String, Object>> getUsersData() {
        return userService.findAll(PageRequest.of(0, 1000)).getContent().stream()
                .map(this::convertUserToMap)
                .collect(Collectors.toList());
    }

    @GetMapping("/professors")
    @ResponseBody
    public List<Map<String, Object>> getProfessors() {
        return userService.findProfessorsAsMap();
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getUser(@PathVariable Long id) {
        return userService.findById(id)
                .map(user -> ResponseEntity.ok(convertUserToMap(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Erreur lors de la suppression: " + e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody UserDTO userDTO) {
        try {
            LOGGER.debug("📌 Requête reçue : {}", userDTO);
            User user = createUserByRole(userDTO);
            User savedUser = userService.save(user);
            return ResponseEntity.ok(convertUserToMap(savedUser));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Erreur lors de la création: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        try {
            User user = userService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
            updateUserFromDTO(user, userDTO);
            User savedUser = userService.save(user);
            return ResponseEntity.ok(convertUserToMap(savedUser));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Erreur lors de la mise à jour: " + e.getMessage()));
        }
    }

    // Méthode de lecture et suppression laissées inchangées...

    private User createUserByRole(UserDTO userDTO) {

        LOGGER.debug("createUserByRole: {}", userDTO.toString());
        Set<RoleName> roles = userDTO.getRoles();
        User user;

        if (roles.contains(RoleName.ROLE_STUDENT)) {
            Student student = new Student();
            student.setClasse(classeService.findById(userDTO.getClasseId())
                    .orElseThrow(() -> new RuntimeException("Classe non trouvée")));
            student.setRegistrationDate(java.time.LocalDateTime.now());
            user = student;
        } else if (roles.contains(RoleName.ROLE_PARENT)) {
            user = new Parent();
        } else if (roles.contains(RoleName.ROLE_PROFESSOR)) {
            Professor professor = new Professor();
            if (userDTO.getSubjectIds() != null && !userDTO.getSubjectIds().isEmpty()) {
                professor.setSubjects(subjectService.findSubjectsByIds(userDTO.getSubjectIds()));
            }
            user = professor;
        } else if (roles.contains(RoleName.ROLE_SECRETARY)) {
            user = new Secretary();
        } else {
            user = new Administrator();
        }

        updateUserFromDTO(user, userDTO);
        return user;
    }

    private void updateUserFromDTO(User user, UserDTO userDTO) {
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEnabled(userDTO.isEnabled());
        user.setRoles(userDTO.getRoles().stream()
                .map(role -> roleService.findByName(role)
                        .orElseThrow(() -> new RuntimeException("Rôle introuvable: " + role)))
                .collect(Collectors.toSet()));

        // Mise à jour spécifique pour les étudiants : affecter la classe si présente
        if (user instanceof Student) {
            Student student = (Student) user;
            if (userDTO.getClasseId() != null) {
                student.setClasse(classeService.findById(userDTO.getClasseId())
                        .orElseThrow(() -> new RuntimeException("Classe non trouvée")));
            }
        }

        // Mise à jour spécifique pour les professeurs : affecter les matières (subjects) si fournies
        if (user instanceof Professor) {
            Professor professor = (Professor) user;
            if (userDTO.getSubjectIds() != null && !userDTO.getSubjectIds().isEmpty()) {
                professor.setSubjects(subjectService.findSubjectsByIds(userDTO.getSubjectIds()));
            }
            // Si nécessaire, vous pouvez également gérer ici le type d'emploi (employmentType)
            // Ex : if (userDTO.getEmploymentType() != null) { professor.setEmploymentType(userDTO.getEmploymentType()); }
        }
    }

    private Map<String, Object> convertUserToMap(User user) {
        Map<String, Object> result = Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail(),
                "firstName", user.getFirstName(),
                "lastName", user.getLastName(),
                "enabled", user.isEnabled(),
                "roles", user.getRoles().stream().map(role -> role.getName().toString()).collect(Collectors.toList())
        );
        if (user.getClasse() != null) {
            result = new java.util.HashMap<>(result);
            ((java.util.HashMap) result).put("classe", Map.of(
                    "id", user.getClasse().getId(),
                    "name", user.getClasse().getName()
            ));
        }
        if (user instanceof Professor) {
            Professor prof = (Professor) user;
            if (!prof.getSubjects().isEmpty()) {
                result = new java.util.HashMap<>(result);
                ((java.util.HashMap) result).put("subjects", prof.getSubjects().stream()
                        .map(subject -> Map.of(
                                "id", subject.getId(),
                                "name", subject.getName()))
                        .collect(Collectors.toList()));
            }
        }
        return result;
    }
}
