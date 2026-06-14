package com.gym.manager.dao;

import com.gym.manager.model.Plan;
import com.gym.manager.util.DatabaseManager;
import com.gym.manager.interfaces.DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PlanDAO implements DAO<Plan> {

    @Override
    public void guardar(Plan plan) {
        String sqlPlan = "INSERT INTO planes (nombre_plan, duracion_meses, descripcion, precio_mensual) VALUES (?, ?, ?, ?)";

        Connection conn = DatabaseManager.getInstance().getConnection();
        
        try(PreparedStatement pstmtPlan = conn.prepareStatement(sqlPlan, Statement.RETURN_GENERATED_KEYS)){
            pstmtPlan.setString(1, plan.getNombrePlan());
            pstmtPlan.setInt(2, plan.getDuracionMeses());
            pstmtPlan.setString(3, plan.getDescripcion());
            pstmtPlan.setDouble(4, plan.getPrecioMensual());

            pstmtPlan.executeUpdate();

            try(ResultSet rs = pstmtPlan.getGeneratedKeys()) {
                if (rs.next()) {
                    plan.setId(rs.getInt(1));
                }
            }
        } catch(SQLException e) {
            System.out.println("Error al guardar plan " + e.getMessage());
        }

    }

    @Override
    public Optional<Plan> buscarPorId(int id) {
        String sqlBuscar = "SELECT * FROM Planes WHERE id_planes = ?";

        Connection conn = DatabaseManager.getInstance().getConnection();

        try(PreparedStatement pstmtPlan = conn.prepareStatement(sqlBuscar)) {
            pstmtPlan.setInt(1, id);
            ResultSet rs = pstmtPlan.executeQuery();

            if(rs.next()) {
                Plan planEncontrado = new Plan(rs.getInt("id_planes"), rs.getString("nombre_plan"), rs.getInt("duracion_meses"), rs.getString("descripcion"), rs.getDouble("precio_mensual"));

                return Optional.of(planEncontrado);
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar el plan por ID: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<Plan> obtenerTodos(){
        List<Plan> lista = new ArrayList<>();
        String sqlTodos = "SELECT * FROM planes";
        Connection conn = DatabaseManager.getInstance().getConnection();

        try (PreparedStatement pstmtPlan = conn.prepareStatement(sqlTodos)) {
            ResultSet rs = pstmtPlan.executeQuery();

            while (rs.next()) {
                Plan plan = new Plan(rs.getInt("id_planes"), rs.getString("nombre_plan"), rs.getInt("duracion_meses"), rs.getString("descripcion"), rs.getDouble("precio_mensual"));
                lista.add(plan);
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener todos los planes: " + e.getMessage());
        }
        return lista;

    }

    @Override
    public void actualizar(Plan plan){
        String sql = "UPDATE planes SET nombre_plan = ?, duracion_meses = ?, descripcion = ?, precio_mensual = ? WHERE id_planes = ?";
        Connection conn = DatabaseManager.getInstance().getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, plan.getNombrePlan());
            pstmt.setInt(2, plan.getDuracionMeses());
            pstmt.setString(3, plan.getDescripcion());
            pstmt.setDouble(4, plan.getPrecioMensual());
            pstmt.setInt(5, plan.getId());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al actualizar el plan " + e.getMessage());
        }
    }

    @Override
    public void eliminar(int id){
        String sql = "DELETE FROM planes WHERE id_planes = ?";
        Connection conn = DatabaseManager.getInstance().getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("No se puede eliminar el plan porque hay miembros inscriptos usándolo.");
        }
    }

    
} 