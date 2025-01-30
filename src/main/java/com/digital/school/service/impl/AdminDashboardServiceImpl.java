package com.digital.school.service.impl;

import com.digital.school.service.AdminDashboardService;

@Service
public class AdminDashboardServiceImpl implements AdminDashboardService {
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
