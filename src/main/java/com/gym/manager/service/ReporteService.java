package com.gym.manager.service;

import com.gym.manager.dao.InstructorDAO;
import com.gym.manager.dao.PagoDAO;
import com.gym.manager.model.EstadoPago;
import com.gym.manager.model.Instructor;
import com.gym.manager.model.Pago;
import com.gym.manager,model.enums.EstadoPago;
import java.util.List;


public class ReporteService {
    private PagoDAO pagoDAOL
    private instructorDAO instructorDAO;

    public ReporteService(){
        this.pagoDAO = new PagoDAO();
        this.instructorDAO = new InstructorDAO();

    }

    public double calcularIngresos(){
        List<Pago> pagos = pagoDAO.obtenerTodos();
        double total = 0;
        for(Pago pago : pagos){
            if (pago.getEstado() == EstadoPago.PAGADO) {
                total += pago.getMonto();
            }
        }
        return total;
    }
    public double CalcularGastosFijos(){
        list<Instructor> instructores = instructorDAO.obtenerTodos();
        double total = 0;
        for(Instructor instructor : instructores){
            total += instructor.getSuldo();
    }
    return total;
}
public double calcularBalanceNeto(){
    double ingresos = calcularIngresos();
    double gastosFijos = CalcularGastosFijos();
    return ingresos - gastosFijos;
}

public double calcularBalancePorMes(int mes, int anio){
    list<Pago> pagos = pagoDAO.obtenerTodos();
    double ingresos = 0;

    for (Pago pago : pagos){
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