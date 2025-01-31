package com.digital.school.repository;

import com.digital.school.model.enumerated.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import com.digital.school.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    @Query("SELECT r.name, COUNT(u) FROM User u JOIN u.roles r " +
            "WHERE r.name IN (:roles) " +
            "GROUP BY r.name")
    List<Object[]> countUsersByRole(@Param("roles") List<RoleName> roles);

    @Query("SELECT COUNT(u) FROM User u")
    Long countUsers();
}