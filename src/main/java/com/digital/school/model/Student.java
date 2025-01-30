package com.digital.school.model;

import jakarta.persistence.*;


@Entity
@Table(name = "students")
@PrimaryKeyJoinColumn(name = "user_id")
public class Student extends User {

    private String specialNeeds; // Besoins spécifiques

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Parent parent; // Parent de l'élève

    @ManyToOne
    @JoinColumn(name = "class_id")
    private Classe classe; // Classe de l'élève

    public String getSpecialNeeds() {
        return specialNeeds;
    }

    public void setSpecialNeeds(String specialNeeds) {
        this.specialNeeds = specialNeeds;
    }

    public Parent getParent() {
        return parent;
    }

    public void setParent(Parent parent) {
        this.parent = parent;
    }

    public Classe getClasse() {
        return classe;
    }

    public void setClasse(Classe classe) {
        this.classe = classe;
    }
}

