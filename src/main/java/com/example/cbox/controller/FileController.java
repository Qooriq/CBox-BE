package com.example.cbox.controller;

import com.example.cbox.annotation.ValidatedController;
import com.example.cbox.dto.create.FileCreateEditDto;
import com.example.cbox.dto.create.UserAuthDto;
import com.example.cbox.dto.read.FileGetDto;
import com.example.cbox.dto.read.FileReadDto;
import com.example.cbox.dto.read.PageResponse;
import com.example.cbox.service.FileService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.ResponseEntity.*;

@Slf4j
@ValidatedController()
@RequestMapping(value = "/files")
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;

    @GetMapping
    public ResponseEntity<PageResponse<FileReadDto>> findAll(@RequestParam(defaultValue = "1") @Min(1) Integer page,
                                                             @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer limit) {
        return ok().body(PageResponse.of(fileService.findAll(page, limit)));
    }

    @SneakyThrows
    @PostMapping("/upload")
    public ResponseEntity<FileReadDto> upload(@AuthenticationPrincipal UserAuthDto dto,
                                              @ModelAttribute FileCreateEditDto fileDto) {
        return ok().body(fileService.create(dto, fileDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FileGetDto> findById(@AuthenticationPrincipal UserAuthDto dto,
                                               @PathVariable("id") Long id) {
        return fileService.findById(dto, id)
                .map(obj -> ok()
                        .body(obj))
                .orElseGet(notFound()::build);
    }

    @GetMapping("/get")
    public ResponseEntity<PageResponse<FileReadDto>> get(@AuthenticationPrincipal UserAuthDto user,
                                                         @RequestParam(defaultValue = "1") @Min(1) Integer page,
                                                         @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer limit) {
        return ok().body(PageResponse.of(fileService.findAllUserFiles(user, page, limit)));
    }

    @SneakyThrows
    @PutMapping
    public ResponseEntity<FileReadDto> update(@AuthenticationPrincipal UserAuthDto dto,
                                                @RequestParam FileCreateEditDto fileDto) {
        return fileService.update(dto, fileDto)
                .map(obj -> ok().body(obj))
                .orElseGet(notFound()::build);
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(@RequestParam UserAuthDto dto,
                                       @RequestParam Long id) {
        log.info("delete() for file with id {} called", id);
        return fileService.delete(id)
                ? noContent().build()
                : notFound().build();
    }
}
