package com.gym.manager.dao;

import com.gym.manager.model.ClaseGimnasio;
import com.gym.manager.model.ClaseGrupal;
import com.gym.manager.model.ClasePersonal;
import com.gym.manager.util.DatabaseManager;
import com.gym.manager.exceptions.ConexionBDException;
import com.gym.manager.interfaces.DAO;
import com.gym.manager.model.Instructor;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

public class ClaseDAO implements DAO<ClaseGimnasio> {

    @Override
    public void guardar(ClaseGimnasio clase) {
       String sql = "INSERT INTO Clases (nombre, tipo, horario, duracion_minutos, capacidad_max, activo, Instructores_idInstructores) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn = DatabaseManager.getInstance().getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, clase.getNombre());
            pstmt.setString(2, clase.getTipoClase()); // Llama al método abstracto que devuelve "GRUPAL" o "PERSONAL"
            pstmt.setTimestamp(3, Timestamp.valueOf(clase.getHorario()));
            pstmt.setInt(4, clase.getDuracionMinutos());
            
            // Evaluamos mediante polimorfismo si es grupal para guardarle la capacidad
            if (clase instanceof ClaseGrupal) {
                pstmt.setInt(5, ((ClaseGrupal) clase).getCapacidadMax());
            } else {
                pstmt.setNull(5, Types.INTEGER); // Las clases personales no tienen capacidad máxima
            }
            
            pstmt.setBoolean(6, clase.isActivo());
            pstmt.setInt(7, clase.getInstructor().getIdInstructor()); // Clave foránea del instructor asignado

            pstmt.executeUpdate();

            // Recuperar ID generado
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    clase.setIdClase(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new ConexionBDException("Error al guardar la nueva clase en la base de datos.", e);
        }
    }

    // --- MÉTODOS OBLIGATORIOS RESTANTES PARA EL CRUD ---
    @Override
    public Optional<ClaseGimnasio> buscarPorId(int id) {
        throw new UnsupportedOperationException("Búsqueda por ID aún no implementada.");
    }

    @Override
    public List<ClaseGimnasio> obtenerTodos() {
        List<ClaseGimnasio> lista = new ArrayList<>();
        // Hacemos un JOIN triple para traer los datos de la Clase, su Instructor y su Persona de una sola vez
        String sql = "SELECT c.*, i.idInstructores, i.especialidad, i.sueldo, p.idPersona, p.nombre AS prof_nombre, p.apellido AS prof_apellido, p.dni, p.email, p.telefono " +
                     "FROM Clases c " +
                     "INNER JOIN Instructores i ON c.Instructores_idInstructores = i.idInstructores " +
                     "INNER JOIN Persona p ON i.Persona_idPersona = p.idPersona";
                     
        Connection conn = DatabaseManager.getInstance().getConnection();

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                // 1. Reconstruimos el objeto Instructor con su constructor completo
                Instructor profe = new Instructor(
                    rs.getInt("idPersona"),
                    rs.getString("prof_nombre"),
                    rs.getString("prof_apellido"),
                    rs.getString("dni"),
                    rs.getString("email"),
                    rs.getString("telefono"),
                    rs.getString("especialidad"),
                    rs.getDouble("sueldo")
                );
                // No te olvides de setearle el ID propio de la tabla de instructores
                profe.setIdInstructor(rs.getInt("idInstructores"));

                // 2. Extraemos las variables de la Clase
                int idClase = rs.getInt("idClases");
                String nombre = rs.getString("nombre");
                String tipo = rs.getString("tipo");
                java.time.LocalDateTime horario = rs.getTimestamp("horario").toLocalDateTime();
                int duracion = rs.getInt("duracion_minutos");
                boolean activo = rs.getBoolean("activo");

                // 3. Aplicamos polimorfismo para instanciar el tipo correcto de clase
                ClaseGimnasio clase;
                if ("GRUPAL".equals(tipo)) {
                    int capacidad = rs.getInt("capacidad_max");
                    clase = new ClaseGrupal(idClase, nombre, profe, horario, duracion, activo, capacidad);
                } else {
                    clase = new ClasePersonal(idClase, nombre, profe, horario, duracion, activo);
                }

                lista.add(clase);
            }
        } catch (SQLException e) {
            throw new ConexionBDException("Error al listar las clases desde la base de datos: " + e.getMessage(), e);
        }

        return lista;
    }

    @Override
    public void actualizar(ClaseGimnasio clase) {
        throw new UnsupportedOperationException("Actualización aún no implementada.");
    }

    @Override
    public void eliminar(int id) {
        throw new UnsupportedOperationException("Eliminación aún no implementada.");
    }
}