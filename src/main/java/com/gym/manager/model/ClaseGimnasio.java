package com.gym.manager.model;

import java.time.LocalDateTime;

/**
 * Clase abstracta que define la estructura base para cualquier clase.
 * Aplica el concepto de POO (Polimorfismo).
 */
public abstract class ClaseGimnasio {
    private int idClase;
    private String nombre;
    private Instructor instructor;
    private LocalDateTime horario;
    private int duracionMinutos;
    private boolean activo;

    public ClaseGimnasio(int idClase, String nombre, Instructor instructor, LocalDateTime horario, int duracionMinutos, boolean activo) {
        this.idClase = idClase;
        this.nombre = nombre;
        this.instructor = instructor;
        this.horario = horario;
        this.duracionMinutos = duracionMinutos;
        this.activo = activo;
    }

    /**
     * Método abstracto para obtener el tipo de clase (Polimorfismo).
     * @return Una cadena de texto indicando si es "GRUPAL" o "PERSONAL".
     */
    public abstract String getTipoClase();

    // Getters y Setters
    public int getIdClase() { return idClase; }
    public void setIdClase(int idClase) { this.idClase = idClase; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Instructor getInstructor() { return instructor; }
    public void setInstructor(Instructor instructor) { this.instructor = instructor; }
    public LocalDateTime getHorario() { return horario; }
    public void setHorario(LocalDateTime horario) { this.horario = horario; }
    public int getDuracionMinutos() { return duracionMinutos; }
    public void setDuracionMinutos(int duracionMinutos) { this.duracionMinutos = duracionMinutos; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}