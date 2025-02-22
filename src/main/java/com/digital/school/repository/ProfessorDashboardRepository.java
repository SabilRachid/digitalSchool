package com.digital.school.repository;

import com.digital.school.model.Classe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProfessorDashboardRepository extends JpaRepository<Classe, Long> {

        @Query("SELECT c.name, AVG(ss.value) " +
                "FROM StudentSubmission ss " +
                "JOIN ss.evaluation e " +
                "JOIN e.classe c " +
                "GROUP BY c.name")
        List<Object[]> getClassPerformance();

        @Query("SELECT CASE " +
                "WHEN ss.value BETWEEN 0 AND 5 THEN '0-5' " +
                "WHEN ss.value BETWEEN 5 AND 8 THEN '5-8' " +
                "WHEN ss.value BETWEEN 8 AND 10 THEN '8-10' " +
                "WHEN ss.value BETWEEN 10 AND 12 THEN '10-12' " +
                "WHEN ss.value BETWEEN 12 AND 15 THEN '12-15' " +
                "WHEN ss.value BETWEEN 15 AND 18 THEN '15-18' " +
                "ELSE '18-20' END AS gradeRange, COUNT(ss) " +
                "FROM StudentSubmission ss " +
                "GROUP BY gradeRange " )
        List<Object[]> getGradesDistribution();

        @Query("SELECT " +
                "SUM(CASE WHEN p.level = 'ACTIVE' THEN 1 ELSE 0 END) AS high, " +
                "SUM(CASE WHEN p.level = 'MEDIUM' THEN 1 ELSE 0 END) AS medium, " +
                "SUM(CASE WHEN p.level = 'LOW' THEN 1 ELSE 0 END) AS low " +
                "FROM Participation p")
        List<Object[]> getParticipationRate();

        @Query("SELECT FUNCTION('TO_CHAR', ss.gradedAt, 'YYYY-MM') AS month, AVG(ss.value) AS average " +
                "FROM StudentSubmission ss " +
                "WHERE ss.gradedAt >= :startDate " +
                "GROUP BY FUNCTION('TO_CHAR', ss.gradedAt, 'YYYY-MM') " )
        List<Object[]> getAverageProgression(@Param("startDate") LocalDateTime startDate);
}
