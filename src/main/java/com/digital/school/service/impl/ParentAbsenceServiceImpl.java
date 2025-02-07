package com.digital.school.service.impl;

```java
package com.digital.school.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.digital.school.model.*;
import com.digital.school.repository.*;
import com.digital.school.service.ParentAbsenceService;
import com.digital.school.service.StorageService;

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
    private DocumentRepository documentRepository;
    
    @Autowired
    private StorageService storageService;

    @Override
    public List<Map<String, Object>> getChildrenAbsences(User parent) {
        return parentStudentRepository.findByParent(parent).stream()
            .map(association -> {
                User child = association.getStudent();
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
    public void submitJustification(Long absenceId, MultipartFile file, String reason, User parent) {
        Attendance absence = attendanceRepository.findById(absenceId)
            .orElseThrow(() -> new RuntimeException("Absence non trouvée"));
            
        if (!canJustifyAbsence(absenceId, parent)) {
            throw new RuntimeException("Vous n'êtes pas autorisé à justifier cette absence");
        }
        
        // Sauvegarder le fichier
        String filePath = storageService.store(file, generateFileName(absence, file));
        
        // Créer le document
        Document justification = new Document();
        justification.setName(file.getOriginalFilename());
        justification.setType("ABSENCE_JUSTIFICATION");
        justification.setCategory("ADMINISTRATIVE");
        justification.setFilePath(filePath);
        justification.setMimeType(file.getContentType());
        justification.setFileSize(file.getSize());
        justification.setUploadedBy(parent);
        justification.setUploadedAt(LocalDateTime.now());
        justification.setStudent(absence.getStudent());
        justification.setParent(parent);
        justification.setDescription(reason);
        
        documentRepository.save(justification);
        
        // Mettre à jour le statut de l'absence
        absence.setJustification(reason);
        absence.setStatus(AttendanceStatus.EXCUSED);
        attendanceRepository.save(absence);
    }

    @Override
    public Map<String, Object> getChildAbsenceStats(Long childId) {
        User child = parentStudentRepository.findByStudentId(childId)
            .map(ParentStudent::getStudent)
            .orElseThrow(() -> new RuntimeException("Enfant non trouvé"));
            
        Map<String, Object> stats = new HashMap<>();
        
        // Statistiques générales
        stats.put("totalAbsences", attendanceRepository.countAbsences(child));
        stats.put("justifiedAbsences", attendanceRepository.countJustifiedAbsences(child));
        stats.put("unjustifiedAbsences", attendanceRepository.countUnjustifiedAbsences(child));
        stats.put("absenceRate", calculateAbsenceRate(child));
        
        // Répartition par matière
        stats.put("subjectAbsences", attendanceRepository.getAbsencesBySubject(child));
        
        // Tendance mensuelle
        stats.put("monthlyTrend", attendanceRepository.getMonthlyAbsenceTrend(child));
        
        return stats;
    }

    @Override
    public boolean canJustifyAbsence(Long absenceId, User parent) {
        return attendanceRepository.findById(absenceId)
            .map(absence -> parentStudentRepository.existsByParentAndStudent(parent, absence.getStudent()))
            .orElse(false);
    }

    private String generateFileName(Attendance absence, MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        return String.format("absence_%d_student_%d_parent_%d%s",
            absence.getId(),
            absence.getStudent().getId(),
            absence.getStudent().getParent().getId(),
            extension);
    }

    private List<Map<String, Object>> getAbsenceDetails(User child) {
        return attendanceRepository.findByStudent(child).stream()
            .map(absence -> {
                Map<String, Object> details = new HashMap<>();
                details.put("id", absence.getId());
                details.put("date", absence.getRecordedAt());
                details.put("subject", absence.getCourse().getSubject().getName());
                details.put("professor", absence.getCourse().getProfessor().getLastName());
                details.put("status", absence.getStatus());
                details.put("justification", absence.getJustification());
                return details;
            })
            .collect(Collectors.toList());
    }

    private double calculateAbsenceRate(User student) {
        long totalSessions = attendanceRepository.countByStudent(student);
        if (totalSessions == 0) return 0.0;
        
        long absences = attendanceRepository.countByStudentAndStatus(student, AttendanceStatus.ABSENT);
        return (double) absences / totalSessions * 100;
    }
}
```