package com.digital.school.service.impl;


import com.digital.school.model.enumerated.AttendanceStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.digital.school.model.*;
import com.digital.school.repository.*;
import com.digital.school.service.StudentAbsenceService;
import com.digital.school.service.StorageService;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class StudentAbsenceServiceImpl implements StudentAbsenceService {

    @Autowired
    private AttendanceRepository attendanceRepository;
    
    @Autowired
    private DocumentRepository documentRepository;
    
    @Autowired
    private StorageService storageService;

    @Override
    public List<Attendance> findStudentAbsences(Student student) {
        return attendanceRepository.findByStudent(student);
    }

    @Override
    public List<Attendance> findAbsencesByDateRange(Student student, LocalDateTime start, LocalDateTime end) {
        return attendanceRepository.findByStudentAndRecordedAtBetween(student, start, end);
    }

    @Override
    @Transactional
    public Document submitJustification(Long absenceId, MultipartFile file, String reason, Student student) {
        Attendance absence = attendanceRepository.findById(absenceId)
            .orElseThrow(() -> new RuntimeException("Absence non trouvée"));
            
        if (!absence.getStudent().equals(student)) {
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
        justification.setUploadedBy(student);
        justification.setUploadedAt(LocalDateTime.now());
        justification.setStudent(student);
        justification.setDescription(reason);
        
        documentRepository.save(justification);
        
        // Mettre à jour le statut de l'absence
        absence.setJustification(reason);
        absence.setStatus(AttendanceStatus.EXCUSE);
        attendanceRepository.save(absence);
        
        return justification;
    }

    @Override
    public Map<String, Object> getAbsenceStatistics(Student student) {
        Map<String, Object> stats = new HashMap<>();
        
        LocalDateTime startOfYear = LocalDateTime.now().withMonth(9).withDayOfMonth(1)
            .withHour(0).withMinute(0).withSecond(0);
            
        List<Attendance> absences = findAbsencesByDateRange(student, startOfYear, LocalDateTime.now());
        
        long totalAbsences = absences.size();
        long justifiedAbsences = absences.stream()
            .filter(a -> a.getStatus() == AttendanceStatus.EXCUSE)
            .count();
        long unjustifiedAbsences = totalAbsences - justifiedAbsences;
        
        stats.put("totalAbsences", totalAbsences);
        stats.put("justifiedAbsences", justifiedAbsences);
        stats.put("unjustifiedAbsences", unjustifiedAbsences);
        stats.put("absenceRate", calculateAbsenceRate(student));
        
        return stats;
    }

    @Override
    public boolean isAbsenceJustified(Long absenceId) {
        return attendanceRepository.findById(absenceId)
            .map(absence -> absence.getStatus() == AttendanceStatus.EXCUSE)
            .orElse(false);
    }

    private String generateFileName(Attendance absence, MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        return String.format("absence_%d_student_%d%s",
            absence.getId(),
            absence.getStudent().getId(),
            extension);
    }

    private double calculateAbsenceRate(Student student) {
        long totalSessions = attendanceRepository.countByStudent(student);
        if (totalSessions == 0) return 0.0;
        
        long absences = attendanceRepository.countByStudentAndStatus(student, AttendanceStatus.ABSENT);
        return (double) absences / totalSessions * 100;
    }
}
