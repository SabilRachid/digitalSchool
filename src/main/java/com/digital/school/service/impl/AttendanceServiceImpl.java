package com.digital.school.service.impl;

import com.digital.school.dto.AttendanceDTO;
import com.digital.school.dto.AttendanceRequest;
import com.digital.school.model.Attendance;
import com.digital.school.model.Course;
import com.digital.school.model.Professor;
import com.digital.school.model.Student;
import com.digital.school.model.StudentAttendance;
import com.digital.school.model.enumerated.AttendanceStatus;
import com.digital.school.model.enumerated.StudentAttendanceStatus;
import com.digital.school.repository.AttendanceRepository;
import com.digital.school.repository.CourseRepository;
import com.digital.school.repository.StudentRepository;
import com.digital.school.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttendanceServiceImpl implements AttendanceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AttendanceServiceImpl.class);

    // Répertoire où les fichiers justificatifs sont stockés
    private final String justificationDir = "uploads/justifications/";

    private final AttendanceRepository attendanceRepository;
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;

    @Override
    public void save(List<Attendance> attendanceList) {
        attendanceRepository.saveAll(attendanceList);
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
    public Attendance justifyAttendance(Long attendanceId, String justificationText, MultipartFile justificationFile) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new RuntimeException("Attendance non trouvé"));
        // Pour ce nouveau modèle, vous pourriez avoir à justifier un enregistrement individuel
        // (via StudentAttendance) plutôt que la fiche d'attendance globale.
        attendance.setJustification(justificationText);
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

    /**
     * Pour le nouveau modèle, cette méthode cherche la fiche d'attendance existante pour le cours et la date.
     * Si elle n'existe pas, elle la crée. Ensuite, pour chaque étudiant, elle crée ou met à jour l'enregistrement
     * individuel (StudentAttendance).
     */
    @Override
    @Transactional
    public AttendanceDTO saveAttendance(AttendanceRequest request) {
        LOGGER.debug("saveAttendance called with request: {}", request);

        Attendance attendance;
        if (request.getAttendanceId() != null) {
            attendance = attendanceRepository.findById(request.getAttendanceId())
                    .orElseThrow(() -> new RuntimeException("Fiche d'attendance non trouvée pour l'ID: " + request.getAttendanceId()));
            LOGGER.debug("Fiche d'attendance récupérée via ID: {}", attendance.getId());
        } else {
            Course course = courseRepository.findById(request.getCourseId())
                    .orElseThrow(() -> new RuntimeException("Cours non trouvé pour l'ID: " + request.getCourseId()));
            attendance = attendanceRepository.findByCourseAndDate(course.getId(), request.getDate());
            if (attendance == null) {
                attendance = new Attendance();
                attendance.setCourse(course);
                attendance.setDateEvent(request.getDate());
                LOGGER.debug("Création d'une nouvelle fiche d'attendance pour le cours ID {} à la date {}", course.getId(), request.getDate());
            } else {
                LOGGER.debug("Fiche d'attendance existante trouvée (ID: {}) pour le cours ID {} à la date {}",
                        attendance.getId(), course.getId(), request.getDate());
            }
        }

        // Pour chaque enregistrement dans la map, traiter ou créer un StudentAttendance
        Attendance finalAttendance = attendance;
        request.getAttendances().forEach((studentKey, statusStr) -> {
            Long studentId;
            try {
                studentId = Long.parseLong(studentKey);
                LOGGER.debug("Traitement pour l'étudiant avec ID: {}", studentKey);
            } catch (NumberFormatException e) {
                throw new RuntimeException("Identifiant étudiant invalide: " + studentKey, e);
            }
            // Récupération de l'étudiant
            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new RuntimeException("Étudiant non trouvé pour l'ID: " + studentId));

            Optional<StudentAttendance> existing = finalAttendance.getStudentAttendances().stream()
                    .filter(sa -> sa.getStudent().getId().equals(studentId))
                    .findFirst();

            if (existing.isPresent()) {
                StudentAttendance sa = existing.get();
                sa.setStatus(StudentAttendanceStatus.valueOf(statusStr));
                sa.setRecordedAt(LocalDateTime.now());
                LOGGER.debug("Mise à jour de StudentAttendance existant pour l'étudiant ID: {}", studentId);
            } else {
                StudentAttendance studentAttendance = new StudentAttendance();
                studentAttendance.setStudent(student);
                studentAttendance.setStatus(StudentAttendanceStatus.valueOf(statusStr));
                studentAttendance.setRecordedAt(LocalDateTime.now());
                finalAttendance.addStudentAttendance(studentAttendance);
                LOGGER.debug("Ajout d'un nouveau StudentAttendance pour l'étudiant ID: {}", studentId);
            }
        });

        // Déterminer le statut global de la fiche
        // Récupérer le nombre total d'élèves dans la classe du cours
        int totalStudents = studentRepository.countByClasse(attendance.getCourse().getClasse().getId());
        if (request.getAttendances() != null && request.getAttendances().size() == totalStudents) {
            attendance.setStatus(AttendanceStatus.COMPLETED);
            LOGGER.debug("Fiche d'attendance marquée COMPLETED (tous les élèves traités)");
        } else {
            attendance.setStatus(AttendanceStatus.NOT_COMPLETED);
            LOGGER.debug("Fiche d'attendance marquée NOT_COMPLETED (il manque des enregistrements)");
        }

        LOGGER.debug("Avant sauvegarde, fiche d'attendance: {}", finalAttendance);
        Attendance savedAttendance = attendanceRepository.save(finalAttendance);
        LOGGER.debug("Fiche d'attendance sauvegardée avec ID: {}", savedAttendance.getId());

        AttendanceDTO dto = toAttendanceDTO(savedAttendance);
        LOGGER.debug("DTO généré: {}", dto);
        return dto;
    }

    private AttendanceDTO toAttendanceDTO(Attendance attendance) {
        AttendanceDTO dto = new AttendanceDTO();
        dto.setId(attendance.getId());
        dto.setCourseName(attendance.getCourse().getName());
        dto.setDate(attendance.getDateEvent());
        dto.setStudentAttendanceCount(
                attendance.getStudentAttendances() != null ? attendance.getStudentAttendances().size() : 0
        );
        return dto;
    }


    @Override
    @Transactional
    public void deleteById(Long id) {
        attendanceRepository.deleteById(id);
    }

    @Override
    public List<Map<String, Object>> getGroupedAttendanceData(Professor professor, Long classId, LocalDate startDate, LocalDate endDate) {
        LOGGER.debug("GroupedAttendanceData - professorId: {}, classId: {}, startDate: {}, endDate: {}",
                professor.getId(), classId, startDate, endDate);
        List<Object[]> results = attendanceRepository.findGroupedAttendances(professor.getId(), classId, startDate, endDate);
        LOGGER.debug("findGroupedAttendances results size = " + results.size());
        // Transformation en List<Map<String, Object>>
        return results.stream().map(row -> {
            Long attendanceId = (Long) row[0];
            Course course = (Course) row[1];
            LocalDate date = (LocalDate) row[2];
            String status = ((AttendanceStatus) row[3]).name();
            Long count = ((Number) row[4]).longValue();
            Map<String, Object> map = new HashMap<>();
            map.put("attendanceId", attendanceId);
            map.put("courseId", course.getId());
            map.put("classId", course.getClasse().getId());
            map.put("courseName", course.getName());
            map.put("className", course.getClasse().getName());
            map.put("date", date);
            map.put("count", count);
            map.put("status", status);
            return map;
        }).collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getStudentAttendances(Long attendanceId) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new RuntimeException("Fiche d'attendance non trouvée pour l'ID: " + attendanceId));
        return attendance.getStudentAttendances().stream()
                .map(sa -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("studentId", sa.getStudent().getId());
                    map.put("status", sa.getStatus().name());
                    return map;
                })
                .collect(Collectors.toList());
    }


    @Override
    public Optional<Attendance> findByIdAndTeacher(Long id, Long professorId) {
        return attendanceRepository.findById(id)
                .filter(a -> a.getCourse().getProfessor().getId().equals(professorId));
    }

    @Override
    public boolean isTeacherAllowedToModify(Long teacherId, Long courseId) {
        // Adaptation possible selon votre logique métier (peut-être vérifier si l'Attendance existe et appartient au professeur)
        return attendanceRepository.existsByCourseIdAndRecordedBy_Id(courseId, teacherId);
    }

    @Override
    public boolean existsById(Long id) {
        return attendanceRepository.existsById(id);
    }

    @Override
    public List<Attendance> getAbsenceStatistics() {
        // À adapter selon le nouveau modèle, en utilisant éventuellement le repository de StudentAttendance
        throw new UnsupportedOperationException("Méthode à adapter pour le nouveau modèle");
    }

    @Override
    public ResponseEntity<?> getJustificationFile(Long id) {
        // Implémentation à adapter selon vos besoins
        return ResponseEntity.notFound().build();
    }

    @Override
    public Attendance validateJustification(Long id) {
        // Implémentation à adapter selon vos besoins
        return null;
    }

    @Override
    public Attendance rejectJustification(Long id) {
        // Implémentation à adapter selon vos besoins
        return null;
    }

    @Override
    public void sendAbsenceReminder(Long id) {
        // Implémentation à adapter selon vos besoins
    }
}
