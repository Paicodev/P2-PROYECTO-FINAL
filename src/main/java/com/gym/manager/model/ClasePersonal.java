package com.gym.manager.model;

import java.time.LocalDateTime;

/**
 * Representa una clase personalizada (entrenamiento 1 a 1).
 * Hereda de ClaseGimnasio. No requiere capacidad máxima.
 */
public class ClasePersonal extends ClaseGimnasio {
    
    public ClasePersonal(int idClase, String nombre, Instructor instructor, LocalDateTime horario, int duracionMinutos, boolean activo) {
        super(idClase, nombre, instructor, horario, duracionMinutos, activo);
    }

    @Override
    public String getTipoClase() {
        return "PERSONAL";
    }
}