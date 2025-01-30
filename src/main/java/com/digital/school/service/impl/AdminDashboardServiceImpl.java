package com.digital.school.service.impl;

import com.digital.school.repository.SubjectRepository;
import com.digital.school.service.AdminDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminDashboardServiceImpl implements AdminDashboardService {

    @Autowired
    private SubjectRepository subjectRepository;

    @Override
    public List<Map<String, Object>> getProfessorCountBySubject() {
        List<Object[]> results = subjectRepository.findProfessorCountBySubject();
        return results.stream()
                .map(row -> Map.of(
                        "subject", row[0],
                        "count", row[1]
                ))
                .collect(Collectors.toList());
    }
}
