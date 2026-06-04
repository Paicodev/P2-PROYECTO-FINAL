package main.java.com.gym.manager.model;

/**
 * Representa los roles de acceso disponibles en el sistema.
 * Define qué permisos y vistas tendrá cada usuario al iniciar sesión.
 */
public enum RolUsuario {
    /** Acceso total al sistema (ABMs, Reportes, Configuración) */
    ADMIN,
    
    /** Acceso limitado a tareas diarias (Inscripciones, Pagos, Miembros) */
    RECEPCIONISTA
}