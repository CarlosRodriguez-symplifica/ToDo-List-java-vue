package com.example.todo.controller;

import com.example.todo.dto.*;
import com.example.todo.service.TareaService;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/tareas")
@CrossOrigin(origins = "*")
@Validated
public class TareaController {
    private final TareaService tareaService;

    public TareaController(TareaService tareaService) {
        this.tareaService = tareaService;
    }

    @GetMapping
    public List<TareaCrearDTO> listar() {
        return tareaService.listar().stream()
                            .map(Mapper::toTareaDTO)
                            .toList();
    }

    @PostMapping
    public TareaCrearDTO crear(@Valid @RequestBody TareaCrearDTO dto) {
        return Mapper.toTareaDTO(tareaService.guardar(dto));
    }

    @PutMapping("/{id}")
    public TareaResponseDTO actualizar(@PathVariable Long id, @RequestBody TareaActualizarDTO dto) {
        return tareaService.actualizar(id, dto);
    }

    @PutMapping("/{id}/asociar-sprint")
    public TareaResponseDTO asociarSprint(@PathVariable Long id, @RequestBody Map<String, Long> body) {
        Long sprintId = body.get("sprintId");
        return tareaService.asociarSprint(id, sprintId);
    }

    @PutMapping("/{id}/estado")
    public TareaResponseDTO cambiarEstado(@PathVariable Long id, @Valid @RequestBody EstadoRequestDTO request) {
        return tareaService.cambiarEstado(id, request.getEstado());
    }
}
