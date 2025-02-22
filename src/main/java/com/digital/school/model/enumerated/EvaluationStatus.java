package com.digital.school.model.enumerated;

/**
 * Représente le statut global d'un devoir tel que défini par le professeur.
 */
public enum EvaluationStatus {
    /**  * Le devoir est en cours de préparation et n'est pas encore publié*/
    DRAFT,

    /*** Le devoir est publié et accessible aux étudiants.*/
    PUBLISHED,

    /*** Le devoir est clôturé (la date d'échéance est passée).*/
    CLOSED,

    SCHEDULED,
    /*** Le devoir a été annulé par le professeur.*/
    CANCELED
}
