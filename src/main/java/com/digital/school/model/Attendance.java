package com.digital.school.model;

import com.digital.school.model.enumerated.AttendanceStatus;
import com.digital.school.model.enumerated.StudentAttendanceStatus;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "attendances")
public class Attendance extends AuditableEntity {

    // Relation OneToOne avec le cours : chaque cours a une fiche d'attendance unique
    @OneToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    // Date de l'événement (par exemple, la date du cours)
    @Column(nullable = false)
    private LocalDate dateEvent;

    // Liste des enregistrements de présence pour chaque élève du cours
    @OneToMany(mappedBy = "attendance", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudentAttendance> studentAttendances = new ArrayList<>();

    private String justification;

    private String justificationFile;

    @ManyToOne
    @JoinColumn(name = "recorded_by_id")
    private User recordedBy;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttendanceStatus status;

    public Attendance() {
    }

    public Attendance(Course course, LocalDate dateEvent) {
        this.course = course;
        this.dateEvent = dateEvent;
    }

    // Getters et setters

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public LocalDate getDateEvent() {
        return dateEvent;
    }

    public void setDateEvent(LocalDate dateEvent) {
        this.dateEvent = dateEvent;
    }

    public List<StudentAttendance> getStudentAttendances() {
        return studentAttendances;
    }

    public void setStudentAttendances(List<StudentAttendance> studentAttendances) {
        this.studentAttendances = studentAttendances;
    }

    // Méthode utilitaire pour ajouter un StudentAttendance
    public void addStudentAttendance(StudentAttendance studentAttendance) {
        studentAttendances.add(studentAttendance);
        studentAttendance.setAttendance(this);
    }

    // Méthode utilitaire pour retirer un StudentAttendance
    public void removeStudentAttendance(StudentAttendance studentAttendance) {
        studentAttendances.remove(studentAttendance);
        studentAttendance.setAttendance(null);
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustificationFile(String justificationFile) {
        this.justificationFile = justificationFile;
    }

    public String getJustificationFile() {
        return justificationFile;
    }

    public User getRecordedBy() {
        return recordedBy;
    }

    public void setRecordedBy(User recordedBy) {
        this.recordedBy = recordedBy;
    }

    public AttendanceStatus getStatus() {
        return status;
    }

    public void setStatus(AttendanceStatus status) {
        this.status = status;
    }


    @Override
    public String toString() {
        return "Attendance{" +
                "course=" + course +
                ", dateEvent=" + dateEvent +
                ", studentAttendances=" + studentAttendances +
                ", justification='" + justification + '\'' +
                ", justificationFile='" + justificationFile + '\'' +
                ", recordedBy=" + recordedBy +
                ", status=" + status +
                "} " + super.toString();
    }
}
