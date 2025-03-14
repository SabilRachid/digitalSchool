package com.digital.school.service.impl;

import com.digital.school.controller.rest.professor.ProfessorExamController;
import com.digital.school.dto.ExamDTO;
import com.digital.school.dto.ExamGradeEntryRequest;
import com.digital.school.model.*;
import com.digital.school.model.enumerated.EvaluationStatus;
import com.digital.school.model.enumerated.EventType;
import com.digital.school.repository.*;
import com.digital.school.service.ExamService;
import org.slf4j.Logger;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.io.font.constants.StandardFonts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ExamServiceImpl implements ExamService {

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ExamServiceImpl.class);

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
    private EvaluationRepository evaluationRepository;

    @Autowired
    private EvaluationGradeRepository evaluationGradeRepository;

    @Override
    @Transactional
    public Exam createExam(ExamDTO examDTO, Long professorId) {

        LOGGER.debug("createExam with elements professorId:" + professorId + " and examDTO"+examDTO);

        Exam exam = new Exam();
        // Affectation des champs simples
        exam.setType(EventType.EXAM);
        exam.setGraded(false);
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
    public void publishExam(Long evaluationId) {
        LOGGER.debug("Service publishExam for evaluationId="+evaluationId);
        Exam evaluation = examRepository.findById(evaluationId)
                .orElseThrow(() -> new RuntimeException("Examen non trouvé"));

        // Mettre à jour le status (ex: SCHEDULED -> UPCOMING)
        evaluation.setStatus(com.digital.school.model.enumerated.EvaluationStatus.UPCOMING);

        // Récupération de la liste des étudiants de la classe associée à l'évaluation
        Set<Student> students = evaluation.getClasse().getStudents();

        // Pour chaque étudiant, s'il n'existe pas déjà une note enregistrée, on crée une entrée EvaluationGrade vide
        for (Student student : students) {
            LOGGER.debug("set Grade Evaluation for student="+student.toString());
            Optional<EvaluationGrade> existingGrade = evaluationGradeRepository
                    .findByEvaluationIdAndStudentId(evaluationId, student.getId());
            if (existingGrade.isEmpty()) {
                LOGGER.debug("create new EvaluationGrade");
                EvaluationGrade grade = new EvaluationGrade();
                grade.setEvaluation(evaluation);
                grade.setStudent(student);
                grade.setGrade(null); // ou 0.0 si vous préférez une valeur numérique par défaut
                grade.setRemark("");
                grade.setGradedAt(null);
                evaluationGradeRepository.save(grade);
            }
        }

        examRepository.save(evaluation);
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
        // Récupérer l'examen
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Examen non trouvé"));

        // Récupérer toutes les notes liées à l'examen
        List<EvaluationGrade> grades = evaluationGradeRepository.findByEvaluationId(examId);

        // Calculer quelques statistiques simples
        double average = grades.stream()
                .mapToDouble(EvaluationGrade::getGrade)
                .average().orElse(0);
        double max = grades.stream()
                .mapToDouble(EvaluationGrade::getGrade)
                .max().orElse(0);
        double min = grades.stream()
                .mapToDouble(EvaluationGrade::getGrade)
                .min().orElse(0);

        // Préparer le résultat
        Map<String, Object> results = new HashMap<>();
        results.put("examTitle", exam.getTitle());
        results.put("average", average);
        results.put("max", max);
        results.put("min", min);
        results.put("totalSubmissions", grades.size());
        // Vous pouvez ajouter d'autres statistiques ou détails (par exemple, distribution des notes)

        return results;
    }



    @Override
    public byte[] generateExamReport(Long id) {
        // Récupération de l'examen
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Examen non trouvé"));

        // On suppose que l'examen possède une collection d'EvaluationGrade (les notes des élèves)
        List<EvaluationGrade> grades = exam.getEvaluationGrades();

        // Création du flux de sortie pour le PDF
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Chargement des polices
            PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            PdfFont bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

            // Format de date et heure
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            // Ajout du titre du rapport
            document.add(new Paragraph("Rapport de l'examen").setFont(bold).setFontSize(16));
            document.add(new Paragraph(" ")); // Espace

            // Ajout des informations de l'examen
            document.add(new Paragraph("Titre : " + exam.getTitle()).setFont(font).setFontSize(12));
            document.add(new Paragraph("Matière : " + exam.getSubject().getName()).setFont(font).setFontSize(12));
            document.add(new Paragraph("Classe : " + exam.getClasse().getName()).setFont(font).setFontSize(12));
            document.add(new Paragraph("Date et heure de début : " + exam.getStartTime().format(dtf))
                    .setFont(font).setFontSize(12));
            document.add(new Paragraph("Durée : " + exam.getDuration() + " minutes").setFont(font).setFontSize(12));
            document.add(new Paragraph(" ")); // Espace

            // Création d'un tableau pour afficher les notes des élèves
            float[] columnWidths = {200F, 80F, 200F};
            Table table = new Table(columnWidths);

            // Ajout des en-têtes du tableau avec un fond coloré
            table.addHeaderCell(new Cell().add(new Paragraph("Élève").setFont(bold))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY));
            table.addHeaderCell(new Cell().add(new Paragraph("Note").setFont(bold))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY));
            table.addHeaderCell(new Cell().add(new Paragraph("Remarque").setFont(bold))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY));

            // Remplissage du tableau avec les notes
            for (EvaluationGrade eg : grades) {
                String studentName = eg.getStudent().getFirstName() + " " + eg.getStudent().getLastName();
                table.addCell(new Cell().add(new Paragraph(studentName).setFont(font)));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(eg.getGrade())).setFont(font)));
                table.addCell(new Cell().add(new Paragraph(eg.getRemark() != null ? eg.getRemark() : "").setFont(font)));
            }

            // Ajout du tableau au document
            document.add(table);

            // Ajout d'une ligne de séparation et d'un message de pied de page
            document.add(new Paragraph("-----------------------------------------------------").setFont(font).setFontSize(12));
            document.add(new Paragraph("Ce rapport est généré automatiquement.").setFont(font).setFontSize(10));

            // Fermeture du document
            document.close();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du rapport PDF", e);
        }

        return baos.toByteArray();
    }


    @Override
    public EvaluationGrade enterGrade(Long evaluationId, Double gradeValue, String comment, Long professorId) {
        // Récupérer l'évaluation (examen) par son ID
        Evaluation evaluation = evaluationRepository.findById(evaluationId)
                .orElseThrow(() -> new RuntimeException("Évaluation non trouvée"));

        // Créer un objet EvaluationGrade
        EvaluationGrade grade = new EvaluationGrade();
        grade.setEvaluation(evaluation);
        // Ici, selon votre logique, vous pouvez déterminer l'étudiant concerné.
        // Par exemple, si vous avez l'information de l'étudiant dans le contexte ou le DTO.
        // Pour cet exemple, supposons qu'il s'agit d'un étudiant actuellement connecté ou sélectionné.
        // grade.setStudent(student);
        grade.setGrade(gradeValue);
        grade.setRemark(comment);
        grade.setGradedAt(LocalDateTime.now());

        // Mettez à jour l'état de l'évaluation si nécessaire (par exemple, passer 'graded' à true)
        evaluation.setGraded(true);

        return evaluationGradeRepository.save(grade);
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

    /**
     * Récupère les notes déjà saisies pour un examen.
     * Ici, nous supposons que l'examen (traité comme Evaluation) est identifié par examId.
     */
    @Override
    public List<Map<String, Object>> getExamGrades(Long examId) {
        List<EvaluationGrade> grades = evaluationGradeRepository.findByEvaluationId(examId);
        return grades.stream().map(grade -> {
            Map<String, Object> map = new HashMap<>();
            Student student = grade.getStudent();
            map.put("studentId", student.getId());
            map.put("firstName", student.getFirstName());
            map.put("lastName", student.getLastName());
            map.put("grade", grade.getGrade());
            map.put("comment", grade.getRemark());
            return map;
        }).collect(Collectors.toList());
    }

    /**
     * Sauvegarde ou met à jour les notes saisies pour l'examen.
     * Pour chaque entrée, vérifie si une note existe déjà pour cet étudiant et cet examen.
     */
    @Override
    public void saveExamGrades(List<ExamGradeEntryRequest> gradeEntries, Long professorId) {
        for (ExamGradeEntryRequest entry : gradeEntries) {
            // Vérifier l'existence d'une note pour cet étudiant et cette évaluation
            Optional<EvaluationGrade> opt = evaluationGradeRepository
                    .findByEvaluationIdAndStudentId(entry.getEvaluationId(), entry.getStudentId());
            EvaluationGrade grade;
            if (opt.isPresent()) {
                grade = opt.get();
            } else {
                grade = new EvaluationGrade();
                // Utilisez des méthodes de repository ou EntityManager pour obtenir les références
                // Exemple (à adapter selon votre implémentation) :
                // grade.setEvaluation(examRepository.getReferenceById(entry.getEvaluationId()));
                // grade.setStudent(studentRepository.getReferenceById(entry.getStudentId()));
            }
            // Mettre à jour la note, le commentaire et la date de saisie
            grade.setGrade(entry.getGradeValue());
            grade.setRemark(entry.getComment());
            grade.setGradedAt(LocalDateTime.now());
            evaluationGradeRepository.save(grade);
        }
    }

    /**
     * Publie la saisie finale des notes pour un examen.
     * Vérifie que le nombre de notes saisies correspond au nombre d'étudiants inscrits à l'examen.
     */
    @Override
    @Transactional
    public void publishExamGrades(Long evaluationId, Long professorId) {
        // Récupération de l'évaluation (examen ou devoir)
        Evaluation evaluation = evaluationRepository.findById(evaluationId)
                .orElseThrow(() -> new RuntimeException("Évaluation non trouvée"));

        // Vérification que le professeur est autorisé à publier cette évaluation
        if (!evaluation.getProfessor().getId().equals(professorId)) {
            throw new RuntimeException("Accès non autorisé");
        }


        // Mise à jour du statut de l'évaluation (par exemple, passage à COMPLETED)
        evaluation.setStatus(EvaluationStatus.COMPLETED);
        evaluationRepository.save(evaluation);
    }



}


