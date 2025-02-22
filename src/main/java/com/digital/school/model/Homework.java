package com.digital.school.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "homeworks")
@PrimaryKeyJoinColumn(name = "id")
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
