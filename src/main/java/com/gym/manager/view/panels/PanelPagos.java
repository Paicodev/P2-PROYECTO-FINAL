package com.gym.manager.view.panels;

import com.gym.manager.dao.PagoDAO;
import com.gym.manager.model.Miembro;
import com.gym.manager.model.Pago;
import com.gym.manager.model.enums.EstadoPago;
import com.gym.manager.model.enums.TipoPago;
import com.gym.manager.service.MiembroService;
import com.gym.manager.service.PagoService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class PanelPagos extends JPanel {

    // Paleta de Colores de FitBase
    private static final Color BG_CENTRAL      = new Color(28, 43, 51);
    private static final Color BG_FORMULARIO   = new Color(22, 38, 45);
    private static final Color ACENTO_TURQUESA = new Color(0, 150, 136);
    private static final Color TEXTO_BLANCO    = new Color(230, 230, 230);
    private static final Color TEXTO_GRIS      = new Color(160, 175, 180);
    private static final Color BG_INPUTS       = new Color(35, 58, 70);

    private PagoService pagoService;
    private PagoDAO pagoDAO;
    private MiembroService miembroService;

    private JTable tablaPagos;
    private DefaultTableModel modeloTabla;

    private JTextField txtDniMiembro;
    private JTextField txtMonto;
    private JComboBox<TipoPago> comboTipo;
    private JComboBox<EstadoPago> comboEstado;
    private JTextField txtDescripcion;

    public PanelPagos() {
        this.pagoService = new PagoService();
        this.pagoDAO = new PagoDAO(); 
        this.miembroService = new MiembroService();

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

        TitledBorder borde = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(50, 70, 80)), " Gestión de Pagos ");
        borde.setTitleColor(ACENTO_TURQUESA);
        borde.setTitleFont(new Font("Segoe UI", Font.BOLD, 14));
        panelContenedor.setBorder(BorderFactory.createCompoundBorder(borde, new EmptyBorder(10, 10, 10, 10)));

        JPanel panelCampos = new JPanel(new GridLayout(3, 4, 15, 15));
        panelCampos.setBackground(BG_FORMULARIO);

        txtDniMiembro = crearTextField();
        txtMonto = crearTextField();
        comboTipo = new JComboBox<>(TipoPago.values());
        estilizarComponenteUI(comboTipo);
        comboEstado = new JComboBox<>(EstadoPago.values());
        estilizarComponenteUI(comboEstado);
        txtDescripcion = crearTextField();

        // Fila 1
        panelCampos.add(crearLabel("DNI Miembro:"));
        panelCampos.add(txtDniMiembro);
        panelCampos.add(crearLabel("Monto ($):"));
        panelCampos.add(txtMonto);

        // Fila 2
        panelCampos.add(crearLabel("Tipo de Pago:"));
        panelCampos.add(comboTipo);
        panelCampos.add(crearLabel("Estado:"));
        panelCampos.add(comboEstado);

        // Fila 3
        panelCampos.add(crearLabel("Descripción:"));
        panelCampos.add(txtDescripcion);
        panelCampos.add(new JLabel()); // Relleno
        panelCampos.add(new JLabel()); // Relleno

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotones.setBackground(BG_FORMULARIO);

        JButton btnLimpiar = crearBoton("Limpiar", new Color(80, 90, 100));
        JButton btnRegistrar = crearBoton("Registrar Pago", ACENTO_TURQUESA);

        btnLimpiar.addActionListener(e -> limpiarFormulario());
        btnRegistrar.addActionListener(e -> registrarPago());

        panelBotones.add(btnLimpiar);
        panelBotones.add(btnRegistrar);

        panelContenedor.add(panelCampos, BorderLayout.CENTER);
        panelContenedor.add(panelBotones, BorderLayout.SOUTH);

        return panelContenedor;
    }

    private JPanel crearPanelGrilla() {
        JPanel panelTabla = new JPanel(new BorderLayout());
        panelTabla.setBackground(BG_CENTRAL);

        String[] columnas = {"ID Pago", "DNI Miembro", "Monto", "Fecha", "Tipo", "Estado", "Descripción"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tablaPagos = new JTable(modeloTabla);
        tablaPagos.setBackground(BG_FORMULARIO);
        tablaPagos.setForeground(TEXTO_BLANCO);
        tablaPagos.setRowHeight(25);
        tablaPagos.setSelectionBackground(ACENTO_TURQUESA);
        tablaPagos.setSelectionForeground(Color.WHITE);

        JTableHeader header = tablaPagos.getTableHeader();
        header.setBackground(BG_INPUTS);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JScrollPane scrollPane = new JScrollPane(tablaPagos);
        scrollPane.getViewport().setBackground(BG_CENTRAL);
        panelTabla.add(scrollPane, BorderLayout.CENTER);

        JPanel panelBotonesTabla = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotonesTabla.setBackground(BG_CENTRAL);

        JButton btnEliminar = crearBoton("Eliminar Pago", new Color(180, 50, 50));
        JButton btnVerRecibo = crearBoton("Generar Recibo", new Color(41, 128, 185));

        btnEliminar.addActionListener(e -> eliminarPagoSeleccionado());
        btnVerRecibo.addActionListener(e -> generarReciboSeleccionado());

        panelBotonesTabla.add(btnEliminar);
        panelBotonesTabla.add(btnVerRecibo);

        panelTabla.add(panelBotonesTabla, BorderLayout.SOUTH);

        return panelTabla;
    }

    private void cargarDatosEnTabla() {
        SwingWorker<List<Pago>, Void> worker = new SwingWorker<List<Pago>, Void>() {
            @Override
            protected List<Pago> doInBackground() throws Exception {
                return pagoDAO.obtenerTodos();
            }

            @Override
            protected void done() {
                try {
                    List<Pago> pagos = get();
                    modeloTabla.setRowCount(0);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

                    for (Pago p : pagos) {
                        Object[] fila = {
                            p.getId(),
                            p.getMiembro() != null ? p.getMiembro().getDni() : "N/A",
                            p.getMonto(),
                            p.getFecha() != null ? p.getFecha().format(formatter) : "",
                            p.getTipo() != null ? p.getTipo().name() : "N/A",
                            p.getEstado() != null ? p.getEstado().name() : "N/A",
                            p.getDescripcion()
                        };
                        modeloTabla.addRow(fila);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(PanelPagos.this, "Error al refrescar tabla: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void registrarPago() {
        try {
            String dniMiembro = txtDniMiembro.getText().trim();
            double monto = Double.parseDouble(txtMonto.getText().trim());
            TipoPago tipo = (TipoPago) comboTipo.getSelectedItem();
            EstadoPago estado = (EstadoPago) comboEstado.getSelectedItem();
            String desc = txtDescripcion.getText().trim();

            // Validamos que el miembro realmente exista buscando por DNI
            Optional<Miembro> miembroOpt = miembroService.buscarPorDni(dniMiembro);
            if (!miembroOpt.isPresent()) {
                JOptionPane.showMessageDialog(this, "No se encontró ningún miembro con el DNI especificado.", "Miembro Inexistente", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Pago nuevoPago = new Pago(0, miembroOpt.get(), monto, LocalDateTime.now(), tipo, estado, desc);
            pagoService.registrarPago(nuevoPago);
            
            JOptionPane.showMessageDialog(this, "Pago registrado con éxito.");
            limpiarFormulario();
            cargarDatosEnTabla();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Verifique que el DNI de Miembro y el Monto sea un valor numérico.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarPagoSeleccionado() {
        int fila = tablaPagos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un pago del historial para eliminar.");
            return;
        }

        int idPago = (int) tablaPagos.getValueAt(fila, 0);
        int confirmacion = JOptionPane.showConfirmDialog(this, "¿Seguro que desea eliminar el registro de este pago?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                pagoDAO.eliminar(idPago);
                cargarDatosEnTabla();
                JOptionPane.showMessageDialog(this, "Pago eliminado.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al eliminar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void generarReciboSeleccionado() {
        int fila = tablaPagos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un pago de la tabla para generar su recibo.");
            return;
        }

        int idPago = (int) tablaPagos.getValueAt(fila, 0);
        Optional<Pago> pagoOpt = pagoDAO.buscarPorId(idPago);
        
        if (pagoOpt.isPresent()) {
            String recibo = pagoOpt.get().generarRecibo();
            JOptionPane.showMessageDialog(this, recibo, "Comprobante de Pago", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void limpiarFormulario() {
        txtDniMiembro.setText("");
        txtMonto.setText("");
        txtDescripcion.setText("");
        comboTipo.setSelectedIndex(0);
        comboEstado.setSelectedIndex(0);
    }

    // --- MÉTODOS AUXILIARES DE DISEÑO UI ---

    private JLabel crearLabel(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setForeground(TEXTO_GRIS);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return lbl;
    }

    private JTextField crearTextField() {
        JTextField txt = new JTextField();
        estilizarComponenteUI(txt);
        txt.setCaretColor(Color.WHITE);
        return txt;
    }

    private void estilizarComponenteUI(JComponent c) {
        c.setBackground(BG_INPUTS);
        c.setForeground(TEXTO_BLANCO);
        c.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        c.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 80, 95), 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
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