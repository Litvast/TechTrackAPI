package ru.litvast.techtrackapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.litvast.techtrackapi.model.dto.equipment.computer.VideoCardDto;
import ru.litvast.techtrackapi.service.VideoCardService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/equipment/video-card")
@Tag(name = "video-cards", description = "Методы для работы с видеокартами")
public class VideoCardController {

    private final VideoCardService videoCardService;

    @Operation(
            summary = "Добавление видеокарты (ADMIN)",
            description = "В ответ выдаётся созданный объект VideoCardDto.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addVideoCard(@Valid @RequestBody VideoCardDto videoCardDto) {
        VideoCardDto created = videoCardService.addVideoCard(videoCardDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
            summary = "Получение всех видеокарт с пагинацией",
            description = "В ответ выдаётся страница с объектами VideoCardDto.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping
    public ResponseEntity<?> getAllVideoCards(@PageableDefault(size = 20, sort = "name") Pageable pageable) {
        Page<VideoCardDto> videoCards = videoCardService.getAllVideoCards(pageable);
        return ResponseEntity.ok(videoCards);
    }

    @Operation(
            summary = "Поиск видеокарты по id",
            description = "В ответ выдаётся найденный объект VideoCardDto.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/{id}")
    public ResponseEntity<?> getVideoCardById(@PathVariable Long id) {
        VideoCardDto videoCard = videoCardService.getVideoCardById(id);
        return ResponseEntity.ok(videoCard);
    }

    @Operation(
            summary = "Поиск видеокарты по имени",
            description = "В ответ выдаётся найденный объект VideoCardDto.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/by-name/{name}")
    public ResponseEntity<?> getVideoCardByName(@PathVariable String name) {
        VideoCardDto videoCard = videoCardService.getVideoCardByName(name);
        return ResponseEntity.ok(videoCard);
    }

    @Operation(
            summary = "Обновление видеокарты по id (ADMIN)",
            description = "В ответ выдаётся обновлённый объект VideoCardDto.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateVideoCard(@PathVariable Long id,
                                             @Valid @RequestBody VideoCardDto videoCardDto) {
        VideoCardDto updated = videoCardService.updateVideoCard(id, videoCardDto);
        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "Удаление видеокарты по id (ADMIN)",
            description = "В ответ выдаётся сообщение об успешном удалении.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteVideoCard(@PathVariable Long id) {
        videoCardService.deleteVideoCard(id);
        return ResponseEntity.ok("Successfully");
    }
}