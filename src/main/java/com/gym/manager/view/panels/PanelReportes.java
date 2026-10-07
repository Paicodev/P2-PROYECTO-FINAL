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
import com.itextpdf.text.BaseColor;
import com.gym.manager.util.DatabaseManager;
import com.gym.manager.service.ReporteService;

import java.io.File;
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
    private ReporteService reporteService;

    public PanelReportes() { 
        reporteService = new ReporteService();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        inicializarComponentes();
    }

    private void inicializarComponentes() { 
        setBackground(new java.awt.Color(28, 43, 51));

        JLabel titulo = new JLabel("REPORTES Y BALANCES");
        titulo.setForeground(java.awt.Color.WHITE);
        titulo.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 22));
        titulo.setAlignmentX(CENTER_ALIGNMENT);

        btnIngresos  = new JButton("Generar Balance de Ingresos/Gastos");
        btnInscriptos = new JButton("Generar Reporte de Inscriptos");

        btnIngresos.addActionListener(e -> exportarPDF("INGRESOS"));
        btnInscriptos.addActionListener(e -> exportarPDF("INSCRIPTOS"));

        btnIngresos.setBackground(new java.awt.Color(0, 150, 136));
        btnIngresos.setForeground(java.awt.Color.WHITE);
        btnIngresos.setFocusPainted(false);
        
        btnInscriptos.setBackground(new java.awt.Color(0, 150, 136));
        btnInscriptos.setForeground(java.awt.Color.WHITE);
        btnInscriptos.setFocusPainted(false);

        String[] columnas = {"Reporte", "Estado", "Última Generación"};
        tablaReportes = new JTable(new javax.swing.table.DefaultTableModel(new Object[][]{}, columnas));

        tablaReportes.setBackground(new java.awt.Color(22, 38, 45));
        tablaReportes.setForeground(java.awt.Color.WHITE);
        tablaReportes.setRowHeight(25);
        tablaReportes.getTableHeader().setBackground(new java.awt.Color(35, 58, 70));
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
        String nombreSugerido = tipoReporte.equals("INGRESOS") ? "Balance_Financiero.pdf" : "Reporte_Inscriptos.pdf";
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File(nombreSugerido));
        int seleccion = fileChooser.showSaveDialog(this);

        if (seleccion != JFileChooser.APPROVE_OPTION) {
            return; // El usuario canceló la selección
        }

        File archivoDestino = fileChooser.getSelectedFile();

        try {
            Document documento = new Document();
            PdfWriter.getInstance(documento, new FileOutputStream(archivoDestino));
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
            JOptionPane.showMessageDialog(this, "PDF generado con éxito en:\n" + archivoDestino.getAbsolutePath(), "Reporte Exportado", JOptionPane.INFORMATION_MESSAGE);

            ((javax.swing.table.DefaultTableModel) tablaReportes.getModel()).addRow(new Object[]{
                tipoReporte, "Generado OK", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date())
            });

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al generar PDF: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generarReporteIngresos(
        Document documento,
        Connection conn,
        SimpleDateFormat sdfMes,
        com.itextpdf.text.Font fontMes)
        throws Exception {

    double totalSueldos = reporteService.calcularGastosFijos();

    String sql = "SELECT p.fecha_pago, per.dni, per.nombre, per.apellido, p.monto "
            + "FROM Pagos p "
            + "JOIN Miembros m ON p.Miembros_idMiembros = m.idMiembros "
            + "JOIN Persona per ON m.Persona_idPersona = per.idPersona "
            + "WHERE p.estado = 'PAGADO' "
            + "ORDER BY YEAR(p.fecha_pago) DESC, "
            + "MONTH(p.fecha_pago) DESC, "
            + "p.fecha_pago DESC";

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            String mesActual = "";
            double ingresosMes = 0;
            PdfPTable tablaMes = null;

            while (rs.next()) {
                java.sql.Date fecha = rs.getDate("fecha_pago");
                String mesFila = (fecha != null) ? sdfMes.format(fecha).toUpperCase() : "DESCONOCIDO";

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

                double monto = rs.getDouble("monto");
                ingresosMes += monto;
                
                tablaMes.addCell(fecha != null ? fecha.toString() : "-");
                tablaMes.addCell(rs.getString("nombre") + " " + rs.getString("apellido"));
                tablaMes.addCell(rs.getString("dni"));
                tablaMes.addCell(String.format("$%.2f", monto));
            }

            if (tablaMes != null) {
                cerrarTablaFinanciera(tablaMes, ingresosMes, totalSueldos);
                documento.add(tablaMes);
            } else {
                documento.add(new Paragraph("No se encontraron registros de pagos registrados."));
            }
        }
    }

    private void cerrarTablaFinanciera(PdfPTable tabla, double ingresos, double gastos) {
        com.itextpdf.text.Font fontBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD);

        // Fila 1: Total Ingresos
        tabla.addCell(crearCeldaVacia(2));
        tabla.addCell(new PdfPCell(new Phrase("TOTAL INGRESOS:", fontBold)));
        tabla.addCell(new PdfPCell(new Phrase(String.format("$%.2f", ingresos))));

        // Fila 2: Gastos
        PdfPCell cTituloGasto = new PdfPCell(new Phrase("GASTOS (Sueldos):", fontBold));
        PdfPCell cGasto = new PdfPCell(new Phrase(String.format("$%.2f", gastos)));
        cTituloGasto.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cGasto.setBackgroundColor(BaseColor.LIGHT_GRAY);

        tabla.addCell(crearCeldaVacia(2));
        tabla.addCell(cTituloGasto);
        tabla.addCell(cGasto);

        // Fila 3: Balance
        tabla.addCell(crearCeldaVacia(2));
        tabla.addCell(new PdfPCell(new Phrase("BALANCE NETO:", fontBold)));
        double balanceNeto = reporteService.calcularBalanceNeto(ingresos, gastos);

tabla.addCell(new PdfPCell(
    new Phrase(String.format("$%.2f", balanceNeto))));
    }

    private PdfPCell crearCeldaVacia(int colspan) {
        PdfPCell celda = new PdfPCell(new Phrase(""));
        celda.setColspan(colspan);
        celda.setBorder(PdfPCell.NO_BORDER);
        return celda;
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
                java.sql.Date fechaVenc = rs.getDate("fecha_vencimiento");
                String mesFila = (fechaInsc != null) ? sdfMes.format(fechaInsc).toUpperCase() : "SIN FECHA";

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
                tablaMes.addCell(fechaInsc != null ? fechaInsc.toString() : "-");
                tablaMes.addCell(fechaVenc != null ? fechaVenc.toString() : "-");
                tablaMes.addCell(rs.getString("estado"));
            }

            if (tablaMes != null) {
                documento.add(tablaMes);
            } else {
                documento.add(new Paragraph("No se encontraron miembros inscriptos."));
            }
        }
    }

    private PdfPCell crearCeldaHeader(String texto) {
        PdfPCell celda = new PdfPCell(new Phrase(texto, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
        celda.setBackgroundColor(BaseColor.LIGHT_GRAY);
        celda.setHorizontalAlignment(Element.ALIGN_CENTER);
        return celda;
    }
}