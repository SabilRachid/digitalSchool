package com.digital.school.service;

import com.digital.school.model.ParentProfile;
import com.digital.school.model.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ParentProfileService {
    List<Map<String, Object>> findAllAsMap();
    Optional<ParentProfile> findById(Long id);
    ParentProfile save(ParentProfile profile);
    void deleteById(Long id);
    List<ParentProfile> findByContactMethod(String contactMethod);
	List<ParentProfile> findAll();
	Optional<ParentProfile> findByParent(User parent);
	List<ParentProfile> findByProfession(String profession);
	List<ParentProfile> findByMaritalStatus(String maritalStatus);
	List<ParentProfile> findByWorkAddressCity(String city);
	List<ParentProfile> findByPreferredContactMethod(String contactMethod);
	boolean existsByParent(User parent);
	ParentProfile updateContactPreferences(Long id, String method, String time);
	ParentProfile updateWorkInfo(Long id, String profession, String workPhone, String workAddress);
}