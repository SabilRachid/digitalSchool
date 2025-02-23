package com.digital.school.model;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("homework")
@Table(name = "homeworks")
@PrimaryKeyJoinColumn(name = "id")
public class Homework extends Evaluation {

    @Column(columnDefinition = "TEXT")
    private String description;

    // Getters and setters

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
