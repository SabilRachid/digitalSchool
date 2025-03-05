package com.digital.school.service;

import com.digital.school.model.Document;
import com.digital.school.model.Student;
import com.digital.school.model.StudentAttendance;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface StudentAttendanceService {

    /**
     * Retourne la liste des enregistrements individuels d'absence (StudentAttendance) pour un étudiant.
     */
    List<StudentAttendance> findStudentAbsences(Student student);

    /**
     * Retourne les enregistrements individuels d'absence pour un étudiant dans une plage de dates.
     */
    List<StudentAttendance> findAbsencesByDateRange(Student student, LocalDateTime start, LocalDateTime end);

    /**
     * Permet à un étudiant de soumettre une justification pour un enregistrement individuel d'absence.
     * Le paramètre studentAttendanceId correspond à l'ID du StudentAttendance.
     */
    Document submitJustification(Long studentAttendanceId, MultipartFile file, String reason, Student student);

    /**
     * Retourne les statistiques d'absence pour un étudiant sous forme de map.
     */
    Map<String, Object> getAbsenceStatistics(Student student);

    /**
     * Vérifie si l'enregistrement individuel d'absence est déjà justifié.
     */
    boolean isAbsenceJustified(Long studentAttendanceId);
}
