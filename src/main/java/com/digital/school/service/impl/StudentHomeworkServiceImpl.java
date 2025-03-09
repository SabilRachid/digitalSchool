package com.digital.school.service.impl;

import com.digital.school.model.Document;
import com.digital.school.model.enumerated.DocumentType;
import com.digital.school.model.Homework;
import com.digital.school.model.EvaluationGrade;
import com.digital.school.model.Student;
import com.digital.school.repository.DocumentRepository;
import com.digital.school.repository.EvaluationGradeRepository;
import com.digital.school.repository.HomeworkRepository;
import com.digital.school.service.StudentHomeworkService;
import com.digital.school.service.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class StudentHomeworkServiceImpl implements StudentHomeworkService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StudentHomeworkServiceImpl.class);

    @Autowired
    private HomeworkRepository homeworkRepository;

    @Autowired
    private EvaluationGradeRepository evaluationGradeRepository;

    @Autowired
    private StorageService storageService;

    @Autowired
    private DocumentRepository documentRepository;

    @Override
    public List<Homework> findUpcomingHomeworks(Student student) {
        // Retourne les devoirs pour la classe de l'étudiant dont la date d'échéance est aujourd'hui ou ultérieure.
        return homeworkRepository.findUpcomingHomeworksByStudent(student.getId(), LocalDate.now());
    }

    @Override
    public List<Homework> findHomeworksForNextDays(Student student) {
        LocalDate today = LocalDate.now();
        LocalDate nextWeek = today.plusDays(7);
        return homeworkRepository.findHomeworksByStudentBetweenDates(student.getId(), today, nextWeek);
    }

    @Override
    @Transactional
    public EvaluationGrade submitHomework(Long homeworkId, MultipartFile file, String comment, Student student) {
        // Récupérer le devoir
        Homework homework = homeworkRepository.findById(homeworkId)
                .orElseThrow(() -> new RuntimeException("Devoir non trouvé"));

        // Récupérer ou créer la soumission sous forme d'EvaluationGrade
        Optional<EvaluationGrade> submissionOpt = evaluationGradeRepository.findByEvaluationIdAndStudentId(homeworkId, student.getId());
        EvaluationGrade submission = submissionOpt.orElseGet(() -> {
            EvaluationGrade eg = new EvaluationGrade();
            eg.setEvaluation(homework);
            eg.setStudent(student);
            return eg;
        });
        submission.setRemark(comment);
        submission.setSubmitted(true);
        submission.setGradedAt(LocalDateTime.now());

        try {
            // Stocker le fichier et créer un document associé
            String fileUrl = storageService.storeFile(file);
            Document doc = new Document();
            doc.setName(file.getOriginalFilename());
            doc.setType(DocumentType.PDF); // Ou déduire dynamiquement le type
            doc.setContentType(file.getContentType());
            doc.setFileUrl(fileUrl);
            doc.setFileSize(file.getSize());
            doc.setOwner(student);
            // Sauvegarder la soumission pour obtenir un ID
            submission = evaluationGradeRepository.save(submission);
            // Associer le document à cette soumission
            doc.setRelatedEntityId(submission.getId());
            doc.setRelatedEntityType("HomeworkSubmission");
            Document savedDoc = documentRepository.save(doc);
          //  submission.getEvaluation().setDocument(savedDoc);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la soumission du devoir: " + e.getMessage(), e);
        }
        return evaluationGradeRepository.save(submission);
    }

    @Override
    public Optional<EvaluationGrade> findByIdAndStudent(Long id, Student student) {
        return evaluationGradeRepository.findByEvaluationIdAndStudentId(id, student.getId());
    }
}
