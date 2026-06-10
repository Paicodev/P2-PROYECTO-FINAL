package com.gym.manager.dao;

import com.gym.manager.model.Inscripciones;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
/**
 * Prueba para verificar el registro de inscripciones en la BD.
 */
public class InscripcionesDAOTest {

    @Test
    public void testInscribirMiembroRetornaVerdadero() {
        InscripcionesDAO dao = new InscripcionesDAO();

        Inscripciones nuevaInscripcion = new Inscripciones(
            LocalDate.now(), // fechaInscripcion
            false,           //asistio
            1,        // id de la Clase
            1     // id del Miembro
        );

    boolean resultado = dao.registrar(nuevaInscripcion);
    assertTrue(resultado, "La operación registrar debería retornar verdadero");
    }
}