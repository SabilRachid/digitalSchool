package com.digital.school.service;

import com.digital.school.model.Parent;
import com.digital.school.model.Student;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import com.digital.school.model.Document;
import com.digital.school.model.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface DocumentService {
    List<Map<String, Object>> findAllAsMap();
    Optional<Document> findById(Long id);
    Document save(Document document);
    Document upload(MultipartFile file, String type, String category, Long studentId, Long parentId);
    ResponseEntity<?> download(Long id);
    Document validate(Long id);
    void deleteById(Long id);
	List<Document> findAll();
	List<Document> findByStudent(Student student);
	List<Document> findByParent(Parent parent);
	List<Document> findByCategory(String category);
	List<Document> findByType(String type);
	List<Document> findByStudentAndType(Student student, String type);
	Document validate(Long id, User validator);
	boolean existsByStudentAndType(Student student, String type);
	boolean existsByParentAndType(Parent parent, String type);
	List<Document> findByParentAndType(Parent parent, String type);
	Document upload(MultipartFile file, String type, String category, User uploader, Student student, Parent parent);

	ResponseEntity<?> downloadDocument(Long id);

	ResponseEntity<?> previewDocument(Long id);

	Object generateCertificate(Long studentId, String title, String comments);

	Object generateAttestation(Long studentId, String title, String comments);

	void sendDocument(Long id, Map<String, Object> request);
}