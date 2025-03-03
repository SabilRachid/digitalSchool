package com.digital.school.service.impl;

import com.digital.school.model.Document;
import com.digital.school.model.Student;
import com.digital.school.model.StudentAttendance;
import com.digital.school.model.User;
import com.digital.school.model.enumerated.StudentAttendanceStatus;
import com.digital.school.model.enumerated.DocumentType;
import com.digital.school.repository.DocumentRepository;
import com.digital.school.repository.ParentStudentRepository;
import com.digital.school.repository.StudentAttendanceRepository;
import com.digital.school.service.StudentAttendanceService;
import com.digital.school.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class StudentAbsenceServiceImpl implements StudentAttendanceService {

    @Autowired
    private ParentStudentRepository parentStudentRepository;

    @Autowired
    private StudentAttendanceRepository studentAttendanceRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private StorageService storageService;

    /**
     * Retourne la liste des enregistrements individuels d'absence (StudentAttendance)
     * pour un étudiant donné.
     */
    @Override
    public List<StudentAttendance> findStudentAbsences(Student student) {
        return studentAttendanceRepository.findByStudent(student);
    }

    /**
     * Retourne les enregistrements individuels d'absence pour un étudiant dans une plage de dates.
     */
    @Override
    public List<StudentAttendance> findAbsencesByDateRange(Student student, LocalDateTime start, LocalDateTime end) {
        // Supposons que StudentAttendanceRepository dispose d'une méthode findByStudentAndRecordedAtBetween
        return studentAttendanceRepository.findByStudentAndRecordedAtBetween(student, start, end);
    }

    /**
     * Permet à un étudiant de soumettre une justification pour une absence individuelle.
     * Le paramètre absenceId correspond désormais à l'ID de StudentAttendance.
     */
    @Override
    @Transactional
    public Document submitJustification(Long studentAttendanceId, MultipartFile file, String reason, Student student) {
        StudentAttendance absence = studentAttendanceRepository.findById(studentAttendanceId)
                .orElseThrow(() -> new RuntimeException("Enregistrement d'absence non trouvé"));

        if (!absence.getStudent().equals(student)) {
            throw new RuntimeException("Vous n'êtes pas autorisé à justifier cette absence");
        }

        // Sauvegarder le fichier justificatif
        String filePath = storageService.store(file, generateFileName(absence, file));

        // Créer et sauvegarder le document justificatif
        Document justification = new Document();
        justification.setName(file.getOriginalFilename());
        justification.setType(DocumentType.ABSENCE_JUSTIFICATION);
        justification.setCategory("ADMINISTRATIVE");
        justification.setFileUrl(filePath);
        justification.setContentType(file.getContentType());
        justification.setFileSize(file.getSize());
        justification.setOwner(student);
        justification.getAuditable().setCreated(new Date());
        Set<User> sharedWith = new HashSet<>();
        if (absence.getStudent() != null) {
            sharedWith.add(absence.getStudent());
        }
        // On peut aussi partager avec le parent si disponible
        if (absence.getStudent().getParent() != null) {
            sharedWith.add(absence.getStudent().getParent());
        }
        justification.setSharedWith(sharedWith);
        justification.setDescription(reason);
        documentRepository.save(justification);

        // Mettre à jour le statut de l'enregistrement individuel d'absence
        absence.setJustification(reason);
        absence.setStatus(StudentAttendanceStatus.EXCUSE);
        studentAttendanceRepository.save(absence);

        return justification;
    }

    /**
     * Retourne des statistiques d'absence pour un étudiant à partir des enregistrements individuels.
     */
    @Override
    public Map<String, Object> getAbsenceStatistics(Student student) {
        Map<String, Object> stats = new HashMap<>();

        // Définir par exemple le début de l'année scolaire (ici, le 1er septembre de l'année en cours)
        LocalDateTime startOfYear = LocalDateTime.now().withMonth(9).withDayOfMonth(1)
                .withHour(0).withMinute(0).withSecond(0);

        List<StudentAttendance> absences = findAbsencesByDateRange(student, startOfYear, LocalDateTime.now());

        long totalAbsences = absences.size();
        long justifiedAbsences = absences.stream()
                .filter(sa -> sa.getStatus() == StudentAttendanceStatus.EXCUSE)
                .count();
        long unjustifiedAbsences = totalAbsences - justifiedAbsences;

        stats.put("totalAbsences", totalAbsences);
        stats.put("justifiedAbsences", justifiedAbsences);
        stats.put("unjustifiedAbsences", unjustifiedAbsences);
        stats.put("absenceRate", calculateAbsenceRate(student));

        // Répartition par matière et tendance mensuelle,
        // en supposant que le repository fournit ces agrégations pour StudentAttendance.
        stats.put("subjectAbsences", studentAttendanceRepository.getAbsencesBySubject(student));
        stats.put("monthlyTrend", studentAttendanceRepository.getMonthlyAbsenceTrend(student));

        return stats;
    }

    @Override
    public boolean isAbsenceJustified(Long studentAttendanceId) {
        return studentAttendanceRepository.findById(studentAttendanceId)
                .map(absence -> absence.getStatus() == StudentAttendanceStatus.EXCUSE)
                .orElse(false);
    }

    private String generateFileName(StudentAttendance absence, MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        return String.format("absence_%d_student_%d%s",
                absence.getId(),
                absence.getStudent().getId(),
                extension);
    }

    private double calculateAbsenceRate(Student student) {
        long totalSessions = studentAttendanceRepository.countByStudent(student);
        if (totalSessions == 0) return 0.0;
        long absences = studentAttendanceRepository.countByStudentAndStatus(student, StudentAttendanceStatus.ABSENT);
        return (double) absences / totalSessions * 100;
    }
}
