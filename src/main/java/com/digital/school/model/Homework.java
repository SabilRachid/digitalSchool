package com.digital.school.model;

import com.digital.school.model.Document;
import com.digital.school.model.Evaluation;
import jakarta.persistence.*;
import org.hibernate.annotations.Where;
import java.util.HashSet;
import java.util.Set;

@Entity
@DiscriminatorValue("homeworks")
@Table(name = "homeworks")
@PrimaryKeyJoinColumn(name = "id")
public class Homework extends Evaluation {

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = true)
    private int submittedCount = 0;

    // Association avec Document (ici, la table "documents" contient un champ "related_entity_id"
    // et "related_entity_type" qui doit être égal à 'HOMEWORK' pour les documents liés aux devoirs)
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_entity_id", referencedColumnName = "id", insertable = false, updatable = false)
    @Where(clause = "related_entity_type = 'HOMEWORK'")
    private Set<Document> documents = new HashSet<>();

    // Getters et setters

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

    public Set<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(Set<Document> documents) {
        this.documents = documents;
    }

    @Override
    public String toString() {
        return "Homework{" +
                "description='" + description + '\'' +
                ", submittedCount=" + submittedCount +
                ", documents=" + documents +
                "} " + super.toString();
    }
}
