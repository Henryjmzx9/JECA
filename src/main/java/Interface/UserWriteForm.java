package Interface;

import dominio.Usuario;
import persistencia.UsuarioDAO;
import utils.CBOption;
import utils.CUD;
import utils.Rol;

import javax.swing.*;
import java.awt.event.ActionEvent;

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
        boolean res = false;

        CBOption selectedStatus = (CBOption) cbStatus.getSelectedItem();
        CBOption selectedRol = (CBOption) cbRol.getSelectedItem();

        byte status = selectedStatus != null ? (byte) selectedStatus.getValue() : 0;
        Rol rol = selectedRol != null ? (Rol) selectedRol.getValue() : null;

        if (txtName.getText().trim().isEmpty()) {
            return res;
        } else if (txtEmail.getText().trim().isEmpty()) {
            return res;
        } else if (status == (byte) 0) {
            return res;
        } else if (rol == null) {
            return res;
        } else if (this.cud != CUD.CREATE && this.en.getId() == 0) {
            return res;
        }

        this.en.setName(txtName.getText());
        this.en.setEmail(txtEmail.getText());
        this.en.setStatus(status);
        this.en.setRol(rol);

        if (this.cud == CUD.CREATE) {
            this.en.setPasswordHash(new String(txtPassword.getPassword()));
            if (this.en.getPasswordHash().trim().isEmpty()) {
                return false;
            }
        }

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
                            "Transacción realizada exitosamente",
                            "Información", JOptionPane.INFORMATION_MESSAGE);
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(null,
                            "No se logró realizar ninguna acción",
                            "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null,
                        "Los campos con * son obligatorios",
                        "Validación", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }
}
