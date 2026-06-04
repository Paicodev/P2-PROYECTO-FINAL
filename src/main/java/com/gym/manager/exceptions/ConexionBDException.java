package com.gym.manager.exceptions;

/**
 * Excepción personalizada para errores de conexión a la base de datos.
 *
 * Hereda de RuntimeException (unchecked) porque un fallo de conexión
 * normalmente es irrecuperable en tiempo de ejecución y no queremos
 * obligar a todos los callers a hacer try-catch.
 */
public class ConexionBDException extends RuntimeException {

    public ConexionBDException(String mensaje) {
        super(mensaje);
    }

    /**
     * Constructor con causa: preserva el stack trace original del SQLException
     * o ClassNotFoundException para que el log sea más informativo.
     */
    public ConexionBDException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}