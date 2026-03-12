package ru.litvast.techtrackapi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.litvast.techtrackapi.model.dto.equipment.computer.ComputerDto;

@RequiredArgsConstructor
@RestController
@RequestMapping("/equipment/computer")
public class ComputerController {

    @PostMapping("/add")
    public ResponseEntity<?> addComputer(@RequestBody @Valid ComputerDto computerDto) {
        System.out.println(computerDto);
        return ResponseEntity.ok("SUPER");
    }

}
