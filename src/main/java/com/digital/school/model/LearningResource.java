package com.digital.school.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "learning_resources")
public class LearningResource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;
    
    private String title;
    private String type; // PDF, VIDEO, LINK
    private String url;
    private String description;
    private LocalDateTime uploadedAt;
    
    @ManyToOne
    @JoinColumn(name = "uploaded_by_id")
    private User uploadedBy;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocalDateTime getUploadedAt() {
		return uploadedAt;
	}

	public void setUploadedAt(LocalDateTime uploadedAt) {
		this.uploadedAt = uploadedAt;
	}

	public User getUploadedBy() {
		return uploadedBy;
	}

	public void setUploadedBy(User uploadedBy) {
		this.uploadedBy = uploadedBy;
	}
    
    
    
    
}

