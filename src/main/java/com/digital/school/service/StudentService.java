package com.digital.school.service;

import com.digital.school.dto.UserDTO;
import com.digital.school.model.Student;
import com.digital.school.model.User;

import java.util.List;
import java.util.Optional;

public interface StudentService {

    List<Student> findAll();
    List<Student> getStudentsByClasseId(Long classId); // Récupère les étudiants d'une classe donnée
    List<UserDTO> getStudentsDtoByClasseId(Long classId);
    User getStudentById(long l);
    Optional<Student> findById(long studentId);

   
}