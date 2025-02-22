package com.digital.school.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("HOMEWORK")
public class Homework extends Evaluation {

    @Column(columnDefinition = "TEXT")
    private String description;

    // Autres champs spécifiques au devoir si nécessaire

    // Getters et setters
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
