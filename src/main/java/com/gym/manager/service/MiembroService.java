package com.gym.manager.service;

import com.gym.manager.dao.MiembroDAO;
import com.gym.manager.exceptions.DatosInvalidosException;
import com.gym.manager.model.Miembro;

import java.util.List;
import java.util.Optional;

/**
 * Contiene toda la lógica de negocio y validaciones antes de interactuar con la BD.
 */
public class MiembroService {
    private MiembroDAO miembroDAO;

    public MiembroService() {
        this.miembroDAO = new MiembroDAO();
    }

    //validaciones y reglas de negocio para guardar un nuevo miembro en el sistema
    public void guardarMiembro(Miembro miembro) {
        //Validaciones basicas heredadas de Persona
        if (!miembro.validarDatos()) {
            throw new DatosInvalidosException("Por favor, complete todos los campos obligatorios con un formato válido.");
        }

        // validar que no exista otro miembro con el mismo DNI
        Optional<Miembro> existente = miembroDAO.buscarPorDNI(miembro.getDni());
        if (existente.isPresent()) {
            throw new DatosInvalidosException("Operación rechazada: Ya existe un miembro registrado con el DNI " + miembro.getDni());
        }

        // validar que la fecha de vencimiento no sea anterior a la fecha de inscripcion
        if (miembro.getFechaVencimiento().isBefore(miembro.getFechaInscripcion())) {
            throw new DatosInvalidosException("La fecha de vencimiento no puede ser anterior a la fecha de inscripción.");
        }

        // Si pasa todas las validaciones, se guarda el miembro en la base de datos
        miembroDAO.guardar(miembro);
    }

    // valida y actualiza un miembro existente en el sistema
    public void actualizarMiembro(Miembro miembro) {
        if (!miembro.validarDatos()) {
            throw new DatosInvalidosException("Por favor, complete todos los campos obligatorios.");
        }

        // si un miembro quiere cambiar su dni, hay que validar que el nuevo no exista en el sistema
        Optional<Miembro> existente = miembroDAO.buscarPorDNI(miembro.getDni());
        if (existente.isPresent() && existente.get().getId() != miembro.getId()) {
            throw new DatosInvalidosException("El DNI ingresado ya pertenece a otro socio del gimnasio.");
        }

        miembroDAO.actualizar(miembro);
    }

    // métodos que solo conectan la vista con el DAO sin validación extra

    public void eliminarMiembro(int id) {
        miembroDAO.eliminar(id);
    }

    public Optional<Miembro> buscarPorId(int id) {
        return miembroDAO.buscarPorId(id);
    }

    public List<Miembro> obtenerTodos() {
        return miembroDAO.obtenerTodos();
    }

    public List<Miembro> obtenerActivos() {
        return miembroDAO.obtenerActivos();
    }

    public List<Miembro> obtenerPorVencer(int dias) {
        return miembroDAO.obtenerPorVencer(dias);
    }
}