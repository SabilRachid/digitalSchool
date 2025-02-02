package com.digital.school.dto;

import com.digital.school.model.Participation;
import com.digital.school.model.User;
import com.digital.school.model.Subject;
import com.digital.school.model.Classe;
import com.digital.school.model.enumerated.ParticipationType;

import java.time.LocalDateTime;

public class ParticipationDto {

    private Long id;
    private Long studentId;
    private String studentName;
    private Long subjectId;
    private String subjectName;
    private Long classId;
    private String className;
    private LocalDateTime participationDate;
    private String participationType;
    private String comments;

    public ParticipationDto() {}

    public ParticipationDto(Long id, Long studentId, String studentName, Long subjectId, String subjectName,
                            Long classId, String className, LocalDateTime participationDate,
                            ParticipationType participationType, String comments) {
        this.id = id;
        this.studentId = studentId;
        this.studentName = studentName;
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.classId = classId;
        this.className = className;
        this.participationDate = participationDate;
        this.participationType = String.valueOf(participationType);
        this.feedback = feedback;
    }

    // Convertir une entité en DTO
    public static ParticipationDto fromEntity(Participation participation) {
        return new ParticipationDto(
                participation.getId(),
                participation.getStudent().getId(),
                participation.getStudent().getFirstName() + " " + participation.getStudent().getLastName(),
                participation.getCourse().getSubject().getId(),
                participation.getCourse().getSubject().getName(),
                participation.getCourse().getClasse().getId(),
                participation.getCourse().getClasse().getName(),
                participation.getRecordedAt(),
                participation.getParticipationType(),
                participation.getFeedback()
        );
    }

    private String feedback; // Remarque sur la participation

    private LocalDateTime recordedAt;
    // Convertir un DTO en entité
    public Participation toEntity() {
        Participation participation = new Participation();
        participation.setId(this.id);

        User student = new User();
        student.setId(this.studentId);
        participation.setStudent(student);

        Subject subject = new Subject();
        subject.setId(this.subjectId);

        Classe classe = new Classe();
        classe.setId(this.classId);

        participation.setRecordedAt(this.participationDate);
        participation.setParticipationType(ParticipationType.valueOf(this.participationType));
        participation.setFeedback(this.feedback);

        return participation;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public Long getClassId() { return classId; }
    public void setClassId(Long classId) { this.classId = classId; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public LocalDateTime getParticipationDate() { return participationDate; }
    public void setParticipationDate(LocalDateTime participationDate) { this.participationDate = participationDate; }

    public String getParticipationType() { return participationType; }
    public void setParticipationType(String participationType) { this.participationType = participationType; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
}
