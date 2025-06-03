package com.example.todo.controller;

import com.example.todo.dto.Mapper;
import com.example.todo.dto.SprintDTO;
import com.example.todo.service.SprintService;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/sprints")
@CrossOrigin
@Validated
public class SprintController {
    private final SprintService sprintService;

    public SprintController(SprintService sprintService) {
        this.sprintService = sprintService;
    }

    @PostMapping
    public SprintDTO crear(@Valid @RequestBody SprintDTO dto) {
        return Mapper.toSprintDTO(sprintService.crear(dto));
    }

    @GetMapping("/{id}")
    public SprintDTO buscar(@PathVariable Long id) {
        return Mapper.toSprintDTO(sprintService.buscar(id));
    }

    @GetMapping("/{id}/progreso")
    public Map<String, Integer> progreso(@PathVariable Long id) {
        return sprintService.progreso(id);
    }
}
