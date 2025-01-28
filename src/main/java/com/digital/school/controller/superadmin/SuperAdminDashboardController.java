package com.digital.school.controller.superadmin;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.digital.school.service.SchoolService;
import java.util.Map;

@Controller
@RequestMapping("/superadmin")
public class SuperAdminDashboardController {

    @Autowired
    private SchoolService schoolService;

    @GetMapping("/dashboard")
    public String dashboard(HttpServletRequest request, Model model) {
        model.addAttribute("currentURI", request.getRequestURI());
        model.addAttribute("recentSchools", schoolService.findRecentlyCreated());
        return "superadmin/dashboard";
    }

    @GetMapping("/dashboard/stats")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(schoolService.getSchoolStatistics());
    }
}