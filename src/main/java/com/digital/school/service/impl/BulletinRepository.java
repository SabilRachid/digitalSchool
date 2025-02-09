package com.digital.school.service.impl;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.digital.school.model.Bulletin;

import java.util.List;
import java.util.Optional;

@Repository
public interface BulletinRepository extends JpaRepository<Bulletin, Integer> {

    Optional<Bulletin> findById(Long id);
    List<Bulletin> findByTitle(String title);
    List<Bulletin> findByPeriod(String period);
    List<Bulletin> findByComments(String comments);
    List<Bulletin> findByClasseId(Long classeId);
    List<Bulletin> findByClasseIdAndPeriod(Long classeId, String period);
    List<Bulletin> findByClasseIdAndTitle(Long classeId, String title);
    List<Bulletin> findByClasseIdAndComments(Long classeId, String comments);
    List<Bulletin> findByClasseIdAndPeriodAndTitle(Long classeId, String period, String title);
    List<Bulletin> findByClasseIdAndPeriodAndComments(Long classeId, String period, String comments);

}
