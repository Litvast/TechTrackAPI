package ru.litvast.techtrackapi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.litvast.techtrackapi.model.dto.equipment.computer.ComputerDto;
import ru.litvast.techtrackapi.service.ComputerService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/equipment/computer")
public class ComputerController {

    private final ComputerService computerService;

    @PostMapping("/add")
    public ResponseEntity<?> addComputer(@Valid @RequestBody ComputerDto computerDto) {
        return ResponseEntity.ok(computerService.addComputer(computerDto));
    }

}
