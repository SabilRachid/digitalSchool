package com.digital.school.service.impl;

import com.digital.school.dto.AttendanceRequest;
import com.digital.school.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import com.digital.school.model.enumerated.AttendanceStatus;
import com.digital.school.repository.AttendanceRepository;
import com.digital.school.repository.StudentRepository;
import com.digital.school.repository.CourseRepository;
import com.digital.school.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttendanceServiceImpl implements AttendanceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AttendanceServiceImpl.class);

    // Répertoire où les fichiers justificatifs seront stockés
    private final String justificationDir = "uploads/justifications/";

	@Autowired 
	AttendanceRepository attendanceRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    StudentRepository studentRepository;

    @Override
    public void save(List<Attendance> attendanceList) {
        attendanceRepository.saveAll(attendanceList); // Enregistre la liste des présences
    }

    @Override
    public Page<Attendance> findAll(Pageable pageable) {
        return attendanceRepository.findAll(pageable);
    }

    @Override
    public List<Attendance> findAll() {
        return attendanceRepository.findAll();
    }

    @Override
    public Optional<Attendance> findById(Long id) {
        return attendanceRepository.findById(id);
    }


    @Override
    public List<Map<String, Object>> findAllAsMap(Long classId, LocalDate startDate, LocalDate endDate) {
        List<Attendance> attendances = attendanceRepository.findByClassIdAndDateRange(classId, startDate, endDate);
        return attendances.stream().map(att -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", att.getId());
            map.put("date", att.getDateEvent());
            map.put("studentName", att.getStudent().getFirstName() + " " + att.getStudent().getLastName());
            map.put("className", att.getStudent().getClasse().getName());
            map.put("status", att.getStatus().name());
            map.put("justification", att.getJustification());
            map.put("justificationFile", att.getJustificationFile());
            map.put("recordedAt", att.getRecordedAt());
            return map;
        }).collect(Collectors.toList());
    }


    @Override
    public Attendance justifyAttendance(Long attendanceId, String justificationText, MultipartFile justificationFile) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new RuntimeException("Attendance non trouvé"));
        // Mise à jour du justificatif textuel
        attendance.setJustification(justificationText);

        // En cas de fichier justificatif envoyé, on le sauvegarde
        if (justificationFile != null && !justificationFile.isEmpty()) {
            try {
                File dir = new File(justificationDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                String filename = System.currentTimeMillis() + "_" + justificationFile.getOriginalFilename();
                Files.copy(justificationFile.getInputStream(), Paths.get(justificationDir, filename));
                attendance.setJustificationFile(filename);
            } catch (Exception e) {
                throw new RuntimeException("Erreur lors de la sauvegarde du fichier justificatif: " + e.getMessage(), e);
            }
        }
        return attendanceRepository.save(attendance);
    }

    @Override
    @Transactional
    public Attendance save(Attendance attendance) {
        return attendanceRepository.save(attendance);
    }

    @Override
    public void saveAttendance(AttendanceRequest request) {
        List<Attendance> attendances = new ArrayList<>();
        Course course = (Course) courseRepository.findByClassIdAndDate(request.getClassId(), request.getDate())
                .orElseThrow(() -> new RuntimeException("Cours non trouvé"));

        request.getAttendances().forEach((studentId, status) -> {
            Student student = studentRepository.findById(studentId).orElseThrow();
            Attendance attendance = new Attendance();
            attendance.setStudent(student);
            attendance.setCourse(course);
            attendance.setDateEvent(request.getDate());
            attendance.setStatus(AttendanceStatus.valueOf(status));
            attendances.add(attendance);
        });

        attendanceRepository.saveAll(attendances);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        attendanceRepository.deleteById(id);
    }

    @Override
    public List<Attendance> findByStudent(User student) {
        return attendanceRepository.findByStudent(student);
    }


    
    public Map<String, Double> getClassAttendanceStats(Course course) {
        Map<String, Double> stats = new HashMap<>();
        List<Object[]> attendanceStats = attendanceRepository.getAttendanceStatsByCourse(course);
        long totalAttendances = 0;

        // Calculate total attendances
        for (Object[] stat : attendanceStats) {
            Long count = (Long) stat[1];
            totalAttendances += count;
        }

        if (totalAttendances == 0) {
            stats.put("presentRate", 0.0);
            stats.put("absentRate", 0.0);
            stats.put("lateRate", 0.0);
            stats.put("excusedRate", 0.0);
            return stats;
        }

        // Calculate rates for each status
        for (Object[] stat : attendanceStats) {
            AttendanceStatus status = (AttendanceStatus) stat[0];
            Long count = (Long) stat[1];
            double rate = ((double) count / totalAttendances) * 100;

            switch (status) {
                case PRESENT:
                    stats.put("presentRate", rate);
                    break;
                case ABSENT:
                    stats.put("absentRate", rate);
                    break;
                case RETARD:
                    stats.put("lateRate", rate);
                    break;
                case EXCUSE:
                    stats.put("excusedRate", rate);
                    break;
            }
        }

        // Ensure all rates are present in the map
        stats.putIfAbsent("presentRate", 0.0);
        stats.putIfAbsent("absentRate", 0.0);
        stats.putIfAbsent("lateRate", 0.0);
        stats.putIfAbsent("excusedRate", 0.0);

        return stats;
    }


    @GetMapping("/data")
    @ResponseBody
    public Map<String, Object> getGroupedAttendanceData(@AuthenticationPrincipal Professor professor,
                                                        @RequestParam(required = false) Long classId,
                                                        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                                        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {

        LOGGER.debug("teacherId: {}, classId: {}, startDate: {}, endDate: {}", professor.getId(), classId, startDate, endDate);

        List<Map<String, Object>> result = attendanceRepository.findGroupedAttendances(professor.getId(), classId, startDate, endDate)
                .stream()
                .map(a -> {
                    Course course = (Course) a[0];
                    return Map.of(
                            "courseId", course.getId(),
                            "course", course.getName(),
                            "classe", course.getClasse().getName(),
                            "date", a[1],
                            "count", a[2]
                    );
                })
                .collect(Collectors.toList());

        return Map.of("data", result);
    }




    @Override
    public Optional<Attendance> findByIdAndTeacher(Long id, Long professorId) {
        return attendanceRepository.findById(id)
                .filter(a -> a.getCourse().getProfessor().getId().equals(professorId));
    }

    @Override
    public boolean isTeacherAllowedToModify(Long teacherId, Long courseId) {
        return attendanceRepository.existsByCourseIdAndRecordedBy_Id (courseId, teacherId);
    }

    @Override
    public boolean existsById(Long id) {
        return attendanceRepository.existsById(id);
    }


    @Override
    public List<Attendance> getAbsenceStatistics() {
        return attendanceRepository.findAbsenceDetails();
    }

    @Override
    public ResponseEntity<?> getJustificationFile(Long id) {
        return null;
    }

    @Override
    public Attendance validateJustification(Long id) {
        return null;
    }

    @Override
    public Attendance rejectJustification(Long id) {
        return null;
    }

    @Override
    public void sendAbsenceReminder(Long id) {

    }

}