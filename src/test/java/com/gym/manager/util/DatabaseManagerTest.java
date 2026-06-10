package com.gym.manager.util;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para la clase DatabaseManager.
 * @author Zoe Brambilla
 */
class DatabaseManagerTest {

    // ── Test 1: la instancia no debe ser nula
    @Test
    @DisplayName("getInstance() no debe retornar null")
    void testInstanciaNoEsNula() {
        DatabaseManager manager = DatabaseManager.getInstance();

        assertNotNull(manager, "La instancia del DatabaseManager no debería ser null.");
    }

    // ── Test 2: Singleton — siempre devuelve el mismo objeto
    @Test
    @DisplayName("Singleton: dos llamadas a getInstance() devuelven el mismo objeto")
    void testSingletonMismaInstancia() {
        DatabaseManager instancia1 = DatabaseManager.getInstance();
        DatabaseManager instancia2 = DatabaseManager.getInstance();

        // assertSame verifica que sean el MISMO objeto en memoria (==)
        // no solo que sean "iguales" con equals()
        assertSame(instancia1, instancia2,
                "Singleton roto: se crearon dos instancias distintas.");
    }

    // ── Test 3: la conexión no debe ser nula
    @Test
    @DisplayName("getConnection() no debe retornar null")
    void testConexionNoEsNula() {
        Connection conexion = DatabaseManager.getInstance().getConnection();

        assertNotNull(conexion,
                "La conexión es null. Verificá que MySQL esté corriendo " +
                "y que la URL/USER/PASSWORD en DatabaseManager sean correctos.");
    }

    // ── Test 4: la conexión debe estar abierta 
    @Test
    @DisplayName("La conexión debe estar abierta (isClosed = false)")
    void testConexionEstaAbierta() throws SQLException {
        Connection conexion = DatabaseManager.getInstance().getConnection();

        assertFalse(conexion.isClosed(),
                "La conexión debería estar abierta.");
    }

    // ── Cierre: se ejecuta UNA VEZ después de todos los tests 
    @AfterAll
    static void cerrarConexion() {
        DatabaseManager.getInstance().cerrarConexion();
    }
}