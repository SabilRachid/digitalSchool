package com.digital.school.service.impl;

import com.digital.school.model.Event;
import com.digital.school.model.enumerated.RoleName;
import com.digital.school.repository.*;
import com.digital.school.service.AdminDashboardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminDashboardServiceImpl.class);

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private ClasseRepository classeRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private PerformanceRepository performanceRepository;

    @Autowired
    private EventRepository eventRepository;

    @Override
    public List<Map<String, Object>> getProfessorCountBySubject() {
        List<Object[]> results = subjectRepository.findProfessorCountBySubject();
        return results.stream()
                .map(row -> Map.of(
                        "subject", row[0],
                        "count", row[1]
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getStudentRegistrationTrend() {
        // Convertir LocalDate en LocalDateTime (début du mois)
        LocalDate sixMonthsAgo = LocalDate.now().minusMonths(5).withDayOfMonth(1);
        LocalDateTime startDateTime = sixMonthsAgo.atStartOfDay(); // ✅ Conversion

        List<Object[]> results = studentRepository.findRegistrationsAfter(startDateTime);
        //afficher les valeurs résults
        LOGGER.info("Résultats des inscriptions pour >=:" + startDateTime);
        for (Object[] result : results) {
            LOGGER.info("Mois : {}, Nombre : {}", result[0], result[1]);
        }

        List<String> months = getLastSixMonths();

        List<Map<String, Object>> registrationTrend = new ArrayList<>();
        for (String month : months) {
            Map<String, Object> data = new HashMap<>();
            data.put("month", month);
            data.put("count", 0);
            registrationTrend.add(data);
        }

        // Mise à jour des valeurs avec les vraies données
        for (Object[] result : results) {
            String month = (String) result[0]; // "Jan 2025"
            Long count = (Long) result[1];

            for (Map<String, Object> entry : registrationTrend) {
                LOGGER.info("month trend=  "+ entry.get("month"));
                LOGGER.info("month registration result=  " + month);
                if (entry.get("month").equals(month)) {
                    entry.put("count", count.intValue());
                    break;
                }
            }
        }

        return registrationTrend;
    }

    private List<String> getLastSixMonths() {
        List<String> months = new ArrayList<>();
        Locale locale = Locale.ENGLISH; // Format en anglais
        LocalDate currentDate = LocalDate.now();

        for (int i = 5; i >= 0; i--) {
            LocalDate monthDate = currentDate.minusMonths(i);
            String monthName = monthDate.getMonth()
                    .getDisplayName(TextStyle.SHORT, locale);
            monthName = capitalize(monthName); // Capitaliser la première lettre
            months.add(monthName + " " + monthDate.getYear());
        }
        return months;
    }

    private String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }


    @Override
    public List<Map<String, Object>> getUserDistribution() {
        List<RoleName> roles = Arrays.asList(RoleName.ROLE_ADMIN, RoleName.ROLE_PROFESSOR, RoleName.ROLE_STUDENT);

        List<Object[]> results = userRepository.countUsersByRole(roles);

        List<Map<String, Object>> distribution = new ArrayList<>();
        Map<RoleName, String> roleLabels = Map.of(
                RoleName.ROLE_ADMIN, "Administrateurs",
                RoleName.ROLE_PROFESSOR, "Professeurs",
                RoleName.ROLE_STUDENT, "Étudiants"
        );

        for (Object[] row : results) {
            RoleName role = (RoleName) row[0];
            Long count = (Long) row[1];

            Map<String, Object> entry = new HashMap<>();
            entry.put("role", roleLabels.getOrDefault(role, "Autre"));
            entry.put("count", count);
            distribution.add(entry);
        }

        return distribution;
    }


    @Override
    public Map<String, Object> getAdminStats() {


        Map<String, Object> stats = new HashMap<>();
        // Total des étudiants
        stats.put("totalStudents", studentRepository.countStudents());
        stats.put("studentsActive", studentRepository.countActiveStudents());
        stats.put("studentsPending", studentRepository.countPendingStudents());

        // Total des professeurs
        stats.put("totalProfessors", professorRepository.countProfessors());
        stats.put("fullTimeProfessors", professorRepository.countFullTimeProfessors());
        stats.put("partTimeProfessors", professorRepository.countPartTimeProfessors());

        // Total des classes
        stats.put("totalClasses", classeRepository.countClasses());
        stats.put("occupancyRate", classeRepository.calculateOccupancyRate());
        stats.put("availableSeats", classeRepository.countAvailableSeats());

        // Taux de présence moyen
        stats.put("attendanceRate", attendanceRepository.getAverageAttendance());
        stats.put("justifiedAbsencesRate", attendanceRepository.getJustifiedAbsencesRate());
        stats.put("unjustifiedAbsencesRate", attendanceRepository.getUnjustifiedAbsencesRate());


        return stats;
    }

    @Override
    public List<Map<String, Object>> getLevelPerformance() {
        List<Object[]> results = performanceRepository.findAveragePerformanceByLevel();
        List<Map<String, Object>> response = new ArrayList<>();

        for (Object[] row : results) {
            Map<String, Object> map = new HashMap<>();
            map.put("level", row[0]);
            map.put("average", row[1] != null ? Math.round((Double) row[1] * 100.0) / 100.0 : 0);
            response.add(map);
        }

        return response;
    }

    @Override
    public List<Map<String, Object>> getSuccessRate() {
        List<Object[]> results = performanceRepository.findSuccessRateBySubject();
        List<Map<String, Object>> response = new ArrayList<>();

        for (Object[] row : results) {
            Map<String, Object> map = new HashMap<>();
            map.put("subject", row[0]);
            map.put("successRate", row[1] != null ? Math.round((Double) row[1] * 100.0) / 100.0 : 0);
            response.add(map);
        }

        return response;
    }

    @Override
    public List<Map<String, Object>> getLastActivities() {
        List<Event> events = eventRepository.findLastEvents();
        List<Map<String, Object>> activities = new ArrayList<>();

        for (Event event : events) {
            Map<String, Object> activity = new HashMap<>();
            activity.put("title", event.getTitle());
            activity.put("eventType", event.getType().name());
            activity.put("timeAgo", formatTimeAgo(event.getStartTime()));

            activities.add(activity);
        }

        return activities;
    }

    // 🔹 Format "Il y a X minutes / heures"
    private String formatTimeAgo(LocalDateTime startTime) {
        Duration duration = Duration.between(startTime, LocalDateTime.now());
        long minutes = duration.toMinutes();
        if (minutes < 60) {
            return "Il y a " + minutes + " minutes";
        } else {
            long hours = duration.toHours();
            return "Il y a " + hours + " heures";
        }
    }


}
