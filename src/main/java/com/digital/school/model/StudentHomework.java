package com.digital.school.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "student_homework")
public class StudentHomework {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student;
    
    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;
    
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private String status; // PENDING, SUBMITTED, GRADED
    private String submissionPath;
    private Double grade;
    private String feedback;
    
    public StudentHomework() {
    }
    
    @ManyToOne
    @JoinColumn(name = "assigned_by_id")
    private User assignedBy;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocalDateTime getDueDate() {
		return dueDate;
	}

	public void setDueDate(LocalDateTime dueDate) {
		this.dueDate = dueDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSubmissionPath() {
		return submissionPath;
	}

	public void setSubmissionPath(String submissionPath) {
		this.submissionPath = submissionPath;
	}

	public Double getGrade() {
		return grade;
	}

	public void setGrade(Double grade) {
		this.grade = grade;
	}

	public String getFeedback() {
		return feedback;
	}

	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}

	public User getAssignedBy() {
		return assignedBy;
	}

	public void setAssignedBy(User assignedBy) {
		this.assignedBy = assignedBy;
	}
    
    
    
    
}

