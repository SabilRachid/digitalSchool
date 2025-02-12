package com.digital.school.controller.rest.parent;



import com.digital.school.model.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.digital.school.model.User;
import com.digital.school.service.ParentDashboardService;

import java.util.Map;

@Controller
@RequestMapping("/parent/api/dashboard")
public class ParentDashboardController {

    @Autowired
    private ParentDashboardService dashboardService;



}