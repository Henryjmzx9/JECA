package Interface;

import dominio.Usuario;
import persistencia.UsuarioDAO;
import utils.CBOption;
import utils.CUD;
import utils.Rol;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class ClientForm extends JDialog {
    private JTextField textTelefono;
    private JTextField textDescripcion;
    private JButton btnGuardar;
    private JButton btnSalir;
    private JComboBox<CBOption> cbUsers;
    private JPanel mainPanel;

    private UsuarioDAO usuarioDAO;
    private CUD cud;
    private Usuario cliente;
    private String filtro;

    public ClientForm(Window parent, CUD cud, Usuario cliente) {
        super(parent);
        this.cud = cud;
        this.cliente = cliente;
        usuarioDAO = new UsuarioDAO();

        setContentPane(mainPanel);
        setModal(true);
        init();
        pack();
        setLocationRelativeTo(parent);

        btnSalir.addActionListener(s -> this.dispose());
        btnGuardar.addActionListener(s -> ok());
    }

    private void init() {
        initCBUsuarios();

        switch (this.cud) {
            case CREATE:
                setTitle("Nuevo Cliente");
                btnGuardar.setText("Guardar");
                break;
            case UPDATE:
                setTitle("Modificar Cliente");
                btnGuardar.setText("Guardar");
                break;
            case DELETE:
                setTitle("Eliminar Cliente");
                btnGuardar.setText("Eliminar");
                break;
        }

        setValuesControls(this.cliente);
    }

    private void initCBUsuarios() {
        try {
            DefaultComboBoxModel<CBOption> model = new DefaultComboBoxModel<>();
            model.addElement(new CBOption("Seleccione un usuario", 0));

            List<Usuario> clientes = usuarioDAO.obtenerUsuariosPorRol(Rol.Cliente, filtro);
            for (Usuario u : clientes) {
                model.addElement(new CBOption(u.getName(), u.getId()));
            }

            cbUsers.setModel(model);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Error al cargar los usuarios CLIENTE:\n" + e.getMessage(),
                    "ERROR",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setValuesControls(Usuario cliente) {
        if (cliente == null) return;

        textTelefono.setText(cliente.getTelefono());
        textDescripcion.setText(cliente.getDescripcion());

        for (int i = 0; i < cbUsers.getItemCount(); i++) {
            CBOption opt = cbUsers.getItemAt(i);
            if ((int) opt.getValue() == cliente.getId()) {
                cbUsers.setSelectedIndex(i);
                break;
            }
        }

        if (cud == CUD.DELETE) {
            textTelefono.setEditable(false);
            textDescripcion.setEditable(false);
            cbUsers.setEnabled(false);
        }
    }

    private boolean getValuesControls() {
        CBOption selected = (CBOption) cbUsers.getSelectedItem();
        int userId = selected != null ? (int) selected.getValue() : 0;

        if (textTelefono.getText().trim().isEmpty() || userId == 0
                || (cud != CUD.CREATE && (cliente == null || cliente.getId() == 0))) {
            return false;
        }

        if (cliente == null) {
            cliente = new Usuario();
        }

        cliente.setId(userId);
        cliente.setTelefono(textTelefono.getText());
        cliente.setDescripcion(textDescripcion.getText());

        return true;
    }

    private void ok() {
        try {
            if (getValuesControls()) {
                boolean r = false;

                switch (cud) {
                    case CREATE:
                        // Aquí deberías invocar usuarioDAO.crearCliente(cliente)
                        System.out.println("Guardando nuevo cliente: " + cliente);
                        r = true;
                        break;
                    case UPDATE:
                        // Aquí deberías invocar usuarioDAO.actualizarCliente(cliente)
                        System.out.println("Actualizando cliente: " + cliente);
                        r = true;
                        break;
                    case DELETE:
                        // Aquí deberías invocar usuarioDAO.eliminarCliente(cliente.getId())
                        System.out.println("Eliminando cliente: " + cliente.getId());
                        r = true;
                        break;
                }

                if (r) {
                    JOptionPane.showMessageDialog(null, "Operación realizada correctamente", "Información", JOptionPane.INFORMATION_MESSAGE);
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "No se logró realizar la operación", "ERROR", JOptionPane.ERROR_MESSAGE);
                }

            } else {
                JOptionPane.showMessageDialog(null, "Complete los campos obligatorios", "Validación", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }
}
