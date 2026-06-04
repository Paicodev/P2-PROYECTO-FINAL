package main.java.com.gym.manager.dao;

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
        
        // Try-with-resources: Cierra automáticamente la conexión, el statement y el resultset
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                // Si rs.next() es true, significa que encontró un usuario con esos datos
                return rs.next();
            }
            
        } catch (SQLException e) {
            // Lanzamos la excepcion personalizada de ConexionBDExeption para manejar errores de base de datos
            throw new ConexionBDException("Error al validar el login en la base de datos.", e);
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