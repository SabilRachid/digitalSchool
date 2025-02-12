package com.digital.school.service;

import com.digital.school.model.ParentStudent;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ParentStudentService {
    List<Map<String, Object>> findAllAsMap();
    Optional<ParentStudent> findById(Long id);
    ParentStudent save(ParentStudent association);
    void deleteById(Long id);
    ParentStudent validate(Long id);
    List<ParentStudent> findByParent(Long parentId);
    List<ParentStudent> findByStudent(Long studentId);
    List<ParentStudent> findByValidationStatus(boolean validated);
    
    List<Map<String, Object>> getAssociationsByClass(Long classId);
    Map<String, Object> getAssociationById(Long id);
    void saveAssociation(Map<String, Object> associationData);
    void updateAssociation(Long id, Map<String, Object> associationData);

    void associateStudentToParents(Long studentId, List<Long> parentIds);
}