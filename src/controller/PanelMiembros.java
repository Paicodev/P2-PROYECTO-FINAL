package com.gym.manager.view.panels;

import com.gym.manager.model.Miembro;
import com.gym.manager.service.MiembroService;
import com.gym.manager.view.dialogs.DialogMiembro;

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

public class PanelMiembros extends JPanel {

    // Colores del tema de FitBase
    private static final Color BG_CENTRAL      = new Color(28, 43, 51);
    private static final Color BG_FORMULARIO   = new Color(22, 38, 45);
    private static final Color ACENTO_TURQUESA = new Color(0, 150, 136);
    private static final Color TEXTO_BLANCO    = new Color(230, 230, 230);
    private static final Color TEXTO_GRIS      = new Color(160, 175, 180);
    private static final Color BG_INPUTS       = new Color(35, 58, 70);

    private MiembroService miembroService;

    private JTable tablaMiembros;
    private DefaultTableModel modeloTabla;
    private TableRowSorter<DefaultTableModel> sorter; 
    private JTextField txtBuscar;

    public PanelMiembros() {
        this.miembroService = new MiembroService();

        setBackground(BG_CENTRAL);
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(crearPanelSuperior(), BorderLayout.NORTH);
        add(crearPanelGrilla(), BorderLayout.CENTER);

        // Llamo al SwingWorker apenas carga el panel
        cargarDatosEnTabla();
    }

    private JPanel crearPanelSuperior() {
        JPanel panelSup = new JPanel(new BorderLayout(10, 10));
        panelSup.setBackground(BG_FORMULARIO);
        
        TitledBorder borde = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(50, 70, 80)), " Buscar Miembro ");
        borde.setTitleColor(ACENTO_TURQUESA);
        borde.setTitleFont(new Font("Segoe UI", Font.BOLD, 14));
        panelSup.setBorder(BorderFactory.createCompoundBorder(borde, new EmptyBorder(10, 10, 10, 10)));

        JPanel panelBuscar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBuscar.setBackground(BG_FORMULARIO);
        
        JLabel lblBuscar = new JLabel("Buscar por Nombre o DNI: ");
        lblBuscar.setForeground(TEXTO_GRIS);
        lblBuscar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        txtBuscar = new JTextField(25); 
        txtBuscar.setBackground(BG_INPUTS);
        txtBuscar.setForeground(TEXTO_BLANCO);
        txtBuscar.setCaretColor(Color.WHITE);
        txtBuscar.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(60, 80, 95)), new EmptyBorder(5, 5, 5, 5)));

        // Filtro en tiempo real al escribir
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
        
        JButton btnNuevo = crearBoton("+ Nuevo Miembro", ACENTO_TURQUESA);
        btnNuevo.addActionListener(e -> abrirDialogoNuevoMiembro());
        panelBotones.add(btnNuevo);

        panelSup.add(panelBuscar, BorderLayout.WEST);
        panelSup.add(panelBotones, BorderLayout.EAST);

        return panelSup;
    }

    private JPanel crearPanelGrilla() {
        JPanel panelTabla = new JPanel(new BorderLayout());
        panelTabla.setBackground(BG_CENTRAL);

        String[] columnas = {"ID", "Nombre Completo", "DNI", "Teléfono", "Plan", "Estado", "Vencimiento"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tablaMiembros = new JTable(modeloTabla);
        tablaMiembros.setBackground(BG_FORMULARIO);
        tablaMiembros.setForeground(TEXTO_BLANCO);
        tablaMiembros.setRowHeight(25);
        tablaMiembros.setSelectionBackground(ACENTO_TURQUESA);
        tablaMiembros.setSelectionForeground(Color.WHITE);

        // Agrego el TableRowSorter que pedía el requerimiento para la búsqueda
        sorter = new TableRowSorter<>(modeloTabla);
        tablaMiembros.setRowSorter(sorter);

        JTableHeader header = tablaMiembros.getTableHeader();
        header.setBackground(BG_INPUTS);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JScrollPane scrollPane = new JScrollPane(tablaMiembros);
        scrollPane.getViewport().setBackground(BG_CENTRAL);
        panelTabla.add(scrollPane, BorderLayout.CENTER);

        JPanel panelBotonesTabla = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotonesTabla.setBackground(BG_CENTRAL);

        JButton btnEditar = crearBoton("Editar Seleccionado", new Color(41, 128, 185));
        JButton btnEliminar = crearBoton("Eliminar Seleccionado", new Color(180, 50, 50));

        btnEditar.addActionListener(e -> abrirDialogoEditarMiembro());
        btnEliminar.addActionListener(e -> eliminarMiembroSeleccionado());

        panelBotonesTabla.add(btnEliminar);
        panelBotonesTabla.add(btnEditar);

        panelTabla.add(panelBotonesTabla, BorderLayout.SOUTH);

        return panelTabla;
    }

    // Uso SwingWorker para que no se trabe la UI mientras busca en MySQL
    public void cargarDatosEnTabla() {
        SwingWorker<List<Miembro>, Void> worker = new SwingWorker<List<Miembro>, Void>() {
            
            @Override
            protected List<Miembro> doInBackground() throws Exception {
                return miembroService.obtenerTodos();
            }

            @Override
            protected void done() {
                try {
                    List<Miembro> miembrosTraidosDeBD = get(); 
                    modeloTabla.setRowCount(0); 
                    
                    for (Miembro m : miembrosTraidosDeBD) {
                        String nombrePlan = (m.getPlan() != null) ? m.getPlan().getNombrePlan() : "Sin Plan";
                        
                        Object[] fila = {
                            m.getId(), 
                            m.getNombreCompleto(), 
                            m.getDni(), 
                            m.getTelefono(), 
                            nombrePlan, 
                            m.getEstado().name(), 
                            m.getFechaVencimiento()
                        };
                        modeloTabla.addRow(fila);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(PanelMiembros.this, "Error al cargar los miembros: " + e.getMessage());
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

    private void eliminarMiembroSeleccionado() {
        int fila = tablaMiembros.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un miembro para eliminar.");
            return;
        }

        // convierto el indice visual a indice del modelo para obtener el ID correcto aunque haya un filtro aplicado
        int filaRealModelo = tablaMiembros.convertRowIndexToModel(fila);
        int idMiembro = (int) modeloTabla.getValueAt(filaRealModelo, 0);

        int confirmacion = JOptionPane.showConfirmDialog(this, "¿Seguro que desea eliminar a este miembro del sistema?", "Confirmar", JOptionPane.YES_NO_OPTION);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            miembroService.eliminarMiembro(idMiembro);
            cargarDatosEnTabla(); 
        }
    }

    private void abrirDialogoNuevoMiembro() {
       // En Swing, para obtener la ventana principal contenedora usamos SwingUtilities
        Window parent = SwingUtilities.getWindowAncestor(this);
        DialogMiembro dialog = new DialogMiembro(parent, null);
        dialog.setVisible(true); // El código se pausa acá hasta que el usuario cierre el dialog

        if (dialog.isConfirmado()) {
            try {
                miembroService.guardarMiembro(dialog.getMiembroResultante());
                cargarDatosEnTabla();
                JOptionPane.showMessageDialog(this, "Miembro registrado con éxito.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void abrirDialogoEditarMiembro() {
        int fila = tablaMiembros.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un miembro para editar.");
            return;
        }

        int filaRealModelo = tablaMiembros.convertRowIndexToModel(fila);
        int idMiembro = (int) modeloTabla.getValueAt(filaRealModelo, 0);
        
        // armo el DialogMiembro pasándole el ID del miembro seleccionado para que traiga sus datos y los muestre en el formulario
        // Buscamos el miembro completo en la base de datos para editarlo
        miembroService.buscarPorId(idMiembro).ifPresent(miembroEditar -> {
            Window parent = SwingUtilities.getWindowAncestor(this);
            DialogMiembro dialog = new DialogMiembro(parent, miembroEditar);
            dialog.setVisible(true);

            if (dialog.isConfirmado()) {
                try {
                    miembroService.actualizarMiembro(dialog.getMiembroResultante());
                    cargarDatosEnTabla();
                    JOptionPane.showMessageDialog(this, "Miembro actualizado con éxito.");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
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