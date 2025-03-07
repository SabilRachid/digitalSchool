package com.digital.school.model;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("homeworks")
@Table(name = "homeworks")
@PrimaryKeyJoinColumn(name = "id")
public class Homework extends Evaluation {

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = true)
    private int submittedCount=0;

    // Getters and setters
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSubmittedCount() {
        return submittedCount;
    }

    public void setSubmittedCount(int submittedCount) {
        this.submittedCount = submittedCount;
    }

    @Override
    public String toString() {
        return "Homework{" +
                "description='" + description + '\'' +
                "submittedCount=" + submittedCount +
                "} " + super.toString();
    }
}
