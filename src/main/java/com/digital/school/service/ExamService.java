
package com.digital.school.service;

import com.digital.school.model.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ExamService {

       List<Exam> findUpcomingExams(Student student);


     /* Crée un nouvel examen et l'associe au professeur identifié par professorId.*/
        Exam createExam(Exam exam, Long professorId);

        /*Publie l'examen identifié par son ID.*/
        void publishExam(Long id);

        /*Termine l'examen identifié par son ID.*/
        void endExam(Long id);

        /*Retourne les résultats de l'examen sous forme de Map.
         * La Map peut contenir des clés telles que "examTitle", "average", "highest", "passRate", "distribution", etc.*/
        Map<String, Object> getExamResults(Long id);

        /* Génère le rapport de l'examen au format PDF et retourne son contenu sous forme de tableau d'octets.*/
        byte[] generateExamReport(Long id);

        // Trouve les devoirs en attente pour un étudiant donné.
        StudentSubmission enterGrade(Long submissionId, Double gradeValue, String comment, Long professorId);

        // Trouve les examens associés à un professeur identifié par son ID.
        List<Exam> findExamsByProfessor(Long id);

        List<Exam> findExamsByProfessor(Long professorId, String month, Long classe, Long subject);
}



