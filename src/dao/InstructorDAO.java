package com.gym.manager.dao;

import com.gym.manager.model.Instructor;
import com.gym.manager.util.DatabaseManager;
import com.gym.manager.exceptions.ConexionBDException;
import com.gym.manager.interfaces.DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InstructorDAO implements DAO<Instructor> {

    @Override
    public List<Instructor> obtenerTodos() {
        List<Instructor> lista = new ArrayList<>();
        String sql = "SELECT p.*, i.idInstructores, i.especialidad, i.sueldo FROM Persona p INNER JOIN Instructores i ON p.idPersona = i.Persona_idPersona";
        
        Connection conn = DatabaseManager.getInstance().getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Instructor instructor = new Instructor(
                    rs.getInt("idPersona"),
                    rs.getString("nombre"),
                    rs.getString("apellido"),
                    rs.getString("dni"),
                    rs.getString("email"),
                    rs.getString("telefono"),
                    rs.getString("especialidad"),
                    rs.getDouble("sueldo")
                );
                // CORRECCIÓN: Guardamos el idPersona para que sea el ID de referencia en los ABM
                instructor.setId(rs.getInt("idPersona"));
                instructor.setIdInstructor(rs.getInt("idInstructores"));
                lista.add(instructor);
            }
        } catch (SQLException e) {
            throw new ConexionBDException("Error al listar los instructores.", e);
        }
        return lista;
    }

    @Override
    public void guardar(Instructor instructor) { 
        String sqlPersona = "INSERT INTO Persona (nombre, apellido, dni, email, telefono, tipo_persona, fecha_registro) VALUES (?, ?, ?, ?, ?, 'INSTRUCTOR', ?)";
        String sqlInstructor = "INSERT INTO Instructores (especialidad, sueldo, Persona_idPersona) VALUES (?, ?, ?)";
        
        Connection conn = DatabaseManager.getInstance().getConnection();
        
        try {
            conn.setAutoCommit(false);
            int idPersonaGenerado = 0;

            // 1. Guardar Persona
            try (PreparedStatement pstmtPersona = conn.prepareStatement(sqlPersona, Statement.RETURN_GENERATED_KEYS)) {
                pstmtPersona.setString(1, instructor.getNombre());
                pstmtPersona.setString(2, instructor.getApellido());
                pstmtPersona.setString(3, instructor.getDni());
                
                // CONTROL DE EMAIL: Si está vacío, se guarda como NULL para evitar error UNIQUE
                if (instructor.getEmail() == null || instructor.getEmail().trim().isEmpty()) {
                    pstmtPersona.setNull(4, java.sql.Types.VARCHAR);
                } else {
                    pstmtPersona.setString(4, instructor.getEmail());
                }
                
                // CONTROL DE TELÉFONO: Si está vacío, se guarda como NULL
                if (instructor.getTelefono() == null || instructor.getTelefono().trim().isEmpty()) {
                    pstmtPersona.setNull(5, java.sql.Types.VARCHAR);
                } else {
                    pstmtPersona.setString(5, instructor.getTelefono());
                }
                
                pstmtPersona.setDate(6, Date.valueOf(java.time.LocalDate.now())); 
                
                pstmtPersona.executeUpdate();
                try (ResultSet rs = pstmtPersona.getGeneratedKeys()) {
                    if (rs.next()) { idPersonaGenerado = rs.getInt(1); }
                }
            }

            // 2. Guardar Instructor
            try (PreparedStatement pstmtInstructor = conn.prepareStatement(sqlInstructor)) {
                pstmtInstructor.setString(1, instructor.getEspecialidad());
                pstmtInstructor.setDouble(2, instructor.getSueldo());
                pstmtInstructor.setInt(3, idPersonaGenerado);
                pstmtInstructor.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ex) { }
            // CORRECCIÓN: Adjuntamos e.getMessage() para saber exactamente qué falló en la BD
            throw new ConexionBDException("Error al guardar: " + e.getMessage(), e);
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException e) { }
        }
    }
    
    @Override
    public Optional<Instructor> buscarPorId(int id) {
        String sql = "SELECT p.*, i.especialidad, i.sueldo FROM Persona p INNER JOIN Instructores i ON p.idPersona = i.Persona_idPersona WHERE p.idPersona = ?";
        Connection conn = DatabaseManager.getInstance().getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Instructor(
                        rs.getInt("idPersona"), rs.getString("nombre"), rs.getString("apellido"),
                        rs.getString("dni"), rs.getString("email"), rs.getString("telefono"),
                        rs.getString("especialidad"), rs.getDouble("sueldo")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new ConexionBDException("Error al buscar el instructor.", e);
        }
        return Optional.empty();
    }

    @Override
    public void actualizar(Instructor instructor) {
        String sqlPersona = "UPDATE Persona SET nombre = ?, apellido = ?, dni = ?, email = ?, telefono = ? WHERE idPersona = ?";
        String sqlInstructor = "UPDATE Instructores SET especialidad = ?, sueldo = ? WHERE Persona_idPersona = ?";
        Connection conn = DatabaseManager.getInstance().getConnection();
        try {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmtPersona = conn.prepareStatement(sqlPersona)) {
                pstmtPersona.setString(1, instructor.getNombre());
                pstmtPersona.setString(2, instructor.getApellido());
                pstmtPersona.setString(3, instructor.getDni());
                pstmtPersona.setString(4, instructor.getEmail());
                pstmtPersona.setString(5, instructor.getTelefono());
                pstmtPersona.setInt(6, instructor.getId()); // Usa idPersona correctamente
                pstmtPersona.executeUpdate();
            }
            try (PreparedStatement pstmtInstructor = conn.prepareStatement(sqlInstructor)) {
                pstmtInstructor.setString(1, instructor.getEspecialidad());
                pstmtInstructor.setDouble(2, instructor.getSueldo());
                pstmtInstructor.setInt(3, instructor.getId()); // Persona_idPersona es igual a idPersona
                pstmtInstructor.executeUpdate();
            }
            conn.commit();
        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ex) { }
            throw new ConexionBDException("Error al actualizar el instructor.", e);
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException e) { }
        }
    }

    @Override
    public void eliminar(int id) {
        String sqlInstructor = "DELETE FROM Instructores WHERE Persona_idPersona = ?";
        String sqlPersona = "DELETE FROM Persona WHERE idPersona = ?";
        Connection conn = DatabaseManager.getInstance().getConnection();
        try {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmtInst = conn.prepareStatement(sqlInstructor)) {
                pstmtInst.setInt(1, id); // id de persona mapeado correctamente
                pstmtInst.executeUpdate();
            }
            try (PreparedStatement pstmtPer = conn.prepareStatement(sqlPersona)) {
                pstmtPer.setInt(1, id); // id de persona mapeado correctamente
                pstmtPer.executeUpdate();
            }
            conn.commit();
        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ex) { }
            throw new RuntimeException("No se puede eliminar el instructor porque está asignado a clases existentes.");
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException e) { }
        }
    }
}