package com.digital.school.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.PrimaryKeyJoinColumn;

@Entity
@Table(name = "secretaries")
@PrimaryKeyJoinColumn(name = "user_id")
public class Secretary extends User {
    // Vous pouvez ajouter des attributs spécifiques au secrétaire ici si nécessaire
}
