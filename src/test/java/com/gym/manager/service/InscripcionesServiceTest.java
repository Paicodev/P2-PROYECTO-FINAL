package com.gym.manager.service;

import com.gym.manager.dao.InscripcionesDAO;
import com.gym.manager.exceptions.DatosInvalidosException;
import com.gym.manager.model.Inscripciones;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InscripcionesServiceTest {
    @Mock
    private InscripcionesDAO inscripcionesDAO;

    private InscripcionesService inscripcionesService;

    @BeforeEach
    void setUp() {
        inscripcionesService = new InscripcionesService(inscripcionesDAO);
    }

    @Test
    void rechazaInscripcionCuandoClaseGrupalEstaLlena() {
        Inscripciones inscripcion = nuevaInscripcion();
        prepararMiembroYClase("GRUPAL", 8);
        when(inscripcionesDAO.obtenerCapacidadMaxima(20)).thenReturn(8);

        assertThrows(DatosInvalidosException.class, () -> inscripcionesService.registrar(inscripcion));
        verify(inscripcionesDAO, never()).registrar(inscripcion);
    }

    @Test
    void rechazaInscripcionCuandoClasePersonalYaTieneUnMiembro() {
        Inscripciones inscripcion = nuevaInscripcion();
        prepararMiembroYClase("PERSONAL", 1);

        assertThrows(DatosInvalidosException.class, () -> inscripcionesService.registrar(inscripcion));
        verify(inscripcionesDAO, never()).registrar(inscripcion);
    }

    @Test
    void registraInscripcionCuandoHayCupoDisponible() {
        Inscripciones inscripcion = nuevaInscripcion();
        prepararMiembroYClase("GRUPAL", 7);
        when(inscripcionesDAO.obtenerCapacidadMaxima(20)).thenReturn(8);
        when(inscripcionesDAO.registrar(inscripcion)).thenReturn(true);

        assertTrue(inscripcionesService.registrar(inscripcion));
        verify(inscripcionesDAO).registrar(inscripcion);
    }

    @Test
    void rechazaInscripcionCuandoMiembroNoEstaActivo() {
        Inscripciones inscripcion = nuevaInscripcion();
        when(inscripcionesDAO.miembroActivo(10)).thenReturn(false);

        assertThrows(DatosInvalidosException.class, () -> inscripcionesService.registrar(inscripcion));
        verify(inscripcionesDAO, never()).registrar(inscripcion);
    }

    private void prepararMiembroYClase(String tipo, int ocupados) {
        when(inscripcionesDAO.miembroActivo(10)).thenReturn(true);
        when(inscripcionesDAO.existeInscripcion(20, 10)).thenReturn(false);
        when(inscripcionesDAO.obtenerTipoClase(20)).thenReturn(tipo);
        when(inscripcionesDAO.contarInscripciones(20)).thenReturn(ocupados);
    }

    private Inscripciones nuevaInscripcion() {
        return new Inscripciones(LocalDate.now(), false, 20, 10);
    }
}