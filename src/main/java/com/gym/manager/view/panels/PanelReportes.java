package com.gym.manager.view.panels;
import javax.swing.*;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPCell;
import com.gym.manager.util.DatabaseManager;
/**
 * Panel de Reportes que exportará un informe de las operaciones
 */
public class PanelReportes extends JPanel {

    private JButton btnIngresos;
    private JButton btnAsistencia;
    private JTable tablaReportes;

    public PanelReportes() { 
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    inicializarComponentes();
    
    
}
 private void inicializarComponentes() { 
    setBackground(new java.awt.Color(17, 34, 46));

JLabel titulo = new JLabel("REPORTES");

titulo.setForeground(java.awt.Color.WHITE);
    titulo.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 22));
    titulo.setAlignmentX(CENTER_ALIGNMENT);


btnIngresos  = new JButton("Ver Ingresos");
btnAsistencia = new JButton("Ver Asistencia");

btnIngresos.addActionListener(e -> exportarPDF("INGRESOS"));
btnAsistencia.addActionListener(e -> exportarPDF("ASISTENCIA"));

 // Estilo botones
    btnIngresos.setBackground(new java.awt.Color(0, 153, 153));
    btnIngresos.setForeground(java.awt.Color.WHITE);

    btnAsistencia.setBackground(new java.awt.Color(0, 153, 153));
    btnAsistencia.setForeground(java.awt.Color.WHITE);

String[] columnas = {"Reporte", "Resultado"};

tablaReportes = new JTable (
    new javax.swing.table.DefaultTableModel ( 
        new Object [][]{},
    columnas
)
);

 // Estilo tabla
    tablaReportes.setBackground(new java.awt.Color(22, 40, 55));
    tablaReportes.setForeground(java.awt.Color.WHITE);
    tablaReportes.setRowHeight(25);

    tablaReportes.getTableHeader().setBackground(
        new java.awt.Color(35, 55, 75)
    );
    tablaReportes.getTableHeader().setForeground(
        java.awt.Color.WHITE
    );
    tablaReportes.setGridColor(java.awt.Color.GRAY);
tablaReportes.setSelectionBackground(
    new java.awt.Color(0, 153, 153)
);


JScrollPane scroll = new JScrollPane(tablaReportes);
scroll.getViewport().setBackground(
        new java.awt.Color(22, 40, 55)
    );

add(Box.createVerticalStrut(20));
add(titulo);
JPanel panelBotones = new JPanel();
panelBotones.setBackground(new java.awt.Color(17, 34, 46));
panelBotones.add(btnIngresos);
panelBotones.add(btnAsistencia);
add(panelBotones);
scroll.setPreferredSize(new java.awt.Dimension(800, 400));
add(scroll);
 
}
private void exportarPDF(String tipoReporte){
    try {
        Document documento = new Document();
        // Asignamos un nombre de archivo dinámico
String nombreArchivo = tipoReporte.equals("INGRESOS") ? 
"Reporte_Ingresos.pdf" : "Reporte_Asistencia.pdf";
PdfWriter.getInstance(documento, new FileOutputStream(nombreArchivo));
documento.open();

documento.add(new Paragraph("REPORTE DE " + tipoReporte + " DEL GIMNASIO\n\n"));


PdfPTable tablaPdf = new PdfPTable(3);
            
            if (tipoReporte.equals("INGRESOS")) {
                tablaPdf.addCell(new PdfPCell(new Phrase("Fecha")));
                tablaPdf.addCell(new PdfPCell(new Phrase("Socio (DNI)")));
                tablaPdf.addCell(new PdfPCell(new Phrase("Monto ($)")));
            } else {
                tablaPdf.addCell(new PdfPCell(new Phrase("Clase")));
                tablaPdf.addCell(new PdfPCell(new Phrase("Socio Asistente")));
                tablaPdf.addCell(new PdfPCell(new Phrase("Fecha Inscripción")));
            }

            Connection conn = DatabaseManager.getInstance().getConnection();
            String sql = "";

            if (tipoReporte.equals("INGRESOS")) {
                // Consulta detallada de ingresos (Uniendo Pagos con Miembros y Persona)
                sql = "SELECT p.fecha_pago, per.dni, per.nombre, per.apellido, p.monto " +
                      "FROM Pagos p " +
                      "JOIN Miembros m ON p.Miembros_idMiembros = m.idMiembros " +
                      "JOIN Persona per ON m.Persona_idPersona = per.idPersona " +
                      "WHERE p.estado = 'PAGADO' ORDER BY p.fecha_pago DESC";
            } else {
                // Consulta detallada de asistencia (Uniendo Inscripciones con Clases, Miembros y Persona)
                sql = "SELECT c.nombre as clase_nombre, per.nombre as socio_nom, per.apellido as socio_ape, i.fecha_inscripcion " +
                      "FROM Inscripciones i " +
                      "JOIN Clases c ON i.Clases_idClases = c.idClases " +
                      "JOIN Miembros m ON i.Miembros_idMiembros = m.idMiembros " +
                      "JOIN Persona per ON m.Persona_idPersona = per.idPersona " +
                      "ORDER BY c.nombre ASC";
            }

            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    if (tipoReporte.equals("INGRESOS")) {
                        tablaPdf.addCell(rs.getDate("fecha_pago").toString());
                        tablaPdf.addCell(rs.getString("nombre") + " " + rs.getString("apellido") + " (" + rs.getString("dni") + ")");
                        tablaPdf.addCell("$" + rs.getString("monto"));
                    } else {
                        tablaPdf.addCell(rs.getString("clase_nombre"));
                        tablaPdf.addCell(rs.getString("socio_nom") + " " + rs.getString("socio_ape"));
                        tablaPdf.addCell(rs.getDate("fecha_inscripcion").toString());
                    }
                }
            }

            documento.add(tablaPdf);
documento.close();

JOptionPane.showMessageDialog(this, "PDF de " + 
tipoReporte + " generado correctamente en la carpeta del proyecto.", "Éxito", 
JOptionPane.INFORMATION_MESSAGE);

} catch (Exception e) {
JOptionPane.showMessageDialog(this, "Error al generar el PDF: " +
e.getMessage(), "Error de E/S", JOptionPane.ERROR_MESSAGE
);
}
}   
}
