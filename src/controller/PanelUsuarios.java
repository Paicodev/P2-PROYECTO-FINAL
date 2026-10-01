package com.gym.manager.view.panels;

import com.gym.manager.model.UsuarioSistema;
import com.gym.manager.model.enums.RolUsuario;
import com.gym.manager.service.UsuarioService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PanelUsuarios extends JPanel {

    private static final Color BG_CENTRAL      = new Color(28, 43, 51);
    private static final Color BG_FORMULARIO   = new Color(22, 38, 45);
    private static final Color ACENTO_TURQUESA = new Color(0, 150, 136);
    private static final Color TEXTO_BLANCO    = new Color(230, 230, 230);
    private static final Color TEXTO_GRIS      = new Color(160, 175, 180);
    private static final Color BG_INPUTS       = new Color(35, 58, 70);

    private UsuarioService usuarioService;

    private JTable tablaUsuarios;
    private DefaultTableModel modeloTabla;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField txtBuscar;

    // Campos de Persona y Usuario
    private JTextField txtNombre, txtApellido, txtDni;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<RolUsuario> comboRol;

    private JButton btnGuardar, btnLimpiar, btnEditar;
    private int idUsuarioEdicion = -1; 
    private int idPersonaEdicion = -1;

    public PanelUsuarios() {
        this.usuarioService = new UsuarioService();

        setBackground(BG_CENTRAL);
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(crearPanelFormulario(), BorderLayout.NORTH);
        add(crearPanelGrilla(), BorderLayout.CENTER);

        inicializarEventos();
        cargarDatosEnTabla();
    }

    private JPanel crearPanelFormulario() {
        JPanel panelContenedor = new JPanel(new BorderLayout(10, 10));
        panelContenedor.setBackground(BG_FORMULARIO);

        TitledBorder borde = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(50, 70, 80)), " Gestión de Accesos (Staff) ");
        borde.setTitleColor(ACENTO_TURQUESA); borde.setTitleFont(new Font("Segoe UI", Font.BOLD, 14));
        panelContenedor.setBorder(BorderFactory.createCompoundBorder(borde, new EmptyBorder(10, 10, 10, 10)));

        // Grilla 3x4 para que entren perfectos los 6 campos
        JPanel panelCampos = new JPanel(new GridLayout(3, 4, 15, 15));
        panelCampos.setBackground(BG_FORMULARIO);

        txtNombre = crearTextField(); txtApellido = crearTextField(); txtDni = crearTextField();
        txtUsername = crearTextField(); txtPassword = new JPasswordField(); estilizarComponenteUI(txtPassword);
        comboRol = new JComboBox<>(RolUsuario.values()); estilizarComponenteUI(comboRol);

        // Fila 1
        panelCampos.add(crearLabel("Nombre:")); panelCampos.add(txtNombre);
        panelCampos.add(crearLabel("Apellido:")); panelCampos.add(txtApellido);
        
        // Fila 2
        panelCampos.add(crearLabel("DNI:")); panelCampos.add(txtDni);
        panelCampos.add(crearLabel("Rol de Permisos:")); panelCampos.add(comboRol);
        
        // Fila 3
        panelCampos.add(crearLabel("Nombre de Usuario:")); panelCampos.add(txtUsername);
        panelCampos.add(crearLabel("Contraseña:")); panelCampos.add(txtPassword);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotones.setBackground(BG_FORMULARIO);

        btnLimpiar = crearBoton("Limpiar", new Color(80, 90, 100));
        btnEditar = crearBoton("Editar Seleccionado", new Color(41, 128, 185));
        btnGuardar = crearBoton("Guardar Empleado", ACENTO_TURQUESA);

        panelBotones.add(btnLimpiar); panelBotones.add(btnEditar); panelBotones.add(btnGuardar);

        panelContenedor.add(panelCampos, BorderLayout.CENTER);
        panelContenedor.add(panelBotones, BorderLayout.SOUTH);

        return panelContenedor;
    }

    private JPanel crearPanelGrilla() {
        JPanel panelTabla = new JPanel(new BorderLayout(0, 10));
        panelTabla.setBackground(BG_CENTRAL);

        JPanel panelBuscar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBuscar.setBackground(BG_CENTRAL);
        txtBuscar = crearTextField(); txtBuscar.setPreferredSize(new Dimension(200, 30));
        txtBuscar.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) {
                String text = txtBuscar.getText().trim();
                if (text.length() == 0) sorter.setRowFilter(null);
                else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            }
        });
        panelBuscar.add(crearLabel("🔎 Buscar por Nombre/Usuario:")); panelBuscar.add(txtBuscar);
        panelTabla.add(panelBuscar, BorderLayout.NORTH);

        String[] columnas = {"ID", "Staff", "DNI", "Username", "Rol", "Último Acceso", "idPer_oculto"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        tablaUsuarios = new JTable(modeloTabla);
        tablaUsuarios.setBackground(BG_FORMULARIO); tablaUsuarios.setForeground(TEXTO_BLANCO);
        tablaUsuarios.setRowHeight(25); tablaUsuarios.setSelectionBackground(ACENTO_TURQUESA); tablaUsuarios.setSelectionForeground(Color.WHITE);
        
        // Ocultar la columna del idPersona para que no moleste visualmente pero podamos usarla al editar
        tablaUsuarios.getColumnModel().getColumn(6).setMinWidth(0);
        tablaUsuarios.getColumnModel().getColumn(6).setMaxWidth(0);

        sorter = new TableRowSorter<>(modeloTabla); tablaUsuarios.setRowSorter(sorter);

        JTableHeader header = tablaUsuarios.getTableHeader();
        header.setBackground(BG_INPUTS); header.setForeground(Color.WHITE); header.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JScrollPane scrollPane = new JScrollPane(tablaUsuarios);
        scrollPane.getViewport().setBackground(BG_CENTRAL);
        panelTabla.add(scrollPane, BorderLayout.CENTER);

        JPanel panelBotonesTabla = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotonesTabla.setBackground(BG_CENTRAL);
        JButton btnEliminar = crearBoton("Despedir Empleado (Revocar Acceso)", new Color(180, 50, 50));
        btnEliminar.addActionListener(e -> eliminarUsuarioSeleccionado());
        panelBotonesTabla.add(btnEliminar);
        panelTabla.add(panelBotonesTabla, BorderLayout.SOUTH);

        return panelTabla;
    }

    private void inicializarEventos() {
        btnGuardar.addActionListener(e -> guardarUsuario());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        btnEditar.addActionListener(e -> cargarUsuarioEnFormulario());
    }

    private void cargarDatosEnTabla() {
        SwingWorker<List<UsuarioSistema>, Void> worker = new SwingWorker<List<UsuarioSistema>, Void>() {
            @Override protected List<UsuarioSistema> doInBackground() { return usuarioService.obtenerTodos(); }
            @Override protected void done() {
                try {
                    List<UsuarioSistema> usuarios = get();
                    modeloTabla.setRowCount(0);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

                    for (UsuarioSistema u : usuarios) {
                        String acceso = u.getUltimoAcceso() != null ? u.getUltimoAcceso().format(formatter) : "Nunca";
                        modeloTabla.addRow(new Object[]{
                            u.getIdUsuarioSistema(), u.getNombre() + " " + u.getApellido(), u.getDni(), u.getUsername(), u.getRol().name(), acceso, u.getPersonaIdPersona()
                        });
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(PanelUsuarios.this, "Error al cargar usuarios: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void guardarUsuario() {
        try {
            UsuarioSistema usuario = new UsuarioSistema(
                idUsuarioEdicion != -1 ? idUsuarioEdicion : 0, 
                txtUsername.getText().trim(), 
                new String(txtPassword.getPassword()).trim(), 
                (RolUsuario) comboRol.getSelectedItem(), 
                null, 
                idPersonaEdicion != -1 ? idPersonaEdicion : 0
            );
            usuario.setNombre(txtNombre.getText().trim());
            usuario.setApellido(txtApellido.getText().trim());
            usuario.setDni(txtDni.getText().trim());

            if (idUsuarioEdicion == -1) {
                usuarioService.guardarUsuario(usuario);
                JOptionPane.showMessageDialog(this, "Empleado y Usuario creados exitosamente.");
            } else {
                usuarioService.actualizarUsuario(usuario);
                JOptionPane.showMessageDialog(this, "Datos actualizados exitosamente.");
            }

            limpiarFormulario();
            cargarDatosEnTabla();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarUsuarioEnFormulario() {
        int fila = tablaUsuarios.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un empleado de la grilla para editar."); return;
        }

        int filaReal = tablaUsuarios.convertRowIndexToModel(fila);
        idUsuarioEdicion = (int) modeloTabla.getValueAt(filaReal, 0);
        idPersonaEdicion = (int) modeloTabla.getValueAt(filaReal, 6); // Recuperamos el ID oculto de la persona
        
        btnGuardar.setText("Actualizar Empleado");

        String nombreCompleto = modeloTabla.getValueAt(filaReal, 1).toString();
        // Separamos el string "Nombre Apellido" a la fuerza
        txtNombre.setText(nombreCompleto.substring(0, nombreCompleto.indexOf(" ")));
        txtApellido.setText(nombreCompleto.substring(nombreCompleto.indexOf(" ") + 1));
        
        txtDni.setText(modeloTabla.getValueAt(filaReal, 2).toString());
        txtUsername.setText(modeloTabla.getValueAt(filaReal, 3).toString());
        comboRol.setSelectedItem(RolUsuario.valueOf(modeloTabla.getValueAt(filaReal, 4).toString()));
        txtPassword.setText(""); 
    }

    private void eliminarUsuarioSeleccionado() {
        int fila = tablaUsuarios.getSelectedRow();
        if (fila == -1) return;

        int filaReal = tablaUsuarios.convertRowIndexToModel(fila);
        int id = (int) modeloTabla.getValueAt(filaReal, 0);

        if (JOptionPane.showConfirmDialog(this, "¿Seguro que desea despedir a este empleado? Esto revocará su acceso y lo eliminará de la base de datos.", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                usuarioService.eliminarUsuario(id);
                cargarDatosEnTabla();
                JOptionPane.showMessageDialog(this, "Empleado eliminado.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void limpiarFormulario() {
        txtNombre.setText(""); txtApellido.setText(""); txtDni.setText("");
        txtUsername.setText(""); txtPassword.setText(""); comboRol.setSelectedIndex(0);
        idUsuarioEdicion = -1; idPersonaEdicion = -1;
        btnGuardar.setText("Guardar Empleado");
    }

    // --- Helpers UI ---
    private JLabel crearLabel(String texto) { JLabel l = new JLabel(texto); l.setForeground(TEXTO_GRIS); l.setFont(new Font("Segoe UI", Font.BOLD, 12)); return l; }
    private JTextField crearTextField() { JTextField t = new JTextField(); estilizarComponenteUI(t); t.setCaretColor(Color.WHITE); return t; }
    private void estilizarComponenteUI(JComponent c) { c.setBackground(BG_INPUTS); c.setForeground(TEXTO_BLANCO); c.setFont(new Font("Segoe UI", Font.PLAIN, 13)); c.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(60, 80, 95), 1), BorderFactory.createEmptyBorder(5, 5, 5, 5))); }
    private JButton crearBoton(String texto, Color c) { JButton b = new JButton(texto); b.setBackground(c); b.setForeground(Color.WHITE); b.setFont(new Font("Segoe UI", Font.BOLD, 13)); b.setFocusPainted(false); b.setCursor(new Cursor(Cursor.HAND_CURSOR)); return b; }
}