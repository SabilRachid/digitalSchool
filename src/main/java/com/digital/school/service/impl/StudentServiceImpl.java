package com.digital.school.service.impl;

import java.util.List;

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
    public User getStudentById(long l) {
        return studentRepository.findById(l).orElse(null);
    }


}
