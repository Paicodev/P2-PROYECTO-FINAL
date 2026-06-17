package com.gym.manager.view.panels;

import javax.swing.*;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Element;
import com.gym.manager.util.DatabaseManager;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Panel de Reportes que exporta informes financieros y de gestión en PDF.
 */
public class PanelReportes extends JPanel {

    private JButton btnIngresos;
    private JButton btnInscriptos;
    private JTable tablaReportes;

    public PanelReportes() { 
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        inicializarComponentes();
    }

    private void inicializarComponentes() { 
        setBackground(new java.awt.Color(28, 43, 51)); // Actualizado a la paleta BG_CENTRAL de FitBase

        JLabel titulo = new JLabel("REPORTES Y BALANCES");
        titulo.setForeground(java.awt.Color.WHITE);
        titulo.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 22));
        titulo.setAlignmentX(CENTER_ALIGNMENT);

        btnIngresos  = new JButton("Generar Balance de Ingresos/Gastos");
        btnInscriptos = new JButton("Generar Reporte de Inscriptos");

        btnIngresos.addActionListener(e -> exportarPDF("INGRESOS"));
        btnInscriptos.addActionListener(e -> exportarPDF("INSCRIPTOS"));

        // Estilo botones
        btnIngresos.setBackground(new java.awt.Color(0, 150, 136)); // ACENTO_TURQUESA
        btnIngresos.setForeground(java.awt.Color.WHITE);
        btnIngresos.setFocusPainted(false);
        
        btnInscriptos.setBackground(new java.awt.Color(0, 150, 136));
        btnInscriptos.setForeground(java.awt.Color.WHITE);
        btnInscriptos.setFocusPainted(false);

        String[] columnas = {"Reporte", "Estado", "Última Generación"};
        tablaReportes = new JTable (new javax.swing.table.DefaultTableModel (new Object [][]{}, columnas));

        // Estilo tabla (Placeholder visual)
        tablaReportes.setBackground(new java.awt.Color(22, 38, 45)); // BG_FORMULARIO
        tablaReportes.setForeground(java.awt.Color.WHITE);
        tablaReportes.setRowHeight(25);
        tablaReportes.getTableHeader().setBackground(new java.awt.Color(35, 58, 70)); // BG_INPUTS
        tablaReportes.getTableHeader().setForeground(java.awt.Color.WHITE);
        tablaReportes.setGridColor(java.awt.Color.GRAY);
        tablaReportes.setSelectionBackground(new java.awt.Color(0, 150, 136));

        JScrollPane scroll = new JScrollPane(tablaReportes);
        scroll.getViewport().setBackground(new java.awt.Color(28, 43, 51));

        add(Box.createVerticalStrut(30));
        add(titulo);
        add(Box.createVerticalStrut(20));
        
        JPanel panelBotones = new JPanel();
        panelBotones.setBackground(new java.awt.Color(28, 43, 51));
        panelBotones.add(btnIngresos);
        panelBotones.add(Box.createHorizontalStrut(15));
        panelBotones.add(btnInscriptos);
        add(panelBotones);
        
        add(Box.createVerticalStrut(20));
        scroll.setPreferredSize(new java.awt.Dimension(800, 300));
        scroll.setMaximumSize(new java.awt.Dimension(800, 300));
        add(scroll);
    }

    private void exportarPDF(String tipoReporte) {
        String nombreArchivo = tipoReporte.equals("INGRESOS") ? "Balance_Financiero.pdf" : "Reporte_Inscriptos.pdf";
        
        try {
            Document documento = new Document();
            PdfWriter.getInstance(documento, new FileOutputStream(nombreArchivo));
            documento.open();

            com.itextpdf.text.Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            com.itextpdf.text.Font fontMes = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            
            Paragraph titulo = new Paragraph("FITBASE - " + (tipoReporte.equals("INGRESOS") ? "BALANCE FINANCIERO" : "REPORTE DE MIEMBROS INSCRIPTOS"), fontTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(20);
            documento.add(titulo);

            Connection conn = DatabaseManager.getInstance().getConnection();
            SimpleDateFormat sdfMes = new SimpleDateFormat("MMMM yyyy", new Locale("es", "ES"));

            if (tipoReporte.equals("INGRESOS")) {
                generarReporteIngresos(documento, conn, sdfMes, fontMes);
            } else {
                generarReporteInscriptos(documento, conn, sdfMes, fontMes);
            }

            documento.close();
            JOptionPane.showMessageDialog(this, "PDF generado con éxito: " + nombreArchivo, "Reporte Exportado", JOptionPane.INFORMATION_MESSAGE);

            // Agregamos un registro a la tablita visual para dar feedback
            ((javax.swing.table.DefaultTableModel)tablaReportes.getModel()).addRow(new Object[]{
                tipoReporte, "Generado OK", new java.util.Date().toString()
            });

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al generar PDF: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generarReporteIngresos(Document documento, Connection conn, SimpleDateFormat sdfMes, com.itextpdf.text.Font fontMes) throws Exception {
        // 1. Obtener gastos fijos (Sueldo mensual de instructores)
        double totalSueldos = 0;
        try (PreparedStatement ps = conn.prepareStatement("SELECT SUM(sueldo) as total FROM Instructores");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) totalSueldos = rs.getDouble("total");
        }

        // 2. Obtener pagos ordenados por fecha
    String sql = "SELECT p.fecha_pago, per.dni, per.nombre, per.apellido, p.monto " +
        "FROM Pagos p JOIN Miembros m ON p.Miembros_idMiembros = m.idMiembros " +
        "JOIN Persona per ON m.Persona_idPersona = per.idPersona " +
         "WHERE p.estado = 'PAGADO' ORDER BY YEAR(p.fecha_pago) DESC, MONTH(p.fecha_pago) DESC, p.fecha_pago DESC";

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

        String mesActual = "";
        double ingresosMes = 0;
        PdfPTable tablaMes = null;

        while (rs.next()) {
        java.sql.Date fecha = rs.getDate("fecha_pago");
        String mesFila = sdfMes.format(fecha).toUpperCase();

    // Si cambia el mes, cerramos la tabla anterior e iniciamos una nueva
        if (!mesFila.equals(mesActual)) {
        if (tablaMes != null) {
        cerrarTablaFinanciera(tablaMes, ingresosMes, totalSueldos);
        documento.add(tablaMes);
        documento.add(new Paragraph("\n"));
        }
        mesActual = mesFila;
        ingresosMes = 0;
                    
        documento.add(new Paragraph("PERÍODO: " + mesActual, fontMes));
        documento.add(new Paragraph("\n"));
                    
        tablaMes = new PdfPTable(4);
        tablaMes.setWidthPercentage(100);
        tablaMes.addCell(crearCeldaHeader("Fecha"));
        tablaMes.addCell(crearCeldaHeader("Socio"));
        tablaMes.addCell(crearCeldaHeader("DNI"));
        tablaMes.addCell(crearCeldaHeader("Monto Ingresado"));
        }

        ingresosMes += rs.getDouble("monto");
        tablaMes.addCell(fecha.toString());
        tablaMes.addCell(rs.getString("nombre") + " " + rs.getString("apellido"));
        tablaMes.addCell(rs.getString("dni"));
        tablaMes.addCell(String.format("$%.2f", rs.getDouble("monto")));
            }

    // Cerrar el último mes
        if (tablaMes != null) {
        cerrarTablaFinanciera(tablaMes, ingresosMes, totalSueldos);
        documento.add(tablaMes);
        }
    }
 }

    private void cerrarTablaFinanciera(PdfPTable tabla, double ingresos, double gastos) {
        PdfPCell celdaVacia = new PdfPCell(new Phrase("")); celdaVacia.setColspan(2); celdaVacia.setBorder(0);
        
        PdfPCell cTituloIngreso = new PdfPCell(new Phrase("TOTAL INGRESOS:", FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
        PdfPCell cIngreso = new PdfPCell(new Phrase(String.format("$%.2f", ingresos)));
        
        PdfPCell cTituloGasto = new PdfPCell(new Phrase("GASTOS (Sueldos):", FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
        PdfPCell cGasto = new PdfPCell(new Phrase(String.format("$%.2f", gastos)));
        cTituloGasto.setBackgroundColor(com.itextpdf.text.BaseColor.LIGHT_GRAY);
        cGasto.setBackgroundColor(com.itextpdf.text.BaseColor.LIGHT_GRAY);

        PdfPCell cTituloBal = new PdfPCell(new Phrase("BALANCE NETO:", FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
        PdfPCell cBalance = new PdfPCell(new Phrase(String.format("$%.2f", (ingresos - gastos))));
        
        tabla.addCell(celdaVacia); tabla.addCell(cTituloIngreso); tabla.addCell(cIngreso);
        tabla.addCell(celdaVacia); tabla.addCell(cTituloGasto); tabla.addCell(cGasto);
        tabla.addCell(celdaVacia); tabla.addCell(cTituloBal); tabla.addCell(cBalance);
    }

    private void generarReporteInscriptos(Document documento, Connection conn, SimpleDateFormat sdfMes, com.itextpdf.text.Font fontMes) throws Exception {
        String sql = "SELECT m.fecha_inscripcion, m.fecha_vencimiento, m.estado, p.nombre, p.apellido, p.dni, pl.nombre_plan " +
                     "FROM Miembros m JOIN Persona p ON m.Persona_idPersona = p.idPersona " +
                     "JOIN Planes pl ON m.Planes_id_planes = pl.id_planes " +
                     "ORDER BY YEAR(m.fecha_inscripcion) DESC, MONTH(m.fecha_inscripcion) DESC, m.fecha_inscripcion DESC";

    try (PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery()) {

        String mesActual = "";
        PdfPTable tablaMes = null;

    while (rs.next()) {
        java.sql.Date fechaInsc = rs.getDate("fecha_inscripcion");
        String mesFila = sdfMes.format(fechaInsc).toUpperCase();

        if (!mesFila.equals(mesActual)) {
        if (tablaMes != null) {
        documento.add(tablaMes);
        documento.add(new Paragraph("\n"));
    }
        mesActual = mesFila;
                    
        documento.add(new Paragraph("INSCRIPTOS EN: " + mesActual, fontMes));
        documento.add(new Paragraph("\n"));
                    
        tablaMes = new PdfPTable(5);
        tablaMes.setWidthPercentage(100);
        tablaMes.addCell(crearCeldaHeader("Socio"));
        tablaMes.addCell(crearCeldaHeader("DNI"));
        tablaMes.addCell(crearCeldaHeader("Inscripción"));
        tablaMes.addCell(crearCeldaHeader("Vencimiento"));
        tablaMes.addCell(crearCeldaHeader("Estado"));
        }

        tablaMes.addCell(rs.getString("nombre") + " " + rs.getString("apellido"));
        tablaMes.addCell(rs.getString("dni"));
        tablaMes.addCell(fechaInsc.toString());
        tablaMes.addCell(rs.getDate("fecha_vencimiento").toString());
        tablaMes.addCell(rs.getString("estado"));
    }

        if (tablaMes != null) documento.add(tablaMes);
        }
    }

    private PdfPCell crearCeldaHeader(String texto) {
        PdfPCell celda = new PdfPCell(new Phrase(texto, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
        celda.setBackgroundColor(com.itextpdf.text.BaseColor.LIGHT_GRAY);
        celda.setHorizontalAlignment(Element.ALIGN_CENTER);
        return celda;
    }
}