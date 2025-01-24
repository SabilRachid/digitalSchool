package com.digital.school.controller.superadmin;

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
    public String dashboard(Model model) {
        model.addAttribute("recentSchools", schoolService.findRecentlyCreated());
        return "superadmin/dashboard";
    }

    @GetMapping("/dashboard/stats")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(schoolService.getSchoolStatistics());
    }
}