package com.gym.manager.service;

import com.gym.manager.dao.PagoDAO;
import com.gym.manager.dao.MiembroDAO;
import com.gym.manager.exceptions.ConexionBDException;
import com.gym.manager.exceptions.DatosInvalidosException;
import com.gym.manager.model.Pago;
import com.gym.manager.model.enums.TipoPago;
import com.gym.manager.util.DatabaseManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Capa de servicio para Pagos.
 * Maneja la lógica de negocio, validaciones y transacciones de base de datos.
 */
public class PagoService {

    private PagoDAO pagoDAO;

    public PagoService() {
        this.pagoDAO = new PagoDAO();
    }

    /**
     * Registra un nuevo pago validando las reglas de negocio y manejando la transacción.
     * 
     * @param pago El objeto pago proveniente de la vista/controlador.
     */
    public void registrarPago(Pago pago) {
        // Validaciones previas
        if (pago.getMonto() <= 0) {
            throw new DatosInvalidosException("El monto del pago debe ser mayor a 0.");
        }
        if (pago.getMiembro() == null) {
            throw new DatosInvalidosException("El pago debe estar asociado a un miembro válido.");
        }
        
        // Validacion para evitar pagos duplicados
        if (yaPagoEsteMes(pago.getMiembro().getId(), pago.getTipo(), pago.getFecha().toLocalDate())) {
            if (pago.getTipo() == TipoPago.MENSUALIDAD) {
                throw new DatosInvalidosException("El miembro ya abonó una mensualidad este mes. No puede pagar 2 veces en el mismo mes calendario.");
            } else {
                throw new DatosInvalidosException("Operación rechazada: El miembro ya registró un pago de tipo " + pago.getTipo().name() + " este mes.");
            }
        }

        Connection conn = DatabaseManager.getInstance().getConnection();
        
        if (conn == null) {
            throw new ConexionBDException("No hay conexión disponible con la base de datos.");
        }
        
        try {
            // Inicia la transacción (desactivamos el auto-guardado de MySQL)
            conn.setAutoCommit(false);

            // Guarda el pago en la base de datos
            pagoDAO.guardar(pago);

            // Lógica extra: Actualizar el vencimiento del miembro según el tipo de pago
            LocalDate nuevoVencimiento = null;
            if (pago.getTipo() == TipoPago.MENSUALIDAD) {
                nuevoVencimiento = pago.getFecha().toLocalDate().plusMonths(1);
            } else if (pago.getTipo() == TipoPago.CLASE) {
                nuevoVencimiento = pago.getFecha().toLocalDate().plusDays(1);
            }

            if (nuevoVencimiento != null) {
                pago.getMiembro().setFechaVencimiento(nuevoVencimiento);
                
                // FIX ARQUITECTÓNICO: Hacemos el UPDATE directamente en esta transacción
                // para no invocar a MiembroDAO y romper el estado de la Conexión.
                String sqlUpdateVencimiento = "UPDATE Miembros SET fecha_vencimiento = ? WHERE Persona_idPersona = ?";
                try (java.sql.PreparedStatement pst = conn.prepareStatement(sqlUpdateVencimiento)) {
                    pst.setDate(1, java.sql.Date.valueOf(nuevoVencimiento));
                    pst.setInt(2, pago.getMiembro().getId()); // ID de la persona
                    pst.executeUpdate();
                }
            }

            // Confirmamos la transacción (Todo salió perfecto, se aplican los cambios)
            conn.commit();

        } catch (Exception e) {
            // Rollback: Si algo falló, deshacemos TODO lo que se haya intentado guardar
            try { 
                if(!conn.getAutoCommit()){
                conn.rollback(); 
                }
            } catch (SQLException ex) { 
                System.err.println("Error GRAVE al intentar revertir la transacción: " + ex.getMessage());
            }
            throw new RuntimeException("Error al registrar el pago. Transacción revertida.", e);
        } finally {
            // Restauramos la conexión a su estado original
            try { 
                if (!conn.getAutoCommit()) {
                conn.setAutoCommit(true); 
                }
            } catch (SQLException ex) { 
                System.err.println("Error al restaurar AutoCommit: " + ex.getMessage());
            }
        }
    }

    /**
     * Elimina un registro de pago de la base de datos.
     * 
     * @param id El ID del pago a eliminar.
     */
    public void eliminarPago(int id) {
        pagoDAO.eliminar(id);
    }

    /**
     * Verifica si el miembro ya realizó un pago del mismo tipo en el mes actual.
     */
    private boolean yaPagoEsteMes(int idPersona, TipoPago tipo, LocalDate fechaPago) {
        String sql = "SELECT COUNT(*) FROM Pagos p JOIN Miembros m ON p.Miembros_idMiembros = m.idMiembros " +
                     "WHERE m.Persona_idPersona = ? AND p.tipo = ? AND MONTH(p.fecha_pago) = ? AND YEAR(p.fecha_pago) = ?";
                     
        Connection conn = DatabaseManager.getInstance().getConnection();
        try (java.sql.PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, idPersona);
            pst.setString(2, tipo.name());
            pst.setInt(3, fechaPago.getMonthValue());
            pst.setInt(4, fechaPago.getYear());
            try (java.sql.ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar pagos del mes: " + e.getMessage());
        }
        return false;
    }
}
