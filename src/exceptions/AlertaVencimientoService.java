package com.gym.manager.service;

import com.gym.manager.dao.MiembroDAO;
import com.gym.manager.model.Miembro;
import com.gym.manager.interfaces.Notificador;

import java.util.List;

/**
 * Servicio encargado de gestionar las alertas de vencimiento de los miembros.
 */
public class AlertaVencimientoService {

    private final MiembroDAO miembroDAO;
    private final Notificador notificador;

    public AlertaVencimientoService(Notificador notificador) {
        this.miembroDAO = new MiembroDAO();
        this.notificador = notificador;
    }

    /**
     * Busca en la base de datos los miembros próximos a vencer y dispara el notificador.
     * @param diasAviso Cantidad de días de anticipación para avisar.
     */
    public void revisarVencimientos(int diasAviso) {
        System.out.println("Buscando miembros a " + diasAviso + " días de vencer...");
        
        List<Miembro> porVencer = miembroDAO.obtenerPorVencer(diasAviso);

        // Disparamos la notificación por cada miembro encontrado
        for (Miembro m : porVencer) {
            notificador.enviarAlerta(m);
        }
        
        System.out.println("Revisión de vencimientos finalizada exitosamente. (" + porVencer.size() + " alertas enviadas).");
    }
}
