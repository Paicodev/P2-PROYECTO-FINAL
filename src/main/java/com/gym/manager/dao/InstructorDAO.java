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
        // Hacemos JOIN entre Persona e Instructor para traer los datos completos
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
                // Le asignamos también el ID interno de la tabla instructor
                instructor.setId(rs.getInt("id_instructor")); 
                lista.add(instructor);
            }
        } catch (SQLException e) {
            throw new ConexionBDException("Error al listar los instructores.", e);
        }
        return lista;
    }

    // --- MÉTODOS OBLIGATORIOS (A implementar en el futuro si piden ABM completo de Instructores) ---
    @Override
    public void guardar(Instructor instructor) { 
        throw new UnsupportedOperationException("No implementado aún."); }
    @Override
    public Optional<Instructor> buscarPorId(int id) {
        throw new UnsupportedOperationException("No implementado aún."); }
    @Override
    public void actualizar(Instructor instructor) { 
        throw new UnsupportedOperationException("No implementado aún."); }
    @Override
    public void eliminar(int id) { throw new UnsupportedOperationException("No implementado aún."); }
}