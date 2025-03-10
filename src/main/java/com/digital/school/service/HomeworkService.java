package com.digital.school.service;

import com.digital.school.dto.HomeworkDTO;
import com.digital.school.model.Homework;
import com.digital.school.model.Professor;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service de gestion des devoirs créés par les professeurs.
 */
public interface HomeworkService {

    Homework createHomework(HomeworkDTO homeworkDTO, Long professorId);
    void publishHomework(Long homeworkId);
    void endHomework(Long homeworkId);
    void enterGrade(Long submissionId, Double gradeValue, String comment, Long professorId);
    List<Homework> findHomeworksByProfessor(Long professorId, String month, Long classe, Long subject);
    byte[] generateHomeworkReport(Long homeworkId);
    List<Map<String, Object>> findAllAsMap(Long classId, Integer year, Integer month);
    Homework findByIdAndProfessor(Long id, Professor professor);
    Optional<Homework> findById(Long id);
    HomeworkDTO convertToDTO(Homework homework);

    List<Homework> findPendingGradingByProfessor(Professor professor);



}
