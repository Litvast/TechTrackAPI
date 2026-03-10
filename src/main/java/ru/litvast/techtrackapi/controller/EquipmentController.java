package ru.litvast.techtrackapi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.litvast.techtrackapi.model.equipment.Equipment;
import ru.litvast.techtrackapi.repository.EquipmentRepository;

import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/equipment")
public class EquipmentController {

    private final EquipmentRepository equipmentRepository;

    @GetMapping("/all")
    public ResponseEntity<?> getAllEquipments(Principal principal) {
        try {
            return ResponseEntity.ok(equipmentRepository.findAll());
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }
}
