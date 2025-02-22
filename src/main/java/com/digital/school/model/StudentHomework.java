package com.digital.school.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
@DiscriminatorValue("homework")
public class StudentHomework extends StudentSubmission {

	@ManyToOne(optional = false)
	@JoinColumn(name = "homework_id")
	private Homework homework;

	// Document associé à la soumission (optionnel)
	@ManyToOne
	@JoinColumn(name = "document_id")
	private Document document;

	// Redéfinition pour renvoyer le devoir associé comme évaluation
	@Override
	public Evaluation getEvaluation() {
		return homework;
	}

	@Override
	public void setEvaluation(Evaluation evaluation) {
		this.homework = (Homework) evaluation;
	}

	// Getters et setters spécifiques

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
