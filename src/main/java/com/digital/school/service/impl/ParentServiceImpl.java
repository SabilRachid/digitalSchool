package com.digital.school.service.impl;

import com.digital.school.model.Administrator;
import com.digital.school.model.Parent;
import com.digital.school.repository.AdminRepository;
import com.digital.school.repository.ParentRepository;
import com.digital.school.service.ParentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParentServiceImpl implements ParentService {
    @Autowired
    private ParentRepository parentRepository;

    @Override
    public List<Parent> findAll() {

        return parentRepository.findAll();
    }
}
