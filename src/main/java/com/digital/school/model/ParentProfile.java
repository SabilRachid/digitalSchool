package com.digital.school.model;

import jakarta.persistence.*;

@Entity
@Table(name = "parent_profiles")
public class ParentProfile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "parent_id", nullable = false)
    private User parent;
    
    private String profession;
    
    @Column(name = "work_phone")
    private String workPhone;
    
    @Column(name = "work_address")
    private String workAddress;
    
    @Column(name = "preferred_contact_method")
    private String preferredContactMethod;
    
    @Column(name = "preferred_contact_time")
    private String preferredContactTime;
    
    @Column(name = "marital_status")
    private String maritalStatus;
    
    @Column(name = "additional_info", columnDefinition = "TEXT")
    private String additionalInfo;

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getParent() {
        return parent;
    }

    public void setParent(User parent) {
        this.parent = parent;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getWorkPhone() {
        return workPhone;
    }

    public void setWorkPhone(String workPhone) {
        this.workPhone = workPhone;
    }

    public String getWorkAddress() {
        return workAddress;
    }

    public void setWorkAddress(String workAddress) {
        this.workAddress = workAddress;
    }

    public String getPreferredContactMethod() {
        return preferredContactMethod;
    }

    public void setPreferredContactMethod(String preferredContactMethod) {
        this.preferredContactMethod = preferredContactMethod;
    }

    public String getPreferredContactTime() {
        return preferredContactTime;
    }

    public void setPreferredContactTime(String preferredContactTime) {
        this.preferredContactTime = preferredContactTime;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }
}