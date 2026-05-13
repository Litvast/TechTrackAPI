package ru.litvast.techtrackapi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.litvast.techtrackapi.repository.equipment.EquipmentRepository;

import java.security.Principal;

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
