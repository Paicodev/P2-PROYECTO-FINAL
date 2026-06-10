package com.gym.manager.service;

import com.gym.manager.dao.PagoDAO;
import com.gym.manager.exceptions.ConexionBDException;
import com.gym.manager.exceptions.DatosInvalidosException;
import com.gym.manager.model.Pago;
import com.gym.manager.model.TipoPago;
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

        Connection conn = DatabaseManager.getInstance().getConnection();
        
        if (conn == null) {
            throw new ConexionBDException("No hay conexión disponible con la base de datos.");
        }
        
        try {
            // Inicia la transacción (desactivamos el auto-guardado de MySQL)
            conn.setAutoCommit(false);

            // Guarda el pago en la base de datos
            pagoDAO.guardar(pago);

            // Lógica extra: Si es una mensualidad, se le suma un mes al vencimiento del miembro
            if (pago.getTipo() == TipoPago.MENSUALIDAD) {
                LocalDate fechaActual = pago.getMiembro().getfechaVencimiento();
                LocalDate nuevoVencimiento = (fechaActual != null && fechaActual.isAfter(LocalDate.now())) 
                        ? fechaActual.plusMonths(1) 
                        : LocalDate.now().plusMonths(1);
                
                pago.getMiembro().setfechaVencimiento(nuevoVencimiento);
                
                // TODO: Cuando se termine el MiembroDAO, se debe descomentar esto o escribir como es debido:
                // MiembroDAO miembroDAO = new MiembroDAO();
                // miembroDAO.actualizar(pago.getMiembro());
            }

            // Confirmamos la transacción (Todo salió perfecto, se aplican los cambios)
            conn.commit();

        } catch (Exception e) {
            // Rollback: Si algo falló, deshacemos TODO lo que se haya intentado guardar
            try { conn.rollback(); } catch (SQLException ex) { }
            try { 
                conn.rollback(); 
            } catch (SQLException ex) { 
                System.err.println("Error GRAVE al intentar revertir la transacción: " + ex.getMessage());
            }
            throw new RuntimeException("Error al registrar el pago. Transacción revertida.", e);
        } finally {
            // Restauramos la conexión a su estado original
            try { conn.setAutoCommit(true); } catch (SQLException ex) { }
            try { 
                conn.setAutoCommit(true); 
            } catch (SQLException ex) { 
                System.err.println("Error al restaurar AutoCommit: " + ex.getMessage());
            }
        }
    }
}
