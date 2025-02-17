package com.digital.school.service.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.digital.school.model.Parent;
import com.digital.school.model.Student;
import com.digital.school.repository.ParentRepository;
import com.digital.school.repository.ParentStudentRepository;
import com.digital.school.repository.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digital.school.model.ParentStudent;
import com.digital.school.service.ParentStudentService;

@Service
public class ParentStudentServiceImpl implements ParentStudentService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ParentStudentServiceImpl.class);


	@Autowired
	private StudentRepository studentRepository;

	@Autowired
	private ParentRepository parentRepository;

	@Autowired
	private ParentStudentRepository parentStudentRepository;


	@Override
	public List<Map<String, Object>> findAllAsMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<ParentStudent> findById(Long id) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	@Override
	public ParentStudent save(ParentStudent association) {
		// TODO Auto-generated method stub
		return parentStudentRepository.save(association);
	}

	@Override
	public void deleteById(Long id) {
		parentStudentRepository.deleteById(id);
		
	}

	@Override
	public ParentStudent validate(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ParentStudent> findByParent(Long parentId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ParentStudent> findByStudent(Long studentId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ParentStudent> findByValidationStatus(boolean validated) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<Map<String, Object>> getAssociationsByClass(Long classId) {
		LOGGER.debug("getAssociationsByClass by ClassId="+ classId);
		List<ParentStudent> associations;
		if (classId == null) {
			associations = parentStudentRepository.findAll();
			LOGGER.debug("associations size()="+associations.size());
		} else {
			associations = parentStudentRepository.findByStudent_Classe_Id(classId);
		}

		return associations.stream().map(assoc -> {
			Map<String, Object> map = new HashMap<>();
			Student student = assoc.getStudent();
			Parent parent = assoc.getParent();
			map.put("id", assoc.getId());
			map.put("studentName", student.getFirstName() + " " + student.getLastName());
			map.put("studentId", student.getId());
			map.put("className", student.getClasse().getName());
			map.put("parentName", parent.getFirstName() + " " + parent.getLastName());
			map.put("parentId", parent.getId());
			map.put("relationship", assoc.getRelationship());
			map.put("primaryContact", assoc.isPrimaryContact());
			map.put("createdAt", assoc.getCreatedAt().toString());
			return map;
		}).collect(Collectors.toList());
	}

	@Override
	public Map<String, Object> getAssociationById(Long id) {
		ParentStudent assoc = parentStudentRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Association non trouvée"));
		Map<String, Object> map = new HashMap<>();
		Student student = assoc.getStudent();
		Parent parent = assoc.getParent();
		map.put("id", assoc.getId());
		map.put("studentId", student.getId());
		map.put("parentId", parent.getId());
		map.put("relationship", assoc.getRelationship());
		map.put("primaryContact", assoc.isPrimaryContact());
		return map;
	}

	@Override
	public void saveAssociation(Map<String, Object> associationData) {
		Long studentId = Long.parseLong(associationData.get("studentId").toString());
		Long parentId = Long.parseLong(associationData.get("parentId").toString());
		String relationship = associationData.get("relationship").toString();
		boolean primaryContact = Boolean.parseBoolean(associationData.get("primaryContact").toString());

		Student student = studentRepository.findById(studentId)
				.orElseThrow(() -> new RuntimeException("Étudiant non trouvé"));
		Parent parent = parentRepository.findById(parentId)
				.orElseThrow(() -> new RuntimeException("Parent non trouvé"));

		ParentStudent association = new ParentStudent();
		association.setStudent(student);
		association.setParent(parent);
		association.setRelationship(relationship);
		association.setPrimaryContact(primaryContact);
		association.setCreatedAt(LocalDateTime.now());
		association.setValidated(true);
		parentStudentRepository.save(association);
	}

	@Override
	public void updateAssociation(Long id, Map<String, Object> associationData) {
		// Récupérer l'association existante
		LOGGER.debug("updateAssociation of ParentStudent id="+id);
		ParentStudent association = parentStudentRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Association non trouvée"));

		// Mettre à jour le lien avec l'étudiant si fourni
		if (associationData.containsKey("studentId")) {
			Long studentId = Long.parseLong(associationData.get("studentId").toString());
			Student student = studentRepository.findById(studentId)
					.orElseThrow(() -> new RuntimeException("Étudiant non trouvé : " + studentId));
			association.setStudent(student);
		}

		// Mettre à jour le lien avec le parent si fourni
		if (associationData.containsKey("parentId")) {
			Long parentId = Long.parseLong(associationData.get("parentId").toString());
			Parent parent = parentRepository.findById(parentId)
					.orElseThrow(() -> new RuntimeException("Parent non trouvé : " + parentId));
			association.setParent(parent);
		}

		// Mettre à jour la relation
		if (associationData.containsKey("relationship")) {
			association.setRelationship(associationData.get("relationship").toString());
		}

		// Mettre à jour le champ contact principal
		if (associationData.containsKey("primaryContact")) {
			association.setPrimaryContact(Boolean.parseBoolean(associationData.get("primaryContact").toString()));
		}

		// Sauvegarder l'association mise à jour
		parentStudentRepository.save(association);
	}


	@Override
	public void associateStudentToParents(Long studentId, List<Long> parentIds) {
		// Récupérer l'étudiant
		Student student = studentRepository.findById(studentId)
				.orElseThrow(() -> new RuntimeException("Étudiant non trouvé"));

		// Optionnel : Supprimer les associations existantes pour éviter les doublons
		parentStudentRepository.deleteByStudent(student);

		// Pour chaque parentId, créer une association
		for (Long parentId : parentIds) {
			Parent parent = parentRepository.findById(parentId)
					.orElseThrow(() -> new RuntimeException("Parent non trouvé : " + parentId));

			ParentStudent association = new ParentStudent();
			association.setStudent(student);
			association.setParent(parent);
			association.setRelationship("Child"); // Valeur par défaut, à ajuster si nécessaire
			association.setPrimaryContact(false);
			association.setHasCustody(false);
			association.setEmergencyContact(false);
			association.setValidated(true);
			association.setCreatedAt(LocalDateTime.now());
			parentStudentRepository.save(association);
		}
	}



}
