package com.digital.school.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "student_profiles")
public class StudentProfile extends AuditableEntity {
    
    @OneToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student;
    
    @Column(name = "birth_date")
    private LocalDate birthDate;
    
    @Column(name = "birth_place")
    private String birthPlace;
    
    private String nationality;
    
    private String gender;
    
    @Column(name = "photo_path")
    private String photoPath;
    
    @Column(name = "medical_info", columnDefinition = "TEXT")
    private String medicalInfo;
    
    @Column(name = "emergency_contact")
    private String emergencyContact;
    
    @Column(name = "emergency_phone")
    private String emergencyPhone;
    
    @Column(name = "blood_type")
    private String bloodType;
    
    @Column(name = "has_allergies")
    private boolean hasAllergies;
    
    @Column(name = "allergies_details", columnDefinition = "TEXT")
    private String allergiesDetails;
    
    @Column(name = "special_needs", columnDefinition = "TEXT")
    private String specialNeeds;

    // Getters and setters

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getBirthPlace() {
        return birthPlace;
    }

    public void setBirthPlace(String birthPlace) {
        this.birthPlace = birthPlace;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public String getMedicalInfo() {
        return medicalInfo;
    }

    public void setMedicalInfo(String medicalInfo) {
        this.medicalInfo = medicalInfo;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public String getEmergencyPhone() {
        return emergencyPhone;
    }

    public void setEmergencyPhone(String emergencyPhone) {
        this.emergencyPhone = emergencyPhone;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public boolean isHasAllergies() {
        return hasAllergies;
    }

    public void setHasAllergies(boolean hasAllergies) {
        this.hasAllergies = hasAllergies;
    }

    public String getAllergiesDetails() {
        return allergiesDetails;
    }

    public void setAllergiesDetails(String allergiesDetails) {
        this.allergiesDetails = allergiesDetails;
    }

    public String getSpecialNeeds() {
        return specialNeeds;
    }

    public void setSpecialNeeds(String specialNeeds) {
        this.specialNeeds = specialNeeds;
    }
}