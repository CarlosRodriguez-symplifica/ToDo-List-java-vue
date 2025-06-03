package com.example.todo.service;

import com.example.todo.dto.SprintDTO;
import com.example.todo.exception.NotFoundException;
import com.example.todo.model.*;
import com.example.todo.repository.SprintRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SprintService {
    private final SprintRepository sprintRepo;

    public SprintService(SprintRepository sprintRepo) {
        this.sprintRepo = sprintRepo;
    }

    public Sprint crear(SprintDTO dto) {
        Sprint sprint = new Sprint();
        sprint.setNombre(dto.getNombre());
        sprint.setPuntosObjetivo(dto.getPuntosObjetivo());

        return sprintRepo.save(sprint);
    }

    public Sprint buscar(Long id) {
        return sprintRepo.findById(id)
                        .orElseThrow(() -> new NotFoundException("Sprint no encontrado con ID: " + id));
    }

    public Map<String, Integer> progreso(Long id) {
        Sprint s = buscar(id);
        int hechos = s.getTareas().stream()
                    .filter(t -> t.getEstado() == EstadoTarea.DONE)
                    .mapToInt(Tarea::getPuntos).sum();
        int faltan = s.getPuntosObjetivo() - hechos;
        return Map.of("hechos", hechos, "faltan", Math.max(faltan, 0));
    }
}
