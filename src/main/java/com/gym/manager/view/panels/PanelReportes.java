package com.gym.manager.view.panels;
import javax.swing.*;

/**
 * Panel de Reportes que exportará un informe de las operaciones
 */
public class PanelReportes extends JPanel {

    private JButton btnIngresos;
    private JButton btnAsistencia;
    private JTable tablaReportes;

    public PanelReportes() { 
    inicializarComponentes();
    
}
 private void inicializarComponentes() { 

 JLabel titulo = new JLabel("REPORTES");

 btnIngresos  = new JButton("Ver Ingresos");
 btnAsistencia = new JButton("Ver Asistencia");

 String[] columnas = {"Reporte", "Resultado"};

 tablaReportes = new JTable (
    new javax.swing.table.DefaultTableModel ( new Object [][]{},
    columnas
 )
);

 JScrollPane scroll = new JScrollPane(tablaReportes);

 add(titulo);
 add(btnIngresos);;
 add(btnAsistencia);
 add(scroll);
}
}