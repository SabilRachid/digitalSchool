package com.digital.school.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.digital.school.model.ParentStudent;
import com.digital.school.service.ParentStudentService;

@Service
public class ParentStudentServiceImpl implements ParentStudentService {

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
		return null;
	}

	@Override
	public void deleteById(Long id) {
		// TODO Auto-generated method stub
		
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

}
