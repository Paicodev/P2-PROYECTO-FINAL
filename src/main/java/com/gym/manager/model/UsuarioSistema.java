package com.gym.manager.model;

import java.time.LocalDateTime;

import com.gym.manager.model.enums.RolUsuario;

/**
 * Clase modelo que representa a un usuario con acceso al sistema (Login).
 * Mapea directamente con la tabla 'UsuarioSistema' de la base de datos.
 */
public class UsuarioSistema {
    private int idUsuarioSistema;
    private String username;
    private String passwordHash;
    private RolUsuario rol;
    private LocalDateTime ultimoAcceso;
    private int personaIdPersona;
    private String nombre;
    private String apellido;
    private String dni;
    /**
     * Constructor vacío requerido para la instanciación desde la Base de Datos.
     */
    public UsuarioSistema() {
    }

    /**
     * Constructor completo para crear un nuevo usuario en el sistema.
     * * @param idUsuarioSistema ID único del usuario en el sistema
     * @param username Nombre de usuario para el login
     * @param passwordHash Contraseña encriptada (Hash)
     * @param rol Rol de permisos (ADMIN o RECEPCIONISTA)
     * @param ultimoAcceso Fecha y hora del último login exitoso
     * @param personaIdPersona ID de la Persona (FK) asociada a esta cuenta
     */
    public UsuarioSistema(int idUsuarioSistema, String username, String passwordHash, RolUsuario rol, LocalDateTime ultimoAcceso, int personaIdPersona) {
        this.idUsuarioSistema = idUsuarioSistema;
        this.username = username;
        this.passwordHash = passwordHash;
        this.rol = rol;
        this.ultimoAcceso = ultimoAcceso;
        this.personaIdPersona = personaIdPersona;
    }

    // --- GETTERS Y SETTERS ---

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
    
    public int getIdUsuarioSistema() { return idUsuarioSistema; }
    public void setIdUsuarioSistema(int idUsuarioSistema) { this.idUsuarioSistema = idUsuarioSistema; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public RolUsuario getRol() { return rol; }
    public void setRol(RolUsuario rol) { this.rol = rol; }

    public LocalDateTime getUltimoAcceso() { return ultimoAcceso; }
    public void setUltimoAcceso(LocalDateTime ultimoAcceso) { this.ultimoAcceso = ultimoAcceso; }

    public int getPersonaIdPersona() { return personaIdPersona; }
    public void setPersonaIdPersona(int personaIdPersona) { this.personaIdPersona = personaIdPersona; }
}