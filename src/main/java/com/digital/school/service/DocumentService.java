package com.digital.school.service;

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
	List<Document> findByStudent(User student);
	List<Document> findByParent(User parent);
	List<Document> findByCategory(String category);
	List<Document> findByType(String type);
	List<Document> findByStudentAndType(User student, String type);
	Document validate(Long id, User validator);
	boolean existsByStudentAndType(User student, String type);
	boolean existsByParentAndType(User parent, String type);
	List<Document> findByParentAndType(User parent, String type);
	Document upload(MultipartFile file, String type, String category, User uploader, User student, User parent);
}