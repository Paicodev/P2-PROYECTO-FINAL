package com.gym.manager.view.panels;

import com.gym.manager.dao.ClaseDAO;
import com.gym.manager.dao.InscripcionesDAO;
import com.gym.manager.dao.MiembroDAO;
import com.gym.manager.model.ClaseGimnasio;
import com.gym.manager.model.Inscripciones;
import com.gym.manager.model.Miembro;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class PanelInscripciones extends JPanel {

    private static final Color BG_CENTRAL      = new Color(28, 43, 51);
    private static final Color BG_FORMULARIO   = new Color(22, 38, 45);
    private static final Color ACENTO_TURQUESA = new Color(0, 150, 136);
    private static final Color TEXTO_BLANCO    = new Color(230, 230, 230);
    private static final Color TEXTO_GRIS      = new Color(160, 175, 180);

    private JComboBox<String> comboMiembros;
    private JComboBox<String> comboClases;
    private List<Miembro> listaMiembros;
    private List<ClaseGimnasio> listaClases;

    public PanelInscripciones() {
        setBackground(BG_CENTRAL);
        setLayout(new GridBagLayout()); // Centramos todo en el medio

        JPanel panelForm = new JPanel(new GridLayout(3, 1, 10, 20));
        panelForm.setBackground(BG_FORMULARIO);
        panelForm.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(50, 70, 80)), " Asignar Miembro a Clase "),
            new EmptyBorder(20, 20, 20, 20)
        ));

        comboMiembros = new JComboBox<>();
        comboClases = new JComboBox<>();
        JButton btnInscribir = new JButton("Confirmar Inscripción");
        btnInscribir.setBackground(ACENTO_TURQUESA);
        btnInscribir.setForeground(Color.WHITE);
        btnInscribir.setFocusPainted(false);

        panelForm.add(crearCaja("1. Seleccionar Miembro:", comboMiembros));
        panelForm.add(crearCaja("2. Seleccionar Clase:", comboClases));
        panelForm.add(btnInscribir);

        add(panelForm);

        btnInscribir.addActionListener(e -> registrarInscripcion());
    }

    private JPanel crearCaja(String titulo, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setBackground(BG_FORMULARIO);
        JLabel l = new JLabel(titulo); l.setForeground(TEXTO_GRIS);
        p.add(l, BorderLayout.NORTH); p.add(comp, BorderLayout.CENTER);
        return p;
    }

    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
        if (aFlag) {
            cargarCombos();
        }
    }

    private void cargarCombos() {
        comboMiembros.removeAllItems();
        comboClases.removeAllItems();
        
        listaMiembros = new MiembroDAO().obtenerActivos(); // Solo miembros al día
        listaClases = new ClaseDAO().obtenerTodos();

        for (Miembro m : listaMiembros) {
            comboMiembros.addItem(m.getNombreCompleto() + " (DNI: " + m.getDni() + ")");
        }
        for (ClaseGimnasio c : listaClases) {
            comboClases.addItem(c.getNombre() + " (" + c.getTipoClase() + ")");
        }
    }

    private void registrarInscripcion() {
        int idxMiembro = comboMiembros.getSelectedIndex();
        int idxClase = comboClases.getSelectedIndex();

        if (idxMiembro == -1 || idxClase == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un Miembro y una Clase.");
            return;
        }

        Miembro miembroSel = listaMiembros.get(idxMiembro);
        ClaseGimnasio claseSel = listaClases.get(idxClase);

        Inscripciones nueva = new Inscripciones(LocalDate.now(), false, claseSel.getIdClase(), miembroSel.getId());
        
        try {
            new InscripcionesDAO().registrar(nueva);
            JOptionPane.showMessageDialog(this, "¡Inscripción exitosa!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al inscribir: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}