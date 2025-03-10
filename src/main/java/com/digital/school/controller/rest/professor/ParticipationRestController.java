package com.digital.school.controller.rest.professor;

import com.digital.school.dto.ParticipationDTO;
import com.digital.school.model.Participation;
import com.digital.school.service.ParticipationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/api/participations")
public class ParticipationRestController {

    @Autowired
    private ParticipationService participationService;

    public ParticipationRestController(ParticipationService participationService) {
        this.participationService = participationService;
    }


    /**
     * Récupérer les participations d'une classe et d'une matière
     */
    @GetMapping("/by-class-subject")
    public ResponseEntity<List<ParticipationDTO>> getParticipationsByClassAndSubject(
            @RequestParam Long classId,
            @RequestParam Long subjectId) {
        List<ParticipationDTO> participations = participationService.getParticipationsByClassIdAndSubjectId(classId, subjectId).stream()
                .collect(Collectors.toList());
        return ResponseEntity.ok(participations);
    }

    //créé moi la methode fromEntity


    /**
     * Mettre à jour une participation existante
     */
    @PutMapping("/{id}")
    public ResponseEntity<ParticipationDTO> updateParticipation(
            @PathVariable Long id,
            @RequestBody ParticipationDTO participationDto) {
        Participation updatedParticipation = participationService.updateParticipation(id, participationDto.toEntity());
        //donne moi toEntity

        return ResponseEntity.ok(ParticipationDTO.fromEntity(updatedParticipation));
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
