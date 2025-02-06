package com.digital.school.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import com.digital.school.dto.StudentDTO;
import com.digital.school.model.User;
import com.digital.school.repository.StudentRepository;
import com.digital.school.service.StudentService;
import com.digital.school.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Override
    public List<Student> getStudentsByClasseId(Long classId) {
        return studentRepository.getStudentsByClasseId(classId);
    }

    @Override
    public List<StudentDTO> getStudentsDtoByClasseId(Long classId) {
        List<Student> students = studentRepository.getStudentsByClasseId(classId);
        return students.stream()
                .map(s -> new StudentDTO(s.getId(), s.getFirstName(), s.getLastName()))
                .collect(Collectors.toList());
    }


    @Override
    public User getStudentById(long l) {
        return studentRepository.findById(l).orElse(null);
    }


}
