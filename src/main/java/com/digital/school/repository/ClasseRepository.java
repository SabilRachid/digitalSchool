package com.digital.school.repository;

import com.digital.school.model.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.digital.school.model.Classe;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClasseRepository extends JpaRepository<Classe, Long> {
    boolean existsByNameAndSchoolYear(String name, String schoolYear);

    @Query("SELECT COUNT(c) FROM Classe c")
    Long countClasses();

    @Query("SELECT (SUM(SIZE(c.students)) * 100.0 / SUM(c.maxStudents)) FROM Classe c")
    Double calculateOccupancyRate();

    @Query("SELECT SUM(c.maxStudents - SIZE(c.students)) FROM Classe c")
    Long countAvailableSeats();

    @Query("SELECT c FROM Classe c WHERE c.level.id = ?1")
    List<Classe> findByLevelId(Long levelId);
    
    @Query("SELECT COUNT(s) FROM Classe c JOIN c.students s WHERE c.id = ?1")
    long countStudents(Long classeId);

    @Query("SELECT c FROM Classe c JOIN FETCH c.subjects WHERE c.id = ?1")
    Classe findByIdWithSubjects(Long id);

    @Query("SELECT c FROM Classe c JOIN c.professors p WHERE p = :professor")
    List<Classe> findByProfessor(@Param("professor") Professor professor);
}