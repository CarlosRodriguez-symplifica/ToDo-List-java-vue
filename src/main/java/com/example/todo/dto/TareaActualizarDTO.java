package com.example.todo.dto;

import com.example.todo.model.EstadoTarea;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TareaActualizarDTO {
    private String titulo;
    private String descripcion;
    private Integer puntos;
    private EstadoTarea estado;
    private Long sprintId;
}
