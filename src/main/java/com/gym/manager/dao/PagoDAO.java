package com.gym.manager.dao;

import com.gym.manager.model.Pago;
import com.gym.manager.util.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PagoDAO implements DAO<Pago> {

    private Connection conexion;

    public PagoDAO() {
        // 1. Obtenemos la conexión única usando el Singleton que ya existe en el proyecto
        this.conexion = DatabaseManager.getInstance().getConnection();
    }

    @Override
    public void guardar(Pago pago) {
        // 2. Preparamos el SQL con "?" para evitar inyección SQL (Seguridad)
        String sql = "INSERT INTO pagos (miembro_id, monto, fecha, tipo, estado, descripcion) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            // 3. Reemplazamos los "?" con los datos de nuestro objeto Pago
            stmt.setInt(1, pago.getMiembro().getId());
            stmt.setDouble(2, pago.getMonto());
            stmt.setTimestamp(3, Timestamp.valueOf(pago.getFecha())); // Convertimos LocalDateTime de Java a Timestamp de SQL
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
            System.err.println("Error al guardar el pago: " + e.getMessage());
            // En un futuro se puede reemplazar por ConexionBDException del proyecto
        }
    }

    @Override
    public Pago buscarPorId(int id) {
        // TODO: Implementar SELECT * FROM pagos WHERE id = ?
        return null;
    }

    @Override
    public List<Pago> obtenerTodos() {
        // TODO: Implementar SELECT * FROM pagos
        return new ArrayList<>();
    }

    @Override
    public void actualizar(Pago pago) {
        // TODO: Implementar UPDATE pagos SET ... WHERE id = ?
    }

    @Override
    public void eliminar(int id) {
        // TODO: Implementar DELETE FROM pagos WHERE id = ?
    }
}