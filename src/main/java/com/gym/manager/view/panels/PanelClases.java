package com.gym.manager.view.panels;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import com.gym.manager.dao.ClaseDAO;
import com.gym.manager.dao.InstructorDAO;
import com.gym.manager.exceptions.ConexionBDException;
import com.gym.manager.model.Instructor;
import com.gym.manager.model.ClaseGimnasio;
import com.gym.manager.model.ClaseGrupal;
import com.gym.manager.model.ClasePersonal;
import com.gym.manager.service.ClaseService;

import java.util.List;

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
    private JTextField txtNombre, txtHorario, txtDuracion, txtCapacidad;;
    private JComboBox<String> comboTipo;
    private JComboBox<Instructor> comboInstructor; // Cambiado a JComboBox de Instructor para cargar desde BD
    
    private InstructorDAO instructorDAO;
    // Botones
    private JButton btnGuardar;
    private JButton btnLimpiar;
    private JButton btnEditar;
    private ClaseService claseService;

    public PanelClases() {
        this.claseService = new ClaseService();
        this.instructorDAO = new InstructorDAO();
        // Configuración del contenedor principal
        setBackground(BG_CENTRAL);
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        // 1. Panel de Formulario Superior (Alta / Edición)
        add(crearPanelFormulario(), BorderLayout.NORTH);
        // 2. Panel de la Grilla Central (JTable)
        add(crearPanelGrilla(), BorderLayout.CENTER);

        inicializarEventos();
        
        cargarClases();
    }

    

    private JPanel crearPanelFormulario() {
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBackground(BG_FORMULARIO);
        
        // Bordes personalizados con tipografía y colores del tema
        TitledBorder bordeTitulo = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(50, 70, 80), 1), 
                " Gestión de Clases"
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
        
        comboInstructor = new JComboBox<>();
        cargarInstructores(); // Cargar instructores desde la BD
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

    private JPanel crearPanelGrilla() {
        JPanel panelTabla = new JPanel(new BorderLayout());
        panelTabla.setBackground(BG_CENTRAL);
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
        //tablaClases.setGridColor(new Color(50, 70, 80));
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
        panelTabla.add(scrollPane, BorderLayout.CENTER);

        return panelTabla;
        
    }

    

    private void inicializarEventos() {
        btnGuardar.addActionListener(e -> guardarClase());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

    }

        private void limpiarFormulario() {
        txtNombre.setText("");
        txtHorario.setText("");
        txtDuracion.setText("");
        txtCapacidad.setText("");
        comboTipo.setSelectedIndex(0);
        comboInstructor.setSelectedIndex(0);
    }

    private void cargarInstructores() {
        comboInstructor.removeAllItems();
        List<Instructor> lista = instructorDAO.obtenerTodos();
        for (Instructor inst : lista) {
            comboInstructor.addItem(inst);
        }
    }

    private void cargarClases() {
    modeloTabla.setRowCount(0); // Vacía la tabla antes de recargar
    
    // NOTA: Si en tu ClaseService el método se llama 'listarClases()' o similar, cambialo acá:
    List<ClaseGimnasio> lista = claseService.obtenerTodas(); 
    
    for (ClaseGimnasio clase : lista) {
        // Manejo de polimorfismo para el cupo
        String cupo = (clase instanceof ClaseGrupal) ? String.valueOf(((ClaseGrupal) clase).getCapacidadMax()) : "N/A";
        
        // Formatear nombre del instructor
        String profeNombre = "Sin asignar";
        if (clase.getInstructor() != null) {
            profeNombre = clase.getInstructor().getNombre() + " " + clase.getInstructor().getApellido();
        }
        
        // Agrega la fila a la grilla
        modeloTabla.addRow(new Object[]{
            clase.getIdClase(),
            clase.getNombre(),
            clase.getTipoClase(),
            clase.getHorario(),
            clase.getDuracionMinutos(),
            cupo,
            profeNombre
        });
    }
}
    //METODO PARA EL BOTON GUARDAR
    private void guardarClase() {
        try {
            // Validaciones previas de campos vacíos en la UI
            if (txtNombre.getText().trim().isEmpty() || txtHorario.getText().trim().isEmpty() || txtDuracion.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, completa los campos obligatorios (Nombre, Horario y Duración).", "Campos incompletos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String nombre = txtNombre.getText().trim();
            String tipoStr = (String) comboTipo.getSelectedItem();
            int duracion = Integer.parseInt(txtDuracion.getText().trim());
            
            // Recuperamos el objeto Instructor REAL seleccionado en el JComboBox
            Instructor profe = (Instructor) comboInstructor.getSelectedItem();
            if (profe == null) {
                JOptionPane.showMessageDialog(this, "Debe registrar y seleccionar un instructor válido.", "Instructor faltante", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Parseamos la fecha del txtHorario con el formato esperado
            java.time.LocalDateTime horario;
            try {
                java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                horario = java.time.LocalDateTime.parse(txtHorario.getText().trim(), formatter);
            } catch (java.time.format.DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "El formato de fecha/hora es inválido.\nUse el formato: AAAA-MM-DD HH:MM:SS\nEjemplo: 2026-06-15 18:30:00", "Error de Fecha", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Evaluamos polimorfismo según el tipo seleccionado
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

            // Enviamos al servicio para impactar en la BD
            claseService.guardarClase(nuevaClase);

            JOptionPane.showMessageDialog(this, "Clase guardada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            cargarClases();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "La duración y la capacidad deben ser valores numéricos enteros.", "Error de formato", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            throw new ConexionBDException("Error al guardar la nueva clase en la base de datos: " + e.getMessage(), e);
        }
    }

    // ── Getters Públicos para el Patrón MVC ──
    private JLabel crearLabel(String texto) { JLabel l = new JLabel(texto); l.setForeground(TEXTO_GRIS); l.setFont(new Font("Segoe UI", Font.BOLD, 12)); return l; }
    private JTextField crearTextField() { JTextField t = new JTextField(); estilizarComponenteUI(t); t.setCaretColor(Color.WHITE); return t; }
    private void estilizarComponenteUI(JComponent c) { c.setBackground(BG_INPUTS); c.setForeground(TEXTO_BLANCO); c.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(60, 80, 95)), new EmptyBorder(5,5,5,5))); }
    private JButton crearBoton(String texto, Color c) { JButton b = new JButton(texto); b.setFont(new Font("Segoe UI", Font.BOLD, 13)); b.setBackground(c); b.setForeground(Color.WHITE); b.setFocusPainted(false); return b; }
    private void colocarComponente(JPanel p, Component c, GridBagConstraints gbc, int x, int y, int w) { gbc.gridx = x; gbc.gridy = y; gbc.gridwidth = w; gbc.weightx = (x % 2 != 0) ? 1.0 : 0.0; p.add(c, gbc); }
}