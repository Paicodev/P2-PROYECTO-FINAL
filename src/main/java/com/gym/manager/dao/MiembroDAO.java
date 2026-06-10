package com.gym.manager.dao;

import com.gym.manager.model.Miembro;
import com.gym.manager.util.DatabaseManager;
import com.gym.manager.exceptions.ConexionBDException;

import java.sql.*;
import java.util.List;
import java.util.Optional;

public class MiembroDAO implements DAO<Miembro> {

    @Override
    public void guardar(Miembro miembro) {
        String sqlPersona = "INSERT INTO Persona (nombre, apellido, dni, email, telefono, tipo_persona, fecha_registro) VALUES (?, ?, ?, ?, ?, 'MIEMBRO', ?)";
        String sqlMiembro = "INSERT INTO Miembros (fecha_inscripcion, fecha_vencimiento, estado, Planes_id_planes, Persona_idPersona) VALUES (?, ?, ?, ?, ?)";
        
        Connection conn = DatabaseManager.getInstance().getConnection();
        
        try {
            // INICIAMOS LA TRANSACCIÓN (no se guarda automaticamente)
            conn.setAutoCommit(false);
            
            int idPersonaGenerado = 0;

            // INSERTAMOS LA PERSONA
            try (PreparedStatement pstmtPersona = conn.prepareStatement(sqlPersona, Statement.RETURN_GENERATED_KEYS)) {
                pstmtPersona.setString(1, miembro.getNombre());
                pstmtPersona.setString(2, miembro.getApellido());
                pstmtPersona.setString(3, miembro.getDni());
                pstmtPersona.setString(4, miembro.getEmail());
                pstmtPersona.setString(5, miembro.getTelefono());
                pstmtPersona.setDate(6, Date.valueOf(miembro.getfechaIscripcion())); 
                
                pstmtPersona.executeUpdate();
                
                // Capturamos el ID que MySQL le dio a la Persona
                try (ResultSet rs = pstmtPersona.getGeneratedKeys()) {
                    if (rs.next()) {
                        idPersonaGenerado = rs.getInt(1);
                        miembro.setId(idPersonaGenerado); // Se lo asignamos al objeto Java
                    } else {
                        throw new SQLException("Falló la creación de la Persona, no se obtuvo el ID.");
                    }
                }
            }

            // INSERTAMOS EL MIEMBRO (Usando el ID de la Persona que acabamos de crear)
            try (PreparedStatement pstmtMiembro = conn.prepareStatement(sqlMiembro, Statement.RETURN_GENERATED_KEYS)) {
                pstmtMiembro.setDate(1, Date.valueOf(miembro.getfechaIscripcion()));
                pstmtMiembro.setDate(2, Date.valueOf(miembro.getfechaVencimiento()));
                pstmtMiembro.setString(3, miembro.getEstado().name()); 
                pstmtMiembro.setInt(4, miembro.getPlan().getId()); 
                pstmtMiembro.setInt(5, idPersonaGenerado); 
                
                pstmtMiembro.executeUpdate();
            }

            // CONFIRMAMOS LA TRANSACCIÓN 
            conn.commit();

        } catch (SQLException e) {
            // SI ALGO FALLA, SE HACE ROLLBACK (Deshacemos toda la transaccion para no dejar datos a medias)
            try {
                conn.rollback();
            } catch (SQLException ex) {
                System.err.println("Error crítico al hacer rollback: " + ex.getMessage());
            }
            throw new ConexionBDException("Error al guardar el miembro. Se deshicieron los cambios.", e);
            
        } finally {
            // DEVOLVEMOS LA CONEXIÓN A SU ESTADO NORMAL
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Error al restaurar el auto-commit: " + e.getMessage());
            }
        }
    }
    @Override
    public Optional<Miembro> buscarPorId(int id) {
        throw new UnsupportedOperationException("Aún no implementado");
    }

    @Override
    public List<Miembro> obtenerTodos() {
        throw new UnsupportedOperationException("Aún no implementado");
    }

    @Override
    public void actualizar(Miembro miembro) {
        throw new UnsupportedOperationException("Aún no implementado");
    }

    @Override
    public void eliminar(int id) {
        throw new UnsupportedOperationException("Aún no implementado");
    }
}