package com.digital.school.config;

public final class Constants {
    // Rôles
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_PROFESSOR = "ROLE_PROFESSOR";
    public static final String ROLE_STUDENT = "ROLE_STUDENT";
    public static final String ROLE_PARENT = "ROLE_PARENT";

    // Statuts
    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_INACTIVE = "INACTIVE";
    public static final String STATUS_PENDING = "PENDING";

    // Pagination
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;

    // Cache
    public static final String CACHE_USERS = "users";
    public static final String CACHE_ROLES = "roles";
    public static final long CACHE_TTL = 3600; // 1 heure

    // Messages
    public static final String ERROR_NOT_FOUND = "Ressource non trouvée";
    public static final String ERROR_UNAUTHORIZED = "Non autorisé";
    public static final String ERROR_VALIDATION = "Erreur de validation";

    private Constants() {
        // Empêche l'instanciation
    }
}