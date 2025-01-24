package com.digital.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.digital.school.model.ParentProfile;
import com.digital.school.model.User;
import java.util.List;
import java.util.Optional;

public interface ParentProfileRepository extends JpaRepository<ParentProfile, Long> {
    Optional<ParentProfile> findByParent(User parent);
    List<ParentProfile> findByProfession(String profession);
    List<ParentProfile> findByMaritalStatus(String maritalStatus);
    List<ParentProfile> findByPreferredContactMethod(String contactMethod);
    
    @Query("SELECT pp FROM ParentProfile pp WHERE pp.workAddress LIKE %:city%")
    List<ParentProfile> findByWorkAddressCity(@Param("city") String city);
    
    boolean existsByParent(User parent);
}