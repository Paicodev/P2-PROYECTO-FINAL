package com.gym.manager.dao;

import com.gym.manager.model.UsuarioSistema;
import com.gym.manager.model.enums.RolUsuario;
import com.gym.manager.util.DatabaseManager;
import com.gym.manager.exceptions.ConexionBDException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.sql.Statement;
import java.sql.Timestamp;

/**
 * Data Access Object para la entidad UsuarioSistema.
 * Maneja la autenticación y el CRUD de los usuarios del sistema.
 */
public class UsuarioDAO implements DAO<UsuarioSistema> {

    /**
     * Valida si las credenciales ingresadas coinciden con un registro en la BD.
     * Utiliza consultas preparadas (PreparedStatement) para evitar Inyección SQL.
     * * @param username El nombre de usuario ingresado
     * @param password La contraseña ingresada
     * @return true si las credenciales son correctas, false si no lo son
     * @throws ConexionBDException si ocurre un error de comunicación con MySQL
     */
    public boolean validarLogin(String username, String password) {
        String sql = "SELECT * FROM UsuarioSistema WHERE username = ? AND password_hash = ?";
        
        //Obtenemos la conexión compartida desde DatabaseManager
        Connection conn = DatabaseManager.getInstance().getConnection();
        
        //Solo el PreparedStatement va dentro del try-with-resources
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

    // Método nuevo en UsuarioDAO — lo agrega tu compañero
    public String obtenerRol(String username) {
    String sql = "SELECT rol FROM UsuarioSistema WHERE username = ?";
    // ... ejecuta la query y devuelve el rol como String
        Connection conn = DatabaseManager.getInstance().getConnection();
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                
                pstmt.setString(1, username);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("rol");
                    } else {
                        return null; // o lanzar excepción si el usuario no existe
                    }
                }
                
            } catch (SQLException e) {
                throw new ConexionBDException("Error al obtener el rol del usuario en la base de datos.", e);
            }
}

    // =========================================================================
    // MÉTODOS OBLIGATORIOS POR LA INTERFAZ DAO<T>
    // =========================================================================

    @Override
    public void guardar(UsuarioSistema usuario) {
        String sql = "INSERT INTO UsuarioSistema (username, password_hash, rol, ultimo_acceso, Persona_idPersona) VALUES (?, ?, ?, ?, ?)";
        Connection conn = DatabaseManager.getInstance().getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, usuario.getUsername());
            pstmt.setString(2, usuario.getPasswordHash());
            pstmt.setString(3, usuario.getRol().name());
            
            if (usuario.getUltimoAcceso() != null) {
                pstmt.setTimestamp(4, Timestamp.valueOf(usuario.getUltimoAcceso()));
            } else {
                pstmt.setNull(4, java.sql.Types.TIMESTAMP);
            }
            pstmt.setInt(5, usuario.getPersonaIdPersona());

            pstmt.executeUpdate();

            // Recuperamos el ID autoincremental asignado por MySQL
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    usuario.setIdUsuarioSistema(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new ConexionBDException("Error al guardar el nuevo usuario en el sistema.", e);
        }
    }

    @Override
    public Optional<UsuarioSistema> buscarPorId(int id) {
        String sql = "SELECT * FROM UsuarioSistema WHERE idUsuarioSistema = ?";
        Connection conn = DatabaseManager.getInstance().getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearUsuario(rs));
                }
            }
        } catch (SQLException e) {
            throw new ConexionBDException("Error al buscar el usuario con ID: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<UsuarioSistema> obtenerTodos() {
        List<UsuarioSistema> lista = new ArrayList<>();
        String sql = "SELECT * FROM UsuarioSistema";
        Connection conn = DatabaseManager.getInstance().getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearUsuario(rs));
            }
        } catch (SQLException e) {
            throw new ConexionBDException("Error al listar los usuarios del sistema.", e);
        }
        return lista;
    }

    @Override
    public void actualizar(UsuarioSistema usuario) {
        String sql = "UPDATE UsuarioSistema SET username = ?, password_hash = ?, rol = ?, ultimo_acceso = ? WHERE idUsuarioSistema = ?";
        Connection conn = DatabaseManager.getInstance().getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, usuario.getUsername());
            pstmt.setString(2, usuario.getPasswordHash());
            pstmt.setString(3, usuario.getRol().name());
            
            if (usuario.getUltimoAcceso() != null) {
                pstmt.setTimestamp(4, Timestamp.valueOf(usuario.getUltimoAcceso()));
            } else {
                pstmt.setNull(4, java.sql.Types.TIMESTAMP);
            }
            pstmt.setInt(5, usuario.getIdUsuarioSistema());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new ConexionBDException("Error al actualizar los datos del usuario.", e);
        }
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM UsuarioSistema WHERE idUsuarioSistema = ?";
        Connection conn = DatabaseManager.getInstance().getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new ConexionBDException("Error al eliminar el usuario con ID: " + id, e);
        }
    }

    /**
     * Método auxiliar privado para mapear las filas de la base de datos a objetos Java.
     */
    private UsuarioSistema mapearUsuario(ResultSet rs) throws SQLException {
        UsuarioSistema user = new UsuarioSistema();
        user.setIdUsuarioSistema(rs.getInt("idUsuarioSistema"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setRol(RolUsuario.valueOf(rs.getString("rol")));
        
        Timestamp timestamp = rs.getTimestamp("ultimo_acceso");
        if (timestamp != null) {
            user.setUltimoAcceso(timestamp.toLocalDateTime());
        }
        user.setPersonaIdPersona(rs.getInt("Persona_idPersona"));
        return user;
    }
}