package com.gym.manager.service;

import java.util.List;

import com.gym.manager.dao.UsuarioDAO;
import com.gym.manager.exceptions.DatosInvalidosException;
import com.gym.manager.model.UsuarioSistema;

public class UsuarioService {

    private UsuarioDAO usuarioDAO;

    //Este constructor lo utilizaremos para la interfaz Swing y conectarse a MySQL
    public UsuarioService() {
        this.usuarioDAO = new UsuarioDAO();
    }

    //Este constructor se utiliza para inyectar dependencias en los tests unitarios con Mockito
    public UsuarioService(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    public void guardarUsuario(UsuarioSistema usuario) {
        validarDatos(usuario);
        usuarioDAO.guardar(usuario);
    }

    public void actualizarUsuario(UsuarioSistema usuario) {
        validarDatos(usuario);
        usuarioDAO.actualizar(usuario);
    }

    public void eliminarUsuario(int idUsuario) {
        // Regla de negocio: No se puede eliminar al Admin principal (ID 1)
        if (idUsuario == 1) {
            throw new DatosInvalidosException("Operación denegada: No se puede eliminar al Administrador principal del sistema.");
        }
        usuarioDAO.eliminar(idUsuario);
    }

    public List<UsuarioSistema> obtenerTodos() {
        return usuarioDAO.obtenerTodos();
    }

    private void validarDatos(UsuarioSistema usuario) {
        if (usuario == null) {
            throw new DatosInvalidosException("El usuario no puede ser nulo.");
        }
        if (usuario.getUsername() == null || usuario.getUsername().trim().isEmpty()) {
            throw new DatosInvalidosException("El nombre de usuario no puede estar vacío.");
        }
        if (usuario.getPasswordHash() == null || usuario.getPasswordHash().trim().isEmpty()) {
            throw new DatosInvalidosException("La contraseña es obligatoria.");
        }
        if (usuario.getRol() == null) {
            throw new DatosInvalidosException("El rol de usuario es obligatorio.");
        }
    }

    public boolean autenticar(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return false;
        }
        return usuarioDAO.validarLogin(username, password);
    }

    public String obtenerRol(String username) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }
        return usuarioDAO.obtenerRol(username);
    }

}
