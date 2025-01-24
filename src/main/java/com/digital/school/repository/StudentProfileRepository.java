package com.digital.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.digital.school.model.StudentProfile;
import com.digital.school.model.User;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {
    Optional<StudentProfile> findByStudent(User student);
    List<StudentProfile> findByGender(String gender);
    List<StudentProfile> findByNationality(String nationality);
    List<StudentProfile> findByHasAllergiesTrue();
    
    @Query("SELECT sp FROM StudentProfile sp WHERE sp.birthDate BETWEEN :start AND :end")
    List<StudentProfile> findByBirthDateBetween(
        @Param("start") LocalDate start, 
        @Param("end") LocalDate end
    );
    
    @Query("SELECT sp FROM StudentProfile sp WHERE sp.specialNeeds IS NOT NULL AND sp.specialNeeds <> ''")
    List<StudentProfile> findWithSpecialNeeds();
    
    boolean existsByStudent(User student);
}