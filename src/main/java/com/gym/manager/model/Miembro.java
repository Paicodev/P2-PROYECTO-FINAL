package com.gym.manager.model;

/**
 * Esta clase es un simulacro para que el Módulo de Pagos pueda compilar.
 * Luego será reemplazada por la entidad completa.
 */
public class Miembro {
    private int id;
    private String nombre; // Atributo básico solo para pruebas

    public Miembro() {}

    public Miembro(int id) {
        this.id = id;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}

