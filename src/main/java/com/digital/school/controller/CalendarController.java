package com.digital.school.controller;

import com.digital.school.dto.EventDTO;
import com.digital.school.model.Classe;
import com.digital.school.model.Event;
import com.digital.school.model.enumerated.EventType;
import com.digital.school.model.Subject;
import com.digital.school.model.User;
import com.digital.school.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/calendar")
public class CalendarController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CalendarController.class);

    @Autowired
    private EventService eventService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private UserService userService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private ParentService parentService;

    @Autowired
    private ProfessorService professorService;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private ClasseService classeService;

    @Autowired
    private RoomService roomService;

    /**
     * Affiche la vue du calendrier en injectant dans le modèle les listes de classes, matières et salles.
     */
    @GetMapping
    public String showCalendar(Model model, @AuthenticationPrincipal User user) {
        model.addAttribute("classes", classeService.findAllBasicInfo());
        model.addAttribute("subjects", subjectService.findAllBasicInfo());
        model.addAttribute("rooms", roomService.findAllBasicInfo());
        model.addAttribute("user", user);
        return "calendar";
    }

    /**
     * Charge les événements dans une plage de dates, renvoyés sous forme de Map pour le calendrier.
     */
    @GetMapping("/api/events")
    public ResponseEntity<List<Map<String, Object>>> getEvents(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @AuthenticationPrincipal User user) {
        LOGGER.debug("/api/events GET: start={}, end={}", start, end);
        List<Event> events = eventService.findEventsByDateRange(start, end, user);
        LOGGER.debug("/api/events GET: found {} events", events.size());

        List<Map<String, Object>> eventMaps = events.stream().map(event -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", event.getId());
            map.put("title", event.getTitle());
            map.put("startTime", event.getStartTime());
            map.put("endTime", event.getEndTime());
            map.put("allDay", event.isAllDay());
            map.put("location", event.getLocation());
            map.put("type", event.getType());
            map.put("description", event.getDescription());
            map.put("duration", event.getDuration());
            map.put("subjectName", event.getSubject() != null ? event.getSubject().getName() : null);
            map.put("roomName", event.getRoom() != null ? event.getRoom().getName() : null);
            map.put("classeName", event.getClasse() != null ? event.getClasse().getName() : null);
            if (event.getSubject() != null) {
                map.put("subject", Map.of(
                        "id", event.getSubject().getId(),
                        "name", event.getSubject().getName()
                ));
            }
            if (event.getRoom() != null) {
                map.put("room", Map.of(
                        "id", event.getRoom().getId(),
                        "name", event.getRoom().getName()
                ));
            }
            if (event.getClasse() != null) {
                map.put("classe", Map.of(
                        "id", event.getClasse().getId(),
                        "name", event.getClasse().getName()
                ));
            }
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(eventMaps);
    }

    /**
     * Crée un nouvel événement.
     */
    @PostMapping("/api/events/create")
    @ResponseBody
    public ResponseEntity<?> createEvent(@RequestBody EventDTO eventDTO, @AuthenticationPrincipal User user) {
        LOGGER.debug("Création d'un événement : {}", eventDTO);
        try {
            Event event = new Event();
            event.setTitle(eventDTO.getTitle());
            event.setType(EventType.valueOf(eventDTO.getType()));
            event.setStartTime(LocalDateTime.parse(eventDTO.getStartTime()));
            event.setEndTime(LocalDateTime.parse(eventDTO.getEndTime()));
            event.setLocation(eventDTO.getLocation());
            event.setOnline(eventDTO.isOnline());
            event.setDescription(eventDTO.getDescription());
            event.setAllDay(eventDTO.isAllDay());

            // Pour les types COURSE et EXAM
            if (List.of("COURSE", "EXAM").contains(eventDTO.getType())) {
                if (eventDTO.getSubject() != null) {
                    Long subjectId = Long.parseLong(eventDTO.getSubject().getId());
                    Subject subject = subjectService.findById(subjectId)
                            .orElseThrow(() -> new RuntimeException("Matière non trouvée"));
                    event.setSubject(subject);
                }
                if (eventDTO.getClasse() != null) {
                    Long classeId = Long.parseLong(eventDTO.getClasse().getId());
                    Classe classe = classeService.findById(classeId)
                            .orElseThrow(() -> new RuntimeException("Classe non trouvée"));
                    event.setClasse(classe);
                    // Ajouter tous les élèves de la classe
                    event.setParticipants(new HashSet<>(studentService.getStudentsByClasseId(classeId)));
                    // Ajouter le professeur associé à la matière pour cette classe
                    professorService.findProfessorsByClasseId(classeId).stream()
                            .filter(prof -> prof.getSubjects().contains(event.getSubject()))
                            .forEach(event.getParticipants()::add);
                }
            } else if ("MEETING".equals(eventDTO.getType())) {
                // Pour les réunions, gérer les participants
                List<String> participantStrings = eventDTO.getParticipants();
                List<Long> numericParticipants = new ArrayList<>();
                boolean special = false;
                if (participantStrings != null) {
                    for (String s : participantStrings) {
                        try {
                            numericParticipants.add(Long.parseLong(s));
                        } catch (NumberFormatException ex) {
                            special = true;
                        }
                    }
                }
                if (special) {
                    if (participantStrings != null && participantStrings.contains("ALL_PROFESSORS")) {
                        event.setParticipants(new HashSet<>(professorService.findAll()));
                    }
                    // Vous pouvez gérer d'autres cas spéciaux ici.
                } else {
                    event.setParticipants(new HashSet<>(userService.findUsersByIds(numericParticipants)));
                }
            } else {
                // Pour les autres types d'événements, gérer les participants via participantType
                handleParticipants(eventDTO, event);
            }

            event.setCreatedBy(user);
            Event savedEvent = eventService.save(event);
            LOGGER.debug("Événement créé avec succès : {}", savedEvent);
            return ResponseEntity.ok(eventDTO);
        } catch (Exception e) {
            LOGGER.error("Erreur lors de la création de l'événement", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Gère les participants en fonction du type d'événement.
     */
    private void handleParticipants(EventDTO eventDTO, Event event) {
        if (eventDTO.getParticipantType() != null) {
            switch (eventDTO.getParticipantType()) {
                case "ALL_STUDENTS":
                    event.setParticipants(new HashSet<>(studentService.findAll()));
                    break;
                case "ALL_PROFESSORS":
                    event.setParticipants(new HashSet<>(professorService.findAll()));
                    break;
                case "ALL_PARENTS":
                    LOGGER.debug("cas ALL_PARENTS size List parents"+parentService.findAll());
                    event.setParticipants(new HashSet<>(parentService.findAll()));
                case "SPECIFIC_PROFESSORS":
                case "STUDENT_GROUP":
                    List<Long> participantIds = eventDTO.getParticipants().stream()
                            .map(s -> Long.parseLong(s))
                            .collect(Collectors.toList());
                    event.setParticipants(new HashSet<>(userService.findUsersByIds(participantIds)));
                    break;
                case "CLASS":
                    if (eventDTO.getClasse() != null && eventDTO.getClasse().getId() != null) {
                        try {
                            Long classeId = Long.parseLong(eventDTO.getClasse().getId());
                            event.setParticipants(new HashSet<>(studentService.getStudentsByClasseId(classeId)));
                        } catch (NumberFormatException e) {
                            throw new RuntimeException("ID de la classe invalide : " + eventDTO.getClasse().getId(), e);
                        }
                    }
                    break;
                case "ALL_ADMIN":
                    event.setParticipants(new HashSet<>(adminService.findAll()));
                    break;
            }
        }
    }

    /**
     * Met à jour un événement existant.
     */
    @PutMapping("/api/events/{id}")
    @ResponseBody
    public ResponseEntity<?> updateEvent(@PathVariable Long id,
                                         @RequestBody Event event,
                                         @AuthenticationPrincipal User user) {
        try {
            event.setId(id);
            Event updatedEvent = eventService.update(event);
            return ResponseEntity.ok(updatedEvent);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Supprime un événement existant.
     */
    @DeleteMapping("/api/events/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteEvent(@PathVariable Long id,
                                         @AuthenticationPrincipal User user) {
        try {
            eventService.delete(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
