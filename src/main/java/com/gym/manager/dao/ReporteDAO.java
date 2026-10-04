package com.gym.manager.dao;

import com.gym.manager.util.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ReporteDAO {

    private final Connection connection;

    public ReporteDAO() {
        this.connection = DatabaseManager.getInstance().getConnection();
    }

    public ResultSet obtenerPagosParaReporte() throws Exception {

        String sql = "SELECT p.fecha_pago, per.dni, per.nombre, per.apellido, p.monto "
                + "FROM Pagos p "
                + "JOIN Miembros m ON p.Miembros_idMiembros = m.idMiembros "
                + "JOIN Persona per ON m.Persona_idPersona = per.idPersona "
                + "WHERE p.estado = 'PAGADO' "
                + "ORDER BY YEAR(p.fecha_pago) DESC, "
                + "MONTH(p.fecha_pago) DESC, "
                + "p.fecha_pago DESC";

        PreparedStatement pstmt = connection.prepareStatement(sql);

        return pstmt.executeQuery();
    }

    public ResultSet obtenerInscriptosParaReporte() throws Exception {

        String sql = "SELECT m.fecha_inscripcion, m.fecha_vencimiento, m.estado, "
                + "p.nombre, p.apellido, p.dni, pl.nombre_plan "
                + "FROM Miembros m "
                + "JOIN Persona p ON m.Persona_idPersona = p.idPersona "
                + "JOIN Planes pl ON m.Planes_id_planes = pl.id_planes "
                + "ORDER BY YEAR(m.fecha_inscripcion) DESC, "
                + "MONTH(m.fecha_inscripcion) DESC, "
                + "m.fecha_inscripcion DESC";

        PreparedStatement pstmt = connection.prepareStatement(sql);

        return pstmt.executeQuery();
    }
}