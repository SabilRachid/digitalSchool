package com.digital.school.dto;

import java.util.List;

public class EventDTO {
    private String title;
    private String type; // Par exemple, "EXAM", "COURSE", etc.
    private String startTime; // au format ISO, ex: "2025-02-26T15:09"
    private String endTime;
    private String location;
    private String description;
    private boolean allDay;
    private Integer duration; // facultatif (pour un examen)
    private NestedIdDTO subject; // pour cours/examen
    private NestedIdDTO room;    // pour cours/examen
    private NestedIdDTO classe;
    private NestedIdDTO professor;
    private boolean online;// pour cours/examen
    private String participantType;
    private List<String> participants;

    // Getters et setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isAllDay() { return allDay; }
    public void setAllDay(boolean allDay) { this.allDay = allDay; }

    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }

    public NestedIdDTO getSubject() { return subject; }
    public void setSubject(NestedIdDTO subject) { this.subject = subject; }

    public NestedIdDTO getRoom() { return room; }
    public void setRoom(NestedIdDTO room) { this.room = room; }

    public NestedIdDTO getClasse() { return classe; }
    public void setClasse(NestedIdDTO classe) { this.classe = classe; }

    public NestedIdDTO getProfessor() { return professor; }
    public void setProfessor(NestedIdDTO professor) { this.professor = professor; }

    public boolean isOnline() { return online; }

    public void setOnline(boolean online) { this.online = online; }

    // Getters et Setters
    public String getParticipantType() { return participantType; }

    public void setParticipantType(String participantType) { this.participantType = participantType; }

    public List<String> getParticipants() { return participants; }

    public void setParticipants(List<String> participants) { this.participants = participants; }

}
