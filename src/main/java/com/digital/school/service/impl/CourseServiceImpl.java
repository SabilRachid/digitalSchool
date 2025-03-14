package com.digital.school.service.impl;

import com.digital.school.dto.CourseDTO;
import com.digital.school.model.*;
import com.digital.school.model.enumerated.CourseStatus;
import com.digital.school.model.enumerated.AttendanceStatus;
import com.digital.school.model.enumerated.EventType;
import com.digital.school.model.enumerated.StudentAttendanceStatus;
import com.digital.school.repository.*;
import com.digital.school.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private SubjectRepository subjectRepository;
    @Autowired
    private ClasseRepository classeRepository;
    @Autowired
    private ProfessorRepository professorRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private StudentAttendanceRepository studentAttendanceRepository;
    @Autowired
    private AttendanceRepository attendanceRepository;



    @Override
    public Page<Course> findAll(Pageable pageable) {
        return courseRepository.findAll(pageable);
    }

    @Override
    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    @Override
    public Optional<Course> findById(Long id) {
        return courseRepository.findById(id);
    }

    @Override
    public List<Course> findTodaySchedule(Student student) {
        // On suppose que la méthode du repository prend l'ID de l'étudiant et la date d'aujourd'hui.
        return courseRepository.findTodayScheduleByStudent(student.getId(), LocalDate.now());
    }


    @Override
    public void publishCourse(Long courseId, User user) {
        // Récupération du cours
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Cours non trouvé"));

        // Mise à jour du statut du cours
        course.setStatus(CourseStatus.UPCOMING);
        courseRepository.save(course);

        // Création automatique de l'entité Attendance associée au cours
        Attendance attendance = new Attendance();
        attendance.setCourse(course);
        attendance.setStatus(AttendanceStatus.NOT_COMPLETED);
        attendance.setRecordedBy(user);
        // Définir la date de l'attendance sur la date du cours s'il est renseigné, sinon sur aujourd'hui
        attendance.setDateEvent(course.getDate() != null ? course.getDate() : LocalDate.now());

        // Sauvegarde initiale de l'attendance
        attendance = attendanceRepository.save(attendance);

        // Récupération des étudiants de la classe du cours
        List<Student> students = studentRepository.findByClasseId(course.getClasse().getId());
        for (Student student : students) {
            // Créer un enregistrement de présence pour chaque étudiant avec un statut par défaut (ici ABSENT)
            StudentAttendance studentAttendance = new StudentAttendance(student, StudentAttendanceStatus.PUBLISHED);
            // On ajoute cet enregistrement à la fiche d'attendance
            attendance.addStudentAttendance(studentAttendance);
        }
        // Sauvegarde de l'attendance avec les enregistrements de présence associés
        attendance = attendanceRepository.save(attendance);

        // Associer l'attendance au cours et sauvegarder à nouveau
        course.setAttendance(attendance);
        courseRepository.save(course);
    }


    @Override
    public void finalizeCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Cours non trouvé"));
        // Ici, vous pouvez vérifier que la saisie de présence est complète
        // Par exemple, vérifier que pour tous les élèves, la présence a été renseignée.
        // Si tout est validé, le statut passe à COMPLETED.
        course.setStatus(CourseStatus.COMPLETED);
        courseRepository.save(course);
    }


    @Override
    public void recordAttendance(Map<String, Object> attendanceData) {
        // Vérifier que des données ont été reçues et que la clé "courseId" est présente
        if (attendanceData == null || attendanceData.isEmpty() || !attendanceData.containsKey("courseId")) {
            throw new RuntimeException("Aucune donnée d'assiduité reçue.");
        }

        // Extraire l'ID du cours et récupérer le cours
        Long courseId = Long.parseLong(attendanceData.get("courseId").toString());
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Cours non trouvé"));

        // Récupérer ou créer la fiche d'assiduité associée au cours
        Attendance attendanceRecord = course.getAttendance();
        if (attendanceRecord == null) {
            // Créer une nouvelle fiche d'assiduité pour le cours en utilisant la date du cours
            attendanceRecord = new Attendance(course, course.getDate());
            // Vous pouvez initialiser d'autres champs si nécessaire, par exemple le statut par défaut
            attendanceRecord.setStatus(AttendanceStatus.NOT_COMPLETED);
            // Sauvegarder et flusher l'enregistrement pour le rendre persistant
            attendanceRecord = attendanceRepository.saveAndFlush(attendanceRecord);
            // Lier cette fiche d'assiduité au cours et la sauvegarder également
            course.setAttendance(attendanceRecord);
            courseRepository.saveAndFlush(course);
        }

        // Parcourir les clés de attendanceData pour traiter chaque présence
        Attendance finalAttendanceRecord = attendanceRecord;
        attendanceData.forEach((key, value) -> {
            if (key.startsWith("attendance_")) {
                // Extraire l'ID de l'étudiant (ex: "attendance_54")
                String studentIdStr = key.substring("attendance_".length());
                Long studentId = Long.parseLong(studentIdStr);

                // Par défaut, l'étudiant est considéré comme ABSENT
                StudentAttendanceStatus status = StudentAttendanceStatus.ABSENT;
                // Si la case est cochée ("on"), alors il est PRESENT
                if ("on".equals(value.toString())) {
                    status = StudentAttendanceStatus.PRESENT;
                } else {
                    // Sinon, tenter de récupérer un statut explicite (via "status_{studentId}")
                    Object statusObj = attendanceData.get("status_" + studentIdStr);
                    if (statusObj != null) {
                        try {
                            status = StudentAttendanceStatus.valueOf(statusObj.toString().toUpperCase());
                        } catch (Exception ex) {
                            status = StudentAttendanceStatus.ABSENT;
                        }
                    }
                }

                // Récupérer une justification éventuelle
                String justification = "";
                Object justObj = attendanceData.get("justification_" + studentIdStr);
                if (justObj != null) {
                    justification = justObj.toString();
                }

                // Récupérer l'étudiant correspondant
                Student student = studentRepository.findById(studentId)
                        .orElseThrow(() -> new RuntimeException("Étudiant non trouvé : " + studentId));

                // Récupérer ou créer l'enregistrement de présence pour cet étudiant
                StudentAttendance sa = studentAttendanceRepository
                        .findByAttendanceAndStudent(finalAttendanceRecord, student)
                        .orElse(new StudentAttendance(student, status));

                // Mettre à jour les informations de présence
                sa.setStatus(status);
                sa.setJustification(justification);
                sa.setAttendance(finalAttendanceRecord);

                // Sauvegarder l'enregistrement de présence
                studentAttendanceRepository.save(sa);
            }
        });
    }


    @Override
    public CourseDTO createCourse(CourseDTO courseDTO, User user) {
        // Création et remplissage de l'objet Course à partir du DTO
        Course course = new Course();
        course.setTitle(courseDTO.getTitle());
        course.setType(EventType.COURSE);
        // Récupération de la matière
        Subject subject = subjectRepository.findById(courseDTO.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Matière non trouvée"));
        course.setSubject(subject);

        // Récupération du professeur
        Professor professor = professorRepository.findById(courseDTO.getProfessorId())
                .orElseThrow(() -> new RuntimeException("Professeur non trouvé"));
        course.setProfessor(professor);

        // Récupération de la classe
        Classe classe = classeRepository.findById(courseDTO.getClasseId())
                .orElseThrow(() -> new RuntimeException("Classe non trouvée"));
        course.setClasse(classe);

        // Affectation des dates et horaires
        course.setStartTime(courseDTO.getStartTime());
        course.setEndTime(courseDTO.getEndTime());

        // Autres propriétés
        course.setDescription(courseDTO.getDescription());
        course.setStatus(courseDTO.getStatus() != null ? courseDTO.getStatus() : CourseStatus.SCHEDULED);

        // Sauvegarde initiale du cours
        course = courseRepository.save(course);

        return mapToDTO(course);
    }


    private CourseDTO mapToDTO(Course course) {
        CourseDTO dto = new CourseDTO();
        dto.setId(course.getId());
        dto.setTitle(course.getTitle());
        dto.setSubjectId(course.getSubject().getId());
        dto.setProfessorId(course.getProfessor().getId());
        dto.setClasseId(course.getClasse().getId());
        dto.setStartTime(course.getStartTime());
        dto.setEndTime(course.getEndTime());
       // dto.setRoom(course.getRoom());
        dto.setStatus(course.getStatus());
        dto.setDescription(course.getDescription());
        return dto;
    }




    @Override
    public void deleteById(Long id) {
        courseRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return courseRepository.existsById(id);
    }

    @Override
    public List<Course> findByClasse(Classe classe) {
        return courseRepository.findByClasse(classe);
    }

    @Override
    public List<Map<String, Object>> findByProfessor(Professor professor) {
        return courseRepository.findByProfessor(professor).stream()
                .map(course -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", course.getId());
                    map.put("title", course.getTitle());
                    map.put("subject", course.getSubject().getName());
                    map.put("classe", course.getClasse().getName());
                    map.put("startTime", course.getStartTime());
                    map.put("endTime", course.getEndTime());
                    map.put("room", course.getRoom());
                    map.put("description", course.getDescription());
                    map.put("status", course.getStatus().name());
                    return map;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> findAllAsMap() {
        return courseRepository.findAll().stream()
                .map(course -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", course.getId());
                    map.put("subject", Map.of(
                            "id", course.getSubject().getId(),
                            "name", course.getSubject().getName()
                    ));
                    map.put("professor", Map.of(
                            "id", course.getProfessor().getId(),
                            "firstName", course.getProfessor().getFirstName(),
                            "lastName", course.getProfessor().getLastName()
                    ));
                    map.put("class", Map.of(
                            "id", course.getClasse().getId(),
                            "name", course.getClasse().getName()
                    ));
                    map.put("startTime", course.getStartTime());
                    map.put("endTime", course.getEndTime());
                    map.put("room", course.getRoom());
                    map.put("description", course.getDescription());
                    map.put("status", course.getStatus().name());
                    return map;
                })
                .collect(Collectors.toList());
    }


    @Override
    public List<Map<String, Object>> findAllBasicInfo() {
        return courseRepository.findAll().stream()
                .map(course -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", course.getId());
                    map.put("name", String.format("%s - %s",
                            course.getSubject().getName(),
                            course.getClasse().getName()));
                    return map;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Course> findByStudent(Student student) {
        return courseRepository.findByClasse(student.getClasse());
    }

    @Override
    public List<Map<String, Object>> getCourseResourcesAsMap(Long id) {
        // À implémenter selon votre logique de gestion des ressources (fichiers, liens, etc.)
        return List.of();
    }

    @Override
    public Map<String, String> generateMeetingLink(Long id) {
        // Implémenter la génération d'un lien de réunion pour le cours (exemple : via Zoom ou Google Meet)
        return Map.of("meetingLink", "https://meeting.example.com/" + id);
    }

    @Override
    public Object findTodaySchedule(Professor professor) {
        // Implémenter la récupération des cours d'aujourd'hui pour un professeur
        return courseRepository.findTodayScheduleByProfessor(professor.getId(), LocalDate.now());
    }

    @Override
    public Object findTodayCourses() {
        // Par exemple, retourner les cours programmés pour aujourd'hui pour tous les professeurs
        return courseRepository.findTodayCourses(LocalDate.now());
    }

    @Override
    public Course updateCourse(Long id, Course course, Professor professor) {
        Course existingCourse = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cours non trouvé"));
        if (!existingCourse.getProfessor().equals(professor)) {
            throw new RuntimeException("Vous n'êtes pas autorisé à modifier ce cours");
        }
        // Mise à jour des champs essentiels
        existingCourse.setName(course.getName());
        existingCourse.setSubject(course.getSubject());
        existingCourse.setClasse(course.getClasse());
        existingCourse.setDate(course.getDate());
        existingCourse.setStartTime(course.getStartTime());
        existingCourse.setEndTime(course.getEndTime());
        existingCourse.setRoom(course.getRoom());
        existingCourse.setOnlineLink(course.getOnlineLink());
        existingCourse.setDescription(course.getDescription());
        existingCourse.setStatus(course.getStatus());
        return courseRepository.save(existingCourse);
    }


    @Override
    public void deleteCourseById(Long id, Professor professor) {
        Course existingCourse = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cours non trouvé"));
        if (!existingCourse.getProfessor().equals(professor)) {
            throw new RuntimeException("Vous n'êtes pas autorisé à supprimer ce cours");
        }
        courseRepository.delete(existingCourse);
    }

    @Override
    public Course updateCourseRoom(Long id, String newRoom, Professor professor) {
        Course existingCourse = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cours non trouvé"));
        if (!existingCourse.getProfessor().equals(professor)) {
            throw new RuntimeException("Vous n'êtes pas autorisé à modifier ce cours");
        }
        existingCourse.setRoom(roomRepository.findByName(newRoom));
        return courseRepository.save(existingCourse);
    }


    @Override
    public Object findTodayCoursesForStudent(Long studentId) {
        // Implémenter selon vos besoins
        return courseRepository.findTodayCoursesForStudent(studentId, LocalDate.now());
    }

    @Override
    public List<Map<String, Object>> findByProfessorAndFilters(Professor professor, Long classeId, Long subjectId, String startDate, String endDate) {
        // Récupérer d'abord tous les cours du professeur
        List<Course> courses = courseRepository.findByProfessor(professor);

        // Appliquer le filtre sur la classe, si fourni
        if (classeId != null) {
            courses = courses.stream()
                    .filter(course -> course.getClasse() != null && course.getClasse().getId().equals(classeId))
                    .collect(Collectors.toList());
        }

        // Appliquer le filtre sur la matière, si fourni
        if (subjectId != null) {
            courses = courses.stream()
                    .filter(course -> course.getSubject() != null && course.getSubject().getId().equals(subjectId))
                    .collect(Collectors.toList());
        }

        // Appliquer le filtre sur la date de début, si fourni
        if (startDate != null && !startDate.isEmpty()) {
            LocalDate start = LocalDate.parse(startDate);
            courses = courses.stream()
                    .filter(course -> course.getDate() != null && !course.getDate().isBefore(start))
                    .collect(Collectors.toList());
        }

        // Appliquer le filtre sur la date de fin, si fourni
        if (endDate != null && !endDate.isEmpty()) {
            LocalDate end = LocalDate.parse(endDate);
            courses = courses.stream()
                    .filter(course -> course.getDate() != null && !course.getDate().isAfter(end))
                    .collect(Collectors.toList());
        }

        // Transformation des cours en Map<String, Object>
        return courses.stream().map(course -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", course.getId());
            map.put("name", course.getName());
            map.put("subjectName", course.getSubject().getName());
            map.put("classeName", course.getClasse().getName());
            map.put("startTime", course.getStartTime());
            map.put("endTime", course.getEndTime());
            map.put("room", course.getRoom());
            map.put("description", course.getDescription());
            map.put("status", course.getStatus().name());
            // Ajoutez d'autres champs si nécessaire (onlineLink, resourceCount, etc.)
            return map;
        }).collect(Collectors.toList());
    }

    @Override
    public List<Course> findCoursesForProfessorByClassAndDate(Long professorId, Long classId, LocalDate date) {
        return courseRepository.findCoursesForProfessorByClassAndDate(professorId, classId, date);
    }

    @Override
    public List<Course> findCoursesByProfessor(Long professorId, String month, Long classe, Long subject) {
        if (month != null && !month.isEmpty()) {
            // On attend un format "YYYY-MM"
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
            LocalDateTime start = LocalDateTime.parse(month, formatter).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime end = start.plusMonths(1);
            return courseRepository.findByProfessorIdAndClasseAndSubjectAndStartTimeBetween(
                    professorId,
                    classe,
                    subject,
                    start,
                    end
            );
        } else {
            return courseRepository.findByProfessorIdAndClasseAndSubject(professorId, classe, subject);
        }
    }

    @Override
    public Course save(Course course, Administrator administrator) {
        // Implémenter la logique de sauvegarde du cours
        return courseRepository.save(course);
    }


}
