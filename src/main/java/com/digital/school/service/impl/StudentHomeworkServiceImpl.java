package com.digital.school.service.impl;

import com.digital.school.model.Document;
import com.digital.school.model.enumerated.DocumentType;
import com.digital.school.model.Homework;
import com.digital.school.model.Student;
import com.digital.school.model.StudentHomework;
import com.digital.school.repository.DocumentRepository;
import com.digital.school.repository.HomeworkRepository;
import com.digital.school.repository.ParentHomeworkRepository;
import com.digital.school.repository.StudentHomeworkRepository;
import com.digital.school.service.StudentHomeworkService;
import com.digital.school.service.StorageService;
import org.slf4j.Logger;
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

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(StudentHomeworkServiceImpl.class);


    @Autowired
    private HomeworkRepository HomeworkRepository;

    @Autowired
    private StudentHomeworkRepository studentHomeworkRepository;

    @Autowired
    private StorageService storageService;

    @Autowired
    private DocumentRepository documentRepository;

    @Override
    public List<StudentHomework> findUpcomingHomeworks(Student student) {
        return studentHomeworkRepository.findUpcomingHomeworksByStudent(student.getId(), LocalDate.now());
    }

    @Override
    public List<StudentHomework> findHomeworksForNextDays(Student student) {
        LocalDate today = LocalDate.now();
        LocalDate nextWeek = today.plusDays(7);
        return studentHomeworkRepository.findHomeworksByStudentBetweenDates(student.getId(), today, nextWeek);
    }

    @Override
    @Transactional
    public StudentHomework submitHomework(Long homeworkId, MultipartFile file, String comment, Student student) {
        // Récupérer le devoir assigné à l'étudiant
        StudentHomework studentHomework = studentHomeworkRepository.findByIdAndStudent(homeworkId, student)
                .orElseThrow(() -> new RuntimeException("Devoir non trouvé pour cet étudiant"));

        studentHomework.setStudent(student);
       // studentHomework.setCompletionDate(LocalDateTime.now());
       // studentHomework.setComment(comment);

        try {
            // Stocker le fichier via StorageService
            String fileUrl = storageService.storeFile(file);

            // Créer le document associé à la soumission
            Document doc = new Document();
            doc.setName(file.getOriginalFilename());
            doc.setType(DocumentType.PDF); // ou déterminer dynamiquement le type
            doc.setContentType(file.getContentType());
            doc.setFileUrl(fileUrl);
            doc.setFileSize(file.getSize());
            doc.setOwner(student);
            doc.setRelatedEntityId(studentHomework.getId());
            doc.setRelatedEntityType("HomeworkSubmission");

            Document savedDoc = documentRepository.save(doc);
            studentHomework.setDocument(savedDoc);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la soumission du devoir: " + e.getMessage(), e);
        }

        return studentHomeworkRepository.save(studentHomework);
    }

    @Override
    public Optional<StudentHomework> findByIdAndStudent(Long id, Student student) {
        return studentHomeworkRepository.findByIdAndStudent(id, student);
    }
}
