package com.digital.school.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.digital.school.model.User;
import com.digital.school.model.enumerated.RoleName;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserService {
    Page<User> findAll(Pageable pageable);
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    User save(User user);
    void deleteById(Long id);
    boolean existsById(Long id);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    List<User> findByRole(RoleName roleName);
    List<Map<String, Object>> findProfessorsAsMap();

}