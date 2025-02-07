package com.digital.school.service;

import com.digital.school.model.Bulletin;
import java.util.List;

public interface BulletinService {
    List<Bulletin> findAll();
    Bulletin generateBulletin(Long classeId, String period, String title, String comments);
    void generateAllBulletins();
    byte[] generatePDF(Long bulletinId);
}
