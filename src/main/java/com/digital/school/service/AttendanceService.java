package com.digital.school.service;

import com.digital.school.dto.AttendanceRequest;
import com.digital.school.model.*;
import com.digital.school.model.enumerated.AttendanceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface AttendanceService {

    Page<Attendance> findAll(Pageable pageable);
    List<Attendance> findAll();

    /*Retourne la liste des présences sous forme de Map pour l'affichage, avec filtrage optionnel par classe et plage de dates.*/
    List<Map<String, Object>> findAllAsMap(Long classId, LocalDate startDate, LocalDate endDate);

    Optional<Attendance> findById(Long id);

    List<Attendance> findByStudent(User student);

    Map<String, Object> getGroupedAttendanceData(Professor professor, Long classId, LocalDate startDate, LocalDate endDate);
    Optional<Attendance> findByIdAndTeacher(Long id, Long teacherId);



    /*Permet de justifier une absence (ou de modifier le justificatif) en mettant à jour le champ justification.
     * Le justificatif peut être un texte et/ou un fichier.*/
    Attendance justifyAttendance(Long attendanceId, String justificationText, MultipartFile justificationFile);




    Attendance save(Attendance attendance);
    void saveAttendance(AttendanceRequest request);
    void save(List<Attendance> attendanceList);

    void deleteById(Long id);
    boolean isTeacherAllowedToModify(Long teacherId, Long courseId);
    boolean existsById(Long id);
    List<Attendance> getAbsenceStatistics();

    ResponseEntity<?> getJustificationFile(Long id);

    Attendance validateJustification(Long id);

    Attendance rejectJustification(Long id);

    void sendAbsenceReminder(Long id);
}