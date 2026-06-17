package com.gym.manager.service;

import com.gym.manager.dao.UsuarioDAO;
import com.gym.manager.exceptions.DatosInvalidosException;
import com.gym.manager.model.UsuarioSistema;
import java.util.List;

public class UsuarioService {
    private UsuarioDAO usuarioDAO;

    public UsuarioService() {
        this.usuarioDAO = new UsuarioDAO();
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
        if (usuario.getNombre() == null || usuario.getNombre().trim().isEmpty()) throw new DatosInvalidosException("El nombre no puede estar vacío.");
        if (usuario.getApellido() == null || usuario.getApellido().trim().isEmpty()) throw new DatosInvalidosException("El apellido no puede estar vacío.");
        if (usuario.getDni() == null || usuario.getDni().trim().isEmpty()) throw new DatosInvalidosException("El DNI no puede estar vacío.");
        if (usuario.getUsername() == null || usuario.getUsername().trim().isEmpty()) throw new DatosInvalidosException("El nombre de usuario no puede estar vacío.");
        if (usuario.getPasswordHash() == null || usuario.getPasswordHash().trim().isEmpty()) throw new DatosInvalidosException("La contraseña es obligatoria.");
    }
}