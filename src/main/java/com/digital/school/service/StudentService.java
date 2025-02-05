package com.digital.school.service;

import com.digital.school.model.Student;
import com.digital.school.model.User;

import java.util.List;

public interface StudentService {

    List<Student> getStudentsByClasseId(Long classId); // Récupère les étudiants d'une classe donnée


    User getStudentById(long l);
}