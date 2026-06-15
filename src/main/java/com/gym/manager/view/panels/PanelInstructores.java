package com.gym.manager.view.panels;

import com.gym.manager.dao.InstructorDAO;
import com.gym.manager.model.Instructor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class PanelInstructores extends JPanel {

    private static final Color BG_CENTRAL      = new Color(28, 43, 51);
    private static final Color BG_FORMULARIO   = new Color(22, 38, 45);
    private static final Color ACENTO_TURQUESA = new Color(0, 150, 136);
    private static final Color TEXTO_BLANCO    = new Color(230, 230, 230);
    private static final Color TEXTO_GRIS      = new Color(160, 175, 180);
    private static final Color BG_INPUTS       = new Color(35, 58, 70);

    private InstructorDAO instructorDAO = new InstructorDAO();
    private JTable tablaInstructores;
    private DefaultTableModel modeloTabla;
    
    private JTextField txtNombre, txtApellido, txtDni, txtEmail, txtTelefono, txtEspecialidad, txtSueldo;
    private int idEdicion = -1;

    public PanelInstructores() {
        setBackground(BG_CENTRAL);
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        add(crearPanelFormulario(), BorderLayout.NORTH);
        add(crearPanelGrilla(), BorderLayout.CENTER);
        cargarDatos();
    }

    private JPanel crearPanelFormulario() {
        JPanel panelContenedor = new JPanel(new BorderLayout(10, 10));
        panelContenedor.setBackground(BG_FORMULARIO);
        TitledBorder borde = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(50, 70, 80)), " Gestión de Instructores ");
        borde.setTitleColor(ACENTO_TURQUESA);
        borde.setTitleFont(new Font("Segoe UI", Font.BOLD, 14));
        panelContenedor.setBorder(BorderFactory.createCompoundBorder(borde, new EmptyBorder(10, 10, 10, 10)));

        JPanel panelCampos = new JPanel(new GridLayout(4, 4, 15, 15));
        panelCampos.setBackground(BG_FORMULARIO);

        txtNombre = crearTextField(); txtApellido = crearTextField();
        txtDni = crearTextField(); txtEmail = crearTextField();
        txtTelefono = crearTextField(); txtEspecialidad = crearTextField();
        txtSueldo = crearTextField();

        panelCampos.add(crearLabel("Nombre:")); panelCampos.add(txtNombre);
        panelCampos.add(crearLabel("Apellido:")); panelCampos.add(txtApellido);
        panelCampos.add(crearLabel("DNI:")); panelCampos.add(txtDni);
        panelCampos.add(crearLabel("Email:")); panelCampos.add(txtEmail);
        panelCampos.add(crearLabel("Teléfono:")); panelCampos.add(txtTelefono);
        panelCampos.add(crearLabel("Especialidad:")); panelCampos.add(txtEspecialidad);
        panelCampos.add(crearLabel("Sueldo ($):")); panelCampos.add(txtSueldo);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotones.setBackground(BG_FORMULARIO);
        JButton btnLimpiar = crearBoton("Limpiar", new Color(80, 90, 100));
        JButton btnGuardar = crearBoton("Guardar Instructor", ACENTO_TURQUESA);
        btnLimpiar.addActionListener(e -> limpiarForm());
        btnGuardar.addActionListener(e -> guardarInstructor());
        panelBotones.add(btnLimpiar); panelBotones.add(btnGuardar);

        panelContenedor.add(panelCampos, BorderLayout.CENTER);
        panelContenedor.add(panelBotones, BorderLayout.SOUTH);
        return panelContenedor;
    }

    private JPanel crearPanelGrilla() {
        JPanel panelTabla = new JPanel(new BorderLayout());
        panelTabla.setBackground(BG_CENTRAL);
        String[] col = {"ID", "Nombre Completo", "DNI", "Especialidad", "Sueldo"};
        modeloTabla = new DefaultTableModel(col, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        tablaInstructores = new JTable(modeloTabla);
        tablaInstructores.setBackground(BG_FORMULARIO);
        tablaInstructores.setForeground(TEXTO_BLANCO);
        tablaInstructores.setRowHeight(25);
        tablaInstructores.setSelectionBackground(ACENTO_TURQUESA);
        tablaInstructores.setSelectionForeground(Color.WHITE);
        JTableHeader h = tablaInstructores.getTableHeader();
        h.setBackground(BG_INPUTS); h.setForeground(Color.WHITE); h.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JScrollPane scroll = new JScrollPane(tablaInstructores);
        scroll.getViewport().setBackground(BG_CENTRAL);
        panelTabla.add(scroll, BorderLayout.CENTER);

        JPanel panelBot = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBot.setBackground(BG_CENTRAL);
        JButton btnEditar = crearBoton("Editar Seleccionado", new Color(41, 128, 185));
        JButton btnEliminar = crearBoton("Eliminar Seleccionado", new Color(180, 50, 50));
        btnEditar.addActionListener(e -> cargarFormulario());
        btnEliminar.addActionListener(e -> eliminarInstructor());
        panelBot.add(btnEliminar); panelBot.add(btnEditar);
        panelTabla.add(panelBot, BorderLayout.SOUTH);

        return panelTabla;
    }

    private void cargarDatos() {
        modeloTabla.setRowCount(0);
        List<Instructor> lista = instructorDAO.obtenerTodos();
        for (Instructor i : lista) {
            modeloTabla.addRow(new Object[]{i.getId(), i.getNombreCompleto(), i.getDni(), i.getEspecialidad(), i.getSueldo()});
        }
    }

    private void guardarInstructor() {
        try {
            double sueldo = Double.parseDouble(txtSueldo.getText());
            Instructor inst = new Instructor(
                idEdicion == -1 ? 0 : idEdicion, txtNombre.getText(), txtApellido.getText(),
                txtDni.getText(), txtEmail.getText(), txtTelefono.getText(), txtEspecialidad.getText(), sueldo
            );
            if (idEdicion == -1) instructorDAO.guardar(inst);
            else instructorDAO.actualizar(inst);
            
            JOptionPane.showMessageDialog(this, "Instructor guardado exitosamente.");
            limpiarForm();
            cargarDatos();
        } catch (Exception ex) {
            ex.printStackTrace(); 
    JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarFormulario() {
        int f = tablaInstructores.getSelectedRow();
        if (f == -1) return;
        int id = (int) tablaInstructores.getValueAt(f, 0);
        instructorDAO.buscarPorId(id).ifPresent(i -> {
            idEdicion = i.getId();
            txtNombre.setText(i.getNombre()); txtApellido.setText(i.getApellido());
            txtDni.setText(i.getDni()); txtEmail.setText(i.getEmail());
            txtTelefono.setText(i.getTelefono()); txtEspecialidad.setText(i.getEspecialidad());
            txtSueldo.setText(String.valueOf(i.getSueldo()));
        });
    }

    private void eliminarInstructor() {
        int f = tablaInstructores.getSelectedRow();
        if (f == -1) return;
        int id = (int) tablaInstructores.getValueAt(f, 0);
        if (JOptionPane.showConfirmDialog(this, "¿Eliminar este instructor?") == JOptionPane.YES_OPTION) {
            try {
                instructorDAO.eliminar(id);
                cargarDatos();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void limpiarForm() {
        txtNombre.setText(""); txtApellido.setText(""); txtDni.setText("");
        txtEmail.setText(""); txtTelefono.setText(""); txtEspecialidad.setText(""); txtSueldo.setText("");
        idEdicion = -1;
    }

    private JLabel crearLabel(String texto) {
        JLabel l = new JLabel(texto); l.setForeground(TEXTO_GRIS); l.setFont(new Font("Segoe UI", Font.BOLD, 12)); return l;
    }
    private JTextField crearTextField() {
        JTextField t = new JTextField(); t.setBackground(BG_INPUTS); t.setForeground(TEXTO_BLANCO);
        t.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(60, 80, 95)), new EmptyBorder(5,5,5,5)));
        return t;
    }
    private JButton crearBoton(String texto, Color c) {
        JButton b = new JButton(texto); b.setBackground(c); b.setForeground(Color.WHITE); b.setFocusPainted(false); return b;
    }
}