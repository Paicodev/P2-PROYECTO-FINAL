package com.gym.manager.dao;

import com.gym.manager.model.Inscripciones;
import com.gym.manager.util.DatabaseManager;
import java.sql.*;
import com.gym.manager.exceptions.ConexionBDException;
import com.gym.manager.exceptions.DatosInvalidosException;
import java.util.ArrayList;
import java.util.List;
/**
 * DAO para Inscripciones.
 * Implementa las operaciones CRUD y validaciones requeridas.
 */
public class InscripcionesDAO {

    public boolean registrar(Inscripciones inscripciones) {
        Connection con = DatabaseManager.getInstance().getConnection();

       try {
            // 1. VALIDAR ESTADO DEL MIEMBRO
            String sqlEstado = "SELECT estado FROM Miembros WHERE idMiembros = (SELECT idMiembros FROM Miembros WHERE Persona_idPersona = ? ORDER BY idMiembros DESC LIMIT 1)";

            try (PreparedStatement ps = con.prepareStatement(sqlEstado)) {
                ps.setInt(1, inscripciones.getMiembrosIdMiembros());
                ResultSet rs = ps.executeQuery();
                if (rs.next() && !rs.getString("estado").equals("ACTIVO")) {
                    throw new DatosInvalidosException("El miembro no se encuentra ACTIVO. Por favor, regularice su situación.");
                }
            }

            // 2. VALIDAR DUPLICADO
            String sqlDuplicado = "SELECT idInscripciones FROM Inscripciones WHERE Clases_idClases = ? AND Miembros_idMiembros = (SELECT idMiembros FROM Miembros WHERE Persona_idPersona = ? ORDER BY idMiembros DESC LIMIT 1)";
            try (PreparedStatement ps = con.prepareStatement(sqlDuplicado)) {
                ps.setInt(1, inscripciones.getClasesIdClases());
                ps.setInt(2, inscripciones.getMiembrosIdMiembros());
                if (ps.executeQuery().next()) {
                    throw new DatosInvalidosException("El miembro ya se encuentra inscripto en esta clase.");
                }
            }

            // 3. VALIDAR CAPACIDAD 
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

            // 4. INSERT 
            String sqlInsert = "INSERT INTO Inscripciones (fecha_inscripcion, asistio, Clases_idClases, Miembros_idMiembros) " +
                               "VALUES (?, ?, ?, (SELECT idMiembros FROM Miembros WHERE Persona_idPersona = ? ORDER BY idMiembros DESC LIMIT 1))";
            
            try (PreparedStatement ps = con.prepareStatement(sqlInsert)) {
                ps.setDate(1, Date.valueOf(inscripciones.getFechaInscripcion()));
                ps.setBoolean(2, inscripciones.isAsistio());
                ps.setInt(3, inscripciones.getClasesIdClases());
                ps.setInt(4, inscripciones.getMiembrosIdMiembros()); 
                
                return ps.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            throw new ConexionBDException("Error de base de datos al registrar la inscripción: " + e.getMessage(), e);
        }
    }
    
    // MÉTODO NUEVO: DAR DE BAJA (Al borrar, automáticamente se libera un cupo en la BD)
    public void darDeBaja(int idInscripcion) {
        String sql = "DELETE FROM Inscripciones WHERE idInscripciones = ?";
        Connection con = DatabaseManager.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idInscripcion);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new ConexionBDException("Error al dar de baja la inscripción.", e);
        }
    }

    // MÉTODO ARREGLADO: Ya no cierra la conexión global, evitando bugs en otras pestañas
    public List<Object[]> obtenerTodasConDetalles() {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT i.idInscripciones, i.fecha_inscripcion, p.nombre, p.apellido, c.nombre AS nombre_clase, i.asistio " +
                     "FROM Inscripciones i " +
                     "INNER JOIN Miembros m ON i.Miembros_idMiembros = m.idMiembros " +
                     "INNER JOIN Persona p ON m.Persona_idPersona = p.idPersona " +
                     "INNER JOIN Clases c ON i.Clases_idClases = c.idClases";
                     
        Connection con = DatabaseManager.getInstance().getConnection();
        // Solo metemos en el try el Statement y el ResultSet
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
             
            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getInt("idInscripciones"),
                    rs.getDate("fecha_inscripcion"),
                    rs.getString("nombre") + " " + rs.getString("apellido"),
                    rs.getString("nombre_clase"),
                    rs.getBoolean("asistio") ? "Sí" : "No"
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}