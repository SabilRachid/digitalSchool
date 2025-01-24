package com.digital.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.digital.school.model.Level;
import com.digital.school.model.enumerated.LevelName;
import java.util.List;
import java.util.Optional;

public interface LevelRepository extends JpaRepository<Level, Long> {
    boolean existsByCode(String code);
    
    boolean existsByLevelName(LevelName levelName);
    
    Optional<Level> findByLevelName(LevelName levelName);
    
    List<Level> findAllByOrderByOrderAsc();
    
    List<Level> findByCycleOrderByOrder(String cycle);
    
    @Query("SELECT COUNT(c) FROM Classe c WHERE c.level.id = ?1")
    long countClasses(Long levelId);
    
    @Query("SELECT l FROM Level l JOIN l.subjects s WHERE s.id = ?1 ORDER BY l.order")
    List<Level> findBySubjectId(Long subjectId);
    
    @Query("SELECT MIN(l.order) FROM Level l WHERE l.cycle = ?1")
    Integer findMinOrderByCycle(String cycle);
    
    @Query("SELECT MAX(l.order) FROM Level l WHERE l.cycle = ?1")
    Integer findMaxOrderByCycle(String cycle);
}