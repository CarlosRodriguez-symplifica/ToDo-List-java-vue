package com.example.todo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Tarea {

    @Id @GeneratedValue
    private Long id;

    private String titulo;
    private String descripcion;
    private int puntos;

    @Enumerated(EnumType.STRING)
    private EstadoTarea estado;

    @ManyToOne
    private Sprint sprint;
}
