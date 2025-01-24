package com.digital.school.model;

import jakarta.persistence.*;
import com.digital.school.model.enumerated.LevelName;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "levels")
public class Level {
    
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
    
	@Column(nullable = false, unique = true)
	private String code;
    
    @Column(nullable = false)
    private String name;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LevelName levelName;
    
    @Column(nullable = false)
    private String cycle;
    
    @Column(name = "\"order\"", nullable = false)
    private Integer order;
    
    @JsonManagedReference
    @OneToMany(mappedBy = "level")
    private Set<Classe> classes = new HashSet<>();
    
    @ManyToMany
    @JoinTable(
        name = "level_subjects",
        joinColumns = @JoinColumn(name = "level_id"),
        inverseJoinColumns = @JoinColumn(name = "subject_id")
    )
    private Set<Subject> subjects = new HashSet<>();

    public Level() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LevelName getLevelName() {
        return levelName;
    }

    public void setLevelName(LevelName levelName) {
        this.levelName = levelName;
    }

    public String getCycle() {
        return cycle;
    }

    public void setCycle(String cycle) {
        this.cycle = cycle;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Set<Classe> getClasses() {
        return classes;
    }

    public void setClasses(Set<Classe> classes) {
        this.classes = classes != null ? classes : new HashSet<>();
    }

    public Set<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(Set<Subject> subjects) {
        this.subjects = subjects != null ? subjects : new HashSet<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Level level = (Level) o;
        return id != null && id.equals(level.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}