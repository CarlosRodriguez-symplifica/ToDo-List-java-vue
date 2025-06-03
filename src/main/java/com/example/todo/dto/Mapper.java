package com.example.todo.dto;

import com.example.todo.model.Sprint;
import com.example.todo.model.Tarea;

import java.util.LinkedHashMap;
import java.util.Map;

public class Mapper {

    public static TareaCrearDTO toTareaDTO(Tarea tarea) {
        TareaCrearDTO dto = new TareaCrearDTO();

        dto.setId(tarea.getId());
        dto.setTitulo(tarea.getTitulo());
        dto.setDescripcion(tarea.getDescripcion());
        dto.setPuntos(tarea.getPuntos());
        dto.setEstado(tarea.getEstado());

        if (tarea.getSprint() != null) {
            dto.setSprintId(tarea.getSprint().getId());
        }
        return dto;
    }

    public static SprintDTO toSprintDTO(Sprint sprint) {
        SprintDTO dto = new SprintDTO();

        dto.setId(sprint.getId());
        dto.setNombre(sprint.getNombre());
        dto.setPuntosObjetivo(sprint.getPuntosObjetivo());

        Map<String, TareaCrearDTO> tareas = new LinkedHashMap<>();
        int i = 1;
        for (Tarea t : sprint.getTareas()) {
            tareas.put("tarea_" + i, toTareaDTO(t));
            i++;
        }

        dto.setTareas(tareas);
        return dto;
    }
}
