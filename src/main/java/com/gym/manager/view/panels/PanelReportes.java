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


PdfPTable tablaPdf = new PdfPTable(2);
tablaPdf.addCell(
    new PdfPCell(
        new Phrase(tipoReporte.equals("INGRESOS") 
        ? "Tipo de Pago" 
        : "Clase"))
    );
tablaPdf.addCell(new PdfPCell(
    new Phrase(
        tipoReporte.equals("INGRESOS") ?
         "Total ($)"
         : "Asistentes"))
        );

// Conexión JDBC para traer los datos
Connection conn = DatabaseManager.getInstance().getConnection();
String sql = "";

if (tipoReporte.equals("INGRESOS")) {

sql = "SELECT tipo, SUM(monto) AS resultado " +
"FROM Pagos " +
"WHERE estado = 'PAGADO' " +
"GROUP BY tipo";

} else {

sql = "SELECT c.nombre, COUNT(i.idInscripciones) AS resultado " +
"FROM Clases c " +
"JOIN Inscripciones i " +
"ON c.idClases = i.Clases_idClases " +
"WHERE i.asistio = 1 " +
"GROUP BY c.idClases";
}

try (PreparedStatement pstmt = conn.prepareStatement(sql);
ResultSet rs = pstmt.executeQuery()) {

// Llenamos la tabla del PDF con los resultados de la base de datos
while (rs.next()) {
tablaPdf.addCell(rs.getString(1));
tablaPdf.addCell(rs.getString("resultado"));
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
