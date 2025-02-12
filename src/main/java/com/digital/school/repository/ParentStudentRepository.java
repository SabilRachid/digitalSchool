package com.digital.school.repository;

import aj.org.objectweb.asm.commons.Remapper;
import com.digital.school.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.digital.school.model.ParentStudent;
import com.digital.school.model.User;
import java.util.List;
import java.util.Optional;

public interface ParentStudentRepository extends JpaRepository<ParentStudent, Long> {
    List<ParentStudent> findByParent(User parent);
    List<ParentStudent> findByStudent(User student);

    List<ParentStudent> findByParentId(Long parentId);

    @Query("SELECT ps FROM ParentStudent ps WHERE ps.student.classe.id = :classId")
    List<ParentStudent> findByStudent_Classe_Id(@Param("classId") Long classId);

    boolean existsByParentAndStudent(User parent, User student);

    Optional<ParentStudent> findByStudentId(Long childId);


    void deleteByStudent(Student student);
}