package com.gym.manager.dao;

import com.gym.manager.model.Inscripciones;
import com.gym.manager.util.DatabaseManager;
import java.sql.*;
import com.gym.manager.exceptions.ConexionBDException;
import com.gym.manager.exceptions.DatosInvalidosException;

/**
 * DAO para Inscripciones.
 * Implementa las operaciones CRUD y validaciones requeridas.
 */
public class InscripcionesDAO {

    public boolean registrar(Inscripciones inscripciones) {
        Connection con = DatabaseManager.getInstance().getConnection();

        try {
            // 1. VALIDAR ESTADO DEL MIEMBRO (Solo ACTIVO puede anotarse)
            String sqlEstado = "SELECT estado FROM Miembros WHERE idMiembros = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlEstado)) {
                ps.setInt(1, inscripciones.getMiembrosIdMiembros());
                ResultSet rs = ps.executeQuery();
                if (rs.next() && !rs.getString("estado").equals("ACTIVO")) {
                    throw new DatosInvalidosException("El miembro no se encuentra ACTIVO. Por favor, regularice su situación.");
                }
            }

            // 2. VALIDAR DUPLICADO (Que no se anote 2 veces en la misma clase)
            String sqlDuplicado = "SELECT idInscripciones FROM Inscripciones WHERE Clases_idClases = ? AND Miembros_idMiembros = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlDuplicado)) {
                ps.setInt(1, inscripciones.getClasesIdClases());
                ps.setInt(2, inscripciones.getMiembrosIdMiembros());
                if (ps.executeQuery().next()) {
                    throw new DatosInvalidosException("El miembro ya se encuentra inscripto en esta clase.");
                }
            }

            // 3. VALIDAR CAPACIDAD (Solo si es GRUPAL)
            String sqlCapacidad = "SELECT c.tipo, c.capacidad_max, COUNT(i.idInscripciones) as ocupados " +
                                  "FROM Clases c LEFT JOIN Inscripciones i ON c.idClases = i.Clases_idClases " +
                                  "WHERE c.idClases = ? GROUP BY c.idClases";
            try (PreparedStatement ps = con.prepareStatement(sqlCapacidad)) {
                ps.setInt(1, inscripciones.getClasesIdClases());
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    String tipo = rs.getString("tipo");
                    int capacidadMax = rs.getInt("capacidad_max");
                    int ocupados = rs.getInt("ocupados");
                    
                    if ("GRUPAL".equals(tipo) && ocupados >= capacidadMax) {
                        throw new DatosInvalidosException("La clase ha alcanzado su capacidad máxima (" + capacidadMax + " lugares).");
                    }
                    if ("PERSONAL".equals(tipo) && ocupados >= 1) {
                        throw new DatosInvalidosException("Esta es una clase personal y ya tiene un miembro asignado.");
                    }
                }
            }

            // 4. SI PASA TODAS LAS PRUEBAS, HACEMOS EL INSERT (Con Subquery para traducir el ID)
            String sqlInsert = "INSERT INTO Inscripciones (fecha_inscripcion, asistio, Clases_idClases, Miembros_idMiembros) " +
                               "VALUES (?, ?, ?, (SELECT idMiembros FROM Miembros WHERE Persona_idPersona = ?))";
            
            try (PreparedStatement ps = con.prepareStatement(sqlInsert)) {
                ps.setDate(1, Date.valueOf(inscripciones.getFechaInscripcion()));
                ps.setBoolean(2, inscripciones.isAsistio());
                ps.setInt(3, inscripciones.getClasesIdClases());
                // Le pasamos el ID de la Persona, y MySQL se encarga de buscar su ID de Miembro
                ps.setInt(4, inscripciones.getMiembrosIdMiembros()); 
                
                return ps.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            throw new ConexionBDException("Error de base de datos al registrar la inscripción: " + e.getMessage(), e);
        }
    }
    
    // Aquí irían los métodos cancelar() y verificarCapacidad() posteriormente
}