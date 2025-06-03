package com.example.todo.dto;

import com.example.todo.model.EstadoTarea;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EstadoRequestDTO {

    @NotNull(message = "El estado no puedo ser nulo")
    private EstadoTarea estado;
}
