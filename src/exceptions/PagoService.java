package com.gym.manager.service;

import com.gym.manager.dao.PagoDAO;
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

    public void registrarPago(Pago pago) {
        // Validaciones previas
        if (pago.getMonto() <= 0) {
            throw new DatosInvalidosException("El monto del pago debe ser mayor a 0.");
        }
        if (pago.getMiembro() == null) {
            throw new DatosInvalidosException("El pago debe estar asociado a un miembro válido.");
        }
        
        // BUG FIX 3: Solo evitamos pagos duplicados si es una MENSUALIDAD
        if (pago.getTipo() == TipoPago.MENSUALIDAD && yaPagoEsteMes(pago.getMiembro().getId(), TipoPago.MENSUALIDAD, pago.getFecha().toLocalDate())) {
            throw new DatosInvalidosException("El miembro ya abonó una mensualidad este mes calendario. No puede pagar 2 veces.");
        }

        Connection conn = DatabaseManager.getInstance().getConnection();
        if (conn == null) {
            throw new ConexionBDException("No hay conexión disponible con la base de datos.");
        }
        
        try {
            // Inicia la transacción
            conn.setAutoCommit(false);

            // Guarda el pago en la base de datos
            pagoDAO.guardar(pago);

            // BUG FIX 2: Restauramos la lógica de sumar a la fecha de vencimiento actual, no a la de hoy
            LocalDate fechaActual = pago.getMiembro().getFechaVencimiento();
            LocalDate nuevoVencimiento = null;

            if (pago.getTipo() == TipoPago.MENSUALIDAD) {
                nuevoVencimiento = (fechaActual != null && fechaActual.isAfter(LocalDate.now())) 
                        ? fechaActual.plusMonths(1) 
                        : LocalDate.now().plusMonths(1);
            } else if (pago.getTipo() == TipoPago.CLASE) {
                nuevoVencimiento = (fechaActual != null && fechaActual.isAfter(LocalDate.now())) 
                        ? fechaActual.plusDays(1) 
                        : LocalDate.now().plusDays(1);
            }

            if (nuevoVencimiento != null) {
                pago.getMiembro().setFechaVencimiento(nuevoVencimiento);
                
                // BUG FIX 1: Hacemos el UPDATE incluyendo el estado = 'ACTIVO' para revivir a los morosos
                String sqlUpdateVencimiento = "UPDATE Miembros SET fecha_vencimiento = ?, estado = 'ACTIVO' WHERE Persona_idPersona = ?";
                try (java.sql.PreparedStatement pst = conn.prepareStatement(sqlUpdateVencimiento)) {
                    pst.setDate(1, java.sql.Date.valueOf(nuevoVencimiento));
                    pst.setInt(2, pago.getMiembro().getId()); // ID de la persona
                    pst.executeUpdate();
                }
            }

            // Confirmamos la transacción
            conn.commit();

        } catch (Exception e) {
            try { 
                if(!conn.getAutoCommit()){
                    conn.rollback(); 
                }
            } catch (SQLException ex) { 
                System.err.println("Error GRAVE al intentar revertir la transacción: " + ex.getMessage());
            }
            
            // DESEMPAQUETAR ERROR: Para que la vista muestre el error real de SQL y no uno genérico
            String errorReal = (e instanceof ConexionBDException && e.getCause() != null) ? e.getCause().getMessage() : e.getMessage();
            throw new RuntimeException("Error en BD: " + errorReal, e);
            
        } finally {
            try { 
                if (!conn.getAutoCommit()) {
                    conn.setAutoCommit(true); 
                }
            } catch (SQLException ex) { }
        }
    }

    public void eliminarPago(int id) {
        pagoDAO.eliminar(id);
    }

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