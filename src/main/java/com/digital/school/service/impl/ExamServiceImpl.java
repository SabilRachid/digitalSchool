package com.digital.school.service.impl;

import com.digital.school.model.Exam;
import com.digital.school.model.Professor;
import com.digital.school.model.Student;
import com.digital.school.model.enumerated.ExamStatus;
import com.digital.school.repository.ExamRepository;
import com.digital.school.service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ExamServiceImpl implements ExamService {

    @Autowired
    private ExamRepository examRepository;


    @Override
    public List<Exam> findUpcomingExams(Student student) {
        return examRepository.findUpcomingExamsByStudent(student.getId(), LocalDate.now());
    }

    @Override
    public List<Exam> findByProfessor(Professor professor) {
        return examRepository.findExamsByProfessor(professor);
    }

    @Override
    public Exam createExam(Exam exam, Long professorId) {
        // Vous pouvez associer l'examen au professeur via professorId, par exemple en utilisant un champ "professorId" dans Exam.
        // Ici, nous supposons que l'examen est initialisé en mode brouillon ("DRAFT")
        exam.setStatus(ExamStatus.DRAFT);
        // Si nécessaire, effectuez d'autres initialisations spécifiques
        return examRepository.save(exam);
    }

    @Override
    public void publishExam(Long id) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Examen non trouvé pour l'ID : " + id));
        // Mettre à jour le statut pour la publication
        exam.setStatus(ExamStatus.SCHEDULED);
        examRepository.save(exam);
    }

    @Override
    public void endExam(Long id) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Examen non trouvé pour l'ID : " + id));
        // Mettre à jour le statut pour indiquer la fin de l'examen
        exam.setStatus(ExamStatus.COMPLETED);
        examRepository.save(exam);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getExamResults(Long id) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Examen non trouvé pour l'ID : " + id));
        Map<String, Object> results = new HashMap<>();
        // Exemple de données à retourner
        results.put("examTitle", exam.getName());
        // Pour l'exemple, nous utilisons des valeurs fictives :
        results.put("average", 12.5);
        results.put("highest", 18.0);
        results.put("passRate", 75.0);
        // Distribution des notes (exemple fictif, vous pouvez ajuster selon votre logique)
        results.put("distribution", new int[]{2, 5, 10, 8, 3, 1, 0});
        // Si vous avez une collection de résultats, vous pouvez l'ajouter également
        // results.put("studentGrades", exam.getStudentGrades());
        return results;
    }

    @Override
    public byte[] generateExamReport(Long id) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Examen non trouvé pour l'ID : " + id));
        // Implémentez ici la génération de PDF à l'aide d'une bibliothèque comme iText ou Apache PDFBox.
        // Pour cet exemple, nous retournons un tableau d'octets vide.
        return new byte[0];
    }

    @Override
    public List<Exam> findExamsByProfessor(Long professorId) {
        return examRepository.findByProfessor_Id(professorId);
    }
}
