package com.example.todo.dto;

import com.example.todo.model.EstadoTarea;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TareaResponseDTO {
    private Long id;
    private String titulo;
    private String descripcion;
    private int puntos;
    private EstadoTarea estado;
    private SprintSimpleDTO sprint;
}
