package com.gym.manager.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gym.manager.dao.MiembroDAO;
import com.gym.manager.dao.PagoDAO;
import com.gym.manager.exceptions.DatosInvalidosException;
import com.gym.manager.model.Miembro;
import com.gym.manager.model.Pago;
import com.gym.manager.model.enums.EstadoMiembro;
import com.gym.manager.model.enums.EstadoPago;
import com.gym.manager.model.enums.TipoPago;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de la Capa de Negocio (PagoService con Mocks)")
public class PagoServiceTest {

    static {
        System.setProperty("net.bytebuddy.experimental", "true");
    }

    @Mock
    private PagoDAO pagoDAO; // Simulación del DAO de Pagos

    @Mock
    private MiembroDAO miembroDAO; // Simulación del DAO de Miembros

    @InjectMocks
    private PagoService pagoService; // Clase real bajo prueba, Mockito le inyecta los mocks de arriba

    private Miembro miembroPrueba;

    @BeforeEach
    void setUp() {
        // Configuramos un socio en memoria para asociar a los pagos
        miembroPrueba = new Miembro(
                LocalDate.now().minusMonths(1),
                LocalDate.now().plusDays(10),
                null,
                EstadoMiembro.ACTIVO,
                1,
                "Carlos", "Tevez", "12345678", "carlos@gym.com", "1122334455");
    }

    @Test
    @DisplayName("Debe lanzar DatosInvalidosException si el pago a registrar es nulo")
    void testRegistrarPago_PagoNulo_LanzaDatosInvalidosException() {
        DatosInvalidosException ex = assertThrows(
                DatosInvalidosException.class,
                () -> pagoService.registrarPago(null));
        assertTrue(ex.getMessage().contains("no puede ser nulo"));
        verify(pagoDAO, never()).guardar(any());
    }

    @Test
    @DisplayName("Debe lanzar DatosInvalidosException si el monto es menor o igual a cero")
    void testRegistrarPago_MontoMenorOIgualACero_LanzaDatosInvalidosException() {
        Pago pagoMontoInvalido = new Pago(0, miembroPrueba, 0.0, LocalDateTime.now(), TipoPago.MENSUALIDAD,
                EstadoPago.PAGADO, "Monto 0");
        DatosInvalidosException ex = assertThrows(
                DatosInvalidosException.class,
                () -> pagoService.registrarPago(pagoMontoInvalido));
        assertTrue(ex.getMessage().contains("mayor a 0"));
        verify(pagoDAO, never()).guardar(any()); // Aseguramos que NUNCA intentó guardar en la BD
    }

    @Test
    @DisplayName("Debe lanzar DatosInvalidosException si el pago no tiene miembro asignado")
    void testRegistrarPago_MiembroNulo_LanzaDatosInvalidosException() {
        Pago pagoSinMiembro = new Pago(0, null, 15000.0, LocalDateTime.now(), TipoPago.MENSUALIDAD, EstadoPago.PAGADO,
                "Sin miembro");
        DatosInvalidosException ex = assertThrows(
                DatosInvalidosException.class,
                () -> pagoService.registrarPago(pagoSinMiembro));
        assertTrue(ex.getMessage().contains("miembro válido"));
        verify(pagoDAO, never()).guardar(any());
    }

    @Test
    @DisplayName("Debe rechazar el pago si el socio ya abonó una mensualidad en el mismo mes")
    void testRegistrarPago_MensualidadDuplicadaEnElMismoMes_LanzaExcepcion() {
        // Arrange: Le ordenamos al mock simular que el socio ID 1 ya pagó este mes
        when(pagoDAO.yaPagoEnMes(eq(1), eq(TipoPago.MENSUALIDAD), any(LocalDate.class))).thenReturn(true);
        Pago pagoDuplicado = new Pago(0, miembroPrueba, 15000.0, LocalDateTime.now(), TipoPago.MENSUALIDAD,
                EstadoPago.PAGADO, "Pago doble");
        // Act & Assert
        DatosInvalidosException ex = assertThrows(
                DatosInvalidosException.class,
                () -> pagoService.registrarPago(pagoDuplicado));
        assertTrue(ex.getMessage().contains("ya abonó una mensualidad este mes"));
        // Regla de negocio crítica: ¡No debe guardar el pago repetido!
        verify(pagoDAO, never()).guardar(any());
    }

    @Test
    @DisplayName("Debe lanzar DatosInvalidosException al intentar eliminar un pago con ID inválido")
    void testEliminarPago_IdInvalido_LanzaDatosInvalidosException() {
        assertThrows(
                DatosInvalidosException.class,
                () -> pagoService.eliminarPago(0));
        verify(pagoDAO, never()).eliminar(anyInt());
    }

    @Test
    @DisplayName("Debe invocar a pagoDAO.eliminar cuando el ID es válido")
    void testEliminarPago_IdValido_InvocaDAO() {
        // Act
        pagoService.eliminarPago(10);
        // Assert: Verificamos que el Service haya delegado la eliminación al DAO con el
        // ID correcto
        verify(pagoDAO, times(1)).eliminar(10);
    }

    @Test
    @DisplayName("Debe retornar el pago esperado al buscar por ID válido")
    void testBuscarPorId_IdValido_RetornaPago() {
        // Arrange
        Pago pagoEsperado = new Pago(5, miembroPrueba, 12000.0, LocalDateTime.now(), TipoPago.MENSUALIDAD,
                EstadoPago.PAGADO, "Pago 5");
        when(pagoDAO.buscarPorId(5)).thenReturn(Optional.of(pagoEsperado));
        // Act
        Optional<Pago> resultado = pagoService.buscarPorId(5);
        // Assert
        assertTrue(resultado.isPresent(), "El pago debe estar presente");
        assertEquals(5, resultado.get().getId());
        assertEquals(12000.0, resultado.get().getMonto());
        verify(pagoDAO).buscarPorId(5);
    }
}
