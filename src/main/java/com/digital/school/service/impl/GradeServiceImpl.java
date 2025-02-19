package com.digital.school.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.digital.school.model.StudentGrade;
import com.digital.school.repository.StudentGradeRepository;
import com.digital.school.service.GradeService;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class GradeServiceImpl implements GradeService {


        @Autowired
        private StudentGradeRepository studentGradeRepository;

    @Override
    public int calculateStudentRank(Long studentId, Long classeId) {
        return studentGradeRepository.calculateStudentRank(studentId, classeId);
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
        List<StudentGrade> grades = studentGradeRepository.findByEvaluationId(evaluationId);
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
        // Pour chaque mise à jour, récupérer la note existante ou en créer une nouvelle.
        for (Map<String, Object> update : updates) {
            Long studentId = Long.parseLong(update.get("studentId").toString());
            Double value = Double.parseDouble(update.get("value").toString());
            String comments = update.get("comments") != null ? update.get("comments").toString() : null;

            // On suppose que vous disposez d'une méthode pour récupérer la note d'un étudiant pour une évaluation
            Optional<StudentGrade> optionalGrade = studentGradeRepository.findByEvaluationIdAndStudentId(evaluationId, studentId);
            StudentGrade grade;
            if (optionalGrade.isPresent()) {
                grade = optionalGrade.get();
            } else {
                grade = new StudentGrade();
                // Vous devez définir ici les associations nécessaires :
                // grade.setEvaluation(evaluationService.findById(evaluationId));
                // grade.setStudent(studentRepository.findById(studentId).orElseThrow(() -> new RuntimeException("Étudiant non trouvé")));
            }
            grade.setValue(value);
            grade.setComments(comments);
            studentGradeRepository.save(grade);
        }
    }

    @Override
    public byte[] generateGradeReport(Long evaluationId, Long subjectId) {
        // Implémentez la génération du PDF à l'aide d'une bibliothèque comme iText ou Apache PDFBox.
        // Pour cet exemple, nous retournons un tableau d'octets vide.
        return new byte[0];
    }
}




