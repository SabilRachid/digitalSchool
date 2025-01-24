package com.digital.school.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.digital.school.model.StudentProfile;
import com.digital.school.model.User;
import com.digital.school.repository.StudentProfileRepository;
import com.digital.school.service.StudentProfileService;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class StudentProfileServiceImpl implements StudentProfileService {

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Override
    public List<StudentProfile> findAll() {
        return studentProfileRepository.findAll();
    }

    @Override
    public Optional<StudentProfile> findById(Long id) {
        return studentProfileRepository.findById(id);
    }

    @Override
    public Optional<StudentProfile> findByStudent(User student) {
        return studentProfileRepository.findByStudent(student);
    }

    @Override
    @Transactional
    public StudentProfile save(StudentProfile profile) {
        return studentProfileRepository.save(profile);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        studentProfileRepository.deleteById(id);
    }

    @Override
    public List<StudentProfile> findByGender(String gender) {
        return studentProfileRepository.findByGender(gender);
    }

    @Override
    public List<StudentProfile> findByNationality(String nationality) {
        return studentProfileRepository.findByNationality(nationality);
    }

    @Override
    public List<StudentProfile> findByHasAllergies() {
        return studentProfileRepository.findByHasAllergiesTrue();
    }

    @Override
    public List<StudentProfile> findByBirthDateBetween(LocalDate start, LocalDate end) {
        return studentProfileRepository.findByBirthDateBetween(start, end);
    }

    @Override
    public List<StudentProfile> findWithSpecialNeeds() {
        return studentProfileRepository.findWithSpecialNeeds();
    }

    @Override
    public boolean existsByStudent(User student) {
        return studentProfileRepository.existsByStudent(student);
    }

    @Override
    @Transactional
    public StudentProfile updateMedicalInfo(Long id, String medicalInfo, 
                                          String allergiesDetails, String specialNeeds) {
        return studentProfileRepository.findById(id).map(profile -> {
            profile.setMedicalInfo(medicalInfo);
            profile.setAllergiesDetails(allergiesDetails);
            profile.setSpecialNeeds(specialNeeds);
            return studentProfileRepository.save(profile);
        }).orElseThrow(() -> new RuntimeException("Profil étudiant non trouvé"));
    }

    @Override
    @Transactional
    public StudentProfile updateEmergencyContact(Long id, String contact, String phone) {
        return studentProfileRepository.findById(id).map(profile -> {
            profile.setEmergencyContact(contact);
            profile.setEmergencyPhone(phone);
            return studentProfileRepository.save(profile);
        }).orElseThrow(() -> new RuntimeException("Profil étudiant non trouvé"));
    }

	@Override
	public List<Map<String, Object>> findAllAsMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<StudentProfile> findBySpecialNeeds(boolean hasSpecialNeeds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<StudentProfile> findByClass(Long classId) {
		// TODO Auto-generated method stub
		return null;
	}
}
