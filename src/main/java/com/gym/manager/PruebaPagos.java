package com.gym.manager;

import com.gym.manager.model.Miembro;
import com.gym.manager.model.Pago;
import com.gym.manager.model.TipoPago;
import com.gym.manager.service.AlertaVencimientoService;
import com.gym.manager.model.enums.EstadoMiembro;
import com.gym.manager.model.enums.EstadoPago;
import com.gym.manager.model.enums.TipoPago;
import com.gym.manager.service.PagoService;
import com.gym.manager.interfaces.Notificador;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PruebaPagos {
    public static void main(String[] args) {
        System.out.println("=== Iniciando Prueba de PagoService ===");

        // Simulamos en Java el Miembro que acabamos de crear en MySQL (ID 1)
        Miembro miembro = new Miembro(
            LocalDate.now().minusMonths(1), // fecha inscripcion
            LocalDate.now(),                // fecha vencimiento (supongamos que vence hoy)
            null,                           // plan (no lo necesitamos para esta prueba)
            EstadoMiembro.ACTIVO,           // estado
            1,                              // ID Miembro (El que insertamos en la BD)
            "Juan", "Perez", "12345678", "juan@gym.com", "123456789"
        );

        // Creamos un nuevo Pago de Mensualidad por $15,000
        Pago pago = new Pago(0, miembro, 15000.0, LocalDateTime.now(), TipoPago.MENSUALIDAD, EstadoPago.PAGADO, "Mensualidad Junio");

        // Llamamos al Service
        PagoService service = new PagoService();
        
        try {
            service.registrarPago(pago);
            System.out.println("ÉXITO: El pago se guardó correctamente en la base de datos.");
            System.out.println("Nueva fecha de vencimiento calculada en memoria: " + miembro.getFechaVencimiento());
        } catch (Exception e) {
            System.err.println("ERROR al procesar el pago: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n=== Iniciando Prueba de Alertas (Sin Hilos) ===");
        
        // 1. Definimos cómo notificar
        Notificador miNotificador = (miembroVencido) -> {
            System.out.println("⚠️ ALERTA: El miembro " + miembroVencido.getNombreCompleto() + 
                               " vence el " + miembroVencido.getFechaVencimiento());
        };

        // 2. Creamos el servicio
        AlertaVencimientoService alertaService = new AlertaVencimientoService(miNotificador);
        
        // 3. Revisamos vencimientos en los próximos 5 días
        alertaService.revisarVencimientos(5);
    }
}

/*
INSERTAR CONSULTA

INSERT INTO Persona (idPersona, nombre, apellido, dni, email, telefono, tipo_persona, fecha_registro, activo) 
VALUES (1, 'Juan', 'Perez', '12345678', 'juan@gym.com', '123456789', 'MIEMBRO', '2026-05-10', 1);

INSERT INTO Planes (id_planes, nombre_plan, duracion_meses, descripcion, precio_mensual)
VALUES (1, 'Plan Basico', 1, 'Plan mensual', 15000.00);

INSERT INTO Miembros (idMiembros, fecha_inscripcion, fecha_vencimiento, estado, Planes_id_planes, Persona_idPersona) 
VALUES (1, '2026-05-10', '2026-06-10', 'ACTIVO', 1, 1);

*/