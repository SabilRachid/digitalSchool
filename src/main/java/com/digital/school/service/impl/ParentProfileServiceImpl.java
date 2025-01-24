package com.digital.school.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.digital.school.model.ParentProfile;
import com.digital.school.model.User;
import com.digital.school.repository.ParentProfileRepository;
import com.digital.school.service.ParentProfileService;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ParentProfileServiceImpl implements ParentProfileService {

    @Autowired
    private ParentProfileRepository parentProfileRepository;

    @Override
    public List<ParentProfile> findAll() {
        return parentProfileRepository.findAll();
    }

    @Override
    public Optional<ParentProfile> findById(Long id) {
        return parentProfileRepository.findById(id);
    }

    @Override
    public Optional<ParentProfile> findByParent(User parent) {
        return parentProfileRepository.findByParent(parent);
    }

    @Override
    @Transactional
    public ParentProfile save(ParentProfile profile) {
        return parentProfileRepository.save(profile);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        parentProfileRepository.deleteById(id);
    }

    @Override
    public List<ParentProfile> findByProfession(String profession) {
        return parentProfileRepository.findByProfession(profession);
    }

    @Override
    public List<ParentProfile> findByMaritalStatus(String maritalStatus) {
        return parentProfileRepository.findByMaritalStatus(maritalStatus);
    }

    @Override
    public List<ParentProfile> findByPreferredContactMethod(String contactMethod) {
        return parentProfileRepository.findByPreferredContactMethod(contactMethod);
    }

    @Override
    public List<ParentProfile> findByWorkAddressCity(String city) {
        return parentProfileRepository.findByWorkAddressCity(city);
    }

    @Override
    public boolean existsByParent(User parent) {
        return parentProfileRepository.existsByParent(parent);
    }

    @Override
    @Transactional
    public ParentProfile updateContactPreferences(Long id, String method, String time) {
        return parentProfileRepository.findById(id).map(profile -> {
            profile.setPreferredContactMethod(method);
            profile.setPreferredContactTime(time);
            return parentProfileRepository.save(profile);
        }).orElseThrow(() -> new RuntimeException("Profil parent non trouvé"));
    }

    @Override
    @Transactional
    public ParentProfile updateWorkInfo(Long id, String profession, String workPhone, String workAddress) {
        return parentProfileRepository.findById(id).map(profile -> {
            profile.setProfession(profession);
            profile.setWorkPhone(workPhone);
            profile.setWorkAddress(workAddress);
            return parentProfileRepository.save(profile);
        }).orElseThrow(() -> new RuntimeException("Profil parent non trouvé"));
    }

	@Override
	public List<Map<String, Object>> findAllAsMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ParentProfile> findByContactMethod(String contactMethod) {
		// TODO Auto-generated method stub
		return null;
	}
}