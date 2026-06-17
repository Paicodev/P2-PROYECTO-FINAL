package com.gym.manager.view.panels;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import com.gym.manager.dao.InstructorDAO;
import com.gym.manager.model.Instructor;
import com.gym.manager.model.ClaseGimnasio;
import com.gym.manager.model.ClaseGrupal;
import com.gym.manager.model.ClasePersonal;
import com.gym.manager.service.ClaseService;

import java.util.List;
import java.awt.*;

public class PanelClases extends JPanel {

    private static final Color BG_CENTRAL      = new Color(28, 43, 51);
    private static final Color BG_FORMULARIO   = new Color(22, 38, 45);
    private static final Color ACENTO_TURQUESA = new Color(0, 150, 136);
    private static final Color TEXTO_BLANCO    = new Color(230, 230, 230);
    private static final Color TEXTO_GRIS      = new Color(160, 175, 180);
    private static final Color BG_INPUTS       = new Color(35, 58, 70);

    private JTable tablaClases;
    private DefaultTableModel modeloTabla;
    
    private JTextField txtNombre, txtDuracion, txtCapacidad;
    private JSpinner spinnerHorario;
    private JComboBox<String> comboTipo;
    private JComboBox<Instructor> comboInstructor; 
    
    private InstructorDAO instructorDAO;
    private ClaseService claseService;
    
    private JButton btnGuardar, btnLimpiar, btnEditar, btnEliminar;
    private int idClaseEdicion = -1; // -1 significa "Modo Crear", otro número significa "Modo Editar"

    public PanelClases() {
        this.claseService = new ClaseService();
        this.instructorDAO = new InstructorDAO();
        
        setBackground(BG_CENTRAL);
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        add(crearPanelFormulario(), BorderLayout.NORTH);
        add(crearPanelGrilla(), BorderLayout.CENTER);

        inicializarEventos();
        cargarClases();
    }

    private JPanel crearPanelFormulario() {
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBackground(BG_FORMULARIO);
        
        TitledBorder bordeTitulo = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(50, 70, 80), 1), " Gestión de Clases ");
        bordeTitulo.setTitleColor(ACENTO_TURQUESA);
        bordeTitulo.setTitleFont(new Font("Segoe UI", Font.BOLD, 14));
        panelForm.setBorder(BorderFactory.createCompoundBorder(bordeTitulo, new EmptyBorder(15, 15, 15, 15)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtNombre = crearTextField();
        
        comboTipo = new JComboBox<>(new String[]{"GRUPAL", "PERSONAL"});
        estilizarComponenteUI(comboTipo);
        
        SpinnerDateModel modeloFecha = new SpinnerDateModel();
        spinnerHorario = new JSpinner(modeloFecha);
        JSpinner.DateEditor editorFecha = new JSpinner.DateEditor(spinnerHorario, "yyyy-MM-dd HH:mm");
        spinnerHorario.setEditor(editorFecha);
        estilizarComponenteUI(editorFecha.getTextField());
        editorFecha.getTextField().setCaretColor(Color.WHITE);
        
        txtDuracion = crearTextField();
        txtCapacidad = crearTextField();
        
        comboInstructor = new JComboBox<>();
        cargarInstructores(); 
        estilizarComponenteUI(comboInstructor);

        // Fila 0
        colocarComponente(panelForm, crearLabel("Nombre de la Clase:"), gbc, 0, 0, 1);
        colocarComponente(panelForm, txtNombre, gbc, 1, 0, 1);
        colocarComponente(panelForm, crearLabel("Tipo de Clase:"), gbc, 2, 0, 1);
        colocarComponente(panelForm, comboTipo, gbc, 3, 0, 1);

        // Fila 1
        colocarComponente(panelForm, crearLabel("Horario (Fecha/Hora):"), gbc, 0, 1, 1);
        colocarComponente(panelForm, spinnerHorario, gbc, 1, 1, 1);
        colocarComponente(panelForm, crearLabel("Duración (Minutos):"), gbc, 2, 1, 1);
        colocarComponente(panelForm, txtDuracion, gbc, 3, 1, 1);

        // Fila 2
        colocarComponente(panelForm, crearLabel("Capacidad Máxima:"), gbc, 0, 2, 1);
        colocarComponente(panelForm, txtCapacidad, gbc, 1, 2, 1);
        colocarComponente(panelForm, crearLabel("Instructor Asignado:"), gbc, 2, 2, 1);
        colocarComponente(panelForm, comboInstructor, gbc, 3, 2, 1);

        // Fila 3 - Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBotones.setOpaque(false);
        btnLimpiar = crearBoton("Limpiar", new Color(80, 90, 100));
        btnEditar = crearBoton("Editar Seleccionada", new Color(41, 128, 185));
        btnGuardar = crearBoton("Guardar Clase", ACENTO_TURQUESA);

        panelBotones.add(btnLimpiar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnGuardar);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4; gbc.weightx = 1.0;
        panelForm.add(panelBotones, gbc);

        return panelForm;
    }

    private JPanel crearPanelGrilla() {
        JPanel panelTabla = new JPanel(new BorderLayout());
        panelTabla.setBackground(BG_CENTRAL);
        String[] columnas = {"ID", "Clase", "Tipo", "Horario", "Duración (min)", "Cupo Máx", "Instructor"};
        
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        tablaClases = new JTable(modeloTabla);
        tablaClases.setBackground(BG_FORMULARIO);
        tablaClases.setForeground(TEXTO_BLANCO);
        tablaClases.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaClases.setRowHeight(25);
        tablaClases.setSelectionBackground(ACENTO_TURQUESA);
        tablaClases.setSelectionForeground(Color.WHITE);

        JTableHeader header = tablaClases.getTableHeader();
        header.setBackground(BG_INPUTS);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JScrollPane scrollPane = new JScrollPane(tablaClases);
        scrollPane.getViewport().setBackground(BG_CENTRAL);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panelTabla.add(scrollPane, BorderLayout.CENTER);

        JPanel panelBotonesGrilla = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotonesGrilla.setBackground(BG_CENTRAL);
        btnEliminar = crearBoton("Eliminar Seleccionada", new Color(180, 50, 50));
        btnEliminar.addActionListener(e -> eliminarClaseSeleccionada());
        panelBotonesGrilla.add(btnEliminar);
        
        panelTabla.add(panelBotonesGrilla, BorderLayout.SOUTH);
        return panelTabla;
    }

    private void inicializarEventos() {
        btnGuardar.addActionListener(e -> guardarClase());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        btnEditar.addActionListener(e -> editarClaseSeleccionada()); 
        
        // UX: Deshabilita "Capacidad Máxima" si es una clase Personal
        comboTipo.addActionListener(e -> {
            boolean esGrupal = "GRUPAL".equals(comboTipo.getSelectedItem());
            txtCapacidad.setEnabled(esGrupal);
            if (!esGrupal) txtCapacidad.setText("");
        });
    }

    private void limpiarFormulario() {
        txtNombre.setText("");
        spinnerHorario.setValue(new java.util.Date()); 
        txtDuracion.setText("");
        txtCapacidad.setText("");
        comboTipo.setSelectedIndex(0);
        if (comboInstructor.getItemCount() > 0) {
            comboInstructor.setSelectedIndex(0);
        }
        idClaseEdicion = -1; // Volvemos al Modo Crear
        btnGuardar.setText("Guardar Clase"); // Restaurar texto del botón
    }

    private void editarClaseSeleccionada() {
        int fila = tablaClases.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una clase de la grilla para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        idClaseEdicion = (int) tablaClases.getValueAt(fila, 0); // Pasamos a "Modo Editar"
        btnGuardar.setText("Actualizar Clase");

        txtNombre.setText(modeloTabla.getValueAt(fila, 1).toString());
        comboTipo.setSelectedItem(modeloTabla.getValueAt(fila, 2).toString());
        
        // 1. Reconstruimos la fecha
        String fechaStr = modeloTabla.getValueAt(fila, 3).toString().replace(" ", "T");
        java.time.LocalDateTime fechaGrilla = java.time.LocalDateTime.parse(fechaStr);
        java.util.Date fechaConvertida = java.util.Date.from(fechaGrilla.atZone(java.time.ZoneId.systemDefault()).toInstant());
        spinnerHorario.setValue(fechaConvertida);

        txtDuracion.setText(modeloTabla.getValueAt(fila, 4).toString());
        
        // 2. Capacidad
        String cupo = modeloTabla.getValueAt(fila, 5).toString();
        txtCapacidad.setText(cupo.equals("N/A") ? "" : cupo);

        // 3. ¡Magia UX! Auto-seleccionar el Instructor en el ComboBox
        String nombreInstructorTabla = modeloTabla.getValueAt(fila, 6).toString();
        for (int i = 0; i < comboInstructor.getItemCount(); i++) {
            Instructor inst = comboInstructor.getItemAt(i);
            if ((inst.getNombre() + " " + inst.getApellido()).equals(nombreInstructorTabla)) {
                comboInstructor.setSelectedIndex(i);
                break;
            }
        }
    }

    private void cargarInstructores() {
        comboInstructor.removeAllItems();
        List<Instructor> lista = instructorDAO.obtenerTodos();
        for (Instructor inst : lista) {
            comboInstructor.addItem(inst);
        }
    }

    private void cargarClases() {
        SwingWorker<List<ClaseGimnasio>, Void> worker = new SwingWorker<List<ClaseGimnasio>, Void>() {
            @Override
            protected List<ClaseGimnasio> doInBackground() throws Exception {
                return claseService.obtenerTodas(); 
            }
            @Override
            protected void done() {
                try {
                    List<ClaseGimnasio> lista = get();
                    modeloTabla.setRowCount(0); 
                    
                    for (ClaseGimnasio clase : lista) {
                        String cupo = (clase instanceof ClaseGrupal) ? String.valueOf(((ClaseGrupal) clase).getCapacidadMax()) : "N/A";
                        String profeNombre = clase.getInstructor() != null ? 
                            (clase.getInstructor().getNombre() + " " + clase.getInstructor().getApellido()) : "Sin asignar";
                        String fechaFormateada = clase.getHorario().toString().replace("T", " ");
                        
                        modeloTabla.addRow(new Object[]{
                            clase.getIdClase(), clase.getNombre(), clase.getTipoClase(),
                            fechaFormateada, clase.getDuracionMinutos(), cupo, profeNombre
                        });
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(PanelClases.this, "Error al cargar las clases: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void guardarClase() {
        try {
            if (txtDuracion.getText().trim().isEmpty() || txtNombre.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, completa los campos obligatorios.", "Campos incompletos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String nombre = txtNombre.getText().trim();
            String tipoStr = (String) comboTipo.getSelectedItem();
            int duracion = Integer.parseInt(txtDuracion.getText().trim());
            
            Instructor profe = (Instructor) comboInstructor.getSelectedItem();
            if (profe == null) {
                JOptionPane.showMessageDialog(this, "Debe registrar y seleccionar un instructor válido.", "Falta Instructor", JOptionPane.ERROR_MESSAGE);
                return;
            }

            java.util.Date fechaSeleccionada = (java.util.Date) spinnerHorario.getValue();
            java.time.LocalDateTime horario = fechaSeleccionada.toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDateTime();

            ClaseGimnasio nuevaClase;
            if ("GRUPAL".equals(tipoStr)) {
                if (txtCapacidad.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Las clases grupales requieren definir una Capacidad Máxima.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int capacidad = Integer.parseInt(txtCapacidad.getText().trim());
                nuevaClase = new ClaseGrupal(0, nombre, profe, horario, duracion, true, capacidad);
            } else {
                nuevaClase = new ClasePersonal(0, nombre, profe, horario, duracion, true);
            }

            // --- LÓGICA DE SERVICE (CREAR vs EDITAR) ---
            if (idClaseEdicion == -1) {
                claseService.guardarClase(nuevaClase); 
                JOptionPane.showMessageDialog(this, "Clase registrada exitosamente.");
            } else {
                nuevaClase.setIdClase(idClaseEdicion); 
                claseService.actualizarClase(nuevaClase); 
                JOptionPane.showMessageDialog(this, "Clase actualizada exitosamente.");
            }

            limpiarFormulario();
            cargarClases();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "La duración y la capacidad deben ser valores numéricos enteros.", "Error de formato", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarClaseSeleccionada() {
        int fila = tablaClases.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una clase para eliminar.");
            return;
        }
        int id = (int) tablaClases.getValueAt(fila, 0);
        
        int confirmacion = JOptionPane.showConfirmDialog(this, 
            "¿Seguro que desea eliminar esta clase?\nEl sistema verificará que no tenga alumnos inscriptos antes de borrarla.", 
            "Confirmar Eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                claseService.eliminarClase(id); // Si falla la FK, el Service lanzará el error amigable
                cargarClases();
                JOptionPane.showMessageDialog(this, "Clase eliminada del sistema.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Operación Bloqueada", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // --- Helpers Visuales ---
    private JLabel crearLabel(String texto) { JLabel l = new JLabel(texto); l.setForeground(TEXTO_GRIS); l.setFont(new Font("Segoe UI", Font.BOLD, 12)); return l; }
    private JTextField crearTextField() { JTextField t = new JTextField(); estilizarComponenteUI(t); t.setCaretColor(Color.WHITE); return t; }
    private void estilizarComponenteUI(JComponent c) { c.setBackground(BG_INPUTS); c.setForeground(TEXTO_BLANCO); c.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(60, 80, 95)), new EmptyBorder(5,5,5,5))); }
    private JButton crearBoton(String texto, Color c) { JButton b = new JButton(texto); b.setFont(new Font("Segoe UI", Font.BOLD, 13)); b.setBackground(c); b.setForeground(Color.WHITE); b.setFocusPainted(false); return b; }
    private void colocarComponente(JPanel p, Component c, GridBagConstraints gbc, int x, int y, int w) { gbc.gridx = x; gbc.gridy = y; gbc.gridwidth = w; gbc.weightx = (x % 2 != 0) ? 1.0 : 0.0; p.add(c, gbc); }
    
    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
        if (aFlag) {
            cargarInstructores(); 
            cargarClases();       
        }
    }
}