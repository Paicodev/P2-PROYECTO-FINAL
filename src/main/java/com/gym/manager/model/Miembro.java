package com.gym.manager.model;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit; //importo para calcular los dias para vencer
import java.util.List;
import java.util.ArrayList;

public class Miembro extends Persona {
    private LocalDate fechaInscripcion;
    private LocalDate fechaVencimiento;
    private Plan plan; 
    private EstadoMiembro estado;
    private List<Pago> historialPagos; 

    public Miembro(LocalDate fechaInscripcion, LocalDate fechaVencimiento, Plan plan, EstadoMiembro estado, int id, String nombre, String apellido, String dni, String email, String telefono) {
        super(id, nombre, apellido, dni, email, telefono);
        this.fechaInscripcion = fechaInscripcion;
        this.fechaVencimiento = fechaVencimiento;
        this.plan = plan;
        this.estado = estado;
        this.historialPagos = new ArrayList<>(); //inicializo el historial de pagos como una lista vacia
    }

    public LocalDate getFechaInscripcion() {
        return fechaInscripcion;
    }

    public void setFechaInscripcion(LocalDate fechaInscripcion) {
        this.fechaInscripcion = fechaInscripcion;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public Plan getPlan() {
        return plan;
    }

    public void setPlan(Plan plan) {
        this.plan = plan;
    }

    public EstadoMiembro getEstado() {
        return estado;
    }

    public void setEstado(EstadoMiembro estado) {
        this.estado = estado;
    }

    public List<Pago> getHistorialPagos() {
        return historialPagos;
    }

    public void setHistorialPagos(List<Pago> historialPagos) {
        this.historialPagos = historialPagos;
    }

    public boolean estaActivo() {
        return estado == EstadoMiembro.ACTIVO;
    }
    public int diasParaVencer() {
        if (this.fechaVencimiento == null) {
                    return 0; 
        } 
        LocalDate hoy = LocalDate.now(); 
        return (int) ChronoUnit.DAYS.between(hoy, this.fechaVencimiento); //calculo los días entre la fecha actual y la fecha de vencimiento y además la convierto a entero porque chronoUnit devuelve un long
    }
}