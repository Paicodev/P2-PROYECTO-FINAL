package com.gym.manager.model;

public class Plan {
    private int id;
    private String nombrePlan;
    private double precioMensual;
    private int duracionMeses;

    public Plan(int id, String nombrePlan, double precioMensual, int duracionMeses) {
        this.id = id;
        this.nombrePlan = nombrePlan;
        this.precioMensual = precioMensual;
        this.duracionMeses = duracionMeses;
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

    public double getPrecioMensual() {
        return precioMensual;
    }

    public void setPrecioMensual(double precioMensual) {
        this.precioMensual = precioMensual;
    }

    public int getDuracionMeses() {
        return duracionMeses;
    }

    public void setDuracionMeses(int duracionMeses) {
        this.duracionMeses = duracionMeses;
    }
    
    public double calcularPrecioTotal() {
        return precioMensual * duracionMeses;
    }
}
