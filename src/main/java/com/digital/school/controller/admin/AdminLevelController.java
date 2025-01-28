package com.digital.school.controller.admin;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.digital.school.model.Level;
import com.digital.school.service.LevelService;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/levels")
public class AdminLevelController {

    @Autowired
    private LevelService levelService;

    @GetMapping
    public String showLevels(HttpServletRequest request, Model model) {
        model.addAttribute("currentURI", request.getRequestURI());
        return "admin/levels";
    }

    @GetMapping("/data")
    @ResponseBody
    public List<Map<String, Object>> getLevelsData() {
        return levelService.findAllAsMap();
    }

    @GetMapping("/list")
    @ResponseBody
    public List<Map<String, Object>> getLevelsList() {
        return levelService.findAllBasicInfo();
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Level> getLevel(@PathVariable Long id) {
        return levelService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/maxOrder")
    @ResponseBody
    public ResponseEntity<Integer> getMaxOrder(@RequestParam String cycle) {
        return ResponseEntity.ok(levelService.findMaxOrderByCycle(cycle));
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<?> createLevel(@RequestBody Level level) {
        try {
            Level savedLevel = levelService.save(level);
            return ResponseEntity.ok(savedLevel);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Erreur lors de la création: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> updateLevel(@PathVariable Long id, @RequestBody Level level) {
        try {
            if (!levelService.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            level.setId(id);
            Level updatedLevel = levelService.save(level);
            return ResponseEntity.ok(updatedLevel);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Erreur lors de la mise à jour: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteLevel(@PathVariable Long id) {
        try {
            levelService.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}