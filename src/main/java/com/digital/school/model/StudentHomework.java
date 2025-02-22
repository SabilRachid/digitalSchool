package com.digital.school.model;


import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;

@Entity
@DiscriminatorValue("homework")
public class StudentHomework extends StudentSubmission {

	@ManyToOne(optional = false)
	@JoinColumn(name = "homework_id")
	private Homework homework;

	// Document associé à la soumission (facultatif)
	// (Si nécessaire, vous pouvez garder l'annotation @OneToOne ici.)
	@ManyToOne
	@JoinColumn(name = "document_id")
	private Document document;

	// Getters et setters

	public Homework getHomework() {
		return homework;
	}

	public void setHomework(Homework homework) {
		this.homework = homework;
	}


	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}
}
