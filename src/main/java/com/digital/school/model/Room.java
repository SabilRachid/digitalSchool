package com.digital.school.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "rooms")
public class Room extends AuditableEntity {
    
    @Column(nullable = false, unique = true)
    private String name;
    
    @Column(name = "max_capacity")
    private Integer maxCapacity;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @ElementCollection
    @CollectionTable(name = "room_equipment", 
                    joinColumns = @JoinColumn(name = "room_id"))
    @Column(name = "equipment")
    private Set<String> equipment = new HashSet<>();
    
    @Column(nullable = false)
    private String status = "AVAILABLE";
    
    @Column(name = "floor_number")
    private Integer floorNumber;
    
    @Column(name = "building_name")
    private String buildingName;
    
    @Column(name = "is_accessible")
    private boolean accessible;
    
    @Column(name = "maintenance_notes", columnDefinition = "TEXT")
    private String maintenanceNotes;

    // Getters and setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(Integer maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<String> getEquipment() {
        return equipment;
    }

    public void setEquipment(Set<String> equipment) {
        this.equipment = equipment != null ? equipment : new HashSet<>();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getFloorNumber() {
        return floorNumber;
    }

    public void setFloorNumber(Integer floorNumber) {
        this.floorNumber = floorNumber;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public boolean isAccessible() {
        return accessible;
    }

    public void setAccessible(boolean accessible) {
        this.accessible = accessible;
    }

    public String getMaintenanceNotes() {
        return maintenanceNotes;
    }

    public void setMaintenanceNotes(String maintenanceNotes) {
        this.maintenanceNotes = maintenanceNotes;
    }
}