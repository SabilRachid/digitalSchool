package com.digital.school.service;

import com.digital.school.model.Submission;
import java.util.List;

public interface SubmissionService {
    List<Submission> getSubmissionsByAssignment(Long assignmentId);
    void gradeSubmission(Long submissionId, Double grade, String feedback);
}
