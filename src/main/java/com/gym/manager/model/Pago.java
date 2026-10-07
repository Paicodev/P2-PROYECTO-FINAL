package com.gym.manager.model;

import java.time.LocalDateTime;
import java.util.Objects;

import com.gym.manager.model.enums.EstadoPago;
import com.gym.manager.model.enums.TipoPago;

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
    public Pago(int id, Miembro miembro, double monto, LocalDateTime fecha, TipoPago tipo, EstadoPago estado,
            String descripcion) {
        this.id = id;
        this.miembro = miembro;
        this.monto = monto;
        this.fecha = fecha;
        this.tipo = tipo;
        this.estado = estado;
        this.descripcion = descripcion;
    }

    // --- GETTERS Y SETTERS ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Miembro getMiembro() {
        return miembro;
    }

    public void setMiembro(Miembro miembro) {
        this.miembro = miembro;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public TipoPago getTipo() {
        return tipo;
    }

    public void setTipo(TipoPago tipo) {
        this.tipo = tipo;
    }

    public EstadoPago getEstado() {
        return estado;
    }

    public void setEstado(EstadoPago estado) {
        this.estado = estado;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    // --- MÉTODOS DE NEGOCIO PROPIOS DE LA ENTIDAD ---

    /**
     * Genera una cadena de texto representando el comprobante del pago.
     */
    public String generarRecibo() {
        String tipoStr = (tipo != null) ? tipo.name() : "N/A";
        String estadoStr = (estado != null) ? estado.name() : "N/A";
        String descStr = (descripcion != null) ? descripcion : "Sin descripción";

        return String.format("=== RECIBO ===\nPago ID: %d\nMonto: $%.2f\nTipo: %s\nEstado: %s\nDescripción: %s",
                id, monto, tipoStr, estadoStr, descStr);
    }

    /**
     * Calcula el monto extra si el pago está vencido.
     * 
     * @param porcentajeMora porcentaje adicional a cobrar (ej. 0.10 para un 10%)
     * @return El monto adicional de mora, o 0 si no corresponde.
     */
    public double calcularMora(double porcentajeMora) {
        if (porcentajeMora < 0) {
            throw new IllegalArgumentException("El porcentaje de mora no puede ser negativo");
        }
        if (this.estado == EstadoPago.VENCIDO) {
            return this.monto * porcentajeMora;
        }
        return 0.0;
    }

    // --- MÉTODOS ESTÁNDAR DE EQUIVALENCIA Y REPRESENTACIÓN ---
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Pago pago = (Pago) o;
        return id == pago.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Pago{" +
                "id=" + id +
                ", miembro=" + (miembro != null ? miembro.getNombreCompleto() : "null") +
                ", monto=" + monto +
                ", fecha=" + fecha +
                ", tipo=" + tipo +
                ", estado=" + estado +
                '}';
    }
}
