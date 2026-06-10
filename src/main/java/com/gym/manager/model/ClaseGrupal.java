package com.gym.manager.model;

import java.time.LocalDateTime;

/**
 * Representa una clase de tipo grupal en el gimnasio.
 * Hereda de ClaseGimnasio y añade límite de capacidad.
 */
public class ClaseGrupal extends ClaseGimnasio {
    private int capacidadMax;

    public ClaseGrupal(int idClase, String nombre, Instructor instructor, LocalDateTime horario, int duracionMinutos, boolean activo, int capacidadMax) {
        super(idClase, nombre, instructor, horario, duracionMinutos, activo);
        this.capacidadMax = capacidadMax;
    }

    @Override
    public String getTipoClase() {
        return "GRUPAL";
    }

    public int getCapacidadMax() { return capacidadMax; }
    public void setCapacidadMax(int capacidadMax) { this.capacidadMax = capacidadMax; }
}