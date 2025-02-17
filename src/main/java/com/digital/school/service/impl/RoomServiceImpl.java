package com.digital.school.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.digital.school.model.Room;
import com.digital.school.repository.RoomRepository;
import com.digital.school.service.RoomService;

import java.util.*;
import java.util.stream.Collectors;

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
    public List<Map<String, Object>> findAllAsMap(String building, Integer floor, String equipment, String status) {
        return roomRepository.findAll().stream()
                // Filtrer par bâtiment (si renseigné)
                .filter(room -> building == null || building.isEmpty()
                        || room.getBuildingName().equalsIgnoreCase(building))
                // Filtrer par étage (si renseigné)
                .filter(room -> floor == null || room.getFloorNumber().equals(floor))
                // Filtrer par statut (si renseigné)
                .filter(room -> status == null || status.isEmpty()
                        || room.getStatus().equalsIgnoreCase(status))
                // Filtrer par équipement (si renseigné)
                .filter(room -> equipment == null || equipment.isEmpty()
                        || room.getEquipment().contains(equipment))
                .map(room -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", room.getId());
                    map.put("name", room.getName());
                    map.put("maxCapacity", room.getMaxCapacity());
                    map.put("description", room.getDescription());
                    // On retourne la liste des équipements
                    map.put("equipment", new ArrayList<>(room.getEquipment()));
                    map.put("status", room.getStatus());
                    map.put("floorNumber", room.getFloorNumber());
                    map.put("buildingName", room.getBuildingName());
                    map.put("accessible", room.isAccessible());
                    map.put("maintenanceNotes", room.getMaintenanceNotes());
                    return map;
                })
                .collect(Collectors.toList());
    }



    @Override
	public boolean existsById(Long id) {
        return roomRepository.existsById(id);
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