package com.gym.manager.service;

import com.gym.manager.dao.InstructorDAO;
import com.gym.manager.dao.PagoDAO;
import com.gym.manager.model.Instructor;
import com.gym.manager.model.Pago;
import com.gym.manager.model.enums.EstadoPago;
import java.util.List;

public class ReporteService {
    private PagoDAO pagoDAO;
    private InstructorDAO instructorDAO;

    public ReporteService() {
        this.pagoDAO = new PagoDAO();
        this.instructorDAO = new InstructorDAO();
    }

    // Constructor para inyección de dependencias con Mockito
    public ReporteService(PagoDAO pagoDAO, InstructorDAO instructorDAO) {
        this.pagoDAO = pagoDAO;
        this.instructorDAO = instructorDAO;
    }

    public double calcularIngresos() {
        List<Pago> pagos = pagoDAO.obtenerTodos();
        double total = 0;
        for (Pago pago : pagos) {
            if (pago.getEstado() == EstadoPago.PAGADO) {
                total += pago.getMonto();
            }
        }
        return total;
    }

    public double calcularGastosFijos() {
        List<Instructor> instructores = instructorDAO.obtenerTodos();
        double total = 0;
        for (Instructor instructor : instructores) {
            total += instructor.getSueldo();
        }
        return total;
    }

    public double calcularBalanceNeto() {
        double ingresos = calcularIngresos();
        double gastosFijos = calcularGastosFijos();
        return ingresos - gastosFijos;
    }

    public double calcularBalancePorMes(int mes, int anio) {
        List<Pago> pagos = pagoDAO.obtenerTodos();
        double ingresos = 0;

        for (Pago pago : pagos) {
            if (pago.getFecha().getMonthValue() == mes
                    && pago.getFecha().getYear() == anio
                    && pago.getEstado() == EstadoPago.PAGADO) {
                ingresos += pago.getMonto();
            }
        }
        double gastos = calcularGastosFijos();
        return ingresos - gastos;
    }
}