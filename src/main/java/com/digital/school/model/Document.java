package com.digital.school.model;

import com.digital.school.model.enumerated.DocumentType;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "documents")
public class Document extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Titre du document
    @Column(nullable = false)
    private String name;

    // Type de document sous forme d'enum (ex : PDF, Vidéo, Lien, etc.)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentType type;

    // Description détaillée du document
    @Column(columnDefinition = "TEXT")
    private String description;

    // Contenu du fichier, pour les documents stockés dans la base (optionnel)
    @Lob
    private byte[] fileContent;

    // URL du fichier, pour les ressources hébergées à l'externe
    private String fileUrl;

    // Type MIME du fichier (ex : "application/pdf")
    private String contentType;

    // Association générique pour lier le document à une entité (ex : cours, facture, bulletin, etc.)
    private Long relatedEntityId;
    private String relatedEntityType;

    // Propriétaire du document
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    // Utilisateurs avec lesquels le document est partagé
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "document_shared_with",
            joinColumns = @JoinColumn(name = "document_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> sharedWith = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = true)
    private Course course;

    private String category;

    private long fileSize;

    // Getters et setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DocumentType getType() {
        return type;
    }

    public void setType(DocumentType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getFileContent() {
        return fileContent;
    }

    public void setFileContent(byte[] fileContent) {
        this.fileContent = fileContent;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getRelatedEntityId() {
        return relatedEntityId;
    }

    public void setRelatedEntityId(Long relatedEntityId) {
        this.relatedEntityId = relatedEntityId;
    }

    public String getRelatedEntityType() {
        return relatedEntityType;
    }

    public void setRelatedEntityType(String relatedEntityType) {
        this.relatedEntityType = relatedEntityType;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Set<User> getSharedWith() {
        return sharedWith;
    }

    public void setSharedWith(Set<User> sharedWith) {
        this.sharedWith = sharedWith != null ? sharedWith : new HashSet<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Document)) return false;
        Document document = (Document) o;
        return id != null && id.equals(document.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public long getFileSize() {
        return fileSize;
    }
}
