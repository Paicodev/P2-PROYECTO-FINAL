package com.gym.manager.service;

import com.gym.manager.dao.PlanDAO;
import com.gym.manager.exceptions.DatosInvalidosException;
import com.gym.manager.model.Plan;

import java.util.List;
import java.util.Optional;

public class PlanService {

    private PlanDAO planDAO;

    public PlanService() {
        this.planDAO = new PlanDAO();
    }

    public void guardarPlan(Plan plan) {
        // validaciones basicas del plan
        if (plan.getPrecioMensual() <= 0) {
            throw new DatosInvalidosException("El precio del plan debe ser mayor a cero.");
        }
        if (plan.getDuracionMeses() <= 0) {
            throw new DatosInvalidosException("La duración en meses debe ser mayor a cero.");
        }

        // Si pasa las validaciones, se guarda el plan en la base de datos
        planDAO.guardar(plan);
    }

    public void actualizarPlan(Plan plan) {
        if (plan.getPrecioMensual() <= 0 || plan.getDuracionMeses() <= 0) {
            throw new DatosInvalidosException("Precio y duración deben ser mayores a cero.");
        }
        planDAO.actualizar(plan);
    }

    //métodos que solo conectan la vista con el DAO sin validación extra
    public void eliminarPlan(int id) {
        planDAO.eliminar(id);
    }

    public List<Plan> obtenerTodos() {
        return planDAO.obtenerTodos();
    }

    public Optional<Plan> buscarPorId(int id) {
        return planDAO.buscarPorId(id);
    }
}
