package Interface;

import dominio.Usuario;
import persistencia.UsuarioDAO;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class LoginForm extends JDialog {
    private JPanel mainPanel;
    private JTextField TxtEmail;
    private JButton btnLogin;
    private JButton btnSalir;
    private JPasswordField txtPassword;

    private UsuarioDAO userDAO; // Manejo de usuarios en la base de datos
    private MainForm mainForm;  // Referencia al formulario principal

    public LoginForm(MainForm mainForm) {
        this.mainForm = mainForm; // Vincular LoginForm con MainForm
        userDAO = new UsuarioDAO(); // Inicializar el DAO
        setContentPane(mainPanel);
        setModal(true);
        setTitle("Login");
        pack();
        setLocationRelativeTo(mainForm); // Centrar respecto al MainForm

        // Botón para salir de la aplicación
        btnSalir.addActionListener(e -> System.exit(0));

        // Botón para iniciar sesión
        btnLogin.addActionListener(e -> login());

        // Acción al cerrar la ventana
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0); // Salir de la aplicación al cerrar LoginForm
            }
        });
    }

    private void login() {
        try {
            String email = TxtEmail.getText().trim();
            String password = new String(txtPassword.getPassword()).trim();

            // Validar que sea un correo gmail
            if (!email.toLowerCase().endsWith("@gmail.com")) {
                JOptionPane.showMessageDialog(this,
                        "Por favor ingrese un correo válido de Gmail.",
                        "Validación de Email",
                        JOptionPane.WARNING_MESSAGE);
                return; // Salir del método para evitar llamar a authenticate
            }

            if (password.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "La contraseña es obligatoria.",
                        "Validación de Contraseña",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Autenticación del usuario
            Usuario userAut = userDAO.authenticate(email, password);

            if (userAut != null) {
                // Pasar el usuario autenticado al MainForm
                mainForm.setUserAutenticate(userAut);
                this.dispose(); // Cerrar el LoginForm
            } else {
                // Mostrar mensaje si las credenciales son incorrectas
                JOptionPane.showMessageDialog(this,
                        "Email o contraseña incorrectos.",
                        "Error de Login",
                        JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            // Mostrar error en caso de excepción
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Error del sistema",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
