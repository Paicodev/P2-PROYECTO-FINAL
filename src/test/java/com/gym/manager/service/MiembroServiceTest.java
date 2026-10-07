package com.gym.manager.service;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gym.manager.dao.MiembroDAO;
import com.gym.manager.exceptions.DatosInvalidosException;
import com.gym.manager.model.Miembro;
import com.gym.manager.model.enums.EstadoMiembro;

@ExtendWith(MockitoExtension.class)
public class MiembroServiceTest {

    static {
        System.setProperty("net.bytebuddy.experimental", "true");
    }

    @Mock
    private MiembroDAO miembroDAO;

    @InjectMocks
    private MiembroService miembroService;

    // Método auxiliar para crear un miembro válido rápidamente
    private Miembro crearMiembro(int id, String dni, LocalDate inscripcion, LocalDate vencimiento) {
        return new Miembro(
                inscripcion,
                vencimiento,
                null, // Plan
                EstadoMiembro.ACTIVO,
                id,
                "Juan",
                "Perez",
                dni,
                "juan@gym.com",
                "1122334455"
        );
    }

    @Test
    @DisplayName("Debe guardar un miembro exitosamente cuando los datos son válidos y el DNI no existe")
    public void testGuardarMiembro_DatosValidos_InvocaGuardarEnDAO() {
        // Arrange: creamos un miembro que vence el mes que viene
        Miembro nuevo = crearMiembro(0, "40123456", LocalDate.now(), LocalDate.now().plusMonths(1));

        // El DAO dice que NO existe nadie con ese DNI
        when(miembroDAO.buscarPorDNI("40123456")).thenReturn(Optional.empty());

        // Act
        miembroService.guardarMiembro(nuevo);

        // Assert
        verify(miembroDAO, times(1)).guardar(nuevo);
    }

    @Test
    @DisplayName("Debe lanzar excepción y NO guardar si el DNI ya pertenece a otro socio")
    public void testGuardarMiembro_DniDuplicado_LanzaExcepcionYNoGuarda() {
        // Arrange
        Miembro nuevo = crearMiembro(0, "40123456", LocalDate.now(), LocalDate.now().plusMonths(1));
        Miembro existente = crearMiembro(1, "40123456", LocalDate.now(), LocalDate.now().plusMonths(1));

        // El DAO dice que SÍ encontró a alguien con ese DNI
        when(miembroDAO.buscarPorDNI("40123456")).thenReturn(Optional.of(existente));

        // Act & Assert
        assertThrows(DatosInvalidosException.class, () -> {
            miembroService.guardarMiembro(nuevo);
        });
        // Verificamos que NUNCA se haya llamado al método guardar
        verify(miembroDAO, never()).guardar(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción si la fecha de vencimiento es anterior a la de inscripción")
    public void testGuardarMiembro_VencimientoAnteriorAInscripcion_LanzaExcepcion() {
        // Arrange: fecha de vencimiento en el pasado respecto a la inscripción
        LocalDate hoy = LocalDate.now();
        Miembro invalido = crearMiembro(0, "40123456", hoy, hoy.minusDays(5));

        // El DNI está libre
        when(miembroDAO.buscarPorDNI("40123456")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(DatosInvalidosException.class, () -> {
            miembroService.guardarMiembro(invalido);
        });

        verify(miembroDAO, never()).guardar(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar si el nuevo DNI ya le pertenece a otro socio distinto")
    public void testActualizarMiembro_DniDeOtroSocio_LanzaExcepcionYNoActualiza() {
        // Arrange: Miembro ID 1 quiere ponerse el DNI que ya tiene el socio ID 2
        Miembro miembroAActualizar = crearMiembro(1, "40123456", LocalDate.now(), LocalDate.now().plusMonths(1));
        Miembro otroSocio = crearMiembro(2, "40123456", LocalDate.now(), LocalDate.now().plusMonths(1));

        // El DAO dice que ese DNI le pertenece al socio ID 2
        when(miembroDAO.buscarPorDNI("40123456")).thenReturn(Optional.of(otroSocio));

        // Act & Assert
        assertThrows(DatosInvalidosException.class, () -> {
            miembroService.actualizarMiembro(miembroAActualizar);
        });

        verify(miembroDAO, never()).actualizar(any());
    }

}
