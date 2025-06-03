package com.example.todo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class SprintDTO {

    private Long id;

    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;

    @Min(value = 0, message = "Los puntos objetivos deben ser mayores o iguales a 0")
    private int puntosObjetivo;


    private Map<String, TareaCrearDTO> tareas;
}
