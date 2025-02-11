package com.digital.school.service;

import com.digital.school.dto.StudentDTO;
import com.digital.school.model.Student;
import com.digital.school.model.User;

import java.util.List;
import java.util.Optional;

public interface StudentService {

    List<Student> getStudentsByClasseId(Long classId); // Récupère les étudiants d'une classe donnée
    List<StudentDTO> getStudentsDtoByClasseId(Long classId);
    User getStudentById(long l);
    Optional<Student> findById(long studentId);
}