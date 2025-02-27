package com.digital.school.service.impl;

import com.digital.school.model.Administrator;
import com.digital.school.repository.AdminRepository;
import com.digital.school.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminServiceimpl implements AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Override
    public List<Administrator> findAll() {
        return adminRepository.findAll();
    }
}
