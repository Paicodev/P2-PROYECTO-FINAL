
package com.gym.manager.service;

import com.gym.manager.dao.InstructorDAO;
import com.gym.manager.dao.PagoDAO;
import com.gym.manager.model.Instructor;
import com.gym.manager.model.Pago;
import com.gym.manager.model.enums.EstadoPago;

import java.util.List;

public class ReporteService {

    private final PagoDAO pagoDAO;
    private final InstructorDAO instructorDAO;

    // Constructor principal
    public ReporteService() {
        this.pagoDAO = new PagoDAO();
        this.instructorDAO = new InstructorDAO();
    }

    // Constructor con inyección de dependencias para las pruebas
    public ReporteService(PagoDAO pagoDAO, InstructorDAO instructorDAO) {
        this.pagoDAO = pagoDAO;
        this.instructorDAO = instructorDAO;
    }

    // Calcula los ingresos de todos los pagos realizados
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

    // Calcula los gastos fijos correspondientes a los sueldos
    public double calcularGastosFijos() {
        List<Instructor> instructores = instructorDAO.obtenerTodos();
        double total = 0;

        for (Instructor instructor : instructores) {
            total += instructor.getSueldo();
        }

        return total;
    }

    // Calcula el balance general
    public double calcularBalanceNeto() {
        double ingresos = calcularIngresos();
        double gastosFijos = calcularGastosFijos();

        return ingresos - gastosFijos;
    }

    // Calcula el balance de un mes y año determinados
    public double calcularBalancePorMes(int mes, int anio) {
        List<Pago> pagos = pagoDAO.obtenerTodos();
        double ingresos = 0;

        for (Pago pago : pagos) {
            if (pago.getFecha() != null
                    && pago.getFecha().getMonthValue() == mes
                    && pago.getFecha().getYear() == anio
                    && pago.getEstado() == EstadoPago.PAGADO) {

                ingresos += pago.getMonto();
            }
        }

        double gastos = calcularGastosFijos();

        return ingresos - gastos;
    }

    // Calcula el balance con ingresos y gastos ya obtenidos
    public double calcularBalanceNeto(double ingresos, double gastosFijos) {
        return ingresos - gastosFijos;
    }
}