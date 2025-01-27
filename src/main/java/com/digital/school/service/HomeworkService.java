package com.digital.school.service;

import com.digital.school.controller.professor.ProfessorController;
import com.digital.school.model.Homework;
import com.digital.school.model.User;
import com.digital.school.repository.HomeworkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class HomeworkService {

    @Autowired

    private HomeworkRepository homeworkRepository;

    public List<Homework> findPendingGradingByProfessor(User professor) {
        return homeworkRepository.findPendingGradingByProfessor(professor);
    }
}