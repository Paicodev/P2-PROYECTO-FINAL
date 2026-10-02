package com.gym.manager.dao;

import com.gym.manager.model.Inscripciones;
import com.gym.manager.util.DatabaseManager;
import java.sql.*;
import com.gym.manager.exceptions.ConexionBDException;
import java.util.ArrayList;
import java.util.List;
/**
 * DAO para Inscripciones.
 * Implementa de persistencia y consulta de inscripciones.
 */
public class InscripcionesDAO {

    public boolean registrar(Inscripciones inscripciones) {
    Connection con = DatabaseManager.getInstance().getConnection();
    String sql = "INSERT INTO Inscripciones " +
                 "(fecha_inscripcion, asistio, Clases_idClases, Miembros_idMiembros) " +
                 "VALUES (?, ?, ?, (SELECT idMiembros FROM Miembros " +
                 "WHERE Persona_idPersona = ? ORDER BY idMiembros DESC LIMIT 1))";

    try (PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setDate(1, Date.valueOf(inscripciones.getFechaInscripcion()));
        ps.setBoolean(2, inscripciones.isAsistio());
        ps.setInt(3, inscripciones.getClasesIdClases());
        ps.setInt(4, inscripciones.getMiembrosIdMiembros());

        return ps.executeUpdate() > 0;
    } catch (SQLException e) {
        throw new ConexionBDException(
            "Error de base de datos al registrar la inscripción: " + e.getMessage(),
            e
        );
    }
}

public boolean miembroActivo(int idPersona) {
    String sql = "SELECT estado FROM Miembros " +
                 "WHERE Persona_idPersona = ? ORDER BY idMiembros DESC LIMIT 1";

    try (PreparedStatement ps =
             DatabaseManager.getInstance().getConnection().prepareStatement(sql)) {
        ps.setInt(1, idPersona);

        try (ResultSet rs = ps.executeQuery()) {
            return rs.next() && "ACTIVO".equals(rs.getString("estado"));
        }
    } catch (SQLException e) {
        throw new ConexionBDException("Error al consultar el estado del miembro.", e);
    }
}

public boolean existeInscripcion(int idClase, int idPersona) {
    String sql = "SELECT 1 FROM Inscripciones i " +
                 "JOIN Miembros m ON i.Miembros_idMiembros = m.idMiembros " +
                 "WHERE i.Clases_idClases = ? AND m.Persona_idPersona = ? LIMIT 1";

    try (PreparedStatement ps =
             DatabaseManager.getInstance().getConnection().prepareStatement(sql)) {
        ps.setInt(1, idClase);
        ps.setInt(2, idPersona);

        try (ResultSet rs = ps.executeQuery()) {
            return rs.next();
        }
    } catch (SQLException e) {
        throw new ConexionBDException("Error al consultar la inscripción existente.", e);
    }
}

public String obtenerTipoClase(int idClase) {
    String sql = "SELECT tipo FROM Clases WHERE idClases = ?";

    try (PreparedStatement ps =
             DatabaseManager.getInstance().getConnection().prepareStatement(sql)) {
        ps.setInt(1, idClase);

        try (ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getString("tipo") : null;
        }
    } catch (SQLException e) {
        throw new ConexionBDException("Error al consultar el tipo de clase.", e);
    }
}

public int obtenerCapacidadMaxima(int idClase) {
    String sql = "SELECT capacidad_max FROM Clases WHERE idClases = ?";

    try (PreparedStatement ps =
             DatabaseManager.getInstance().getConnection().prepareStatement(sql)) {
        ps.setInt(1, idClase);

        try (ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt("capacidad_max") : 0;
        }
    } catch (SQLException e) {
        throw new ConexionBDException("Error al consultar la capacidad de la clase.", e);
    }
}

public int contarInscripciones(int idClase) {
    String sql = "SELECT COUNT(*) FROM Inscripciones WHERE Clases_idClases = ?";

    try (PreparedStatement ps =
             DatabaseManager.getInstance().getConnection().prepareStatement(sql)) {
        ps.setInt(1, idClase);

        try (ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    } catch (SQLException e) {
        throw new ConexionBDException("Error al consultar las inscripciones de la clase.", e);
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
