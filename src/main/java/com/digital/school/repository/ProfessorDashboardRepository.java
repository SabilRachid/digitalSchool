package com.digital.school.repository;

import com.digital.school.model.Classe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProfessorDashboardRepository extends JpaRepository<Classe, Long> {

        @Query("SELECT c.name, AVG(g.value) FROM StudentGrade g JOIN g.student s JOIN s.classe c GROUP BY c.name")
        List<Object[]> getClassPerformance();

        @Query("SELECT CASE " +
                "WHEN g.value BETWEEN 0 AND 5 THEN '0-5' " +
                "WHEN g.value BETWEEN 5 AND 8 THEN '5-8' " +
                "WHEN g.value BETWEEN 8 AND 10 THEN '8-10' " +
                "WHEN g.value BETWEEN 10 AND 12 THEN '10-12' " +
                "WHEN g.value BETWEEN 12 AND 15 THEN '12-15' " +
                "WHEN g.value BETWEEN 15 AND 18 THEN '15-18' " +
                "ELSE '18-20' END AS gradeRange, COUNT(g) " +
                "FROM StudentGrade g GROUP BY gradeRange ORDER BY gradeRange")
        List<Object[]> getGradesDistribution();

        @Query("SELECT " +
                "SUM(CASE WHEN p.level = 'HIGH' THEN 1 ELSE 0 END) AS high, " +
                "SUM(CASE WHEN p.level = 'MEDIUM' THEN 1 ELSE 0 END) AS medium, " +
                "SUM(CASE WHEN p.level = 'LOW' THEN 1 ELSE 0 END) AS low " +
                "FROM Participation p")
        List<Object[]> getParticipationRate();

        @Query("SELECT TO_CHAR(g.date, 'TMMon', 'fr_FR'), AVG(g.value) FROM StudentGrade g " +
                "WHERE g.date >= :startDate " +
                "GROUP BY TO_CHAR(g.date, 'TMMon', 'fr_FR') " +
                "ORDER BY MIN(g.date)")
        List<Object[]> getAverageProgression(@Param("startDate") LocalDate startDate);
}
