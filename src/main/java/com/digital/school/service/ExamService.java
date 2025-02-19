
package com.digital.school.service;

import com.digital.school.model.Exam;
import com.digital.school.model.ExamResult;
import com.digital.school.model.Professor;
import com.digital.school.model.User;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ExamService {
       List<Exam> findByProfessor(Professor professor);
     /* Crée un nouvel examen et l'associe au professeur identifié par professorId.*/
        Exam createExam(Exam exam, Long professorId);

        /*Publie l'examen identifié par son ID.*/
        void publishExam(Long id);

        /*Termine l'examen identifié par son ID.*/
        void endExam(Long id);

        /*Retourne les résultats de l'examen sous forme de Map.
         * La Map peut contenir des clés telles que "examTitle", "average", "highest", "passRate", "distribution", etc.*/
        Map<String, Object> getExamResults(Long id);

        /**
         * Génère le rapport de l'examen au format PDF et retourne son contenu sous forme de tableau d'octets.
         */
        byte[] generateExamReport(Long id);

    List<Exam> findExamsByProfessor(Long id);
}



