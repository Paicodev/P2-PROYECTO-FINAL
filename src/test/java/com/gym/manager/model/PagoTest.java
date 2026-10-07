package com.gym.manager.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.gym.manager.model.enums.EstadoPago;
import com.gym.manager.model.enums.TipoPago;

@DisplayName("Test Unitarios de la Entidad Pago")
public class PagoTest {

    private Pago pagoVencido;
    private Pago pagoAlDia;

    @BeforeEach
    void setUp() {
        // Configuramos dos pagos en memoria antes de cada test
        pagoVencido = new Pago(1, null, 10000.0, LocalDateTime.now(), TipoPago.MENSUALIDAD, EstadoPago.VENCIDO,
                "Cuota vencida");
        pagoAlDia = new Pago(2, null, 10000.0, LocalDateTime.now(), TipoPago.MENSUALIDAD, EstadoPago.PAGADO,
                "Cuota al día");
    }

    @Test
    @DisplayName("Debe calcular el 10% de recargo si el estado del pago es VENCIDO")
    void testCalcularMora_EstadoVencido_RetornandoMoraCorrecta() {
        double mora = pagoVencido.calcularMora(0.10);
        assertEquals(1000.0, mora, 0.001, "El 10% de 10.000 debe ser exactamente 1.000");
    }

    @Test
    @DisplayName("Debe retornar 0.0 de recargo si el estado del pago es PAGADO")
    void testCalcularMora_EstadoPagado_RetornaCero() {
        double mora = pagoAlDia.calcularMora(0.10);
        assertEquals(0.0, mora, 0.001, "Un pago al día no debe generar mora");
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException si el porcentaje de mora es negativo")
    void testCalcularMora_PorcentajeNegativo_LanzaIllegalArgumentException() {
        IllegalArgumentException excepcion = assertThrows(
                IllegalArgumentException.class,
                () -> pagoVencido.calcularMora(-0.05),
                "Un porcentaje negativo debe lanzar IllegalArgumentException");
        assertTrue(excepcion.getMessage().contains("no puede ser negativo"));
    }

    @Test
    @DisplayName("Debe generar el comprobante de recibo con todos los datos esperados")
    void testGenerarRecibo_FormatoCorrecto() {
        String recibo = pagoAlDia.generarRecibo();
        assertNotNull(recibo, "El recibo no debe ser nulo");
        assertTrue(recibo.contains("Pago ID: 2"), "Debe contener el ID");
        assertTrue(recibo.contains("10000"), "Debe contener el monto");
        assertTrue(recibo.contains("PAGADO"), "Debe contener el estado");
        assertTrue(recibo.contains("Cuota al día"), "Debe contener la descripción");
    }

    @Test
    @DisplayName("Dos pagos con el mismo ID deben ser considerados iguales (equals y hashCode)")
    void testEqualsYHashCode_MismoId_SonIguales() {
        Pago pagoClon = new Pago(1, null, 5000.0, LocalDateTime.now(), TipoPago.CLASE, EstadoPago.PAGADO,
                "Otra descripcion");
        assertEquals(pagoVencido, pagoClon, "Deben ser iguales porque ambos tienen id = 1");
        assertEquals(pagoVencido.hashCode(), pagoClon.hashCode(), "Sus hashCodes deben ser idénticos");
    }
}
