package com.gym.manager.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.gym.manager.view.panels.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class VentanaPrincipal extends JFrame {

    // ── Paleta de colores, declaro acá asi no tengo que repetir new Color(...) cada vez que quiero usar un color específico
    private static final Color SIDEBAR_BG     = new Color(22, 38, 45);
    private static final Color SIDEBAR_HOVER  = new Color(35, 58, 70);
    private static final Color SIDEBAR_ACTIVO = new Color(0, 150, 136);
    private static final Color CENTRAL_BG     = new Color(28, 43, 51);
    private static final Color TEXTO_BLANCO   = new Color(230, 230, 230);
    private static final Color TEXTO_GRIS     = new Color(160, 175, 180);

    private final String usuarioLoggeado;
    private final String rolUsuario;       // "ADMIN" o "RECEPCIONISTA"

    private JPanel panelCentral;
    private CardLayout cardLayout;
    private JButton botonActivo;           // botón resaltado actualmente

    // ── Constructor ────────────────────────────────────────────────────────
    public VentanaPrincipal(String usuario, String rol) {
        this.usuarioLoggeado = usuario;
        this.rolUsuario      = rol;

        inicializarVentana();
        construirMenuBar();             // menú superior
        construirMenuLateral();         // sidebar con botones de navegación
        construirPanelCentral();        // zona de contenido con CardLayout
    }

    // ── Configuración base ─────────────────────────────────────────────────
    private void inicializarVentana() {
        setTitle("Gym Manager — Fit Base");
        setSize(1100, 650);
        setMinimumSize(new Dimension(800, 500));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    // ── Barra de menú superior (JMenuBar) ─────────────────────────────────
    private void construirMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menuArchivo = new JMenu("Archivo");

        JMenuItem itemCerrarSesion = new JMenuItem("Cerrar sesión");
        itemCerrarSesion.addActionListener(e -> cerrarSesion());

        JMenuItem itemSalir = new JMenuItem("Salir");
        itemSalir.addActionListener(e -> salir());

        menuArchivo.add(itemCerrarSesion);
        menuArchivo.addSeparator();
        menuArchivo.add(itemSalir);

        JMenu menuAyuda = new JMenu("Ayuda");

        JMenuItem itemAcerca = new JMenuItem("Acerca de");
        itemAcerca.addActionListener(e -> JOptionPane.showMessageDialog(
            this,
            "GymManager — FitBase  v1.0\nProyecto Final — Programación II",
            "Acerca de",
            JOptionPane.INFORMATION_MESSAGE
        ));
        menuAyuda.add(itemAcerca);

        menuBar.add(menuArchivo);
        menuBar.add(Box.createHorizontalGlue()); // empuja Ayuda a la derecha
        menuBar.add(menuAyuda);

        setJMenuBar(menuBar);
    }

    // ── Menú lateral (sidebar) ─────────────────────────────────────────────
    private void construirMenuLateral() {
        JPanel panelLateral = new JPanel();
        panelLateral.setBackground(SIDEBAR_BG);
        panelLateral.setLayout(new BoxLayout(panelLateral, BoxLayout.Y_AXIS));
        panelLateral.setPreferredSize(new Dimension(220, getHeight()));
        panelLateral.setBorder(new EmptyBorder(20, 12, 20, 12));

        // ── Logo ──
        JLabel lblLogo = new JLabel("FIT BASE");
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── Info del usuario logueado ──
        JLabel lblUsuario = new JLabel(usuarioLoggeado);
        lblUsuario.setForeground(TEXTO_GRIS);
        lblUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblUsuario.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblRol = new JLabel(rolUsuario);
        lblRol.setForeground(SIDEBAR_ACTIVO);
        lblRol.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblRol.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── Separador ──
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(50, 70, 80));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        panelLateral.add(lblLogo);
        panelLateral.add(Box.createRigidArea(new Dimension(0, 5)));
        panelLateral.add(lblUsuario);
        panelLateral.add(Box.createRigidArea(new Dimension(0, 2)));
        panelLateral.add(lblRol);
        panelLateral.add(Box.createRigidArea(new Dimension(0, 16)));
        panelLateral.add(sep);
        panelLateral.add(Box.createRigidArea(new Dimension(0, 16)));

        // ── Botones de navegación ──
        agregarBotonMenu(panelLateral, "Miembros y Planes",      "miembros");
        agregarBotonMenu(panelLateral, "Planes de Entrenamiento", "planes");
        agregarBotonMenu(panelLateral, "Clases",          "clases");
        agregarBotonMenu(panelLateral, "Inscripciones",   "inscripciones");
        agregarBotonMenu(panelLateral, "Registro de Pagos",      "pagos");

        // Reportes solo para ADMIN → control de sesión por rol
        if ("ADMIN".equalsIgnoreCase(rolUsuario)) {
            agregarBotonMenu(panelLateral, "Reportes", "reportes");
            agregarBotonMenu(panelLateral, "Gestión de Instructores", "instructores");
        }

        // ── Botón logout empujado al fondo ──
        panelLateral.add(Box.createVerticalGlue());
        panelLateral.add(crearBotonLogout());

        add(panelLateral, BorderLayout.WEST);
    }

    private void agregarBotonMenu(JPanel sidebar, String texto, String card) {
        JButton btn = new JButton(texto);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setBackground(SIDEBAR_BG);
        btn.setForeground(TEXTO_BLANCO);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Efecto hover
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                if (btn != botonActivo) btn.setBackground(SIDEBAR_HOVER);
            }
            @Override public void mouseExited(MouseEvent e) {
                if (btn != botonActivo) btn.setBackground(SIDEBAR_BG);
            }
        });

        // Acción: cambiar panel + resaltar botón activo
        btn.addActionListener(e -> {
            cardLayout.show(panelCentral, card);
            if (botonActivo != null) botonActivo.setBackground(SIDEBAR_BG);
            btn.setBackground(SIDEBAR_ACTIVO);
            botonActivo = btn;
        });

        sidebar.add(btn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 4)));
    }

    private JButton crearBotonLogout() {
        JButton btn = new JButton("Cerrar sesión");
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setBackground(new Color(120, 30, 30));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> cerrarSesion());
        return btn;
    }

    // ── Panel central con CardLayout ───────────────────────────────────────
    private void construirPanelCentral() {
        cardLayout    = new CardLayout();
        panelCentral  = new JPanel(cardLayout);
        panelCentral.setBackground(CENTRAL_BG);

        // Panel de bienvenida, el que se va a ver por default
        panelCentral.add(crearPanelBienvenida(), "bienvenida");

        // ACÁ REEMPLAZAR CUANDO ESTEN LOS PANELES LISTOS ────────────────────── !!

        // TODO → panelCentral.add(new PanelMiembros(), "miembros");
        PanelMiembros panelMiembros = new PanelMiembros();
        panelCentral.add(panelMiembros, "miembros");

        PanelPlan panelPlan = new PanelPlan();
        panelCentral.add(panelPlan, "planes");

        panelCentral.add(new PanelInstructores(), "instructores");

        PanelClases panelClases = new PanelClases();
        panelCentral.add(panelClases, "clases");

        PanelInscripciones panelInscripciones = new PanelInscripciones();
        panelCentral.add(panelInscripciones, "inscripciones");

        PanelPagos panelPagos = new PanelPagos();
        panelCentral.add(panelPagos, "pagos");

        panelCentral.add(new PanelReportes(), "reportes");

        add(panelCentral, BorderLayout.CENTER);

    }


    private JPanel crearPanelBienvenida() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(CENTRAL_BG);

        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setOpaque(false);

        JLabel lblHola = new JLabel("Hola, " + usuarioLoggeado + "!");
        lblHola.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblHola.setForeground(TEXTO_BLANCO);
        lblHola.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitulo = new JLabel("Seleccioná una opción del menú para comenzar.");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitulo.setForeground(TEXTO_GRIS);
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        contenido.add(lblHola);
        contenido.add(Box.createVerticalStrut(12));
        contenido.add(lblSubtitulo);

        panel.add(contenido);
        return panel;
    }

    // ── Acciones ───────────────────────────────────────────────────────────
    private void cerrarSesion() {
        int opcion = JOptionPane.showConfirmDialog(
            this, "¿Querés cerrar sesión?", "Cerrar sesión",
            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE
        );
        if (opcion == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame().setVisible(true);
        }
    }

    private void salir() {
        int opcion = JOptionPane.showConfirmDialog(
            this, "¿Querés salir de la aplicación?", "Salir",
            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE
        );
        if (opcion == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
}