package com.digital.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.digital.school.model.Room;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByStatus(String status);
    List<Room> findByBuildingName(String buildingName);
    List<Room> findByFloorNumber(Integer floorNumber);
    List<Room> findByMaxCapacityGreaterThanEqual(Integer capacity);
    
    @Query("SELECT r FROM Room r WHERE :equipment MEMBER OF r.equipment")
    List<Room> findByEquipment(@Param("equipment") String equipment);
    
    @Query("SELECT r FROM Room r WHERE r.maxCapacity >= :minCapacity AND r.status = 'AVAILABLE'")
    List<Room> findAvailableRooms(@Param("minCapacity") Integer minCapacity);
    
    boolean existsByName(String name);
}