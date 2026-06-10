package com.gym.manager.dao;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase de pruebas unitarias para UsuarioDAO.
 * Verifica la lógica de conexión y validación de credenciales.
 */
public class UsuarioDAOTest {

    /**
     * Prueba que un login con datos inexistentes devuelva falso en lugar de fallar.
     */
    @Test
    public void testValidarLogin_CredencialesIncorrectas_RetornaFalse() {
        UsuarioDAO dao = new UsuarioDAO();
        System.out.println("Iniciando prueba de Login con datos falsos...");
        // Ejecutamos la prueba con un usuario que sabemos que no existe
        boolean resultado = dao.validarLogin("hacker", "claveMala123");
        
        // Verificamos que el resultado sea FALSE
        System.out.println("El resultado de la BD fue: " + resultado);
        assertFalse(resultado, "El sistema no debe permitir el ingreso con credenciales inválidas.");
    }

    /**
     * Prueba un login exitoso.
     * Para que este test pase, se debe insertar manualmente un usuario
     */
    @Test
    public void testValidarLogin_CredencialesCorrectas_RetornaTrue() {
        UsuarioDAO dao = new UsuarioDAO();
        boolean resultado = dao.validarLogin("admin", "1234");
        assertTrue(resultado, "El sistema debe retornar true si el usuario existe.");
        
    }
}