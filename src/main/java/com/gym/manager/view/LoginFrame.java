package com.gym.manager.view;

// Importamos las librerias de swing y awt para las fuentes, colores, etc.
import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    // Declaramos los componentes de la interfaz
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JButton btnIngresar;

    public LoginFrame() {
        // Configuración básica de la ventana
        setTitle("FitBase - Iniciar Sesión");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Esto centra la ventana en la pantalla
        
        // Usamos GridBagLayout para centrar el formulario en el medio
        setLayout(new GridBagLayout()); 
        
        // Inicializar los componentes
        JLabel lblTitulo = new JLabel("Bienvenido a FitBase!");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        
        JLabel lblUsuario = new JLabel("Usuario:");
        txtUsuario = new JTextField(50);
        
        JLabel lblPassword = new JLabel("Contraseña:");
        txtPassword = new JPasswordField(255);
        
        btnIngresar = new JButton("Ingresar");
        btnIngresar.setBackground(new Color(41, 128, 185)); // Color azul
        btnIngresar.setForeground(Color.WHITE);
        btnIngresar.setFocusPainted(false); // Quita el borde punteado feo al hacer clic

        // Usamos GridBagConstraints para posicionar los componentes 
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Márgenes entre componentes
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Fila 0: Título
        gbc.gridx = 0; 
        gbc.gridy = 0;
        gbc.gridwidth = 2; 
        gbc.anchor = GridBagConstraints.CENTER;
        add(lblTitulo, gbc);

        // Fila 1: Usuario
        gbc.gridwidth = 1;
        gbc.gridx = 0; 
        gbc.gridy = 1;
        add(lblUsuario, gbc);
        
        gbc.gridx = 1; 
        gbc.gridy = 1;
        add(txtUsuario, gbc);

        // Fila 2: Contraseña
        gbc.gridx = 0; 
        gbc.gridy = 2;
        add(lblPassword, gbc);
        
        gbc.gridx = 1; 
        gbc.gridy = 2;
        add(txtPassword, gbc);

        // Fila 3: Botón Ingresar
        gbc.gridx = 0; 
        gbc.gridy = 3;
        gbc.gridwidth = 2; // Que el botón ocupe todo el ancho
        add(btnIngresar, gbc);

        // Inicializamos los eventos para el botón
        inicializarEventos();
    }

    // Getters para obtener los valores ingresados por el usuario y el botón para agregar el ActionListener en el controlador
    public String getUsuario() {
        return txtUsuario.getText();
    }

    public String getPassword() {
        return new String(txtPassword.getPassword());
    }

    public JButton getBtnIngresar() {
        return btnIngresar;
    }

    public void inicializarEventos() {
        btnIngresar.addActionListener(e -> {
            
            String usuario = getUsuario();
            String password = getPassword();
            
            System.out.println("El usuario [" + usuario + "] intentó ingresar con la clave [" + password + "]");
        });
    }
}