package com.digital.school.model.enumerated;

public enum LevelName {
    // École primaire
    CP("Cours Préparatoire", "Primaire", 1),
    CE1("Cours Élémentaire 1", "Primaire", 2),
    CE2("Cours Élémentaire 2", "Primaire", 3),
    CM1("Cours Moyen 1", "Primaire", 4),
    CM2("Cours Moyen 2", "Primaire", 5),
    
    // Collège
    SIXIEME("Sixième", "Collège", 6),
    CINQUIEME("Cinquième", "Collège", 7),
    QUATRIEME("Quatrième", "Collège", 8),
    TROISIEME("Troisième", "Collège", 9),
    
    // Lycée
    SECONDE("Seconde", "Lycée", 10),
    PREMIERE("Première", "Lycée", 11),
    TERMINALE("Terminale", "Lycée", 12);

    private final String displayName;
    private final String cycle;
    private final int order;

    LevelName(String displayName, String cycle, int order) {
        this.displayName = displayName;
        this.cycle = cycle;
        this.order = order;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getCycle() {
        return cycle;
    }

    public int getOrder() {
        return order;
    }

    public static LevelName fromDisplayName(String displayName) {
        for (LevelName level : values()) {
            if (level.getDisplayName().equalsIgnoreCase(displayName)) {
                return level;
            }
        }
        throw new IllegalArgumentException("Niveau non trouvé: " + displayName);
    }

    public static LevelName[] getByCycle(String cycle) {
        return java.util.Arrays.stream(values())
            .filter(level -> level.getCycle().equalsIgnoreCase(cycle))
            .toArray(LevelName[]::new);
    }
}