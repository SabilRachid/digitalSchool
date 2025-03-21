package com.digital.school.service;

import com.digital.school.dto.AttendanceDTO;
import com.digital.school.dto.AttendanceRequest;
import com.digital.school.model.Attendance;
import com.digital.school.model.Professor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface AttendanceService {

    Page<Attendance> findAll(Pageable pageable);
    List<Attendance> findAll();
    Optional<Attendance> findById(Long id);

    /**
     * Retourne les données groupées (par cours et par date)
     * pour l'affichage, en s'appuyant sur la fiche Attendance
     * et la collection de StudentAttendance.
     */
    List<Map<String, Object>> getGroupedAttendanceData(Professor professor, Long classId, LocalDate startDate, LocalDate endDate);

    void recordAttendance(Map<String, Object> attendanceData);

    Optional<Attendance> findByIdAndTeacher(Long id, Long teacherId);

    Attendance justifyAttendance(Long attendanceId, String justificationText, MultipartFile justificationFile);

    /**
     * Permet de justifier une fiche d'attendance globale (si applicable).
     */

    Attendance save(Attendance attendance);

    /**
     * Sauvegarde ou met à jour la fiche d'attendance pour un cours à une date donnée,
     * en créant ou en mettant à jour les enregistrements individuels (StudentAttendance).
     */
    AttendanceDTO saveAttendance(AttendanceRequest request);

    void save(List<Attendance> attendanceList);
    void deleteById(Long id);
    boolean isTeacherAllowedToModify(Long teacherId, Long courseId);
    boolean existsById(Long id);
    List<Attendance> getAbsenceStatistics();
    ResponseEntity<?> getJustificationFile(Long id);
    Attendance validateJustification(Long id);
    Attendance rejectJustification(Long id);
    void sendAbsenceReminder(Long id);

    List<Map<String, Object>> getStudentAttendances(Long attendanceId);


}
