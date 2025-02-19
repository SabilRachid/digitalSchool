package com.digital.school.controller.rest.professor;

import com.digital.school.model.Homework;
import com.digital.school.model.Professor;
import com.digital.school.model.User;
import com.digital.school.service.HomeworkService;
import com.digital.school.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/professor/api/homeworks")
public class ProfessorHomeworkController {

    @Autowired
    private HomeworkService homeworkService;

    @Autowired
    private CourseService courseService;


    @GetMapping("/data")
    @ResponseBody
    public List<Map<String, Object>> getHomeworksData() {
        return homeworkService.findAllAsMap();

    }

    /* Affiche les détails d'un devoir */
    @GetMapping("/{id}")
    public String viewHomework(@PathVariable Long id, Model model) {
        Homework homework = homeworkService.findById(id)
                .orElseThrow(() -> new RuntimeException("Devoir non trouvé"));
        model.addAttribute("homework", homework);
        return "professor/homework-details";
    }

    /* Ajoute un nouveau devoir */
    @PostMapping("/add")
    public String addHomework(@AuthenticationPrincipal Professor professor, @ModelAttribute Homework homework, RedirectAttributes redirectAttributes) {
        homeworkService.createHomework(professor, homework);
        redirectAttributes.addFlashAttribute("successMessage", "Devoir ajouté avec succès !");
        return "redirect:/professor/homework";
    }

    /* Supprime un devoir */
    @PostMapping("/{id}/delete")
    public String deleteHomework(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        homeworkService.deleteHomework(id);
        redirectAttributes.addFlashAttribute("successMessage", "Devoir supprimé avec succès !");
        return "redirect:/professor/homework";
    }

    /* Modifie un devoir */
    @PostMapping("/{id}/edit")
    public String editHomework(@PathVariable Long id, @ModelAttribute Homework updatedHomework, RedirectAttributes redirectAttributes) {
        Optional<Homework> existingHomework = homeworkService.findById(id);

        if (existingHomework.isPresent()) {
            homeworkService.updateHomework(existingHomework.get(), updatedHomework);
            redirectAttributes.addFlashAttribute("successMessage", "Devoir mis à jour !");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Le devoir n'existe pas.");
        }

        return "redirect:/professor/homework";
    }
}
