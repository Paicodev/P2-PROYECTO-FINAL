package com.gym.manager.model;

import java.time.LocalDate;

/**
 * Representa el registro de una inscripción o asistencia de un miembro a una clase.
 */
public class Inscripciones {
    private int idInscripciones;
    private LocalDate fechaInscripcion;
    private boolean asistio;
    
    // Claves Foráneas
    private int clasesIdClases;
    private int miembrosIdMiembros;
    //private int planesIdPlanes;
    //private int personaIdPersona;

    public Inscripciones() {}

    public Inscripciones(LocalDate fechaInscripcion, boolean asistio, int clasesIdClases, 
                       int miembrosIdMiembros) {
        this.fechaInscripcion = fechaInscripcion;
        this.asistio = asistio;
        this.clasesIdClases = clasesIdClases;
        this.miembrosIdMiembros = miembrosIdMiembros;
        //this.planesIdPlanes = planesIdPlanes;
        //this.personaIdPersona = personaIdPersona;
    }

    // Getters y Setters
    public int getIdInscripciones() { return idInscripciones; }
    public void setIdInscripciones(int idInscripciones) { this.idInscripciones = idInscripciones; }
    public LocalDate getFechaInscripcion() { return fechaInscripcion; }
    public void setFechaInscripcion(LocalDate fechaInscripcion) { this.fechaInscripcion = fechaInscripcion; }
    public boolean isAsistio() { return asistio; }
    public void setAsistio(boolean asistio) { this.asistio = asistio; }
    public int getClasesIdClases() { return clasesIdClases; }
    public void setClasesIdClases(int clasesIdClases) { this.clasesIdClases = clasesIdClases; }
    public int getMiembrosIdMiembros() { return miembrosIdMiembros; }
    public void setMiembrosIdMiembros(int miembrosIdMiembros) { this.miembrosIdMiembros = miembrosIdMiembros; }
    //public int getPlanesIdPlanes() { return planesIdPlanes; }
    //public void setPlanesIdPlanes(int planesIdPlanes) { this.planesIdPlanes = planesIdPlanes; }
    //public int getPersonaIdPersona() { return personaIdPersona; }
    //public void setPersonaIdPersona(int personaIdPersona) { this.personaIdPersona = personaIdPersona; }
}