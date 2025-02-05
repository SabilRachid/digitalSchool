package com.digital.school.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import com.digital.school.model.Attendance;
import com.digital.school.model.Course;
import com.digital.school.model.User;
import com.digital.school.model.enumerated.AttendanceStatus;
import com.digital.school.repository.AttendanceRepository;
import com.digital.school.service.AttendanceService;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttendanceServiceImpl implements AttendanceService {

	@Autowired 
	AttendanceRepository attendanceRepository;

    @Override
    public void save(List<Attendance> attendanceList) {
        attendanceRepository.saveAll(attendanceList); // Enregistre la liste des présences
    }

    @Override
    public Page<Attendance> findAll(Pageable pageable) {
        return attendanceRepository.findAll(pageable);
    }

    @Override
    public List<Attendance> findAll() {
        return attendanceRepository.findAll();
    }

    @Override
    public Optional<Attendance> findById(Long id) {
        return attendanceRepository.findById(id);
    }

    @Override
    @Transactional
    public Attendance save(Attendance attendance) {
        return attendanceRepository.save(attendance);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        attendanceRepository.deleteById(id);
    }

    @Override
    public List<Attendance> findByStudent(User student) {
        return attendanceRepository.findByStudent(student);
    }

    @Override
    public List<Attendance> findByCourse(Course course) {
        return attendanceRepository.findByCourse(course);
    }

    @Override
    public List<Attendance> findByStudentAndDateRange(User student, LocalDateTime start, LocalDateTime end) {
        return attendanceRepository.findByStudentAndRecordedAtBetween(student, start, end);
    }

    @Override
    public List<Attendance> findByCourseAndDateRange(Course course, LocalDateTime start, LocalDateTime end) {
        return attendanceRepository.findByCourseAndRecordedAtBetween(course, start, end);
    }

    @Override
    public double getAttendanceRate(User student, Course course) {
        long totalSessions = attendanceRepository.countByStudentAndCourse(student, course);
        if (totalSessions == 0) {
            return 0.0;
        }

        long presentSessions = attendanceRepository.countByStudentAndStatusAndCourse(
            student, AttendanceStatus.PRESENT, course);
        long lateSessions = attendanceRepository.countByStudentAndStatusAndCourse(
            student, AttendanceStatus.RETARD, course);

        return ((double) (presentSessions + lateSessions) / totalSessions) * 100;
    }

    
    public Map<String, Double> getClassAttendanceStats(Course course) {
        Map<String, Double> stats = new HashMap<>();
        List<Object[]> attendanceStats = attendanceRepository.getAttendanceStatsByCourse(course);
        long totalAttendances = 0;

        // Calculate total attendances
        for (Object[] stat : attendanceStats) {
            Long count = (Long) stat[1];
            totalAttendances += count;
        }

        if (totalAttendances == 0) {
            stats.put("presentRate", 0.0);
            stats.put("absentRate", 0.0);
            stats.put("lateRate", 0.0);
            stats.put("excusedRate", 0.0);
            return stats;
        }

        // Calculate rates for each status
        for (Object[] stat : attendanceStats) {
            AttendanceStatus status = (AttendanceStatus) stat[0];
            Long count = (Long) stat[1];
            double rate = ((double) count / totalAttendances) * 100;

            switch (status) {
                case PRESENT:
                    stats.put("presentRate", rate);
                    break;
                case ABSENT:
                    stats.put("absentRate", rate);
                    break;
                case RETARD:
                    stats.put("lateRate", rate);
                    break;
                case EXCUSE:
                    stats.put("excusedRate", rate);
                    break;
            }
        }

        // Ensure all rates are present in the map
        stats.putIfAbsent("presentRate", 0.0);
        stats.putIfAbsent("absentRate", 0.0);
        stats.putIfAbsent("lateRate", 0.0);
        stats.putIfAbsent("excusedRate", 0.0);

        return stats;
    }

    @Override
    public List<Attendance> getAttendancesByCourseAndDate(Course course, LocalDate date) {
        return attendanceRepository.findByCourseAndDate(course, date);
    }

    @Override
    public void updateAttendance(Long id, AttendanceStatus status) {
        Attendance attendance = attendanceRepository.findById(id).orElseThrow(() -> new RuntimeException("Présence non trouvée"));
        attendance.setStatus(status);
        attendanceRepository.save(attendance);
    }
}