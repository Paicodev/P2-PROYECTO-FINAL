package com.gym.manager.model;

/**
 * Representa a un instructor dentro del sistema del gimnasio.
 * Contiene la información personal y laboral del empleado.
 */
public class Instructor extends Persona {
    private int idInstructor;
    private String especialidad;
    private double sueldo;

     public Instructor(int id, String nombre, String apellido, String dni, String email, String telefono, String especialidad, double sueldo) {
       
        super(id, nombre, apellido, dni, email, telefono);
        this.especialidad = especialidad;
        this.sueldo = sueldo;
    }
    // Getters y Setters
     public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }
    
    public double getSueldo() { return sueldo; }
    public void setSueldo(double sueldo) { this.sueldo = sueldo; }
    public int getIdInstructor() {
        return idInstructor;
    }

    public void setIdInstructor(int idInstructor) {
        this.idInstructor = idInstructor;
    }

    @Override
    public String toString() {
        return getNombreCompleto() + " (" + especialidad + ")";
    }
}