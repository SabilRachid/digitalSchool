package com.digital.school.controller.rest.admin;

import com.digital.school.controller.AdminController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.digital.school.model.Room;
import com.digital.school.service.RoomService;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping("/admin/api/rooms")
public class AdminRoomRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminRoomRestController.class);

    @Autowired
    private RoomService roomService;

    @GetMapping("/data")
    @ResponseBody
    public List<Map<String, Object>> getRoomsData(
            @RequestParam(required = false) String buildingFilter,
            @RequestParam(required = false) Integer floorFilter,
            @RequestParam(required = false) String statusFilter,
            @RequestParam(required = false) String equipmentFilter) {
        LOGGER.debug("Fetching rooms data with filters: building={}, floor={}, status={}, equipment={}",
                buildingFilter, floorFilter, statusFilter, equipmentFilter);
        return roomService.findAllAsMap(buildingFilter, floorFilter, equipmentFilter, statusFilter);
    }


    @GetMapping("/available")
    @ResponseBody
    public List<Room> getAvailableRooms(@RequestParam(required = false) Integer minCapacity) {
        return minCapacity != null ? 
            roomService.findAvailableRooms(minCapacity) : 
            roomService.findAvailable();
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Room> getRoom(@PathVariable Long id) {
        return roomService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<?> createRoom(@RequestBody Room room) {
        try {
            Room savedRoom = roomService.save(room);
            return ResponseEntity.ok(savedRoom);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Erreur lors de la création: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> updateRoom(@PathVariable Long id, @RequestBody Room room) {
        try {
            LOGGER.debug("Updating room with id: {}", id);
            if (!roomService.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            room.setId(id);
            Room updatedRoom = roomService.save(room);
            return ResponseEntity.ok(updatedRoom);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Erreur lors de la mise à jour: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteRoom(@PathVariable Long id) {
        try {
            roomService.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Erreur lors de la suppression: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}/status")
    @ResponseBody
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            Room room = roomService.updateStatus(id, status);
            return ResponseEntity.ok(room);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Erreur lors de la mise à jour du statut: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}/equipment")
    @ResponseBody
    public ResponseEntity<?> updateEquipment(@PathVariable Long id, @RequestBody Set<String> equipment) {
        try {
            Room room = roomService.updateEquipment(id, equipment);
            return ResponseEntity.ok(room);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Erreur lors de la mise à jour des équipements: " + e.getMessage()));
        }
    }
}
