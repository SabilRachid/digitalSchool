package com.digital.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.digital.school.model.School;
import java.util.List;
import java.util.Optional;


public interface SchoolRepository extends JpaRepository<School, Long> {
    Optional<School> findByCode(String code);
    List<School> findByStatus(String status);
    List<School> findByCity(String city);
    List<School> findByCountry(String country);
    
    @Query("SELECT COUNT(s) FROM School s WHERE s.status = 'ACTIVE'")
    long countActiveSchools();
    
    @Query("SELECT s FROM School s WHERE s.status = 'ACTIVE' ORDER BY s.createdAt DESC")
    List<School> findRecentlyCreatedSchools();
}