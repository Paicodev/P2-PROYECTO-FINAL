package com.gym.manager.service;

import com.gym.manager.dao.MiembroDAO;
import com.gym.manager.dao.PagoDAO;
import com.gym.manager.exceptions.ConexionBDException;
import com.gym.manager.exceptions.DatosInvalidosException;
import com.gym.manager.model.Pago;
import com.gym.manager.model.enums.EstadoMiembro;
import com.gym.manager.model.enums.TipoPago;
import com.gym.manager.util.DatabaseManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Capa de servicio para Pagos.
 * Maneja la lógica de negocio, validaciones y transacciones de base de datos.
 */
public class PagoService {

    private PagoDAO pagoDAO;
    private MiembroDAO miembroDAO;

    // Constructor por defecto: instanciamiento estándar para la UI (Swing)
    public PagoService() {
        this(new PagoDAO(), new MiembroDAO());
    }

    // Constructor para inyección de PagoDAO (opcional)
    public PagoService(PagoDAO pagoDAO) {
        this(pagoDAO, new MiembroDAO());
    }

    // Constructor completo: fundamental para inyectar MOCKS en pruebas con Mockito
    public PagoService(PagoDAO pagoDAO, MiembroDAO miembroDAO) {
        this.pagoDAO = pagoDAO;
        this.miembroDAO = miembroDAO;
    }

    public void registrarPago(Pago pago) {
        // Validaciones de negocio
        if (pago.getMonto() <= 0) {
            throw new DatosInvalidosException("El monto del pago debe ser mayor a 0.");
        }
        if (pago.getMiembro() == null) {
            throw new DatosInvalidosException("El pago debe estar asociado a un miembro válido.");
        }

        // Evitamos pagos duplicados de mensualidad delegando la verificación al DAO
        if (pago.getTipo() == TipoPago.MENSUALIDAD &&
                pagoDAO.yaPagoEnMes(pago.getMiembro().getId(), TipoPago.MENSUALIDAD, pago.getFecha().toLocalDate())) {
            throw new DatosInvalidosException("El miembro ya abonó una mensualidad este mes. No puede pagar 2 veces.");
        }

        Connection conn = DatabaseManager.getInstance().getConnection();
        if (conn == null) {
            throw new ConexionBDException("No hay conexión disponible con la base de datos.");
        }

        try {
            // Inicia la transacción ACID manual
            conn.setAutoCommit(false);

            // Guarda el pago en la base de datos a trávez del DAO
            pagoDAO.guardar(pago);

            // Calcula la nueva fecha de vencimiento según el tipo de pago
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
                pago.getMiembro().setEstado(EstadoMiembro.ACTIVO);

                miembroDAO.actualizarVencimientoYEstado(pago.getMiembro().getId(), nuevoVencimiento,
                        EstadoMiembro.ACTIVO);
            }

            // Confirmamos la transacción
            conn.commit();

        } catch (Exception e) {
            try {
                if (!conn.getAutoCommit()) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                System.err.println("Error GRAVE al intentar revertir la transacción: " + ex.getMessage());
            }

            // DESEMPAQUETAR ERROR: Para que la vista muestre el error real de SQL y no uno
            // genérico
            String errorReal = (e instanceof ConexionBDException && e.getCause() != null) ? e.getCause().getMessage()
                    : e.getMessage();
            throw new RuntimeException("Error en BD: " + errorReal, e);

        } finally {
            try {
                if (!conn.getAutoCommit()) {
                    conn.setAutoCommit(true);
                }
            } catch (SQLException ex) {
            }
        }
    }

    public void eliminarPago(int id) {
        pagoDAO.eliminar(id);
    }

    public List<Pago> obtenerTodosLosPagos() {
        return pagoDAO.obtenerTodos();
    }

    public Optional<Pago> buscarPorId(int id) {
        return pagoDAO.buscarPorId(id);
    }
}
