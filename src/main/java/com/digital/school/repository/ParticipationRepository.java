package com.digital.school.repository;

import com.digital.school.model.Participation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ParticipationRepository extends JpaRepository<Participation, Long> {
    List<Participation> findByStudentId(Long studentId);
    List<Participation> findByCourseId(Long courseId);
}
