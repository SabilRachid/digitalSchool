package com.digital.school.service;

import com.digital.school.model.Room;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface RoomService {
    List<Map<String, Object>> findAllAsMap();
    Optional<Room> findById(Long id);
    Room save(Room room);
    void deleteById(Long id);
    boolean existsById(Long id);
    Room updateStatus(Long id, String status);
    Room updateEquipment(Long id, Set<String> equipment);
    List<Room> findAvailable();
    List<Room> findByBuilding(String building);
    List<Room> findByFloor(Integer floor);
    List<Room> findByCapacity(Integer minCapacity);
	boolean existsByName(String name);
	List<Room> findAll();
	List<Room> findByStatus(String status);
	List<Room> findByBuildingName(String buildingName);
	List<Room> findByFloorNumber(Integer floorNumber);
	List<Room> findByMaxCapacityGreaterThanEqual(Integer capacity);
	List<Room> findByEquipment(String equipment);
	List<Room> findAvailableRooms(Integer minCapacity);
}
