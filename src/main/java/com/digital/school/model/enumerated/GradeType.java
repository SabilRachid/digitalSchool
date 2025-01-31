package com.digital.school.model.enumerated;

public enum GradeType {
    A(16, 20, "Excellent"),
    B(14, 15.99, "Très Bien"),
    C(12, 13.99, "Bien"),
    D(10, 11.99, "Passable"),
    E(5, 9.99, "Insuffisant"),
    F(0, 4.99, "Échec");

    private final double minScore;
    private final double maxScore;
    private final String description;

    GradeType(double minScore, double maxScore, String description) {
        this.minScore = minScore;
        this.maxScore = maxScore;
        this.description = description;
    }

    public static GradeType fromScore(double score) {
        for (GradeType grade : values()) {
            if (score >= grade.minScore && score <= grade.maxScore) {
                return grade;
            }
        }
        throw new IllegalArgumentException("Score invalide: " + score);
    }

    public String getDescription() {
        return description;
    }
}

