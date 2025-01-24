package com.digital.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.digital.school.model.Role;
import com.digital.school.model.enumerated.RoleName;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}