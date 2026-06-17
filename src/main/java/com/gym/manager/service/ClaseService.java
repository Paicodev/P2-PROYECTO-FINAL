package com.gym.manager.service;

import com.gym.manager.dao.ClaseDAO;
import com.gym.manager.exceptions.DatosInvalidosException;
import com.gym.manager.exceptions.ConexionBDException;
import com.gym.manager.model.ClaseGimnasio;
import java.util.List;

public class ClaseService {
    private ClaseDAO claseDAO;

    public ClaseService() {
        this.claseDAO = new ClaseDAO();
    }

    public void guardarClase(ClaseGimnasio clase) {
        validarClase(clase);
        claseDAO.guardar(clase);
    }

    // MÉTODO NUEVO: ACTUALIZAR
    public void actualizarClase(ClaseGimnasio clase) {
        validarClase(clase);
        claseDAO.actualizar(clase);
    }

    // MÉTODO NUEVO: ELIMINAR (Con protección de Llaves Foráneas)
    public void eliminarClase(int idClase) {
        try {
            claseDAO.eliminar(idClase);
        } catch (ConexionBDException ex) {
            // Si MySQL tira error por la FK (Error 1451), lo atrapamos y lo hacemos amigable
            if (ex.getMessage().contains("foreign key") || ex.getCause().getMessage().contains("a foreign key constraint fails")) {
                throw new DatosInvalidosException("No se puede eliminar la clase porque actualmente tiene alumnos inscriptos. Primero debe dar de baja las inscripciones.");
            } else {
                throw ex; // Si es otro error de red, lo dejamos pasar
            }
        }
    }

    public List<ClaseGimnasio> obtenerTodas() {
        return claseDAO.obtenerTodos();
    }

    // Lógica reutilizable para no repetir código
    private void validarClase(ClaseGimnasio clase) {
        if (clase.getNombre() == null || clase.getNombre().trim().isEmpty()) {
            throw new DatosInvalidosException("El nombre de la clase no puede estar vacío.");
        }
        if (clase.getInstructor() == null) {
            throw new DatosInvalidosException("Debe seleccionar un instructor válido.");
        }
        if (clase.getDuracionMinutos() <= 0) {
            throw new DatosInvalidosException("La duración debe ser mayor a 0.");
        }
    }
}