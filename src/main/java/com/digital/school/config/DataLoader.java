package com.digital.school.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.digital.school.model.*;
import com.digital.school.model.Level;
import com.digital.school.model.enumerated.*;
import com.digital.school.repository.*;
import java.util.*;

@Configuration
public class DataLoader {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    @ConditionalOnProperty(name = "data.loader.enabled", havingValue = "true", matchIfMissing = false)
    public CommandLineRunner loadInitialData(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PermissionRepository permissionRepository,
            LevelRepository levelRepository,
            SubjectRepository subjectRepository) {
        return args -> {
            // Create permissions
            Arrays.stream(PermissionName.values()).forEach(permName -> {
                Permission permission = new Permission(permName);
                permissionRepository.save(permission);
            });

            // Create roles
            Role adminRole = new Role(RoleName.ROLE_ADMIN);
            adminRole.setPermissions(new HashSet<>(permissionRepository.findAll()));
            roleRepository.save(adminRole);

            Role professorRole = new Role(RoleName.ROLE_PROFESSOR);
            professorRole.setPermissions(Set.of(
                permissionRepository.findByName(PermissionName.VIEW_DASHBOARD).get(),
                permissionRepository.findByName(PermissionName.MANAGE_COURSES).get(),
                permissionRepository.findByName(PermissionName.MANAGE_EXAMS).get(),
                permissionRepository.findByName(PermissionName.MANAGE_ATTENDANCE).get()
            ));
            roleRepository.save(professorRole);

            Role studentRole = new Role(RoleName.ROLE_STUDENT);
            studentRole.setPermissions(Set.of(
                permissionRepository.findByName(PermissionName.VIEW_DASHBOARD).get(),
                permissionRepository.findByName(PermissionName.VIEW_RESULTS).get(),
                permissionRepository.findByName(PermissionName.SUBMIT_HOMEWORK).get(),
                permissionRepository.findByName(PermissionName.VIEW_CALENDAR).get()
            ));
            roleRepository.save(studentRole);

            // Create admin user
            if (!userRepository.existsByUsername("admin")) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin"));
                admin.setEmail("admin@digital.school");
                admin.setFirstName("Admin");
                admin.setLastName("System");
                admin.setEnabled(true);
                admin.setRoles(Set.of(adminRole));
                userRepository.save(admin);
            }

            // Initialize levels from enum
            for (LevelName levelName : LevelName.values()) {
                if (!levelRepository.existsByLevelName(levelName)) {
                    Level level = new Level();
                    level.setCode(levelName.name());
                    level.setName(levelName.getDisplayName());
                    level.setLevelName(levelName);
                    level.setCycle(levelName.getCycle());
                    level.setOrder(levelName.getOrder());
                    levelRepository.save(level);
                }
            }

            // Initialize default subjects
            List<Map<String, Object>> defaultSubjects = Arrays.asList(
                Map.of(
                    "code", "MATH",
                    "name", "Mathématiques",
                    "description", "Enseignement des mathématiques",
                    "coefficient", 1.5,
                    "optional", false
                ),
                Map.of(
                    "code", "FRAN",
                    "name", "Français",
                    "description", "Langue et littérature française",
                    "coefficient", 1.5,
                    "optional", false
                ),
                Map.of(
                    "code", "HIST",
                    "name", "Histoire-Géographie",
                    "description", "Histoire, géographie et éducation civique",
                    "coefficient", 1.0,
                    "optional", false
                ),
                Map.of(
                    "code", "ANGL",
                    "name", "Anglais",
                    "description", "Langue vivante 1",
                    "coefficient", 1.0,
                    "optional", false
                ),
                Map.of(
                    "code", "SVT",
                    "name", "Sciences de la Vie et de la Terre",
                    "description", "Biologie et géologie",
                    "coefficient", 1.0,
                    "optional", false
                ),
                Map.of(
                    "code", "PHYS",
                    "name", "Physique-Chimie",
                    "description", "Sciences physiques et chimie",
                    "coefficient", 1.0,
                    "optional", false
                ),
                Map.of(
                    "code", "EPS",
                    "name", "Éducation Physique et Sportive",
                    "description", "Sport et éducation physique",
                    "coefficient", 1.0,
                    "optional", false
                ),
                Map.of(
                    "code", "ARTS",
                    "name", "Arts Plastiques",
                    "description", "Expression artistique et histoire de l'art",
                    "coefficient", 0.5,
                    "optional", true
                ),
                Map.of(
                    "code", "MUSI",
                    "name", "Éducation Musicale",
                    "description", "Musique et chant",
                    "coefficient", 0.5,
                    "optional", true
                ),
                Map.of(
                    "code", "TECH",
                    "name", "Technologie",
                    "description", "Sciences et technologies",
                    "coefficient", 1.0,
                    "optional", false
                )
            );

            // Save subjects if they don't exist
            for (Map<String, Object> subjectData : defaultSubjects) {
                String code = (String) subjectData.get("code");
                if (!subjectRepository.existsByCode(code)) {
                    Subject subject = new Subject();
                    subject.setCode(code);
                    subject.setName((String) subjectData.get("name"));
                    subject.setDescription((String) subjectData.get("description"));
                    subject.setCoefficient((Double) subjectData.get("coefficient"));
                    subject.setOptional((Boolean) subjectData.get("optional"));
                    subjectRepository.save(subject);
                }
            }
        };

    }
}