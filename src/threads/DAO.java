package com.gym.manager.interfaces;

import com.gym.manager.exceptions.ConexionBDException;
import java.util.List;
import java.util.Optional;

/* DAO (Data Access Object) 
   Separa la lógica de negocio (Service) del acceso a la BD.
   Cada entidad (Miembro, Clase, Pago...) tiene su propio DAO
   que implementa esta interfaz y agrega queries específicas 
*/

public interface DAO<T> {

    /**
     * Persiste un nuevo objeto en la base de datos.
     * Ejecuta un INSERT y, si la BD genera el ID automáticamente,
     * la implementación debe asignárselo al objeto con setId().
     *
     * @param t el objeto a guardar (no debe ser null)
     * @throws ConexionBDException si hay un error de base de datos
     */
    void guardar(T t);

    /**
     * Busca un objeto por su clave primaria.
     * Devuelve Optional.empty() si no existe ningún registro con ese ID,
     * en lugar de devolver null (evita NullPointerException).
     *
     * Ejemplo de uso:
     *   miembroDAO.buscarPorId(5)
     *             .ifPresent(m -> System.out.println(m.getNombre()));
     *
     * @param id clave primaria del registro
     * @return Optional con el objeto encontrado, o Optional.empty()
     */
    Optional<T> buscarPorId(int id);

    /**
     * Devuelve todos los registros de la tabla correspondiente.
     * Si la tabla está vacía, devuelve una lista vacía (nunca null).
     *
     * @return lista con todos los objetos; puede ser vacía pero nunca null
     */
    List<T> obtenerTodos();

    /**
     * Actualiza los datos de un registro existente en la BD.
     * Ejecuta un UPDATE usando el ID del objeto como condición WHERE.
     *
     * @param t el objeto con los datos actualizados (debe tener ID válido)
     * @throws ConexionBDException si hay un error de base de datos
     */
    void actualizar(T t);

    /**
     * Elimina el registro con el ID dado de la base de datos.
     * Ejecuta un DELETE WHERE id = ?
     *
     * @param id clave primaria del registro a eliminar
     * @throws ConexionBDException si hay un error de base de datos
     */
    void eliminar(int id);
}