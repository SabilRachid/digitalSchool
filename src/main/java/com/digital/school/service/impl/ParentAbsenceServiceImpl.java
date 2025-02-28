package com.digital.school.service.impl;

import com.digital.school.model.Attendance;
import com.digital.school.model.Document;
import com.digital.school.model.ParentStudent;
import com.digital.school.model.Professor;
import com.digital.school.model.Student;
import com.digital.school.model.StudentAttendance;
import com.digital.school.model.User;
import com.digital.school.model.enumerated.AttendanceStatus;
import com.digital.school.model.enumerated.DocumentType;
import com.digital.school.repository.AttendanceRepository;
import com.digital.school.repository.DocumentRepository;
import com.digital.school.repository.ParentStudentRepository;
import com.digital.school.repository.StudentAttendanceRepository;
import com.digital.school.service.ParentAbsenceService;
import com.digital.school.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ParentAbsenceServiceImpl implements ParentAbsenceService {

    @Autowired
    private ParentStudentRepository parentStudentRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private StudentAttendanceRepository studentAttendanceRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private StorageService storageService;

    @Override
    public List<Map<String, Object>> getChildrenAbsences(User parent) {
        return parentStudentRepository.findByParent(parent).stream()
                .map(association -> {
                    Student child = association.getStudent();
                    Map<String, Object> childAbsences = new HashMap<>();
                    childAbsences.put("childId", child.getId());
                    childAbsences.put("childName", child.getFirstName() + " " + child.getLastName());
                    childAbsences.put("class", child.getClasse().getName());
                    childAbsences.put("absences", getAbsenceDetails(child));
                    childAbsences.put("stats", getChildAbsenceStats(child.getId()));
                    return childAbsences;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void submitJustification(Long studentAttendanceId, MultipartFile file, String reason, User parent) {
        // Ici, studentAttendanceId correspond à l'enregistrement individuel dans StudentAttendance
        StudentAttendance absence = studentAttendanceRepository.findById(studentAttendanceId)
                .orElseThrow(() -> new RuntimeException("Absence non trouvée"));

        if (!canJustifyAbsence(studentAttendanceId, parent)) {
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
        justification.setOwner(parent);
        justification.getAuditable().setCreated(new Date());
        Set<User> sharedWith = new HashSet<>();
        if (absence.getStudent() != null) {
            sharedWith.add(absence.getStudent());
        }
        if (absence.getStudent().getParent() != null) {
            sharedWith.add(absence.getStudent().getParent());
        }
        justification.setSharedWith(sharedWith);
        justification.setDescription(reason);

        documentRepository.save(justification);

        // Mettre à jour l'enregistrement individuel : justifier et changer le statut
        absence.setJustification(reason);
        absence.setStatus(AttendanceStatus.EXCUSE);
        studentAttendanceRepository.save(absence);
    }

    @Override
    public Map<String, Object> getChildAbsenceStats(Long childId) {
        Student child = parentStudentRepository.findByStudentId(childId)
                .map(ParentStudent::getStudent)
                .orElseThrow(() -> new RuntimeException("Enfant non trouvé"));

        Map<String, Object> stats = new HashMap<>();

        // Statistiques générales en utilisant le repository de StudentAttendance
        stats.put("totalAbsences", studentAttendanceRepository.countByStudent(child));
        stats.put("justifiedAbsences", studentAttendanceRepository.countByStudentAndStatus(child, AttendanceStatus.EXCUSE));
        stats.put("unjustifiedAbsences", studentAttendanceRepository.countUnjustifiedAbsences(child));
        stats.put("absenceRate", calculateAbsenceRate(child));

        // Répartition par matière et tendance mensuelle
        stats.put("subjectAbsences", studentAttendanceRepository.getAbsencesBySubject(child));
        stats.put("monthlyTrend", studentAttendanceRepository.getMonthlyAbsenceTrend(child));

        return stats;
    }

    @Override
    public boolean canJustifyAbsence(Long studentAttendanceId, User parent) {
        return studentAttendanceRepository.findById(studentAttendanceId)
                .map(absence -> parentStudentRepository.existsByParentAndStudent(parent, absence.getStudent()))
                .orElse(false);
    }

    private String generateFileName(StudentAttendance absence, MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        return String.format("absence_%d_student_%d_parent_%d%s",
                absence.getId(),
                absence.getStudent().getId(),
                absence.getStudent().getParent().getId(),
                extension);
    }

    private List<Map<String, Object>> getAbsenceDetails(Student child) {
        return studentAttendanceRepository.findByStudent(child).stream()
                .map(sa -> {
                    Map<String, Object> details = new HashMap<>();
                    details.put("id", sa.getId());
                    details.put("date", sa.getRecordedAt());
                    details.put("subject", sa.getAttendance().getCourse().getSubject().getName());
                    details.put("professor", sa.getAttendance().getCourse().getProfessor().getLastName());
                    details.put("status", sa.getStatus());
                    details.put("justification", sa.getJustification());
                    return details;
                })
                .collect(Collectors.toList());
    }

    private double calculateAbsenceRate(Student student) {
        long totalSessions = studentAttendanceRepository.countByStudent(student);
        if (totalSessions == 0) return 0.0;
        long absences = studentAttendanceRepository.countByStudentAndStatus(student, AttendanceStatus.ABSENT);
        return (double) absences / totalSessions * 100;
    }
}
