package com.gym.manager.model;

/**
 * Representa a un instructor dentro del sistema del gimnasio.
 * Contiene la información personal y laboral del empleado.
 */
public class Instructor {
    private int id;
    private String nombre;
    private String apellido;
    private String especialidad;
    private double sueldo;

    public Instructor(int id, String nombre, String apellido, String especialidad, double sueldo) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.especialidad = especialidad;
        this.sueldo = sueldo;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }
    
    public double getSueldo() { return sueldo; }
    public void setSueldo(double sueldo) { this.sueldo = sueldo; }
}