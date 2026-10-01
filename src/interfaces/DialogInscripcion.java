package com.gym.manager.view.dialogs;

import com.gym.manager.dao.ClaseDAO;
import com.gym.manager.dao.InscripcionesDAO;
import com.gym.manager.dao.MiembroDAO;
import com.gym.manager.model.ClaseGimnasio;
import com.gym.manager.model.Inscripciones;
import com.gym.manager.model.Miembro;
import com.gym.manager.exceptions.DatosInvalidosException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.util.List;

public class DialogInscripcion extends JDialog {

    private JTextField txtBuscador;
    private JComboBox<String> comboMiembros;
    private JComboBox<String> comboClases;
    
    private List<Miembro> listaMiembros;
    private List<ClaseGimnasio> listaClases;
    
    private boolean inscripcionRealizada = false;

    public DialogInscripcion(Window parent) {
        super(parent, "Nueva Inscripción", Dialog.ModalityType.APPLICATION_MODAL);
        setSize(450, 380);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        
        // Configuramos la interfaz
        JPanel pnlCentro = new JPanel(new GridLayout(6, 1, 5, 5));
        pnlCentro.setBorder(new EmptyBorder(15, 20, 15, 20));

        // 1. Buscador "En vivo"
        pnlCentro.add(new JLabel("🔎 Buscar Socio (Nombre o pedacito de DNI):"));
        txtBuscador = new JTextField();
        pnlCentro.add(txtBuscador);

        // 2. Combo de Miembros filtrado
        pnlCentro.add(new JLabel("Seleccione el Socio:"));
        comboMiembros = new JComboBox<>();
        pnlCentro.add(comboMiembros);

        // 3. Combo de Clases
        pnlCentro.add(new JLabel("Seleccione la Clase:"));
        comboClases = new JComboBox<>();
        pnlCentro.add(comboClases);

        // Cargar datos de la BD al abrir el diálogo
        cargarDatosBD();

        // Evento que escucha cada tecla presionada
        txtBuscador.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filtrarMiembros();
            }
        });

        // 4. Botón de Inscribir
        JPanel pnlSur = new JPanel();
        JButton btnInscribir = new JButton("Guardar Inscripción");
        btnInscribir.setBackground(new Color(0, 150, 136));
        btnInscribir.setForeground(Color.WHITE);
        btnInscribir.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnInscribir.setFocusPainted(false);
        pnlSur.add(btnInscribir);

        add(pnlCentro, BorderLayout.CENTER);
        add(pnlSur, BorderLayout.SOUTH);

        btnInscribir.addActionListener(e -> guardarInscripcion());
    }

    private void cargarDatosBD() {
        // Cargar todos los miembros activos y las clases disponibles
        listaMiembros = new MiembroDAO().obtenerActivos();
        listaClases = new ClaseDAO().obtenerTodos();

        for (ClaseGimnasio c : listaClases) {
            comboClases.addItem(c.getNombre() + " (" + c.getTipoClase() + ")");
        }
        
        filtrarMiembros(); // Llenar el combo por primera vez con todos
    }

    private void filtrarMiembros() {
        if (listaMiembros == null) return;
        
        String busqueda = txtBuscador.getText().toLowerCase();
        comboMiembros.removeAllItems(); // Vaciamos para rellenar
        
        for (Miembro m : listaMiembros) {
            String texto = m.getNombreCompleto() + " (DNI: " + m.getDni() + ")";
            // Verificamos si contiene la búsqueda (parcial o total)
            if (busqueda.isEmpty() || m.getDni().contains(busqueda) || m.getNombreCompleto().toLowerCase().contains(busqueda)) {
                comboMiembros.addItem(texto);
            }
        }
    }

    private void guardarInscripcion() {
        String seleccionadoMiembro = (String) comboMiembros.getSelectedItem();
        int idxClase = comboClases.getSelectedIndex();

        if (seleccionadoMiembro == null || idxClase == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un socio y una clase válidos.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Recuperar el objeto original buscando por el texto del ComboBox
        Miembro miembroSel = listaMiembros.stream()
            .filter(m -> (m.getNombreCompleto() + " (DNI: " + m.getDni() + ")").equals(seleccionadoMiembro))
            .findFirst().orElse(null);

        if (miembroSel == null) return;
        ClaseGimnasio claseSel = listaClases.get(idxClase);

        Inscripciones nueva = new Inscripciones(LocalDate.now(), false, claseSel.getIdClase(), miembroSel.getId());

        try {
            InscripcionesDAO dao = new InscripcionesDAO();
            dao.registrar(nueva); // Pasa por todas las validaciones de BD
            this.inscripcionRealizada = true;
            JOptionPane.showMessageDialog(this, "¡Inscripción exitosa!");
            dispose(); 
        } catch (DatosInvalidosException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Inscripción Rechazada", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error del Sistema", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isInscripcionRealizada() {
        return inscripcionRealizada;
    }
}