package com.digital.school.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.digital.school.dto.UserDTO;
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
    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    @Override
    public List<Student> getStudentsByClasseId(Long classId) {
        return studentRepository.getStudentsByClasseId(classId);
    }

    @Override
    public List<UserDTO> getStudentsDtoByClasseId(Long classId) {
        List<Student> students = studentRepository.getStudentsByClasseId(classId);
        return students.stream().map(s -> {
            UserDTO dto = new UserDTO();
            dto.setId(s.getId());
            dto.setFirstName(s.getFirstName());
            dto.setLastName(s.getLastName());
            dto.setEmail(s.getEmail());
            dto.setUsername(s.getUsername());
            // Pour un étudiant, renseigner la classe
            if (s.getClasse() != null) {
                dto.setClasseId(s.getClasse().getId());
            }
            return dto;
        }).collect(Collectors.toList());
    }


    @Override
    public User getStudentById(long l) {
        return studentRepository.findById(l).orElse(null);
    }

    @Override
    public Optional<Student> findById(long studentId) {
        return studentRepository.findById(studentId);
    }


}
