package com.digital.school.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.digital.school.dto.ContactRequest;
import com.digital.school.service.SchoolService;
import com.digital.school.service.SubscriptionPlanService;

//import com.digital.school.service.SubscriptionPlanService;
import java.util.Map;

@Controller
public class HomeController {

    @Autowired
    private SchoolService schoolService;

    @Autowired
    private SubscriptionPlanService subscriptionPlanService;

    @GetMapping("/")
    public String landing(Model model) {
        // Ajouter les statistiques globales
        Map<String, Object> stats = schoolService.getSchoolStatistics();
        model.addAttribute("stats", stats);
        
        // Ajouter les plans d'abonnement
        model.addAttribute("plans", subscriptionPlanService.findAllActive());
        
        return "home";
    }

    @PostMapping("/contact")
    @ResponseBody
    public ResponseEntity<?> submitContactForm(@RequestBody ContactRequest request) {
        try {
            // TODO: Implémenter l'envoi d'email et la sauvegarde du contact
            return ResponseEntity.ok(Map.of("message", "Votre message a été envoyé avec succès."));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Erreur lors de l'envoi du message: " + e.getMessage()));
        }
    }

   
}