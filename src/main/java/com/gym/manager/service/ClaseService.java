package com.gym.manager.service;

import com.gym.manager.dao.ClaseDAO;
import com.gym.manager.exceptions.DatosInvalidosException;
import com.gym.manager.model.ClaseGimnasio;
import java.util.List;

public class ClaseService {
    private ClaseDAO claseDAO;

    public ClaseService() {
        this.claseDAO = new ClaseDAO();
    }

    public void guardarClase(ClaseGimnasio clase) {
        // Validación básica
        if (clase.getNombre() == null || clase.getNombre().trim().isEmpty()) {
            throw new DatosInvalidosException("El nombre de la clase no puede estar vacío.");
        }
        if (clase.getInstructor() == null) {
            throw new DatosInvalidosException("Debe seleccionar un instructor válido.");
        }
        if (clase.getDuracionMinutos() <= 0) {
            throw new DatosInvalidosException("La duración debe ser mayor a 0.");
        }

    
        claseDAO.guardar(clase);
    }

    public List<ClaseGimnasio> obtenerTodas() {
        return claseDAO.obtenerTodos();
    }
}