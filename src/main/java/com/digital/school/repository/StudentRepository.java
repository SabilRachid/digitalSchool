package com.digital.school.repository;

import com.digital.school.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findByClasse_Id(Long classId); // Récupérer les élèves d'une classe
}

