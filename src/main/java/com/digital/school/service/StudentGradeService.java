package com.digital.school.service;

import com.digital.school.model.StudentGrade;

import java.util.List;

public interface StudentGradeService {

    StudentGrade assignGrade(StudentGrade studentGrade); // Attribuer une note avec grade

    List<StudentGrade> getGradesByStudent(Long studentId); // Récupérer les notes d'un étudiant

    List<StudentGrade> getGradesBySubject(Long subjectId); // Récupérer les notes d'une matière

}
