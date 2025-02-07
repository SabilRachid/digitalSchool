package com.digital.school.service;

import java.util.Map;

public interface PDFService {
    byte[] generateBulletin(Map<String, Object> data);
    byte[] generateCertificate(Map<String, Object> data);
    byte[] generateAttestation(Map<String, Object> data);
}
