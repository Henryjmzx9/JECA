package Interface;

import dominio.Usuario;
import persistencia.UsuarioDAO;
import utils.CBOption;
import utils.CUD;
import utils.Rol;

import javax.swing.*;
import java.util.regex.Pattern;

public class UserWriteForm extends JDialog {
    private JPanel mainPanel;
    private JTextField txtName;
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JComboBox cbStatus;
    private JComboBox cbRol;
    private JButton btnOk;
    private JButton btnCancel;
    private JLabel lbPassword;

    private UsuarioDAO usuarioDAO;
    private CUD cud;
    private Usuario en;

    public UserWriteForm(JFrame parent, CUD cud, Usuario usuario) {
        this.cud = cud;
        this.en = usuario;
        usuarioDAO = new UsuarioDAO();

        setContentPane(mainPanel);
        setModal(true);
        init();
        pack();
        setLocationRelativeTo(parent);

        btnCancel.addActionListener(s -> this.dispose());
        btnOk.addActionListener(s -> ok());
    }

    private void init() {
        initCBStatus();
        initCBRol();

        switch (this.cud) {
            case CREATE:
                setTitle("Crear Usuario");
                btnOk.setText("Guardar");
                break;
            case UPDATE:
                setTitle("Modificar Usuario");
                btnOk.setText("Guardar");
                break;
            case DELETE:
                setTitle("Eliminar Usuario");
                btnOk.setText("Eliminar");
                break;
        }

        setValuesControls(this.en);
    }

    private void initCBStatus() {
        DefaultComboBoxModel<CBOption> model = new DefaultComboBoxModel<>();
        model.addElement(new CBOption("ACTIVO", (byte) 1));
        model.addElement(new CBOption("INACTIVO", (byte) 2));
        cbStatus.setModel(model);
    }

    private void initCBRol() {
        DefaultComboBoxModel<CBOption> model = new DefaultComboBoxModel<>();
        for (Rol rol : Rol.values()) {
            model.addElement(new CBOption(rol.name(), rol));
        }
        cbRol.setModel(model);
    }

    private void setValuesControls(Usuario usuario) {
        txtName.setText(usuario.getName());
        txtEmail.setText(usuario.getEmail());

        cbStatus.setSelectedItem(new CBOption(null, usuario.getStatus()));
        cbRol.setSelectedItem(new CBOption(null, usuario.getRol()));

        if (this.cud == CUD.CREATE) {
            cbStatus.setSelectedItem(new CBOption(null, (byte) 1));
        }

        if (this.cud == CUD.DELETE) {
            txtName.setEditable(false);
            txtEmail.setEditable(false);
            cbStatus.setEnabled(false);
            cbRol.setEnabled(false);
        }

        if (this.cud != CUD.CREATE) {
            txtPassword.setVisible(false);
            lbPassword.setVisible(false);
        }
    }

    private boolean getValuesControls() {
        CBOption selectedStatus = (CBOption) cbStatus.getSelectedItem();
        CBOption selectedRol = (CBOption) cbRol.getSelectedItem();

        byte status = selectedStatus != null ? (byte) selectedStatus.getValue() : 0;
        Rol rol = selectedRol != null ? (Rol) selectedRol.getValue() : null;

        String name = txtName.getText().trim();
        String email = txtEmail.getText().trim();

        // Validaciones con mensajes
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre es obligatorio.", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El correo electrónico es obligatorio.", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Validación básica de email
        if (!Pattern.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", email)) {
            JOptionPane.showMessageDialog(this, "El correo electrónico no tiene un formato válido.", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (status == (byte) 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un estado válido.", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (rol == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un rol válido.", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (this.cud != CUD.CREATE && this.en.getId() == 0) {
            JOptionPane.showMessageDialog(this, "El usuario seleccionado no es válido.", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (this.cud == CUD.CREATE) {
            String pwd = new String(txtPassword.getPassword()).trim();
            if (pwd.isEmpty()) {
                JOptionPane.showMessageDialog(this, "La contraseña es obligatoria.", "Validación", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            this.en.setPasswordHash(pwd);
        }

        this.en.setName(name);
        this.en.setEmail(email);
        this.en.setStatus(status);
        this.en.setRol(rol);

        return true;
    }

    private void ok() {
        try {
            boolean res = getValuesControls();

            if (res) {
                boolean r = false;

                switch (this.cud) {
                    case CREATE:
                        Usuario u = usuarioDAO.create(this.en);
                        if (u.getId() > 0) {
                            r = true;
                        }
                        break;
                    case UPDATE:
                        r = usuarioDAO.update(this.en);
                        break;
                    case DELETE:
                        r = usuarioDAO.delete(this.en.getId());
                        break;
                }

                if (r) {
                    JOptionPane.showMessageDialog(null,
                            "Usuario registrado correctamente",
                            "Información", JOptionPane.INFORMATION_MESSAGE);
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(null,
                            "No se logro realizar la operación",
                            "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            }
            // Si no pasa validación, ya se mostró mensaje
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }
}
