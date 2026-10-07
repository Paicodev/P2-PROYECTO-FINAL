package com.gym.manager.dao;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.gym.manager.model.Inscripciones;

/**
 * Prueba para verificar el registro de inscripciones en la BD.
 */
@Disabled("Test de integración - requiere base de datos MySQL activa")
public class InscripcionesDAOTest {

    @Test
    public void testInscribirMiembroRetornaVerdadero() {
        InscripcionesDAO dao = new InscripcionesDAO();

        Inscripciones nuevaInscripcion = new Inscripciones(
                LocalDate.now(), // fechaInscripcion
                false, //asistio
                1, // id de la Clase
                1 // id del Miembro
        );

        boolean resultado = dao.registrar(nuevaInscripcion);
        assertTrue(resultado, "La operación registrar debería retornar verdadero");
    }
}
