//donne moi toute la classe ParticipationDto adaptée pour participations```java

package com.digital.school.dto;

import com.digital.school.model.Participation;
import com.digital.school.model.enumerated.ParticipationType;

import java.time.LocalDateTime;

public class ParticipationDto {
    private Long id;
    private String studentFirstName;
    private String studentLastName;
    private String subjectName;
    private String className;
    private LocalDateTime recordedAt;
    private ParticipationType participationType;
    private String feedback;

    public ParticipationDto(Long id, String studentFirstName, String studentLastName, String subjectName, String className, LocalDateTime recordedAt, ParticipationType participationType, String feedback) {
        this.id = id;
        this.studentFirstName = studentFirstName;
        this.studentLastName = studentLastName;
        this.subjectName = subjectName;
        this.className = className;
        this.recordedAt = recordedAt;
        this.participationType = participationType;
        this.feedback = feedback;
    }


    public static ParticipationDto fromEntity(Participation participation) {
        return new ParticipationDto(
                participation.getId(),
                participation.getStudent().getFirstName(),
                participation.getStudent().getLastName(),
                participation.getCourse().getSubject().getName(),
                participation.getCourse().getClasse().getName(),
                participation.getRecordedAt(),
                participation.getParticipationType(),
                participation.getFeedback()
        );
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStudentFirstName() {
        return studentFirstName;
    }

    public void setStudentFirstName(String studentFirstName) {
        this.studentFirstName = studentFirstName;
    }

    public String getStudentLastName() {
        return studentLastName;
    }

    public void setStudentLastName(String studentLastName) {
        this.studentLastName = studentLastName;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(LocalDateTime recordedAt) {
        this.recordedAt = recordedAt;
    }

    public ParticipationType getParticipationType() {
        return participationType;
    }

    public void setParticipationType(ParticipationType participationType) {
        this.participationType = participationType;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

}
