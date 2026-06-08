package com.gym.manager.model;

import java.time.LocalDateTime;

/**
 * Clase modelo que representa un pago en el sistema.
 * Mapea directamente con la tabla 'Pagos' de la base de datos.
 */
public class Pago {
    private int id;
    private Miembro miembro;
    private double monto;
    private LocalDateTime fecha;
    private TipoPago tipo;
    private EstadoPago estado;
    private String descripcion;

    // Constructor vacío (útil cuando traemos datos de la BD)
    public Pago() {
    }

    // Constructor completo
    public Pago(int id, Miembro miembro, double monto, LocalDateTime fecha, TipoPago tipo, EstadoPago estado, String descripcion) {
        this.id = id;
        this.miembro = miembro;
        this.monto = monto;
        this.fecha = fecha;
        this.tipo = tipo;
        this.estado = estado;
        this.descripcion = descripcion;
    }

    // --- GETTERS Y SETTERS ---

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Miembro getMiembro() { return miembro; }
    public void setMiembro(Miembro miembro) { this.miembro = miembro; }

    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public TipoPago getTipo() { return tipo; }
    public void setTipo(TipoPago tipo) { this.tipo = tipo; }

    public EstadoPago getEstado() { return estado; }
    public void setEstado(EstadoPago estado) { this.estado = estado; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    // --- MÉTODOS DE NEGOCIO PROPIOS DE LA ENTIDAD ---

    /**
     * Genera una cadena de texto representando el comprobante del pago.
     */
    public String generarRecibo() {
        return String.format("=== RECIBO ===\nPago ID: %d\nMonto: $%.2f\nTipo: %s\nEstado: %s\nDescripción: %s", 
            id, monto, tipo.name(), estado.name(), descripcion);
    }

    /**
     * Calcula el monto extra si el pago está vencido.
     * @param porcentajeMora porcentaje adicional a cobrar (ej. 0.10 para un 10%)
     * @return El monto adicional de mora, o 0 si no corresponde.
     */
    public double calcularMora(double porcentajeMora) {
        if (this.estado == EstadoPago.VENCIDO) {
            return this.monto * porcentajeMora;
        }
        return 0.0;
    }
}
