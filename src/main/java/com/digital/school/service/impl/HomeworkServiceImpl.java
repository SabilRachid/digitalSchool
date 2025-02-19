package com.digital.school.service.impl;

import com.digital.school.model.Professor;
import com.digital.school.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.digital.school.model.Homework;
import com.digital.school.model.User;
import com.digital.school.repository.HomeworkRepository;
import com.digital.school.service.HomeworkService;
import com.digital.school.service.StorageService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class HomeworkServiceImpl implements HomeworkService {

    @Autowired
    private HomeworkRepository homeworkRepository;

    @Autowired
    private StorageService storageService;

        @Override
        public List<Map<String, Object>> findAllAsMap() {
            List<Homework> homeworks = homeworkRepository.findAll();
            return homeworks.stream().map(hw -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", hw.getId());
                map.put("title", hw.getTitle());
                // On suppose que Homework a une association "course" qui possède un nom et un identifiant
                if (hw.getCourse() != null) {
                    Map<String, Object> courseMap = new HashMap<>();
                    courseMap.put("id", hw.getCourse().getId());
                    courseMap.put("name", hw.getCourse().getName());
                    map.put("course", courseMap);
                } else {
                    map.put("course", null);
                }
                map.put("dueDate", hw.getDueDate());
                map.put("status", hw.getStatus());
                // Ajoutez d'autres champs si nécessaire
                return map;
            }).collect(Collectors.toList());
      }



    /* Crée un nouveau devoir */
        @Override
        public void createHomework(Professor professor, Homework homework) {
            homework.setProfessor(professor);
            homeworkRepository.save(homework);
        }

        /* Supprime un devoir */
        @Override
        public void deleteHomework(Long id) {
            homeworkRepository.deleteById(id);
        }

        /* Récupère la liste des devoirs d'un professeur */
        @Override
        public List<Homework> findHomeworksByProfessor(Professor professor) {
            return homeworkRepository.findByProfessor(professor);
        }

        /* Met à jour un devoir */
        @Override
        public void updateHomework(Homework homework, Homework updatedHomework) {
            homework.setTitle(updatedHomework.getTitle());
            homework.setDescription(updatedHomework.getDescription());
            homework.setDueDate(updatedHomework.getDueDate());
            homework.setCourse(updatedHomework.getCourse());
            homeworkRepository.save(homework);
        }


    /* Récupère les devoirs en attente d'un étudiant. */
    @Override
    public List<Homework> findPendingHomework(Student student) {
        return homeworkRepository.findByStudentAndStatusOrderByDueDateAsc(student, "PENDING");
    }

    /* Récupère les devoirs soumis par un étudiant. */
    @Override
    public List<Homework> findSubmittedHomework(Student student) {
        return homeworkRepository.findByStudentAndStatusOrderByDueDateDesc(student, "SUBMITTED");
    }

    /* Récupère les devoirs notés d'un étudiant. */
    @Override
    public List<Homework> findGradedHomework(Student student) {
        return homeworkRepository.findByStudentAndStatusOrderByDueDateDesc(student, "GRADED");
    }

    /* Recherche un devoir par son identifiant. */
    @Override
    public Optional<Homework> findById(Long id) {
        return homeworkRepository.findById(id);
    }

    /* Permet à un étudiant de soumettre un devoir avec un fichier et un commentaire. */
    @Override
    @Transactional
    public Homework submitHomework(Long id, Student student, MultipartFile file, String comment) {
        Homework homework = findById(id)
                .orElseThrow(() -> new RuntimeException("Devoir non trouvé"));

        if (!homework.getStudent().equals(student)) {
            throw new RuntimeException("Vous n'êtes pas autorisé à soumettre ce devoir");
        }

        if (homework.getDueDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("La date limite est dépassée");
        }

        /* Sauvegarde du fichier */
        String filePath = storageService.store(file, generateSubmissionFileName(homework, file));

        /* Mise à jour du devoir */
        homework.setStatus("SUBMITTED");

        return homeworkRepository.save(homework);
    }

    /* Récupère le fichier soumis pour un devoir. */
    @Override
    public ResponseEntity<?> getSubmissionFile(Long id) {
        Homework homework = findById(id)
                .orElseThrow(() -> new RuntimeException("Devoir non trouvé"));

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" +
                        getSubmissionFileName(homework) + "\"")
                .body(storageService.loadAsResource(homework.getStatus()));
    }

    /* Récupère les devoirs en attente de correction par un professeur. */
    @Override
    public List<Homework> findPendingGradingByProfessor(Professor professor) {
        return homeworkRepository.findPendingGradingByProfessor(professor);
    }

    /* Permet à un professeur de noter un devoir. */
    @Override
    @Transactional
    public Homework gradeHomework(Long homeworkId, Professor professor, double grade, String feedback) {
        Homework homework = findById(homeworkId)
                .orElseThrow(() -> new RuntimeException("Devoir non trouvé"));

        if (!homework.getProfessor().equals(professor)) {
            throw new RuntimeException("Vous n'êtes pas autorisé à noter ce devoir");
        }

        homework.setStatus("GRADED");

        return homeworkRepository.save(homework);
    }

    /* Récupère la liste des devoirs donnés par un professeur. */
    @Override
    public List<Homework> findHomeworkByProfessor(Professor professor) {
        return homeworkRepository.findByProfessor(professor);
    }

    /* Supprime un devoir donné par un professeur. */
    @Override
    @Transactional
    public ResponseEntity<String> deleteHomework(Long homeworkId, Professor professor) {
        Homework homework = findById(homeworkId)
                .orElseThrow(() -> new RuntimeException("Devoir non trouvé"));

        if (!homework.getProfessor().equals(professor)) {
            throw new RuntimeException("Vous n'êtes pas autorisé à supprimer ce devoir");
        }

        homeworkRepository.delete(homework);
        return ResponseEntity.ok("Devoir supprimé avec succès.");
    }



    /* Génère le nom du fichier soumis pour un devoir. */
    private String generateSubmissionFileName(Homework homework, MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        return String.format("homework_%d_student_%d%s",
                homework.getId(),
                homework.getStudent().getId(),
                extension);
    }

    /* Récupère le nom du fichier soumis. */
    private String getSubmissionFileName(Homework homework) {
        String path = homework.getStatus();
        return path.substring(path.lastIndexOf("/") + 1);
    }



}
