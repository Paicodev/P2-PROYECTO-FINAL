package com.gym.manager.dao;

import com.gym.manager.exceptions.ConexionBDException;
import com.gym.manager.model.Miembro;
import com.gym.manager.model.Pago;
import com.gym.manager.model.enums.EstadoPago;
import com.gym.manager.model.enums.TipoPago;
import com.gym.manager.util.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PagoDAO implements DAO<Pago> {

    private Connection conexion;

    public PagoDAO() {
        // 1. Obtenemos la conexión única usando el Singleton que ya existe en el proyecto
        this.conexion = DatabaseManager.getInstance().getConnection();
    }

    @Override
    public void guardar(Pago pago) {
        // 2. Preparamos el SQL con "?" para evitar inyección SQL (Seguridad)
        String sql = "INSERT INTO Pagos (Miembros_idMiembros, monto, fecha_pago, tipo, estado, descripcion) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            // 3. Reemplazamos los "?" con los datos de nuestro objeto Pago
            stmt.setInt(1, pago.getMiembro().getId());
            stmt.setDouble(2, pago.getMonto());
            stmt.setDate(3, java.sql.Date.valueOf(pago.getFecha().toLocalDate())); // Convertimos LocalDateTime a Date porque MySQL usa DATE
            stmt.setString(4, pago.getTipo().name());
            stmt.setString(5, pago.getEstado().name());
            stmt.setString(6, pago.getDescripcion());
            
            // 4. Ejecutamos la consulta
            stmt.executeUpdate();
            
            // 5. Recuperamos el ID autoincremental que generó MySQL y se lo asignamos al objeto
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    pago.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new ConexionBDException("Error al guardar el pago en la base de datos.", e);
        }
    }

    @Override
    public Optional<Pago> buscarPorId(int id) {
        String sql = "SELECT * FROM Pagos WHERE idPagos = ?";
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearPago(rs));
                }
            }
        } catch (SQLException e) {
            throw new ConexionBDException("Error al buscar el pago con ID: " + id, e);
        }
        
        return Optional.empty(); // Retorna vacío si no encontró el pago
    }

    @Override
    public List<Pago> obtenerTodos() {
        List<Pago> lista = new ArrayList<>();
        String sql = "SELECT * FROM Pagos";
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
             
            while (rs.next()) {
                lista.add(mapearPago(rs));
            }
        } catch (SQLException e) {
            throw new ConexionBDException("Error al obtener la lista de pagos.", e);
        }
        
        return lista;
    }

    @Override
    public void actualizar(Pago pago) {
        String sql = "UPDATE Pagos SET Miembros_idMiembros = ?, monto = ?, fecha_pago = ?, tipo = ?, estado = ?, descripcion = ? WHERE idPagos = ?";
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, pago.getMiembro().getId());
            stmt.setDouble(2, pago.getMonto());
            stmt.setDate(3, java.sql.Date.valueOf(pago.getFecha().toLocalDate()));
            stmt.setString(4, pago.getTipo().name());
            stmt.setString(5, pago.getEstado().name());
            stmt.setString(6, pago.getDescripcion());
            stmt.setInt(7, pago.getId());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new ConexionBDException("Error al actualizar el pago con ID: " + pago.getId(), e);
        }
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM Pagos WHERE idPagos = ?";
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new ConexionBDException("Error al eliminar el pago con ID: " + id, e);
        }
    }

    /**
     * Método auxiliar para transformar un ResultSet en un objeto Pago
     */
    private Pago mapearPago(ResultSet rs) throws SQLException {
        // Instanciamos un Miembro temporal con datos de relleno válidos solo para guardar su ID.
        // En un futuro, aquí se debería usar un MiembroDAO.buscarPorId(rs.getInt("Miembros_idMiembros"))
        Miembro miembro = new Miembro(null, null, null, null, rs.getInt("Miembros_idMiembros"), 
                                      "NombreTemp", "ApellidoTemp", "00000000", "test@test.com", "0000000000");
        
        return new Pago(
            rs.getInt("idPagos"),
            miembro,
            rs.getDouble("monto"),
            rs.getDate("fecha_pago").toLocalDate().atStartOfDay(),
            TipoPago.valueOf(rs.getString("tipo")),
            EstadoPago.valueOf(rs.getString("estado")),
            rs.getString("descripcion")
        );
    }
}