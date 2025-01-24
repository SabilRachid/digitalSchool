package com.digital.school.service;

import com.digital.school.model.Role;
import com.digital.school.model.enumerated.RoleName;
import java.util.List;
import java.util.Optional;

public interface RoleService {
    List<Role> findAll();
    Optional<Role> findById(Long id);
    Optional<Role> findByName(RoleName name);
    Role save(Role role);
    void deleteById(Long id);
    boolean existsById(Long id);
    boolean existsByName(RoleName name);
}