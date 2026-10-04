<<<<<<< Updated upstream
public class ReporteServiceTest {
    
=======
package com.gym.manager.service;

import com.gym.manager.dao.InstructorDAO;
import com.gym.manager.dao.PagoDAO;
import com.gym.manager.model.Pago;
import com.gym.manager.model.enums.EstadoPago;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ReporteServiceTest {
    @Test
    void calcularIngresosSoloSumaPagosPagados(){
        PagoDAO pagoDAO = mock(PagoDAO.class); //creamos un DAO falso. no va a msql
        InstructorDAO instructorDAO = mock(InstructorDAO.class);

        Pago pago1 = mock(Pago.class); //creamos un pago falso cuyos valores nosotros podemos controlar.
        Pago pago2 = mock(Pago.class);
        Pago pago3 = mock(Pago.class);

        when(pago1.getMonto()).thenReturn(10000.0); //le decimos a mockiito: cuando reporte service pregunte cuando vale pago 1, responde 10.000, y cuando su estado, response pagado
        when(pago1.getEstado()).thenReturn(EstadoPago.PAGADO);

        when(pago2.getMonto()).thenReturn(5000.0);
        when(pago2.getEstado()).thenReturn(EstadoPago.PAGADO);

        when(pago3.getMonto()).thenReturn(8000.0);
        when(pago3.getEstado()).thenReturn(EstadoPago.PENDIENTE); //esto no deberia sumarse

        when(pagoDAO.obtenerTodos()) //le decimos al dao falso, cuando rs pida todos los pagos, devolve estos tres
        .thenReturn(List.of(pago1, pago2, pago3));

         ReporteService reporteService =
                new ReporteService(pagoDAO, instructorDAO);

        double resultado = reporteService.calcularIngresos();

        assertEquals(15000.0, resultado); //comprueba que el resultado obtenido sea exactamente 15.000
    }
>>>>>>> Stashed changes
}
