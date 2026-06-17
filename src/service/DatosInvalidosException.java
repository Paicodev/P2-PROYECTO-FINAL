package com.gym.manager.exceptions;

/**
 * Excepción personalizada para manejar validaciones de negocio incorrectas.
 */
public class DatosInvalidosException extends RuntimeException {
    
    public DatosInvalidosException(String mensaje) {
        super(mensaje);
    }
}
