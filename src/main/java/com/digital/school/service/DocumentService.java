package com.digital.school.service;

import com.digital.school.model.Document;
import com.digital.school.model.Parent;
import com.digital.school.model.Student;
import com.digital.school.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface DocumentService {

	// Méthodes générales (pour tous types d'utilisateurs)

	List<Map<String, Object>> findAllAsMap();

	Optional<Document> findById(Long id);

	Document save(Document document);

	Document update(Long id, Map<String, Object> resourceData);
	/**
	 * Upload générique : le document est créé à partir du fichier, du type, d'une catégorie et d'un uploader.
	 * Les associations facultatives peuvent concerner un étudiant et/ou un parent.
	 */
	Document upload(MultipartFile file, String type, String category, User uploader, Student student, Parent parent);

	ResponseEntity<?> downloadDocument(Long id);

	ResponseEntity<?> previewDocument(Long id);

	Document validate(Long id);

	/**
	 * Validation avec indication de l’utilisateur validateur
	 */
	Document validate(Long id, User validator);

	void deleteById(Long id);

	List<Document> findAll();

	List<Document> findByStudent(Student student);

	List<Document> findByParent(Parent parent);

	List<Document> findByCategory(String category);

	List<Document> findByType(String type);

	Document findByStudentAndType(Student student, String type);

	boolean existsByStudentAndType(Student student, String type);

	boolean existsByParentAndType(Parent parent, String type);

	List<Document> findByParentAndType(Parent parent, String type);

	// Méthodes spécifiques aux ressources / documents gérés par Admin & Professeur
	List<Map<String, Object>> findAllAsMapForProfessor();

	/**
	 * Upload spécifique pour les professeurs, par exemple avec association à un cours.
	 */
	Document uploadForProfessor(MultipartFile file, String type, Long courseId, String description);


	// Méthodes de génération de documents
	Object generateCertificate(Long studentId, String title, String comments);

	Object generateAttestation(Long studentId, String title, String comments);

	// Envoi de document par email ou autre (par exemple)
	void sendDocument(Long id, Map<String, Object> request);
}
