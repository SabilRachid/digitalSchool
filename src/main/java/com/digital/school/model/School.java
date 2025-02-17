package com.digital.school.model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "schools")
public class School extends AuditableEntity {


    // Informations de l'établissement
    @Column(name = "name", nullable = false)
    private String name;

    // Informations de l'établissement
    @Column(name = "code", nullable = false)
    private String code;

    @Column(nullable = false)
    private String address;

    @Column(name = "groupe_scolaire")
    private String groupeScolaire;

    private String city;

    // Dirigeants
    @Column(name = "dirigeant1")
    private String dirigeant1;

    @Column(name = "phone_dirigeant1")
    private String phoneDirigeant1;

    @Column(name = "email_dirigeant1")
    private String emailDirigeant1;

    @Column(name = "dirigeant2")
    private String dirigeant2;

    @Column(name = "phone_dirigeant2")
    private String phoneDirigeant2;

    @Column(name = "email_dirigeant2")
    private String emailDirigeant2;

    // Directeur scolaire
    @Column(name = "directeur_scolaire")
    private String directeurScolaire;

    @Column(name = "phone_directeur_scolaire")
    private String phoneDirecteurScolaire;

    @Column(name = "email_directeur_scolaire")
    private String emailDirecteurScolaire;

    // Informations générales de contact
    @Column(name = "establishment_phone")
    private String establishmentPhone;

    @Column(name = "establishment_email")
    private String establishmentEmail;

    // Paramètres académiques et administratifs
    @Column(name = "academic_year")
    private String academicYear;

    @Column(name = "tuition_fee")
    private Double tuitionFee;

    @Column(name = "grading_scale")
    private String gradingScale;

    @Column(name = "max_students_per_class")
    private Integer maxStudentsPerClass;

    @Column(name = "opening_hours")
    private String openingHours;

    @Column(name = "website_url")
    private String websiteUrl;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "theme_color")
    private String themeColor;

    @Column(name = "social_media_links", columnDefinition = "TEXT")
    private String socialMediaLinks;



    @Column(name = "library_loan_period")
    private Integer libraryLoanPeriod;

    @Column(name = "exam_duration")
    private Integer examDuration;

    @Column(name = "attendance_policy", columnDefinition = "TEXT")
    private String attendancePolicy;

    @Column(name = "parent_meeting_frequency")
    private String parentMeetingFrequency;

    // Getters and setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getGroupeScolaire() { return groupeScolaire; }
    public void setGroupeScolaire(String groupeScolaire) { this.groupeScolaire = groupeScolaire; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getDirigeant1() { return dirigeant1; }
    public void setDirigeant1(String dirigeant1) { this.dirigeant1 = dirigeant1; }

    public String getPhoneDirigeant1() { return phoneDirigeant1; }
    public void setPhoneDirigeant1(String phoneDirigeant1) { this.phoneDirigeant1 = phoneDirigeant1; }

    public String getEmailDirigeant1() { return emailDirigeant1; }
    public void setEmailDirigeant1(String emailDirigeant1) { this.emailDirigeant1 = emailDirigeant1; }

    public String getDirigeant2() { return dirigeant2; }
    public void setDirigeant2(String dirigeant2) { this.dirigeant2 = dirigeant2; }

    public String getPhoneDirigeant2() { return phoneDirigeant2; }
    public void setPhoneDirigeant2(String phoneDirigeant2) { this.phoneDirigeant2 = phoneDirigeant2; }

    public String getEmailDirigeant2() { return emailDirigeant2; }
    public void setEmailDirigeant2(String emailDirigeant2) { this.emailDirigeant2 = emailDirigeant2; }

    public String getDirecteurScolaire() { return directeurScolaire; }
    public void setDirecteurScolaire(String directeurScolaire) { this.directeurScolaire = directeurScolaire; }

    public String getPhoneDirecteurScolaire() { return phoneDirecteurScolaire; }
    public void setPhoneDirecteurScolaire(String phoneDirecteurScolaire) { this.phoneDirecteurScolaire = phoneDirecteurScolaire; }

    public String getEmailDirecteurScolaire() { return emailDirecteurScolaire; }
    public void setEmailDirecteurScolaire(String emailDirecteurScolaire) { this.emailDirecteurScolaire = emailDirecteurScolaire; }

    public String getEstablishmentPhone() { return establishmentPhone; }
    public void setEstablishmentPhone(String establishmentPhone) { this.establishmentPhone = establishmentPhone; }

    public String getEstablishmentEmail() { return establishmentEmail; }
    public void setEstablishmentEmail(String establishmentEmail) { this.establishmentEmail = establishmentEmail; }

    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }

    public Double getTuitionFee() { return tuitionFee; }
    public void setTuitionFee(Double tuitionFee) { this.tuitionFee = tuitionFee; }

    public String getGradingScale() { return gradingScale; }
    public void setGradingScale(String gradingScale) { this.gradingScale = gradingScale; }

    public Integer getMaxStudentsPerClass() { return maxStudentsPerClass; }
    public void setMaxStudentsPerClass(Integer maxStudentsPerClass) { this.maxStudentsPerClass = maxStudentsPerClass; }

    public String getOpeningHours() { return openingHours; }
    public void setOpeningHours(String openingHours) { this.openingHours = openingHours; }

    public String getWebsiteUrl() { return websiteUrl; }
    public void setWebsiteUrl(String websiteUrl) { this.websiteUrl = websiteUrl; }

    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }

    public String getThemeColor() { return themeColor; }
    public void setThemeColor(String themeColor) { this.themeColor = themeColor; }

    public String getSocialMediaLinks() { return socialMediaLinks; }
    public void setSocialMediaLinks(String socialMediaLinks) { this.socialMediaLinks = socialMediaLinks; }

    public Integer getLibraryLoanPeriod() { return libraryLoanPeriod; }
    public void setLibraryLoanPeriod(Integer libraryLoanPeriod) { this.libraryLoanPeriod = libraryLoanPeriod; }

    public Integer getExamDuration() { return examDuration; }
    public void setExamDuration(Integer examDuration) { this.examDuration = examDuration; }

    public String getAttendancePolicy() { return attendancePolicy; }
    public void setAttendancePolicy(String attendancePolicy) { this.attendancePolicy = attendancePolicy; }

    public String getParentMeetingFrequency() { return parentMeetingFrequency; }
    public void setParentMeetingFrequency(String parentMeetingFrequency) { this.parentMeetingFrequency = parentMeetingFrequency; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof School)) return false;
        School that = (School) o;
        return getId() != null && getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
