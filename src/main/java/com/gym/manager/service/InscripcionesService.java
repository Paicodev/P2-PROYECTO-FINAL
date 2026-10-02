package com.gym.manager.service;

import com.gym.manager.dao.InscripcionesDAO;
import com.gym.manager.exceptions.DatosInvalidosException;
import com.gym.manager.model.Inscripciones;

public class InscripcionesService {
    private final InscripcionesDAO inscripcionesDAO;

    public InscripcionesService() {
        this(new InscripcionesDAO());
    }

    public InscripcionesService(InscripcionesDAO inscripcionesDAO) {
        this.inscripcionesDAO = inscripcionesDAO;
    }

     public boolean registrar(Inscripciones inscripcion) {
        int idPersona = inscripcion.getMiembrosIdMiembros();
        int idClase = inscripcion.getClasesIdClases();

        if (!inscripcionesDAO.miembroActivo(idPersona)) {
            throw new DatosInvalidosException(
                "El miembro no se encuentra ACTIVO. Por favor, regularice su situación."
            );
        }

        if (inscripcionesDAO.existeInscripcion(idClase, idPersona)) {
            throw new DatosInvalidosException(
                "El miembro ya se encuentra inscripto en esta clase."
            );
        }

        String tipoClase = inscripcionesDAO.obtenerTipoClase(idClase);
        if (tipoClase == null) {
            throw new DatosInvalidosException("La clase seleccionada no existe.");
        }

        int ocupados = inscripcionesDAO.contarInscripciones(idClase);

        if ("GRUPAL".equals(tipoClase)) {
            int capacidadMaxima = inscripcionesDAO.obtenerCapacidadMaxima(idClase);
            if (ocupados >= capacidadMaxima) {
                throw new DatosInvalidosException(
                    "La clase ha alcanzado su capacidad máxima (" +
                    capacidadMaxima + " lugares)."
                );
            }
        } else if ("PERSONAL".equals(tipoClase) && ocupados >= 1) {
            throw new DatosInvalidosException(
                "Esta es una clase personal y ya tiene un miembro asignado."
            );
        } else if (!"PERSONAL".equals(tipoClase)) {
            throw new DatosInvalidosException("El tipo de clase no es válido.");
        }

        return inscripcionesDAO.registrar(inscripcion);
    }
}
