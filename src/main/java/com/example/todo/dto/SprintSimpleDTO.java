package com.example.todo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SprintSimpleDTO {

    private Long id;

    @NotBlank(message = "El nombre no puede estar vacio")
    private String nombre;

    @Min(value = 0, message = "Los puntos objetivos deben ser cero o mayores")
    private int puntosObjetivo;
}
