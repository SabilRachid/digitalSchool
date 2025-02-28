package com.digital.school.service.impl;

import com.digital.school.dto.AttendanceRequest;
import com.digital.school.model.Attendance;
import com.digital.school.model.Course;
import com.digital.school.model.Professor;
import com.digital.school.model.Student;
import com.digital.school.model.StudentAttendance;
import com.digital.school.model.enumerated.AttendanceStatus;
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
    public Attendance saveAttendance(AttendanceRequest request) {
        // Récupération du cours via courseId
        LOGGER.debug("saveAttendance for request="+request);
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new RuntimeException("Cours non trouvé"));

        // Chercher la fiche d'attendance existante pour ce cours et cette date
        Attendance attendance = attendanceRepository.findByCourseAndDate(course.getId(), request.getDate());
        if (attendance == null) {
            attendance = new Attendance();
            attendance.setCourse(course);
            attendance.setDateEvent(request.getDate());
        }
        LOGGER.debug("attendanceId="+attendance.getId() +" avant foreach student attendances");
        // Pour chaque entrée dans la map, créer un StudentAttendance
        // On pourrait ici vérifier si un enregistrement existe déjà pour l'étudiant, et le mettre à jour.
        // Pour simplifier, on ajoute de nouveaux enregistrements.
        Attendance finalAttendance = attendance;
        request.getAttendances().forEach((studentKey, statusStr) -> {
            Long studentId;
            try {
                studentId = Long.parseLong(studentKey);
                LOGGER.debug("enregistrement pour studentKey="+studentKey);
            } catch (NumberFormatException e) {
                throw new RuntimeException("Identifiant étudiant invalide: " + studentKey);
            }
            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new RuntimeException("Étudiant non trouvé avec l'ID : " + studentId));
            // Créer un enregistrement de StudentAttendance
            StudentAttendance studentAttendance = new StudentAttendance();
            studentAttendance.setStudent(student);
            studentAttendance.setStatus(AttendanceStatus.valueOf(statusStr));
            studentAttendance.setRecordedAt(LocalDateTime.now());
            // Ajout à la fiche d'attendance
            finalAttendance.addStudentAttendance(studentAttendance);
        });
        LOGGER.debug("before attendanceRepository.save(attendance)");
        return attendanceRepository.save(attendance);
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
            Course course = (Course) row[0];
            LocalDate date = (LocalDate) row[1];
            Long count = ((Number) row[2]).longValue();
            Map<String, Object> map = new HashMap<>();
            map.put("courseId", course.getId());
            map.put("courseName", course.getName());
            map.put("className", course.getClasse().getName());
            map.put("date", date);
            map.put("count", count);
            return map;
        }).collect(Collectors.toList());
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
