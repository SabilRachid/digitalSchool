package com.digital.school.service.impl;

import com.digital.school.dto.ExamDTO;
import com.digital.school.model.*;
import com.digital.school.model.enumerated.EvaluationStatus;
import com.digital.school.model.enumerated.EventType;
import com.digital.school.repository.*;
import com.digital.school.service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class ExamServiceImpl implements ExamService {

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private ClasseRepository classeRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private StudentSubmissionRepository studentSubmissionRepository;

    @Override
    @Transactional
    public Exam createExam(ExamDTO examDTO, Long professorId) {

        Exam exam = new Exam();
        // Affectation des champs simples
        exam.setType(EventType.EXAM);
        exam.setTitle(examDTO.getTitle());
        exam.setDescription(examDTO.getDescription());
        exam.setStartTime(examDTO.getStartTime());
        exam.setEndTime(examDTO.getStartTime()
                .plusMinutes(examDTO.getDuration()));
        exam.setDuration(examDTO.getDuration());
        exam.setStatus(EvaluationStatus.SCHEDULED); // Statut initial
        exam.setMaxScore(examDTO.getMaxScore());
        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new RuntimeException("Professeur non trouvé avec l'ID " + professorId));
        exam.setProfessor(professor);
        Subject subject = subjectRepository.findById(examDTO.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Sujet non trouvé avec l'ID " + examDTO.getSubjectId()));
        exam.setSubject(subject);
        Classe classe = classeRepository.findById(examDTO.getClasseId())
                .orElseThrow(() -> new RuntimeException("Classe non trouvée avec l'ID " + examDTO.getClasseId()));
        exam.setClasse(classe);

        // Optionnel : charger la salle si l'ExamDTO fournit un identifiant de salle
        if (examDTO.getRoomId() != null) {
            Room room = roomRepository.findById(examDTO.getRoomId())
                    .orElseThrow(() -> new RuntimeException("Salle non trouvée avec l'ID " + examDTO.getRoomId()));
            exam.setRoom(room);
        }


        // Ajouter tous les élèves de la classe
        exam.setParticipants(new HashSet<>(studentRepository.getStudentsByClasseId(examDTO.getClasseId())));

        // Ajouter le professeur associé à la matière pour cette classe
        professorRepository.findProfessorsByClasseId(examDTO.getClasseId()).stream()
                .filter(prof -> prof.getSubjects().contains(exam.getSubject()))
                .forEach(exam.getParticipants()::add);


        return examRepository.save(exam);
    }


    @Override
    public void publishExam(Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Examen non trouvé"));
        // Mettre à jour le status (ex: SCHEDULED -> PUBLISHED)
        exam.setStatus(com.digital.school.model.enumerated.EvaluationStatus.PUBLISHED);
        examRepository.save(exam);
    }

    @Override
    public void endExam(Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Examen non trouvé"));
        // Mettre à jour le status pour indiquer qu'il est terminé
        exam.setStatus(com.digital.school.model.enumerated.EvaluationStatus.COMPLETED);
        examRepository.save(exam);
    }

    @Override
    public Map<String, Object> getExamResults(Long examId) {
        // Implémentez la logique pour calculer les résultats de l'examen
        // Par exemple, calculer la moyenne, la note max, etc.
        // Retournez un Map avec les informations nécessaires
        throw new UnsupportedOperationException("Méthode non implémentée");
    }

    @Override
    public byte[] generateExamReport(Long examId) {
        // Implémentez la génération du rapport PDF pour l'examen
        throw new UnsupportedOperationException("Méthode non implémentée");
    }

    @Override
    public StudentSubmission enterGrade(Long submissionId, Double gradeValue, String comment, Long professorId) {
        // Récupérer la soumission d'examen par son identifiant
        StudentSubmission submission = studentSubmissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Soumission non trouvée"));
        // Vérifiez que le professeur a le droit de noter cette soumission
        // (par exemple, en vérifiant que submission.getEvaluation().getProfessor().getId() == professorId)
        submission.setValue(gradeValue);
        submission.setComments(comment);
        // Vous pouvez mettre à jour le status, par exemple passer de IN_PROGRESS à COMPLETED si nécessaire.
        submission.setStatus(com.digital.school.model.enumerated.StudentSubmissionStatus.COMPLETED);
        return studentSubmissionRepository.save(submission);
    }

    @Override
    public List<Exam> findUpcomingExams(Student student) {
        return examRepository.findUpcomingExamsByStudent(student.getId(), LocalDate.now());
    }

    @Override
    public List<Exam> findExamsByProfessor(Long professorId) {
        return examRepository.findByProfessorId(professorId);
    }

    @Override
    public List<Exam> findExamsByProfessor(Long professorId, String month, Long classe, Long subject) {
        // Récupère d'abord tous les examens du professeur
        List<Exam> exams = examRepository.findByProfessorId(professorId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        return exams.stream().filter(exam -> {
            boolean matches = true;
            if (month != null && !month.isEmpty()) {
                // On formate examDate en "yyyy-MM"
                String examMonth = exam.getStartTime().format(formatter);
                matches = matches && examMonth.equals(month);
            }
            if (classe != null) {
                matches = matches && exam.getClasse().getId().equals(classe);
            }
            if (subject != null) {
                matches = matches && exam.getSubject().getId().equals(subject);
            }
            return matches;
        }).collect(Collectors.toList());
    }

    @Override
    public ExamDTO convertToDTO(Exam exam) {
        ExamDTO dto = new ExamDTO();
        dto.setId(exam.getId());
        dto.setTitle(exam.getTitle());
        dto.setDescription(exam.getDescription());
        dto.setStartTime(exam.getStartTime());
        dto.setDuration(exam.getDuration());
        dto.setMaxScore(exam.getMaxScore());
        dto.setGraded(exam.isGraded());

        // Déduire la date d'examen à partir du startTime
        if (exam.getStartTime() != null) {
            dto.setExamDate(exam.getStartTime().toLocalDate());
        }

        // Affecter le nom de la salle si disponible
        if (exam.getRoom() != null) {
            dto.setRoomId(exam.getRoom().getId());
        }

        // Affecter les informations liées au sujet, professeur et classe
        if (exam.getSubject() != null) {
            dto.setSubjectId(exam.getSubject().getId());
        }
        if (exam.getProfessor() != null) {
            dto.setProfessorName(exam.getProfessor().getFirstName() + " " + exam.getProfessor().getLastName());
        }
        if (exam.getClasse() != null) {
            dto.setClasseId(exam.getClasse().getId());
        }

        dto.setStatus(exam.getStatus());

        return dto;
    }


}


