package com.digital.school.model;

import jakarta.persistence.*;

import java.util.Optional;

@Entity
@Table(name = "resources")
public class Resource extends AuditableEntity {

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String type; // PDF, Vidéo, Lien, Autre

    @Column(nullable = false)
    private String url; // Chemin du fichier ou lien

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course; // Ressource liée à un cours

    private String description;


    public Resource() {}

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

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }


}
