package com.digital.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.digital.school.model.ParentStudent;
import com.digital.school.model.User;
import java.util.List;
import java.util.Optional;

public interface ParentStudentRepository extends JpaRepository<ParentStudent, Long> {
    List<ParentStudent> findByParent(User parent);
    List<ParentStudent> findByStudent(User student);
    List<ParentStudent> findByRelationship(String relationship);
    List<ParentStudent> findByPrimaryContactTrue();
    
    Optional<ParentStudent> findByParentAndStudent(User parent, User student);
    
    @Query("SELECT ps FROM ParentStudent ps WHERE ps.student = :student AND ps.emergencyContact = true")
    List<ParentStudent> findEmergencyContactsByStudent(@Param("student") User student);
    
    @Query("SELECT ps FROM ParentStudent ps WHERE ps.parent = :parent AND ps.validated = true")
    List<ParentStudent> findValidatedAssociationsByParent(@Param("parent") User parent);
    
    boolean existsByParentAndStudent(User parent, User student);
}