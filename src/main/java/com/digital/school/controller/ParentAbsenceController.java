package com.digital.school.controller;

```java
package com.digital.school.controller.parent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.digital.school.model.User;
import com.digital.school.service.ParentAbsenceService;

import java.util.Map;

@Controller
@RequestMapping("/parent/absences")
public class ParentAbsenceController {

    @Autowired
    private ParentAbsenceService absenceService;

    @GetMapping
    public String showAbsences(@AuthenticationPrincipal User parent, Model model) {
        model.addAttribute("children", absenceService.getChildrenAbsences(parent));
        return "parent/absences";
    }

    @PostMapping("/{absenceId}/justify")
    @ResponseBody
    public ResponseEntity<?> justifyAbsence(
            @PathVariable Long absenceId,
            @RequestParam("file") MultipartFile file,
            @RequestParam String reason,
            @AuthenticationPrincipal User parent) {
        try {
            absenceService.submitJustification(absenceId, file, reason, parent);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/stats/{childId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getChildStats(@PathVariable Long childId) {
        return ResponseEntity.ok(absenceService.getChildAbsenceStats(childId));
    }
}
```