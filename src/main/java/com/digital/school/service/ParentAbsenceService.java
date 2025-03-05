package com.digital.school.service;

import com.digital.school.model.Document;
import com.digital.school.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface ParentAbsenceService {

    /**
     * Récupère les absences de tous les enfants d'un parent sous forme de liste de maps.
     */
    List<Map<String, Object>> getChildrenAbsences(User parent);

    /**
     * Permet à un parent de soumettre une justification pour un enregistrement individuel d'absence (StudentAttendance).
     * Le paramètre studentAttendanceId correspond à l'ID de l'enregistrement individuel.
     */
    void submitJustification(Long studentAttendanceId, MultipartFile file, String reason, User parent);

    /**
     * Retourne les statistiques d'absence pour un enfant identifié par son ID.
     */
    Map<String, Object> getChildAbsenceStats(Long childId);

    /**
     * Vérifie que le parent est autorisé à justifier l'absence identifiée par studentAttendanceId.
     */
    boolean canJustifyAbsence(Long studentAttendanceId, User parent);
}
