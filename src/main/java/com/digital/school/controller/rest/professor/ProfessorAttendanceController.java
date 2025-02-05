package com.digital.school.controller.rest.professor;


import com.digital.school.model.Attendance;
import com.digital.school.model.Course;
import com.digital.school.model.Student;
import com.digital.school.model.enumerated.AttendanceStatus;
import com.digital.school.service.AttendanceService;
import com.digital.school.service.CourseService;
import com.digital.school.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/professor/attendances")
public class ProfessorAttendanceController {


        @Autowired
        private AttendanceService attendanceService;

        @Autowired
        private StudentService studentService;

        @Autowired
        private CourseService courseService;

        @GetMapping("/mark")
        public String showAttendanceForm(Model model) {
            model.addAttribute("classes", courseService.findAll());
            return "professor/mark-attendance";
        }

        @GetMapping("/students/{classId}")
        @ResponseBody
        public List<Student> getStudentsByClass(@PathVariable Long classId) {
            return studentService.getStudentsByClasseId(classId);
        }

        @PostMapping("/save")
        public String saveAttendance(@RequestParam Long courseId, @RequestParam LocalDate date, @RequestParam Map<String, String> attendances) {
            List<Attendance> attendanceList = new ArrayList<>();
            Course course = courseService.findById(courseId).orElse(null);

            attendances.forEach((studentId, status) -> {
                Attendance attendance = new Attendance();
                attendance.setStudent(studentService.getStudentById(Long.parseLong(studentId)));
                attendance.setCourse(course);
                attendance.setDate(date);
                attendance.setStatus(AttendanceStatus.valueOf(status));
                attendanceList.add(attendance);
            });

            attendanceService.save(attendanceList);
            return "redirect:professor/attendances/list";
        }



}
