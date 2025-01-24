package com.digital.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.digital.school.model.Permission;
import com.digital.school.model.enumerated.PermissionName;
import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByName(PermissionName name);
}