package com.digital.school.service;

import com.digital.school.model.Student;
import com.digital.school.model.StudentProfile;
import com.digital.school.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface StudentProfileService {
    List<Map<String, Object>> findAllAsMap();
    Optional<StudentProfile> findById(Long id);

    Optional<StudentProfile> findByStudent(Student student);

    StudentProfile save(StudentProfile profile);
    void deleteById(Long id);
    List<StudentProfile> findBySpecialNeeds(boolean hasSpecialNeeds);
    List<StudentProfile> findByClass(Long classId);
	List<StudentProfile> findAll();
	Optional<StudentProfile> findByStudent(User student);
	List<StudentProfile> findByGender(String gender);
	List<StudentProfile> findByNationality(String nationality);
	List<StudentProfile> findByHasAllergies();
	List<StudentProfile> findByBirthDateBetween(LocalDate start, LocalDate end);
	List<StudentProfile> findWithSpecialNeeds();
	boolean existsByStudent(User student);
	StudentProfile updateMedicalInfo(Long id, String medicalInfo, String allergiesDetails, String specialNeeds);
	StudentProfile updateEmergencyContact(Long id, String contact, String phone);
}