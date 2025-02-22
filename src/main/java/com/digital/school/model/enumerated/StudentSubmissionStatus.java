package com.digital.school.model.enumerated;

/**
 * Représente le statut d'une soumission de devoir par un étudiant.
 */
public enum StudentSubmissionStatus {
    /*** Le devoir a été soumis et est en attente de correction. */
    PENDING,

    /*** Le devoir a été corrigé et la note a été attribuée.   */
    COMPLETED,

    /*** Le devoir a été soumis après la date limite.   */
    LATE
}
