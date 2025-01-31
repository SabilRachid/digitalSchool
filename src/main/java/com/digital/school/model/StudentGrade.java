package com.digital.school.model;

import java.time.LocalDateTime;

import com.digital.school.model.enumerated.GradeType;
import jakarta.persistence.*;

@Entity
@Table(name = "student_grades")
public class StudentGrade extends AuditableEntity {
	
    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;
    
    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;
    
    private String title;
    
    @Column(name = "grade_value") // Renommage de la colonne
    private Float value;

	@Enumerated(EnumType.STRING)
	@Column(name = "grade_letter", nullable = false)
	private GradeType grade; // Grade (A, B, C...)

    private Double classAverage;
    private LocalDateTime date;
    private String comments;
    
    @ManyToOne
    @JoinColumn(name = "graded_by_id")
    private User gradedBy;

    public StudentGrade() {
    }
    
	public User getStudent() {
		return student;
	}

	public void setStudent(User student) {
		this.student = student;
	}

	public Subject getSubject() {
		return subject;
	}

	public void setSubject(Subject subject) {
		this.subject = subject;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Float getValue() {
		return value;
	}

	public void setValue(Float value) {
		this.value = value;
		this.grade = GradeType.fromScore(value);
	}

	public GradeType getGrade() { return grade; }

	public Double getClassAverage() {
		return classAverage;
	}

	public void setClassAverage(Double classAverage) {
		this.classAverage = classAverage;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public User getGradedBy() {
		return gradedBy;
	}

	public void setGradedBy(User gradedBy) {
		this.gradedBy = gradedBy;
	}
    
    
    
    
    
}

