package com.digital.school.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.digital.school.model.*;
import com.digital.school.repository.*;
import com.digital.school.service.BulletinService;
import com.digital.school.service.PDFService;
import com.digital.school.service.StorageService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class BulletinServiceImpl implements BulletinService {

    @Autowired
    private BulletinRepository bulletinRepository;

    @Autowired
    private ClasseRepository classeRepository;

    @Autowired
    private StudentGradeRepository gradeRepository;

    @Autowired
    private PDFService pdfService;

    @Autowired
    private StorageService storageService;

    @Override
    public List<Bulletin> findAll() {
        return bulletinRepository.findAll();
    }

    @Override
    @Transactional
    public Bulletin generateBulletin(Long classeId, String period, String title, String comments) {
        Classe classe = classeRepository.findById(classeId)
                .orElseThrow(() -> new RuntimeException("Classe non trouvée"));

        period="1";
        // Récupérer les notes de la période
        List<StudentGrade> grades = gradeRepository.findByClasseAndPeriod(classe.getId(), period);

        // Calculer les moyennes
        Map<String, Object> data = calculateAverages(grades);

        // Générer le PDF
        byte[] pdfContent = pdfService.generateBulletin(data);

        // Sauvegarder le fichier
        String filePath = storageService.storeFile(pdfContent, generateBulletinFileName(classe, period));


        // Créer le bulletin
        Bulletin bulletin = new Bulletin();
        bulletin.setClasse(classe);
        bulletin.setPeriod(period);
        bulletin.setTitle(title);
        bulletin.setComments(comments);
        bulletin.setFilePath(filePath);
        bulletin.setGeneratedAt(LocalDateTime.now());

        return bulletinRepository.save(bulletin);
    }

    @Override
    @Transactional
    public void generateAllBulletins() {
        classeRepository.findAll().forEach(classe -> {
            try {
                generateBulletin(
                        classe.getId(),
                        getCurrentPeriod(),
                        "Bulletin " + getCurrentPeriod(),
                        null
                );
            } catch (Exception e) {
                // Log error but continue with other classes
                e.printStackTrace();
            }
        });
    }

    @Override
    public byte[] generatePDF(Long bulletinId) {
        Bulletin bulletin = bulletinRepository.findById(bulletinId)
                .orElseThrow(() -> new RuntimeException("Bulletin non trouvé"));

        //al
        try {
            return storageService.loadAsResource(bulletin.getFilePath()).getInputStream().readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, Object> calculateAverages(List<StudentGrade> grades) {
        Map<String, Object> data = new HashMap<>();

        // Calcul des moyennes par élève et par matière
        Map<Long, Map<String, List<Double>>> gradesByStudent = new HashMap<>();
        grades.forEach(grade -> {
            gradesByStudent
                    .computeIfAbsent(grade.getStudent().getId(), k -> new HashMap<>())
                    .computeIfAbsent(grade.getSubject().getName(), k -> new ArrayList<>())
                    .add(grade.getValue().doubleValue());
        });

        // Calcul des moyennes
        Map<Long, Map<String, Double>> averagesByStudent = new HashMap<>();
        gradesByStudent.forEach((studentId, subjectGrades) -> {
            Map<String, Double> averages = new HashMap<>();
            subjectGrades.forEach((subject, values) -> {
                double average = values.stream()
                        .mapToDouble(Double::doubleValue)
                        .average()
                        .orElse(0.0);
                averages.put(subject, average);
            });
            averagesByStudent.put(studentId, averages);
        });

        data.put("averages", averagesByStudent);

        // Calcul des moyennes de classe par matière
        Map<String, Double> classAverages = new HashMap<>();
        grades.stream()
                .collect(Collectors.groupingBy(
                        grade -> grade.getSubject().getName(),
                        Collectors.averagingDouble(grade -> grade.getValue().doubleValue())
                ))
                .forEach(classAverages::put);

        data.put("classAverages", classAverages);

        return data;
    }

    private String generateBulletinFileName(Classe classe, String period) {
        return String.format("bulletin_%s_%s_%s.pdf",
                classe.getName().toLowerCase().replace(" ", "_"),
                period.toLowerCase(),
                LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"))
        );
    }

    private String getCurrentPeriod() {
        int month = LocalDateTime.now().getMonthValue();
        if (month >= 9 && month <= 11) return "TRIMESTER1";
        if (month >= 12 || month <= 2) return "TRIMESTER2";
        if (month >= 3 && month <= 6) return "TRIMESTER3";
        return "YEAR";
    }
}
