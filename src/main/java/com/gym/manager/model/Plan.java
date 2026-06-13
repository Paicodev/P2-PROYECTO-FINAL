package com.gym.manager.model;

public class Plan {
    private int id;
    private String nombrePlan;
    private int duracionMeses;
    private String descripcion;
    private double precioMensual;

    public Plan(int id, String nombrePlan, int duracionMeses, String descripcion, double precioMensual) {
        this.id = id;
        this.nombrePlan = nombrePlan;
        this.duracionMeses = duracionMeses;
        this.descripcion = descripcion;
        this.precioMensual = precioMensual;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombrePlan() {
        return nombrePlan;
    }

    public void setNombrePlan(String nombrePlan) {
        this.nombrePlan = nombrePlan;
    }

    public int getDuracionMeses() {
        return duracionMeses;
    }

    public void setDuracionMeses(int duracionMeses) {
        this.duracionMeses = duracionMeses;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion (String descripcion){
        this.descripcion = descripcion;
    }
    
    public double getPrecioMensual() {
        return precioMensual;
    }

    public void setPrecioMensual(double precioMensual) {
        this.precioMensual = precioMensual;
    }

    public double calcularPrecioTotal() {
        return precioMensual * duracionMeses;
    }
}
