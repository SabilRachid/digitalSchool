package com.digital.school.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.digital.school.model.Room;
import com.digital.school.repository.RoomRepository;
import com.digital.school.service.RoomService;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class RoomServiceImpl implements RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Override
    public List<Room> findAll() {
        return roomRepository.findAll();
    }

    @Override
    public Optional<Room> findById(Long id) {
        return roomRepository.findById(id);
    }

    @Override
    @Transactional
    public Room save(Room room) {
        return roomRepository.save(room);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        roomRepository.deleteById(id);
    }

    @Override
    public List<Room> findByStatus(String status) {
        return roomRepository.findByStatus(status);
    }

    @Override
    public List<Room> findByBuildingName(String buildingName) {
        return roomRepository.findByBuildingName(buildingName);
    }

    @Override
    public List<Room> findByFloorNumber(Integer floorNumber) {
        return roomRepository.findByFloorNumber(floorNumber);
    }

    @Override
    public List<Room> findByMaxCapacityGreaterThanEqual(Integer capacity) {
        return roomRepository.findByMaxCapacityGreaterThanEqual(capacity);
    }

    @Override
    public List<Room> findByEquipment(String equipment) {
        return roomRepository.findByEquipment(equipment);
    }

    @Override
    public List<Room> findAvailableRooms(Integer minCapacity) {
        return roomRepository.findAvailableRooms(minCapacity);
    }

    @Override
    public boolean existsByName(String name) {
        return roomRepository.existsByName(name);
    }

    @Override
    @Transactional
    public Room updateStatus(Long id, String status) {
        return roomRepository.findById(id).map(room -> {
            room.setStatus(status);
            return roomRepository.save(room);
        }).orElseThrow(() -> new RuntimeException("Salle non trouvée"));
    }

    @Override
    @Transactional
    public Room updateEquipment(Long id, Set<String> equipment) {
        return roomRepository.findById(id).map(room -> {
            room.setEquipment(equipment);
            return roomRepository.save(room);
        }).orElseThrow(() -> new RuntimeException("Salle non trouvée"));
    }

	@Override
	public List<Map<String, Object>> findAllAsMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean existsById(Long id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Room> findAvailable() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Room> findByBuilding(String building) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Room> findByFloor(Integer floor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Room> findByCapacity(Integer minCapacity) {
		// TODO Auto-generated method stub
		return null;
	}
}