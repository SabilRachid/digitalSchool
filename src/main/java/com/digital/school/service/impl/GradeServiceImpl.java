package com.digital.school.service.impl;

import com.digital.school.model.*;
import com.digital.school.repository.StudentRepository;
import com.digital.school.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.digital.school.repository.StudentSubmissionRepository;
import com.digital.school.service.GradeService;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class GradeServiceImpl implements GradeService {


        @Autowired
        private StudentSubmissionRepository studentSubmissionRepository;

        @Autowired
        private EvaluationService evaluationService;

        @Autowired
        private StudentRepository studentRepository;

    @Override
    public int calculateStudentRank(Long studentId, Long classeId) {
        Integer rank = studentSubmissionRepository.calculateStudentRank(studentId, classeId);
        return rank != null ? rank : 0;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> findGroupedEvaluations(Long classeId, Long subjectId, String evaluationType, String evaluationDate) {
        // Vous devez adapter la logique pour grouper les évaluations par matière, type, classe et date.
        // Cette méthode est à adapter selon votre modèle de données.
        // Pour l'exemple, nous retournons une liste vide.
        return new ArrayList<>();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> findGradesForEvaluation(Long evaluationId) {
        // On suppose que votre repository dispose d'une méthode pour récupérer toutes les notes
        // associées à une évaluation donnée.
        List<StudentSubmission> grades = studentSubmissionRepository.findByEvaluationId(evaluationId);
        return grades.stream().map(grade -> {
            Map<String, Object> map = new HashMap<>();
            map.put("studentId", grade.getStudent().getId());
            map.put("studentName", grade.getStudent().getFirstName() + " " + grade.getStudent().getLastName());
            map.put("value", grade.getValue());
            map.put("comments", grade.getComments());
            return map;
        }).collect(Collectors.toList());
    }


    @Override
    public void saveGrades(Long evaluationId, List<Map<String, Object>> updates) {
        // Récupérer l'évaluation
        Evaluation evaluation = evaluationService.findById(evaluationId)
                .orElseThrow(() -> new RuntimeException("Évaluation non trouvée"));

        for (Map<String, Object> update : updates) {
            Long studentId = Long.parseLong(update.get("studentId").toString());
            Double value = Double.parseDouble(update.get("value").toString());
            String comments = update.get("comments") != null ? update.get("comments").toString() : null;

            // Récupérer la soumission existante, si elle existe
            Optional<StudentSubmission> optionalGrade = studentSubmissionRepository
                    .findByEvaluationIdAndStudentId(evaluationId, studentId);
            StudentSubmission grade;
            if (optionalGrade.isPresent()) {
                grade = optionalGrade.get();
            } else {
                // Instancier la soumission selon le type de l'évaluation
                if (evaluation instanceof Homework) {
                    grade = new StudentHomework();
                } else if (evaluation instanceof Exam) {
                    grade = new StudentExam();
                } else {
                    throw new RuntimeException("Type d'évaluation non supporté");
                }
                // Associer l'évaluation
                grade.setEvaluation(evaluation);
                // Récupérer l'étudiant
                Student student = studentRepository.findById(studentId)
                        .orElseThrow(() -> new RuntimeException("Étudiant non trouvé"));
                grade.setStudent(student);
            }
            grade.setValue(value);
            grade.setComments(comments);
            studentSubmissionRepository.save(grade);
        }
    }


    @Override
    public byte[] generateGradeReport(Long evaluationId, Long subjectId) {
        // Implémentez la génération du PDF à l'aide d'une bibliothèque comme iText ou Apache PDFBox.
        // Pour cet exemple, nous retournons un tableau d'octets vide.
        return new byte[0];
    }
}




