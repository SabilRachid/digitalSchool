package com.digital.school.service;

import com.digital.school.model.Assignment;
import java.util.List;

public interface AssignmentService {
    List<Assignment> getAssignmentsByCourse(Long courseId);
    Assignment createAssignment(Assignment assignment);
    void deleteAssignment(Long id);
}
