package com.digital.school.repository;

import com.digital.school.model.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SchoolRepository extends JpaRepository<School, Long> {

    Optional<School> findByCode(String code);

    @Query("SELECT s FROM School s ORDER BY s.auditable.updated DESC")
    List<School> findRecentlyCreatedSchools();


    @Query("SELECT COUNT(s) FROM School s")
    int countActiveSchools();

    @Override
    List<School> findAll();
}
