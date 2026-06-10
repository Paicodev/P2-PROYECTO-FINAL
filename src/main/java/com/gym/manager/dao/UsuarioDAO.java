package com.gym.manager.dao;

import com.gym.manager.model.UsuarioSistema;
import com.gym.manager.model.RolUsuario;
import com.gym.manager.util.DatabaseManager;
import com.gym.manager.exceptions.ConexionBDException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

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
        throw new UnsupportedOperationException("Método guardar aún no implementado.");
    }

    @Override
    public Optional<UsuarioSistema> buscarPorId(int id) {
        throw new UnsupportedOperationException("Método buscarPorId aún no implementado.");
    }

    @Override
    public List<UsuarioSistema> obtenerTodos() {
         throw new UnsupportedOperationException("Método obtenerTodos aún no implementado.");
    }

    @Override
    public void actualizar(UsuarioSistema usuario) {
         throw new UnsupportedOperationException("Método actualizar aún no implementado.");
    }

    @Override
    public void eliminar(int id) {
         throw new UnsupportedOperationException("Método eliminar aún no implementado.");
    }
}