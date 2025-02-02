package com.digital.school.controller.rest;

import com.digital.school.dto.ParticipationDto;
import com.digital.school.model.Participation;
import com.digital.school.service.ParticipationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/api/participations")
public class ParticipationRestController {

    private final ParticipationService participationService;

    public ParticipationRestController(ParticipationService participationService) {
        this.participationService = participationService;
    }

    /**
     * Récupérer toutes les participations
     */
    @GetMapping
    public ResponseEntity<List<ParticipationDto>> getAllParticipations() {
        List<ParticipationDto> participations = participationService.getAllParticipations()
                .stream()
                .map(ParticipationDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(participations);
    }

    /**
     * Récupérer les participations d'une classe et d'une matière
     */
    @GetMapping("/by-class-subject")
    public ResponseEntity<List<ParticipationDto>> getParticipationsByClassAndSubject(
            @RequestParam Long classId,
            @RequestParam Long subjectId) {
        List<ParticipationDto> participations = participationService.getParticipationsByClassAndSubject(classId, subjectId)
                .stream()
                .map(ParticipationDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(participations);
    }

    /**
     * Enregistrer une nouvelle participation
     */
    @PostMapping
    public ResponseEntity<ParticipationDto> createParticipation(@RequestBody ParticipationDto participationDto) {
        Participation savedParticipation = participationService.saveParticipation(participationDto.toEntity());
        return ResponseEntity.ok(ParticipationDto.fromEntity(savedParticipation));
    }

    /**
     * Mettre à jour une participation existante
     */
    @PutMapping("/{id}")
    public ResponseEntity<ParticipationDto> updateParticipation(
            @PathVariable Long id,
            @RequestBody ParticipationDto participationDto) {
        Participation updatedParticipation = participationService.updateParticipation(id, participationDto.toEntity());
        return ResponseEntity.ok(ParticipationDto.fromEntity(updatedParticipation));
    }

    /**
     * Supprimer une participation
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParticipation(@PathVariable Long id) {
        participationService.deleteParticipation(id);
        return ResponseEntity.noContent().build();
    }
}
