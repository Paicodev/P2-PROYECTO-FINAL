package com.gym.manager.view.panels;

import com.gym.manager.model.Plan;
import com.gym.manager.service.PlanService;
import com.gym.manager.exceptions.DatosInvalidosException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class PanelPlan extends JPanel {

    private static final Color BG_CENTRAL      = new Color(28, 43, 51);
    private static final Color BG_FORMULARIO   = new Color(22, 38, 45);
    private static final Color ACENTO_TURQUESA = new Color(0, 150, 136);
    private static final Color TEXTO_BLANCO    = new Color(230, 230, 230);
    private static final Color TEXTO_GRIS      = new Color(160, 175, 180);
    private static final Color BG_INPUTS       = new Color(35, 58, 70);

    private PlanService planService;

    private JTable tablaPlanes;
    private DefaultTableModel modeloTabla;
    
    private JTextField txtNombre;
    private JTextField txtDuracion;
    private JTextField txtDescripcion;
    private JTextField txtPrecio;
    
    private int idPlanEnEdicion = -1; 

    public PanelPlan() {
        this.planService = new PlanService();

        setBackground(BG_CENTRAL);
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(crearPanelFormulario(), BorderLayout.NORTH);
        add(crearPanelGrilla(), BorderLayout.CENTER);

        cargarDatosEnTabla();
    }

    private JPanel crearPanelFormulario() {
        JPanel panelContenedor = new JPanel(new BorderLayout(10, 10));
        panelContenedor.setBackground(BG_FORMULARIO);
        
        TitledBorder borde = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(50, 70, 80)), " Gestión de Planes ");
        borde.setTitleColor(ACENTO_TURQUESA);
        borde.setTitleFont(new Font("Segoe UI", Font.BOLD, 14));
        panelContenedor.setBorder(BorderFactory.createCompoundBorder(borde, new EmptyBorder(10, 10, 10, 10)));

        JPanel panelCampos = new JPanel(new GridLayout(2, 4, 15, 15));
        panelCampos.setBackground(BG_FORMULARIO);

        txtNombre = crearTextField();
        txtDuracion = crearTextField();
        txtDescripcion = crearTextField();
        txtPrecio = crearTextField();

        panelCampos.add(crearLabel("Nombre del Plan:"));
        panelCampos.add(txtNombre);
        panelCampos.add(crearLabel("Duración (Meses):"));
        panelCampos.add(txtDuracion);

        panelCampos.add(crearLabel("Descripción:"));
        panelCampos.add(txtDescripcion);
        panelCampos.add(crearLabel("Precio Mensual ($):"));
        panelCampos.add(txtPrecio);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotones.setBackground(BG_FORMULARIO);

        JButton btnLimpiar = crearBoton("Limpiar", new Color(80, 90, 100));
        JButton btnGuardar = crearBoton("Guardar Plan", ACENTO_TURQUESA);

        btnLimpiar.addActionListener(e -> limpiarFormulario());
        btnGuardar.addActionListener(e -> guardarOActualizarPlan());

        panelBotones.add(btnLimpiar);
        panelBotones.add(btnGuardar);

        panelContenedor.add(panelCampos, BorderLayout.CENTER);
        panelContenedor.add(panelBotones, BorderLayout.SOUTH);

        return panelContenedor;
    }

    private JPanel crearPanelGrilla() {
        JPanel panelTabla = new JPanel(new BorderLayout());
        panelTabla.setBackground(BG_CENTRAL);

        String[] columnas = {"ID", "Nombre", "Meses", "Descripción", "Precio"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tablaPlanes = new JTable(modeloTabla);
        tablaPlanes.setBackground(BG_FORMULARIO);
        tablaPlanes.setForeground(TEXTO_BLANCO);
        tablaPlanes.setRowHeight(25);
        tablaPlanes.setSelectionBackground(ACENTO_TURQUESA);
        tablaPlanes.setSelectionForeground(Color.WHITE);

        JTableHeader header = tablaPlanes.getTableHeader();
        header.setBackground(BG_INPUTS);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JScrollPane scrollPane = new JScrollPane(tablaPlanes);
        scrollPane.getViewport().setBackground(BG_CENTRAL);
        panelTabla.add(scrollPane, BorderLayout.CENTER);

        JPanel panelBotonesTabla = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotonesTabla.setBackground(BG_CENTRAL);

        JButton btnEditar = crearBoton("Editar Seleccionado", new Color(41, 128, 185));
        JButton btnEliminar = crearBoton("Eliminar Seleccionado", new Color(180, 50, 50));

        btnEditar.addActionListener(e -> cargarPlanEnFormulario());
        btnEliminar.addActionListener(e -> eliminarPlanSeleccionado());

        panelBotonesTabla.add(btnEliminar);
        panelBotonesTabla.add(btnEditar);

        panelTabla.add(panelBotonesTabla, BorderLayout.SOUTH);

        return panelTabla;
    }

    private void cargarDatosEnTabla() {
        modeloTabla.setRowCount(0); 
        List<Plan> planes = planService.obtenerTodos(); 
        
        for (Plan p : planes) {
            Object[] fila = { p.getId(), p.getNombrePlan(), p.getDuracionMeses(), p.getDescripcion(), p.getPrecioMensual() };
            modeloTabla.addRow(fila);
        }
    }

    private void guardarOActualizarPlan() {
        try {
            String nombre = txtNombre.getText();
            int meses = Integer.parseInt(txtDuracion.getText());
            String desc = txtDescripcion.getText();
            double precio = Double.parseDouble(txtPrecio.getText());

            if (idPlanEnEdicion == -1) {
                Plan nuevoPlan = new Plan(0, nombre, meses, desc, precio);
                planService.guardarPlan(nuevoPlan); 
                JOptionPane.showMessageDialog(this, "Plan guardado con éxito.");
            } else {
                Plan planActualizado = new Plan(idPlanEnEdicion, nombre, meses, desc, precio);
                planService.actualizarPlan(planActualizado); 
                JOptionPane.showMessageDialog(this, "Plan actualizado con éxito.");
            }

            limpiarFormulario();
            cargarDatosEnTabla();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Error: Verifique que meses y precio sean números válidos.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (DatosInvalidosException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de Validación", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void cargarPlanEnFormulario() {
        int fila = tablaPlanes.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un plan para editar.");
            return;
        }

        idPlanEnEdicion = (int) tablaPlanes.getValueAt(fila, 0);
        txtNombre.setText((String) tablaPlanes.getValueAt(fila, 1));
        txtDuracion.setText(String.valueOf(tablaPlanes.getValueAt(fila, 2)));
        txtDescripcion.setText((String) tablaPlanes.getValueAt(fila, 3));
        txtPrecio.setText(String.valueOf(tablaPlanes.getValueAt(fila, 4)));
    }

    private void eliminarPlanSeleccionado() {
        int fila = tablaPlanes.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un plan para eliminar.");
            return;
        }

        int id = (int) tablaPlanes.getValueAt(fila, 0);
        int confirmacion = JOptionPane.showConfirmDialog(this, "¿Seguro que desea eliminar este plan?", "Confirmar", JOptionPane.YES_NO_OPTION);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                // Intentamos eliminar
                planService.eliminarPlan(id); 
                cargarDatosEnTabla();
                JOptionPane.showMessageDialog(this, "Plan eliminado con éxito.");
            } catch (Exception ex) {
                // Si MySQL frena el borrado, mostramos el cartel de error
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de Integridad", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void limpiarFormulario() {
        txtNombre.setText("");
        txtDuracion.setText("");
        txtDescripcion.setText("");
        txtPrecio.setText("");
        idPlanEnEdicion = -1; 
    }

    private JLabel crearLabel(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setForeground(TEXTO_GRIS);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return lbl;
    }

    private JTextField crearTextField() {
        JTextField txt = new JTextField();
        txt.setBackground(BG_INPUTS);
        txt.setForeground(TEXTO_BLANCO);
        txt.setCaretColor(Color.WHITE); 
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 80, 95)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        return txt;
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