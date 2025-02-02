package com.digital.school.repository;

import com.digital.school.model.Participation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ParticipationRepository extends JpaRepository<Participation, Long> {
    List<Participation> findByStudentId(Long studentId);
    List<Participation> findByCourseId(Long courseId);

    @Query("SELECT p.id, p.student.firstName, p.student.lastName, " +
            "p.course.subject.name, p.course.classe.name, " +
            "p.recordedAt, p.participationType, p.feedback FROM Participation p")
    Collection<Object> findAllProjected();

    @Query("SELECT p.id, p.student.firstName, p.student.lastName, " +
            "p.course.subject.name, p.course.classe.name, " +
            "p.recordedAt, p.participationType, p.feedback " +
            "FROM Participation p WHERE p.course.classe.id = :classId " +
            "AND p.course.subject.id = :subjectId")
    Collection<Object> findByClassAndSubject(Long classId, Long subjectId);
}
