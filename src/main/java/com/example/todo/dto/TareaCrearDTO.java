package com.example.todo.dto;

import com.example.todo.model.EstadoTarea;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TareaCrearDTO {

    private Long id;

    @NotBlank(message = "El titulo es obligatorio")
    private String titulo;

    private String descripcion;

    @Min(value = 0, message = "Los puntos deben ser positivos")
    @NotNull(message = "Los puntos son obligatorios")
    private Integer puntos;

    @NotNull(message = "El estado es obligatorio")
    private EstadoTarea estado;

    private Long sprintId;
}
