package com.gym.manager.view.dialogs;

import com.gym.manager.model.Miembro;
import com.gym.manager.model.Plan;
import com.gym.manager.model.enums.EstadoMiembro;
import com.gym.manager.service.PlanService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class DialogMiembro extends JDialog {

    // Paleta de colores
    private static final Color BG_FORMULARIO   = new Color(22, 38, 45);
    private static final Color ACENTO_TURQUESA = new Color(0, 150, 136);
    private static final Color TEXTO_BLANCO    = new Color(230, 230, 230);
    private static final Color TEXTO_GRIS      = new Color(160, 175, 180);
    private static final Color BG_INPUTS       = new Color(35, 58, 70);

    private JTextField txtNombre, txtApellido, txtDni, txtEmail, txtTelefono;
    private JComboBox<Plan> comboPlan;
    private JComboBox<EstadoMiembro> comboEstado;
    
    private Miembro miembroResultante = null;
    private boolean confirmado = false;
    private int idMiembroEdicion = 0; // 0 significa nuevo
    private LocalDate fechaInscripcionOriginal = null;

    public DialogMiembro(Window parent, Miembro miembroEditar) {
        super(parent, miembroEditar == null ? "Nuevo Miembro" : "Editar Miembro", ModalityType.APPLICATION_MODAL);
        
        setSize(450, 500);
        setLocationRelativeTo(parent);
        setResizable(false);
        
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBackground(BG_FORMULARIO);
        panelPrincipal.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel panelCampos = new JPanel(new GridLayout(7, 2, 10, 15));
        panelCampos.setBackground(BG_FORMULARIO);

        // Inicializar componentes
        txtNombre = crearTextField();
        txtApellido = crearTextField();
        txtDni = crearTextField();
        txtEmail = crearTextField();
        txtTelefono = crearTextField();
        
        // Cargar combos
        comboPlan = new JComboBox<>();
        comboPlan.setBackground(BG_INPUTS);
        comboPlan.setForeground(TEXTO_BLANCO);
        cargarPlanes();

        comboEstado = new JComboBox<>(EstadoMiembro.values());
        comboEstado.setBackground(BG_INPUTS);
        comboEstado.setForeground(TEXTO_BLANCO);

        // Agregar al grid
        panelCampos.add(crearLabel("Nombre:")); panelCampos.add(txtNombre);
        panelCampos.add(crearLabel("Apellido:")); panelCampos.add(txtApellido);
        panelCampos.add(crearLabel("DNI:")); panelCampos.add(txtDni);
        panelCampos.add(crearLabel("Email:")); panelCampos.add(txtEmail);
        panelCampos.add(crearLabel("Teléfono:")); panelCampos.add(txtTelefono);
        panelCampos.add(crearLabel("Plan:")); panelCampos.add(comboPlan);
        panelCampos.add(crearLabel("Estado:")); panelCampos.add(comboEstado);

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotones.setBackground(BG_FORMULARIO);
        
        JButton btnCancelar = crearBoton("Cancelar", new Color(180, 50, 50));
        JButton btnGuardar = crearBoton("Guardar", ACENTO_TURQUESA);

        btnCancelar.addActionListener(e -> dispose());
        btnGuardar.addActionListener(e -> guardarDatos());

        panelBotones.add(btnCancelar);
        panelBotones.add(btnGuardar);

        panelPrincipal.add(panelCampos, BorderLayout.CENTER);
        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);

        add(panelPrincipal);

        // Si es modo edición, precargamos los datos
        if (miembroEditar != null) {
            cargarDatosMiembro(miembroEditar);
        }
    }

    private void cargarPlanes() {
        PlanService planService = new PlanService();
        List<Plan> planes = planService.obtenerTodos();
        for (Plan p : planes) {
            comboPlan.addItem(p); // Requiere que Plan tenga un toString() sobreescrito para verse bien
        }
    }

    private void cargarDatosMiembro(Miembro m) {
        this.idMiembroEdicion = m.getId();
        this.fechaInscripcionOriginal = m.getFechaInscripcion();
        
        txtNombre.setText(m.getNombre());
        txtApellido.setText(m.getApellido());
        txtDni.setText(m.getDni());
        txtEmail.setText(m.getEmail());
        txtTelefono.setText(m.getTelefono());
        comboEstado.setSelectedItem(m.getEstado());
        
        // Seleccionar el plan correcto en el combo
        if (m.getPlan() != null) {
            for (int i = 0; i < comboPlan.getItemCount(); i++) {
                if (comboPlan.getItemAt(i).getId() == m.getPlan().getId()) {
                    comboPlan.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void guardarDatos() {
        Plan planSeleccionado = (Plan) comboPlan.getSelectedItem();
        EstadoMiembro estadoSeleccionado = (EstadoMiembro) comboEstado.getSelectedItem();
        
        LocalDate fechaInscrip = (fechaInscripcionOriginal != null) ? fechaInscripcionOriginal : LocalDate.now();
        // Calculamos el vencimiento automáticamente sumando los meses del plan
        LocalDate fechaVenc = (planSeleccionado != null) ? fechaInscrip.plusMonths(planSeleccionado.getDuracionMeses()) : fechaInscrip;

        miembroResultante = new Miembro(
            fechaInscrip, 
            fechaVenc, 
            planSeleccionado, 
            estadoSeleccionado, 
            idMiembroEdicion, 
            txtNombre.getText(), 
            txtApellido.getText(), 
            txtDni.getText(), 
            txtEmail.getText(), 
            txtTelefono.getText()
        );
        
        confirmado = true;
        dispose();
    }

    public Miembro getMiembroResultante() { return miembroResultante; }
    public boolean isConfirmado() { return confirmado; }

    // Helpers UI
    private JLabel crearLabel(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setForeground(TEXTO_GRIS);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return lbl;
    }

    private JTextField crearTextField() {
        JTextField txt = new JTextField();
        txt.setBackground(BG_INPUTS);
        txt.setForeground(TEXTO_BLANCO);
        txt.setCaretColor(Color.WHITE);
        txt.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 80, 95)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return txt;
    }

    private JButton crearBoton(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}