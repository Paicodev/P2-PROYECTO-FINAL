package com.gym.manager.dao;

import com.gym.manager.model.UsuarioSistema;
import com.gym.manager.model.enums.RolUsuario;
import com.gym.manager.util.DatabaseManager;
import com.gym.manager.exceptions.ConexionBDException;
import com.gym.manager.interfaces.DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioDAO implements DAO<UsuarioSistema> {

    public boolean validarLogin(String username, String password) {
        String sql = "SELECT * FROM UsuarioSistema WHERE username = ? AND password_hash = ?";
        Connection conn = DatabaseManager.getInstance().getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new ConexionBDException("Error al validar el login en la base de datos.", e);
        }
    }

    public String obtenerRol(String username) {
        String sql = "SELECT rol FROM UsuarioSistema WHERE username = ?";
        Connection conn = DatabaseManager.getInstance().getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getString("rol");
                return null;
            }
        } catch (SQLException e) {
            throw new ConexionBDException("Error al obtener el rol del usuario.", e);
        }
    }

    @Override
    public void guardar(UsuarioSistema usuario) {
        String sqlPersona = "INSERT INTO Persona (nombre, apellido, dni, tipo_persona, fecha_registro) VALUES (?, ?, ?, ?, CURDATE())";
        String sqlUsuario = "INSERT INTO UsuarioSistema (username, password_hash, rol, Persona_idPersona) VALUES (?, ?, ?, ?)";
        Connection conn = DatabaseManager.getInstance().getConnection();

        try {
            conn.setAutoCommit(false);
            int idPersonaGenerado = 0;

            // 1. Insertamos a la Persona (Staff)
            try (PreparedStatement pstP = conn.prepareStatement(sqlPersona, Statement.RETURN_GENERATED_KEYS)) {
                pstP.setString(1, usuario.getNombre());
                pstP.setString(2, usuario.getApellido());
                pstP.setString(3, usuario.getDni());
                pstP.setString(4, usuario.getRol().name());
                pstP.executeUpdate();
                try (ResultSet rs = pstP.getGeneratedKeys()) {
                    if (rs.next()) idPersonaGenerado = rs.getInt(1);
                }
            }

            // 2. Insertamos al UsuarioSistema
            try (PreparedStatement pstU = conn.prepareStatement(sqlUsuario, Statement.RETURN_GENERATED_KEYS)) {
                pstU.setString(1, usuario.getUsername());
                pstU.setString(2, usuario.getPasswordHash());
                pstU.setString(3, usuario.getRol().name());
                pstU.setInt(4, idPersonaGenerado);
                pstU.executeUpdate();
                try (ResultSet rs = pstU.getGeneratedKeys()) {
                    if (rs.next()) usuario.setIdUsuarioSistema(rs.getInt(1));
                }
            }
            conn.commit();
        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ex) { }
            throw new ConexionBDException("Error al registrar el staff y su usuario en la base de datos.", e);
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException e) { }
        }
    }

    @Override
    public List<UsuarioSistema> obtenerTodos() {
        List<UsuarioSistema> lista = new ArrayList<>();
        // Usamos JOIN para traer los datos de la persona
        String sql = "SELECT u.*, p.nombre, p.apellido, p.dni FROM UsuarioSistema u INNER JOIN Persona p ON u.Persona_idPersona = p.idPersona";
        Connection conn = DatabaseManager.getInstance().getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearUsuarioConPersona(rs));
            }
        } catch (SQLException e) {
            throw new ConexionBDException("Error al listar los usuarios del sistema.", e);
        }
        return lista;
    }

    @Override
    public void actualizar(UsuarioSistema usuario) {
        String sqlPersona = "UPDATE Persona SET nombre = ?, apellido = ?, dni = ?, tipo_persona = ? WHERE idPersona = ?";
        String sqlUsuario = "UPDATE UsuarioSistema SET username = ?, password_hash = ?, rol = ? WHERE idUsuarioSistema = ?";
        Connection conn = DatabaseManager.getInstance().getConnection();

        try {
            conn.setAutoCommit(false);
            
            try (PreparedStatement pstP = conn.prepareStatement(sqlPersona)) {
                pstP.setString(1, usuario.getNombre());
                pstP.setString(2, usuario.getApellido());
                pstP.setString(3, usuario.getDni());
                pstP.setString(4, usuario.getRol().name());
                pstP.setInt(5, usuario.getPersonaIdPersona());
                pstP.executeUpdate();
            }

            try (PreparedStatement pstU = conn.prepareStatement(sqlUsuario)) {
                pstU.setString(1, usuario.getUsername());
                pstU.setString(2, usuario.getPasswordHash());
                pstU.setString(3, usuario.getRol().name());
                pstU.setInt(4, usuario.getIdUsuarioSistema());
                pstU.executeUpdate();
            }
            conn.commit();
        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ex) { }
            throw new ConexionBDException("Error al actualizar los datos del usuario.", e);
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException e) { }
        }
    }

    @Override
    public void eliminar(int id) {
        // Al eliminar el usuario, MySQL borra la Persona en cascada (si configuraste bien el ON DELETE CASCADE en schema.sql)
        // Sino, borramos directo la Persona y el Usuario cae con ella.
        String sql = "DELETE FROM Persona WHERE idPersona = (SELECT Persona_idPersona FROM UsuarioSistema WHERE idUsuarioSistema = ?)";
        Connection conn = DatabaseManager.getInstance().getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new ConexionBDException("Error al revocar el acceso del usuario con ID: " + id, e);
        }
    }

    @Override
    public Optional<UsuarioSistema> buscarPorId(int id) { return Optional.empty(); }

    private UsuarioSistema mapearUsuarioConPersona(ResultSet rs) throws SQLException {
        UsuarioSistema user = new UsuarioSistema();
        user.setIdUsuarioSistema(rs.getInt("idUsuarioSistema"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setRol(RolUsuario.valueOf(rs.getString("rol")));
        
        Timestamp timestamp = rs.getTimestamp("ultimo_acceso");
        if (timestamp != null) user.setUltimoAcceso(timestamp.toLocalDateTime());
        
        user.setPersonaIdPersona(rs.getInt("Persona_idPersona"));
        user.setNombre(rs.getString("nombre"));
        user.setApellido(rs.getString("apellido"));
        user.setDni(rs.getString("dni"));
        
        return user;
    }
}