package com.gym.manager.view.panels;
import javax.swing.*;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;
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

 btnIngresos.addActionListener(e -> exportarPDF());
 btnAsistencia.addActionListener(e -> exportarPDF());

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
private void exportarPDF(){
    try {
        Document documento = new Document();

        PdfWriter.getInstance(
            documento, 
            new FileOutputStream("ReporteGym.pdf")
        );
        documento.open();

        documento.add(new Paragraph("REPORTE DEL GIMNASIO"));
        documento.close();

    JOptionPane.showMessageDialog(
        this,
        "PDF generado correctamente"
    );
} catch (Exception e){
    JOptionPane.showMessageDialog(
        this,
        "Error al generar pdf:" + e.getMessage()

    );
}
}
}
