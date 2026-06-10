package com.gym.manager.dao;

import com.gym.manager.model.Inscripciones;
import com.gym.manager.util.DatabaseManager;
import java.sql.*;
import com.gym.manager.exceptions.ConexionBDException;

/**
 * DAO para Inscripciones.
 * Implementa las operaciones CRUD y validaciones requeridas.
 */
public class InscripcionesDAO {

    public boolean registrar(Inscripciones inscripciones) {
        String sql = "INSERT INTO Inscripciones (fecha_inscripcion, asistio, Clases_idClases, Miembros_idMiembros) VALUES (?, ?, ?, ?)";
        try (Connection con = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(inscripciones.getFechaInscripcion()));
            ps.setBoolean(2, inscripciones.isAsistio());
            ps.setInt(3, inscripciones.getClasesIdClases());
            ps.setInt(4, inscripciones.getMiembrosIdMiembros());
        
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
                throw new ConexionBDException("Error al obtener el rol del usuario en la base de datos.", e);
            }
    }
    
    // Aquí irían los métodos cancelar() y verificarCapacidad() posteriormente
}