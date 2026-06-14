package com.gym.manager.dao;

import com.gym.manager.model.Miembro;
import com.gym.manager.model.Plan;
import com.gym.manager.model.enums.EstadoMiembro;
import com.gym.manager.util.DatabaseManager;
import com.gym.manager.exceptions.ConexionBDException;
import com.gym.manager.interfaces.DAO;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MiembroDAO implements DAO<Miembro> {

    //MÉTODOS OBLIGATORIOS DE LA INTERFACE DAO
    @Override
    public void guardar(Miembro miembro) {
        String sqlPersona = "INSERT INTO Persona (nombre, apellido, dni, email, telefono, tipo_persona, fecha_registro) VALUES (?, ?, ?, ?, ?, 'MIEMBRO', ?)";
        String sqlMiembro = "INSERT INTO Miembros (fecha_inscripcion, fecha_vencimiento, estado, Planes_id_planes, Persona_idPersona) VALUES (?, ?, ?, ?, ?)";
        
        Connection conn = DatabaseManager.getInstance().getConnection();
        
        try {
            // INICIAMOS LA TRANSACCIÓN (no se guarda automaticamente)
            conn.setAutoCommit(false);
            
            int idPersonaGenerado = 0;

            // INSERTAMOS LA PERSONA
            try (PreparedStatement pstmtPersona = conn.prepareStatement(sqlPersona, Statement.RETURN_GENERATED_KEYS)) {
                pstmtPersona.setString(1, miembro.getNombre());
                pstmtPersona.setString(2, miembro.getApellido());
                pstmtPersona.setString(3, miembro.getDni());
                pstmtPersona.setString(4, miembro.getEmail());
                pstmtPersona.setString(5, miembro.getTelefono());
                pstmtPersona.setDate(6, Date.valueOf(miembro.getFechaInscripcion())); 
                
                pstmtPersona.executeUpdate();
                
                // Capturamos el ID que MySQL le dio a la Persona
                try (ResultSet rs = pstmtPersona.getGeneratedKeys()) {
                    if (rs.next()) {
                        idPersonaGenerado = rs.getInt(1);
                        miembro.setId(idPersonaGenerado); // Se lo asignamos al objeto Java
                    } else {
                        throw new SQLException("Falló la creación de la Persona, no se obtuvo el ID.");
                    }
                }
            }

            // INSERTAMOS EL MIEMBRO (Usando el ID de la Persona que acabamos de crear)
            try (PreparedStatement pstmtMiembro = conn.prepareStatement(sqlMiembro, Statement.RETURN_GENERATED_KEYS)) {
                pstmtMiembro.setDate(1, Date.valueOf(miembro.getFechaInscripcion()));
                pstmtMiembro.setDate(2, Date.valueOf(miembro.getFechaVencimiento()));
                pstmtMiembro.setString(3, miembro.getEstado().name()); 
                pstmtMiembro.setInt(4, miembro.getPlan().getId()); 
                pstmtMiembro.setInt(5, idPersonaGenerado); 
                
                pstmtMiembro.executeUpdate();
            }

            // CONFIRMAMOS LA TRANSACCIÓN 
            conn.commit();

        } catch (SQLException e) {
            // SI ALGO FALLA, SE HACE ROLLBACK (Deshacemos toda la transaccion para no dejar datos a medias)
            try {
                conn.rollback();
            } catch (SQLException ex) {
                System.err.println("Error crítico al hacer rollback: " + ex.getMessage());
            }
            throw new ConexionBDException("Error al guardar el miembro. Se deshicieron los cambios.", e);
            
        } finally {
            // DEVOLVEMOS LA CONEXIÓN A SU ESTADO NORMAL
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Error al restaurar el auto-commit: " + e.getMessage());
            }
        }
    }
    @Override
    public Optional<Miembro> buscarPorId(int id) {
        String buscar = "SELECT * FROM Persona p JOIN Miembros m ON p.idPersona = m.Persona_idPersona WHERE p.idPersona = ?";

        Connection conn = DatabaseManager.getInstance().getConnection();

        try(PreparedStatement pstmt = conn.prepareStatement(buscar)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String nombre = rs.getString("nombre");
                String apellido = rs.getString("apellido");
                String dni = rs.getString("dni");
                String email = rs.getString("email");
                String telefono = rs.getString("telefono");
                LocalDate fechaInscripcion = rs.getDate("fecha_inscripcion").toLocalDate();
                LocalDate fechaVencimiento = rs.getDate("fecha_vencimiento").toLocalDate();
                EstadoMiembro estado = EstadoMiembro.valueOf(rs.getString("estado"));

                int idPlan = rs.getInt("Planes_id_planes");
                PlanDAO planDAO = new PlanDAO();
                Plan planAsignado = planDAO.buscarPorId(idPlan).orElse(null);

                Miembro miembro = new Miembro(fechaInscripcion, fechaVencimiento, planAsignado, estado, id, nombre, apellido, dni, email, telefono);
                return Optional.of(miembro);
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar miembro por id: " + e.getMessage());
        }    
        return Optional.empty();
    }

    @Override
    public List<Miembro> obtenerTodos() {
        List<Miembro> lista = new ArrayList<>();
        String todosSQL = "SELECT * FROM Persona p JOIN Miembros m ON p.idPersona = m.Persona_idPersona";

        Connection conn = DatabaseManager.getInstance().getConnection();

        try(PreparedStatement pstmt = conn.prepareStatement(todosSQL) ){
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                int id = rs.getInt("idPersona");
                String nombre = rs.getString("nombre");
                String apellido = rs.getString("apellido");
                String dni = rs.getString("dni");
                String email = rs.getString("email");
                String telefono = rs.getString("telefono");
                LocalDate fechaInscripcion = rs.getDate("fecha_inscripcion").toLocalDate();
                LocalDate fechaVencimiento = rs.getDate("fecha_vencimiento").toLocalDate();
                EstadoMiembro estado = EstadoMiembro.valueOf(rs.getString("estado"));

                int idPlan = rs.getInt("Planes_id_planes");
                PlanDAO planDAO = new PlanDAO();
                Plan planAsignado = planDAO.buscarPorId(idPlan).orElse(null);

                Miembro miembro = new Miembro(fechaInscripcion, fechaVencimiento, planAsignado, estado, id, nombre, apellido, dni, email, telefono);

                lista.add(miembro);
            }
        } catch(SQLException e){
            System.out.println("Error al obtener todos los miembros " + e.getMessage());
        }
        return lista;
    }

    @Override
    public void actualizar(Miembro miembro) {
        String sqlPersona = "UPDATE Persona SET nombre = ?, apellido = ?, dni = ?, email = ?, telefono = ? WHERE idPersona = ?";
        String sqlMiembro = "UPDATE miembros SET fecha_vencimiento = ?, estado = ?, Planes_id_planes = ? WHERE Persona_idPersona = ?";

        Connection conn = DatabaseManager.getInstance().getConnection();

        try{
            conn.setAutoCommit(false);
            try(PreparedStatement pstmtPersona = conn.prepareStatement(sqlPersona) ){
                pstmtPersona.setString(1, miembro.getNombre());
                pstmtPersona.setString(2, miembro.getApellido());
                pstmtPersona.setString(3, miembro.getDni());
                pstmtPersona.setString(4, miembro.getEmail());
                pstmtPersona.setString(5, miembro.getTelefono());
                pstmtPersona.setInt(6, miembro.getId());

                pstmtPersona.executeUpdate();
            }
            try(PreparedStatement pstmtMiembro = conn.prepareStatement(sqlMiembro)) {
                pstmtMiembro.setDate(1, Date.valueOf(miembro.getFechaVencimiento()));
                pstmtMiembro.setString(2, miembro.getEstado().name());
                if(miembro.getPlan() != null){
                    pstmtMiembro.setInt(3, miembro.getPlan().getId());
                } else {
                    pstmtMiembro.setNull(3, java.sql.Types.INTEGER);
                }
                pstmtMiembro.setInt(4, miembro.getId());

                pstmtMiembro.executeUpdate();
            }
            conn.commit();
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                System.out.println("Error al hacer rollback " + ex.getMessage());
            }
            System.out.println("Error al actualizar miembro " + e.getMessage());    
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                System.out.println("Error al restaurar el auto commit " + e.getMessage());
            }
        }
        
    }

    @Override
    public void eliminar(int id) {
        String sqlMiembro = "DELETE FROM Miembros WHERE Persona_idPersona = ?";
        String sqlPersona = "DELETE FROM Persona WHERE idPersona = ?";

        Connection conn = DatabaseManager.getInstance().getConnection();

        try{
            conn.setAutoCommit(false);
            try(PreparedStatement pstmtMiembro = conn.prepareStatement(sqlMiembro)){
                pstmtMiembro.setInt(1, id);
                pstmtMiembro.executeUpdate();
            }
            try(PreparedStatement pstmtPersona = conn.prepareStatement(sqlPersona)){
                pstmtPersona.setInt(1, id);
                pstmtPersona.executeUpdate();
            }
            conn.commit();
        } catch(SQLException e){
            try{
                conn.rollback();
            } catch(SQLException ex) {
                System.out.println("Error al hacer rollback " + ex.getMessage());
            }
            System.out.println("Error al eliminar miembro " + e.getMessage());
        }finally{
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                System.out.println("Error al restaurar auto commit: " + e.getMessage());
            }
        }    
    }


    // MÉTODOS DE MIEMBRO DAO
    public Optional<Miembro> buscarPorDNI(String dni){
        String sqlBuscarDNI = "SELECT * FROM persona p JOIN miembros m ON p.idPersona = m.Persona_idPersona WHERE p.dni = ?";
        
        Connection conn = DatabaseManager.getInstance().getConnection();

        try(PreparedStatement pstmtBuscar = conn.prepareStatement(sqlBuscarDNI)) {
            pstmtBuscar.setString(1, dni);

            ResultSet rs = pstmtBuscar.executeQuery();
            
            if(rs.next()){
                int id = rs.getInt("idPersona");
                String nombre = rs.getString("nombre");
                String apellido = rs.getString("apellido");
                String email = rs.getString("email");
                String telefono = rs.getString("telefono");
                LocalDate fechaInscripcion = rs.getDate("fecha_inscripcion").toLocalDate();
                LocalDate fechaVencimiento = rs.getDate("fecha_vencimiento").toLocalDate();
                EstadoMiembro estado = EstadoMiembro.valueOf(rs.getString("estado"));

                int idPlan = rs.getInt("Planes_id_planes");
                PlanDAO planDAO = new PlanDAO();
                Plan planAsignado = planDAO.buscarPorId(idPlan).orElse(null);

                Miembro miembro = new Miembro(fechaInscripcion, fechaVencimiento, planAsignado, estado, id, nombre, apellido, dni, email, telefono);
                return Optional.of(miembro);
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar miembro por dni " + e.getMessage());
        }
        return Optional.empty();
    }

    public List<Miembro> obtenerActivos(){
        return this.obtenerTodos().stream()
                .filter(m -> m.estaActivo())
                .collect(Collectors.toList());
    }

    public List<Miembro> obtenerPorVencer(int dias){
        return this.obtenerTodos().stream()
                .filter(m -> m.diasParaVencer() <= dias && m.diasParaVencer() >= 0)
                .collect(Collectors.toList());
    }
}