package com.gym.manager.view.panels;

import com.gym.manager.dao.InscripcionesDAO;
import com.gym.manager.view.dialogs.DialogInscripcion;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

public class PanelInscripciones extends JPanel {

    private static final Color BG_CENTRAL      = new Color(28, 43, 51);
    private static final Color BG_FORMULARIO   = new Color(22, 38, 45);
    private static final Color ACENTO_TURQUESA = new Color(0, 150, 136);
    private static final Color TEXTO_BLANCO    = new Color(230, 230, 230);
    private static final Color TEXTO_GRIS      = new Color(160, 175, 180);
    private static final Color BG_INPUTS       = new Color(35, 58, 70);

    private InscripcionesDAO inscripcionesDAO;
    private JTable tablaInscripciones;
    private DefaultTableModel modeloTabla;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField txtBuscar;

    public PanelInscripciones() {
        this.inscripcionesDAO = new InscripcionesDAO();

        setBackground(BG_CENTRAL);
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(crearPanelSuperior(), BorderLayout.NORTH);
        add(crearPanelGrilla(), BorderLayout.CENTER);

        cargarDatosEnTabla();
    }

    private JPanel crearPanelSuperior() {
        JPanel panelSup = new JPanel(new BorderLayout(10, 10));
        panelSup.setBackground(BG_FORMULARIO);
        
        TitledBorder borde = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(50, 70, 80)), " Gestión de Inscripciones ");
        borde.setTitleColor(ACENTO_TURQUESA);
        borde.setTitleFont(new Font("Segoe UI", Font.BOLD, 14));
        panelSup.setBorder(BorderFactory.createCompoundBorder(borde, new EmptyBorder(10, 10, 10, 10)));

        JPanel panelBuscar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBuscar.setBackground(BG_FORMULARIO);
        
        JLabel lblBuscar = new JLabel("🔎 Buscar Inscripción: ");
        lblBuscar.setForeground(TEXTO_GRIS);
        lblBuscar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        txtBuscar = new JTextField(25); 
        txtBuscar.setBackground(BG_INPUTS);
        txtBuscar.setForeground(TEXTO_BLANCO);
        txtBuscar.setCaretColor(Color.WHITE);
        txtBuscar.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(60, 80, 95)), new EmptyBorder(5, 5, 5, 5)));

        txtBuscar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filtrarTabla();
            }
        });

        panelBuscar.add(lblBuscar);
        panelBuscar.add(txtBuscar);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotones.setBackground(BG_FORMULARIO);
        
        JButton btnNuevo = crearBoton("+ Nueva Inscripción", ACENTO_TURQUESA);
        btnNuevo.addActionListener(e -> abrirDialogoNuevaInscripcion());
        panelBotones.add(btnNuevo);

        panelSup.add(panelBuscar, BorderLayout.WEST);
        panelSup.add(panelBotones, BorderLayout.EAST);

        return panelSup;
    }

    private JPanel crearPanelGrilla() {
        JPanel panelTabla = new JPanel(new BorderLayout());
        panelTabla.setBackground(BG_CENTRAL);

        String[] columnas = {"ID", "Fecha", "Socio Inscripto", "Clase", "Asistió"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tablaInscripciones = new JTable(modeloTabla);
        tablaInscripciones.setBackground(BG_FORMULARIO);
        tablaInscripciones.setForeground(TEXTO_BLANCO);
        tablaInscripciones.setRowHeight(25);
        tablaInscripciones.setSelectionBackground(ACENTO_TURQUESA);
        tablaInscripciones.setSelectionForeground(Color.WHITE);

        sorter = new TableRowSorter<>(modeloTabla);
        tablaInscripciones.setRowSorter(sorter);

        JTableHeader header = tablaInscripciones.getTableHeader();
        header.setBackground(BG_INPUTS);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JScrollPane scrollPane = new JScrollPane(tablaInscripciones);
        scrollPane.getViewport().setBackground(BG_CENTRAL);
        panelTabla.add(scrollPane, BorderLayout.CENTER);

        JPanel panelBotonesTabla = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotonesTabla.setBackground(BG_CENTRAL);

        JButton btnEliminar = crearBoton("Dar de Baja (Liberar Cupo)", new Color(180, 50, 50));
        btnEliminar.addActionListener(e -> eliminarInscripcionSeleccionada());

        panelBotonesTabla.add(btnEliminar);
        panelTabla.add(panelBotonesTabla, BorderLayout.SOUTH);

        return panelTabla;
    }

    // Usamos SwingWorker para cargar sin trabar el programa
    public void cargarDatosEnTabla() {
        SwingWorker<List<Object[]>, Void> worker = new SwingWorker<List<Object[]>, Void>() {
            @Override
            protected List<Object[]> doInBackground() {
                return inscripcionesDAO.obtenerTodasConDetalles();
            }

            @Override
            protected void done() {
                try {
                    List<Object[]> datos = get(); 
                    modeloTabla.setRowCount(0); 
                    for (Object[] fila : datos) {
                        modeloTabla.addRow(fila);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(PanelInscripciones.this, "Error al cargar la grilla: " + e.getMessage());
                }
            }
        };
        worker.execute(); 
    }

    private void filtrarTabla() {
        String textoBuscado = txtBuscar.getText().trim();
        if (textoBuscado.length() == 0) {
            sorter.setRowFilter(null); 
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + textoBuscado));
        }
    }

    private void eliminarInscripcionSeleccionada() {
        int fila = tablaInscripciones.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una inscripción para darla de baja.");
            return;
        }

        int filaRealModelo = tablaInscripciones.convertRowIndexToModel(fila);
        int idInscripcion = (int) modeloTabla.getValueAt(filaRealModelo, 0);

        int confirmacion = JOptionPane.showConfirmDialog(this, "¿Seguro que desea cancelar esta inscripción? Esto liberará el cupo de la clase.", "Confirmar Baja", JOptionPane.YES_NO_OPTION);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                inscripcionesDAO.darDeBaja(idInscripcion);
                JOptionPane.showMessageDialog(this, "Inscripción dada de baja. El cupo ha sido liberado.");
                cargarDatosEnTabla(); // Refrescar SwingWorker
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void abrirDialogoNuevaInscripcion() {
        Window parent = SwingUtilities.getWindowAncestor(this);
        DialogInscripcion dialog = new DialogInscripcion(parent);
        dialog.setVisible(true);

        // Cuando el diálogo se cierra, recargamos la tabla
        if (dialog.isInscripcionRealizada()) {
            cargarDatosEnTabla();
        }
    }

    private JButton crearBoton(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}