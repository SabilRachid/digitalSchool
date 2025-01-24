package com.digital.school.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.digital.school.model.User;
import com.digital.school.model.Role;
import com.digital.school.model.Classe;
import com.digital.school.service.UserService;
import com.digital.school.service.RoleService;
import com.digital.school.service.ClasseService;
import com.digital.school.model.enumerated.RoleName;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private RoleService roleService;
    
    @Autowired
    private ClasseService classeService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("roles", RoleName.values());
        model.addAttribute("classes", classeService.findAllBasicInfo());
        return "admin/users";
    }

    @GetMapping("/data")
    @ResponseBody
    public List<Map<String, Object>> getUsersData() {
        return userService.findAll(PageRequest.of(0, 1000)).getContent().stream()
            .map(this::convertUserToMap)
            .collect(Collectors.toList());
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<?> createUser(@RequestBody UserDTO userDTO) {
        try {
            User user = new User();
            updateUserFromDTO(user, userDTO);
            User savedUser = userService.save(user);
            return ResponseEntity.ok(convertUserToMap(savedUser));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Erreur lors de la création: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        try {
            User user = userService.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
            updateUserFromDTO(user, userDTO);
            User updatedUser = userService.save(user);
            return ResponseEntity.ok(convertUserToMap(updatedUser));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Erreur lors de la mise à jour: " + e.getMessage()));
        }
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

    private void updateUserFromDTO(User user, UserDTO dto) {
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEnabled(dto.isEnabled());

        // Gérer le mot de passe
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        // Gérer les rôles
        Set<Role> roles = new HashSet<>();
        for (RoleName roleName : dto.getRoles()) {
            roleService.findByName(roleName).ifPresent(roles::add);
        }
        user.setRoles(roles);

        // Gérer la classe pour les étudiants
        if (dto.getClasseId() != null && user.getRoles().stream()
                .anyMatch(role -> role.getName() == RoleName.ROLE_STUDENT)) {
            classeService.findById(dto.getClasseId())
                .ifPresent(user::setClasse);
        }
    }

    private Map<String, Object> convertUserToMap(User user) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", user.getId());
        map.put("username", user.getUsername());
        map.put("email", user.getEmail());
        map.put("firstName", user.getFirstName());
        map.put("lastName", user.getLastName());
        map.put("enabled", user.isEnabled());
        map.put("roles", user.getRoles().stream()
            .map(role -> role.getName().toString())
            .collect(Collectors.toList()));

        if (user.getClasse() != null) {
            map.put("classe", Map.of(
                "id", user.getClasse().getId(),
                "name", user.getClasse().getName()
            ));
        }

        return map;
    }

    // DTO pour la gestion des utilisateurs
    private static class UserDTO {
        private Long id;
        private String username;
        private String email;
        private String password;
        private String firstName;
        private String lastName;
        private boolean enabled;
        private Set<RoleName> roles;
        private Long classeId;

        // Getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public Set<RoleName> getRoles() { return roles; }
        public void setRoles(Set<RoleName> roles) { this.roles = roles; }
        public Long getClasseId() { return classeId; }
        public void setClasseId(Long classeId) { this.classeId = classeId; }
    }
}