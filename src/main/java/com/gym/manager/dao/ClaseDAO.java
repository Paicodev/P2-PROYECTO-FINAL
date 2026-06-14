package com.gym.manager.dao;

import com.gym.manager.model.ClaseGimnasio;
import com.gym.manager.model.ClaseGrupal;
import com.gym.manager.util.DatabaseManager;
import com.gym.manager.exceptions.ConexionBDException;
import com.gym.manager.interfaces.DAO;

import java.sql.*;
import java.util.List;
import java.util.Optional;

public class ClaseDAO implements DAO<ClaseGimnasio> {

    @Override
    public void guardar(ClaseGimnasio clase) {
        String sql = "INSERT INTO Clases (nombre, tipo, horario, duracion_minutos, capacidad_max, activo) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = DatabaseManager.getInstance().getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, clase.getNombre());
            pstmt.setString(2, clase.getTipoClase()); // Llama al método abstracto que devuelve "GRUPAL" o "PERSONAL"
            pstmt.setTimestamp(3, Timestamp.valueOf(clase.getHorario()));
            pstmt.setInt(4, clase.getDuracionMinutos());
            
            // Evaluamos mediante polimorfismo si es grupal para guardarle la capacidad
            if (clase instanceof ClaseGrupal) {
                pstmt.setInt(5, ((ClaseGrupal) clase).getCapacidadMax());
            } else {
                pstmt.setNull(5, Types.INTEGER); // Las clases personales no tienen capacidad máxima
            }
            
            pstmt.setBoolean(6, clase.isActivo());
            pstmt.setInt(7, clase.getInstructor().getId()); // Clave foránea del instructor asignado

            pstmt.executeUpdate();

            // Recuperar ID generado
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    clase.setIdClase(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new ConexionBDException("Error al guardar la nueva clase en la base de datos.", e);
        }
    }

    // --- MÉTODOS OBLIGATORIOS RESTANTES PARA EL CRUD ---
    @Override
    public Optional<ClaseGimnasio> buscarPorId(int id) {
        throw new UnsupportedOperationException("Búsqueda por ID aún no implementada.");
    }

    @Override
    public List<ClaseGimnasio> obtenerTodos() {
        //Este método se implementaría con un SELECT * FROM Clase 
        throw new UnsupportedOperationException("Listar clases aún no implementado.");
    }

    @Override
    public void actualizar(ClaseGimnasio clase) {
        throw new UnsupportedOperationException("Actualización aún no implementada.");
    }

    @Override
    public void eliminar(int id) {
        throw new UnsupportedOperationException("Eliminación aún no implementada.");
    }
}