package Interface;

import dominio.Usuario;
import persistencia.UsuarioDAO;

import javax.swing.*;

public class ChangePasswordForm extends JDialog {
    private JPanel mainPanel;
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JButton btnChangePassword;

    private UsuarioDAO userDAO;
    private MainForm mainForm;

    // Constructor que recibe la ventana principal para obtener el usuario autenticado
    public ChangePasswordForm(MainForm mainForm) {
        this.mainForm = mainForm;
        userDAO = new UsuarioDAO();

        // Pre-carga el campo de email con el correo del usuario autenticado
        txtEmail.setText(mainForm.getUserAutenticate().getEmail());

        setContentPane(mainPanel);
        setModal(true);
        setTitle("Cambiar contraseña");
        pack();
        setLocationRelativeTo(mainForm);

        // Acción del botón de cambio de contraseña
        btnChangePassword.addActionListener(e -> changePassword());
    }

    // Método para cambiar la contraseña del usuario autenticado
    private void changePassword() {
        try {
            // Obtiene el usuario autenticado desde la ventana principal
            Usuario userAut = mainForm.getUserAutenticate();
            Usuario user = new Usuario();
            user.setId(userAut.getId());
            user.setPasswordHash(new String(txtPassword.getPassword()));

            // Validación: contraseña no debe estar vacía
            if (user.getPasswordHash().trim().isEmpty()) {
                JOptionPane.showMessageDialog(
                        null,
                        "La contraseña es obligatoria",
                        "Validación",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            // Actualizar la contraseña en la base de datos
            boolean res = userDAO.updatePassword(user);

            // Resultado de la operación
            if (res) {
                // Cierra esta ventana
                this.dispose();
                // Abre la ventana de Login
                LoginForm loginForm = new LoginForm(this.mainForm);
                loginForm.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(
                        null,
                        "No se logró cambiar la contraseña",
                        "Cambiar contraseña",
                        JOptionPane.WARNING_MESSAGE
                );
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Sistema",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}

