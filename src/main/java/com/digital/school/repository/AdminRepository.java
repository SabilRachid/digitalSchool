package com.digital.school.repository;

import com.digital.school.model.Administrator;
import com.digital.school.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public interface AdminRepository extends JpaRepository<Administrator, Long> {
    Administrator findByUsername(String username);
    Administrator findByEmail(String email);
    Administrator findByUsernameOrEmail(String username, String email);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
}
