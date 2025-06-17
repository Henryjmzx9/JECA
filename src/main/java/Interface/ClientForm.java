package Interface;

import dominio.Cliente;
import dominio.Usuario;
import persistencia.ClienteDAO;
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
    private ClienteDAO clienteDAO;
    private CUD cud;
    private Usuario cliente;

    public ClientForm(Window parent, CUD cud, Usuario cliente) {
        super(parent);
        this.cud = cud;
        this.cliente = cliente;
        usuarioDAO = new UsuarioDAO();
        clienteDAO = new ClienteDAO();

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

            List<Usuario> clientes = usuarioDAO.obtenerUsuariosPorRol(Rol.Cliente, "");

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

        String telefono = textTelefono.getText().trim();
        String descripcion = textDescripcion.getText().trim();

        // Validaciones
        if (userId == 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario válido.", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (telefono.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El teléfono es obligatorio.", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Validación teléfono: números, espacios, +, - y mínimo 7 caracteres
        if (!telefono.matches("^[\\d\\s+\\-]{7,15}$")) {
            JOptionPane.showMessageDialog(this, "El teléfono debe tener entre 7 y 15 caracteres y solo puede contener números, espacios, '+' o '-'.", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (descripcion.isEmpty()) {
            JOptionPane.showMessageDialog(this, "La descripción es obligatoria.", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (cliente == null) {
            cliente = new Usuario();
        }

        cliente.setId(userId);
        cliente.setTelefono(telefono);
        cliente.setDescripcion(descripcion);

        return true;
    }

    private void ok() {
        try {
            if (getValuesControls()) {
                boolean r = false;

                switch (cud) {
                    case CREATE:
                        // Verificar si ya existe cliente con ese userId
                        if (clienteDAO.getByUserId(cliente.getId()) != null) {
                            JOptionPane.showMessageDialog(null, "Este usuario ya está registrado como cliente.", "Validación", JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                        Cliente nuevo = new Cliente();
                        nuevo.setUserId(cliente.getId());
                        nuevo.setTelefono(cliente.getTelefono());
                        nuevo.setDireccion(cliente.getDescripcion()); // usando descripcion como dirección

                        Cliente creado = clienteDAO.create(nuevo);
                        r = creado != null;
                        break;

                    case UPDATE:
                        Cliente existente = clienteDAO.getByUserId(cliente.getId());
                        if (existente != null) {
                            existente.setTelefono(cliente.getTelefono());
                            existente.setDireccion(cliente.getDescripcion());
                            r = clienteDAO.update(existente);
                        }
                        break;

                    case DELETE:
                        Cliente paraEliminar = clienteDAO.getByUserId(cliente.getId());
                        if (paraEliminar != null) {
                            r = clienteDAO.delete(paraEliminar.getClienteId());
                        }
                        break;
                }

                if (r) {
                    JOptionPane.showMessageDialog(null, "Operación realizada correctamente", "Información", JOptionPane.INFORMATION_MESSAGE);
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "No se logró realizar la operación", "ERROR", JOptionPane.ERROR_MESSAGE);
                }

            } else {
                // El mensaje ya es mostrado en getValuesControls()
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }
}
