package com.example.todo.service;

import com.example.todo.dto.SprintSimpleDTO;
import com.example.todo.dto.TareaActualizarDTO;
import com.example.todo.dto.TareaCrearDTO;
import com.example.todo.dto.TareaResponseDTO;
import com.example.todo.exception.CambioDeSprintNoPermitidoException;
import com.example.todo.model.*;
import com.example.todo.repository.SprintRepository;
import com.example.todo.repository.TareaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class TareaService {
    private final TareaRepository tareaRepo;
    private final SprintRepository sprintRepo;

    public TareaService(TareaRepository tareaRepo, SprintRepository sprintRepo) {
        this.tareaRepo = tareaRepo;
        this.sprintRepo = sprintRepo;
    }

    public List<Tarea> listar() {
        return tareaRepo.findAll();
    }

    public Tarea guardar(TareaCrearDTO dto) {
        validar(dto);

        Tarea tarea = new Tarea();
        tarea.setTitulo(dto.getTitulo());
        tarea.setDescripcion(dto.getDescripcion());
        tarea.setPuntos(dto.getPuntos());
        tarea.setEstado(dto.getEstado());

        if (dto.getSprintId() != null) {
            Sprint sprint = sprintRepo.findById(dto.getSprintId())
                                    .orElseThrow(() -> new NoSuchElementException("Sprint no encontrado con ID: " + dto.getSprintId()));
            tarea.setSprint(sprint);
        }

        return tareaRepo.save(tarea);
    }

    public TareaResponseDTO actualizar(Long id, TareaActualizarDTO dto) {
        Tarea tarea = tareaRepo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Tarea no encontrada con ID: " + id));

        if (dto.getTitulo() != null) {
            if (dto.getTitulo().isBlank()) {
                throw new IllegalArgumentException("El titulo no puedo estar vacio");
            }
            tarea.setTitulo(dto.getTitulo());
        }

        if (dto.getDescripcion() != null) {
            tarea.setDescripcion(dto.getDescripcion());
        }

        if (dto.getPuntos() != null) {
            if (dto.getPuntos() < 0) {
                throw new IllegalArgumentException("Los puntos deben ser positivos");
            }
            tarea.setPuntos(dto.getPuntos());
        }

        if (dto.getEstado() != null) tarea.setEstado(dto.getEstado());

        if (dto.getSprintId() != null) {
            Sprint sprint = sprintRepo.findById(dto.getSprintId())
                    .orElseThrow(() -> new NoSuchElementException("Sprint no encontrado con ID: " + dto.getSprintId()));
            tarea.setSprint(sprint);
        }

        Tarea actualizada = tareaRepo.save(tarea);

        Sprint sprint = actualizada.getSprint();
        SprintSimpleDTO sprintDTO = sprint != null
                ? new SprintSimpleDTO(sprint.getId(), sprint.getNombre(), sprint.getPuntosObjetivo())
                : null;

        return new TareaResponseDTO(
                actualizada.getId(),
                actualizada.getTitulo(),
                actualizada.getDescripcion(),
                actualizada.getPuntos(),
                actualizada.getEstado(),
                sprintDTO
        );
    }

    public TareaResponseDTO cambiarEstado(Long id, EstadoTarea estado) {
        Tarea t = tareaRepo.findById(id)
                            .orElseThrow(() -> new NoSuchElementException("Tarea no encontrada con ID: " + id));
        t.setEstado(estado);
        Tarea actualizada = tareaRepo.save(t);

        Sprint s = actualizada.getSprint();
        SprintSimpleDTO sprintDTO = null;
        if (s != null) {
            sprintDTO = new SprintSimpleDTO(s.getId(), s.getNombre(), s.getPuntosObjetivo());
        }

        return new TareaResponseDTO(
                actualizada.getId(),
                actualizada.getTitulo(),
                actualizada.getDescripcion(),
                actualizada.getPuntos(),
                actualizada.getEstado(),
                sprintDTO
        );
    }

    public TareaResponseDTO asociarSprint(Long tareaId, Long sprintId) {
        Tarea tarea = tareaRepo.findById(tareaId)
                .orElseThrow(() -> new NoSuchElementException("Tarea no encontrada con ID: " + tareaId));

        if (tarea.getEstado() == EstadoTarea.DONE) {
            String nombreSprint = tarea.getSprint() != null ? tarea.getSprint().getNombre() : "(sin sprint)";

            throw new CambioDeSprintNoPermitidoException(
                    "La tarea " + tarea.getId() + " no se puede mover de sprint ya que esta solucionada en el sprint " + nombreSprint
            );
        }

        Sprint sprint = sprintRepo.findById(sprintId)
                .orElseThrow(() -> new NoSuchElementException("Sprint no encontrado con ID: " + sprintId));

        tarea.setSprint(sprint);
        Tarea actualizada = tareaRepo.save(tarea);

        SprintSimpleDTO sprintDTO = new SprintSimpleDTO(
                sprint.getId(), sprint.getNombre(), sprint.getPuntosObjetivo()
        );

        return new TareaResponseDTO(
                actualizada.getId(),
                actualizada.getTitulo(),
                actualizada.getDescripcion(),
                actualizada.getPuntos(),
                actualizada.getEstado(),
                sprintDTO
        );
    }

    private void validar(TareaCrearDTO dto) {
        if (dto.getPuntos() < 0) {
            throw new IllegalArgumentException("Los puntos deben ser positivos");
        }

        if (dto.getEstado() == null) {
            throw new IllegalArgumentException("El estado es obligatorio");
        }

        if (dto.getTitulo() == null || dto.getTitulo().trim().isEmpty()) {
            throw  new IllegalArgumentException("El titulo es obligatorio");
        }
    }
}
