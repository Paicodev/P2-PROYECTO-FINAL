package com.gym.manager.view.panels;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import com.gym.manager.service.ClaseService;

import java.awt.*;

/**
 * Panel para la gestión de Clases e Instructores.
 */
public class PanelClases extends JPanel {

    // ── Paleta de Colores de FitBase ──
    private static final Color BG_CENTRAL     = new Color(28, 43, 51);
    private static final Color BG_FORMULARIO  = new Color(22, 38, 45);
    private static final Color ACENTO_TURQUESA = new Color(0, 150, 136);
    private static final Color TEXTO_BLANCO   = new Color(230, 230, 230);
    private static final Color TEXTO_GRIS     = new Color(160, 175, 180);
    private static final Color BG_INPUTS      = new Color(35, 58, 70);

    // Componentes de la interfaz
    private JTable tablaClases;
    private DefaultTableModel modeloTabla;
    
    // Campos del Formulario
    private JTextField txtNombre;
    private JComboBox<String> comboTipo;
    private JTextField txtHorario;
    private JTextField txtDuracion;
    private JTextField txtCapacidad;
    private JComboBox<String> comboInstructor; // Se llenará dinámicamente desde la BD
    
    // Botones
    private JButton btnGuardar;
    private JButton btnLimpiar;
    private JButton btnEditar;
    private ClaseService claseService;

    public PanelClases() {
        // Configuración del contenedor principal
        setBackground(BG_CENTRAL);
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        // 1. Panel de Formulario Superior (Alta / Edición)
        add(crearPanelFormulario(), BorderLayout.NORTH);
        // 2. Panel de la Grilla Central (JTable)
        add(crearPanelGrilla(), BorderLayout.CENTER);

        inicializarEventos();
        this.claseService = new ClaseService();
        
    }

    

    private JPanel crearPanelFormulario() {
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBackground(BG_FORMULARIO);
        
        // Bordes personalizados con tipografía y colores del tema
        TitledBorder bordeTitulo = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(50, 70, 80), 1), 
                " Gestión de Clases e Instructores "
        );
        bordeTitulo.setTitleColor(ACENTO_TURQUESA);
        bordeTitulo.setTitleFont(new Font("Segoe UI", Font.BOLD, 14));
        panelForm.setBorder(BorderFactory.createCompoundBorder(bordeTitulo, new EmptyBorder(15, 15, 15, 15)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- Componentes del formulario ---
        txtNombre = crearTextField();
        comboTipo = new JComboBox<>(new String[]{"GRUPAL", "PERSONAL"});
        estilizarComponenteUI(comboTipo);
        
        txtHorario = crearTextField();
        txtHorario.setToolTipText("Formato: YYYY-MM-DD HH:MM:SS");
        
        txtDuracion = crearTextField();
        txtCapacidad = crearTextField();
        
        comboInstructor = new JComboBox<>(new String[]{"Seleccionar Instructor...", "Profe Gimnasio (ID: 1)"});
        estilizarComponenteUI(comboInstructor);

        // Fila 0
        colocarComponente(panelForm, crearLabel("Nombre de la Clase:"), gbc, 0, 0, 1);
        colocarComponente(panelForm, txtNombre, gbc, 1, 0, 1);
        colocarComponente(panelForm, crearLabel("Tipo de Clase:"), gbc, 2, 0, 1);
        colocarComponente(panelForm, comboTipo, gbc, 3, 0, 1);

        // Fila 1
        colocarComponente(panelForm, crearLabel("Horario (Fecha/Hora):"), gbc, 0, 1, 1);
        colocarComponente(panelForm, txtHorario, gbc, 1, 1, 1);
        colocarComponente(panelForm, crearLabel("Duración (Minutos):"), gbc, 2, 1, 1);
        colocarComponente(panelForm, txtDuracion, gbc, 3, 1, 1);

        // Fila 2
        colocarComponente(panelForm, crearLabel("Capacidad Máxima:"), gbc, 0, 2, 1);
        colocarComponente(panelForm, txtCapacidad, gbc, 1, 2, 1);
        colocarComponente(panelForm, crearLabel("Instructor Asignado:"), gbc, 2, 2, 1);
        colocarComponente(panelForm, comboInstructor, gbc, 3, 2, 1);

        // Botones de Acción (Fila 3)
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBotones.setOpaque(false);

        btnLimpiar = crearBoton("Limpiar", new Color(80, 90, 100));
        btnEditar = crearBoton("Editar Seleccionada", new Color(41, 128, 185));
        btnGuardar = crearBoton("Guardar Clase", ACENTO_TURQUESA);

        panelBotones.add(btnLimpiar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnGuardar);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 4;
        gbc.weightx = 1.0;
        panelForm.add(panelBotones, gbc);

        return panelForm;
    }

    private JScrollPane crearPanelGrilla() {
        String[] columnas = {"ID", "Clase", "Tipo", "Horario", "Duración (min)", "Cupo Máx", "Instructor"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // La grilla es de solo lectura, se edita mediante el formulario
            }
        };

        tablaClases = new JTable(modeloTabla);
        tablaClases.setBackground(BG_FORMULARIO);
        tablaClases.setForeground(TEXTO_BLANCO);
        tablaClases.setGridColor(new Color(50, 70, 80));
        tablaClases.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaClases.setRowHeight(25);
        tablaClases.setSelectionBackground(ACENTO_TURQUESA);
        tablaClases.setSelectionForeground(Color.WHITE);

        // Estilo del Header de la tabla
        JTableHeader header = tablaClases.getTableHeader();
        header.setBackground(BG_INPUTS);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JScrollPane scrollPane = new JScrollPane(tablaClases);
        scrollPane.getViewport().setBackground(BG_CENTRAL);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        return scrollPane;
    }

    // ── Métodos Auxiliares de Estilización  ──
    private JLabel crearLabel(String texto) {
        JLabel label = new JLabel(texto);
        label.setForeground(TEXTO_GRIS);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return label;
    }

    private JTextField crearTextField() {
        JTextField tf = new JTextField();
        estilizarComponenteUI(tf);
        tf.setCaretColor(Color.WHITE);
        return tf;
    }

    private void estilizarComponenteUI(JComponent c) {
        c.setBackground(BG_INPUTS);
        c.setForeground(TEXTO_BLANCO);
        c.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        c.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 80, 95), 1),
                BorderFactory.createEmptyBorder(4, 6, 4, 6)
        ));
    }

    private JButton crearBoton(String texto, Color colorFondo) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(colorFondo);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void colocarComponente(JPanel p, Component c, GridBagConstraints gbc, int x, int y, int width) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        gbc.weightx = (x % 2 != 0) ? 1.0 : 0.0;
        p.add(c, gbc);
    }

    private void inicializarEventos() {
        btnGuardar.addActionListener(e -> guardarNuevaClase());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        
        // TODO: Cargar el JComboBox de instructores desde la BD usando un InstructorDAO
        // cargarInstructoresEnCombo();
    }

    private void guardarNuevaClase() {
        try {
            // 1. Capturamos los datos de los JTextFields
            String nombre = txtNombre.getText();
            String tipoStr = (String) comboTipo.getSelectedItem();
            int duracion = Integer.parseInt(txtDuracion.getText());
            int capacidad = 0;
            
            if ("GRUPAL".equals(tipoStr)) {
                capacidad = Integer.parseInt(txtCapacidad.getText());
            }

            // TODO: Obtener el instructor real seleccionado del JComboBox
            // Por ahora, como no hay InstructorDAO, pasamos null para que falle la validación 
            // o crear un Instructor mock temporal.
            com.gym.manager.model.Instructor instructorTemp = null; 

            // 2. Instanciamos el modelo usando Polimorfismo
            com.gym.manager.model.ClaseGimnasio nuevaClase;
            
            // Reemplazar null por la fecha parseada de txtHorario (LocalDateTime) cuando esté listo
            java.time.LocalDateTime horario = java.time.LocalDateTime.now(); 

            if ("GRUPAL".equals(tipoStr)) {
                nuevaClase = new com.gym.manager.model.ClaseGrupal(0, nombre, instructorTemp, horario, duracion, true, capacidad);
            } else {
                nuevaClase = new com.gym.manager.model.ClasePersonal(0, nombre, instructorTemp, horario, duracion, true);
            }

            // 3. Enviamos al Servicio (MVC)
            claseService.guardarClase(nuevaClase);

            // 4. Feedback al usuario
            JOptionPane.showMessageDialog(this, "Clase guardada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            // cargarGrilla(); // Refrescar la tabla

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "La duración y la capacidad deben ser números enteros.", "Error de formato", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

        private void limpiarFormulario() {
        txtNombre.setText("");
        txtHorario.setText("");
        txtDuracion.setText("");
        txtCapacidad.setText("");
        comboTipo.setSelectedIndex(0);
        comboInstructor.setSelectedIndex(0);
    }
    // ── Getters Públicos para el Patrón MVC ──
    public JTable getTablaClases() { return tablaClases; }
    public DefaultTableModel getModeloTabla() { return modeloTabla; }
    public JTextField getTxtNombre() { return txtNombre; }
    public JComboBox<String> getComboTipo() { return comboTipo; }
    public JTextField getTxtHorario() { return txtHorario; }
    public JTextField getTxtDuracion() { return txtDuracion; }
    public JTextField getTxtCapacidad() { return txtCapacidad; }
    public JComboBox<String> getComboInstructor() { return comboInstructor; }
    public JButton getBtnGuardar() { return btnGuardar; }
    public JButton getBtnLimpiar() { return btnLimpiar; }
    public JButton getBtnEditar() { return btnEditar; }
}