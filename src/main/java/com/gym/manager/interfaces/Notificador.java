package com.gym.manager.interfaces;

import com.gym.manager.model.Miembro;

/**
 * Interfaz funcional para definir el comportamiento de una notificación.
 * Permite inyectar distintas formas de aviso (consola, email, GUI) usando Lambdas.
 */
@FunctionalInterface
public interface Notificador {
    
    // Método que será implementado para definir cómo avisar (ej. consola, interfaz)
    void enviarAlerta(Miembro miembro);
}
