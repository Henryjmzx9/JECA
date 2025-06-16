package Interface;

import dominio.Cliente;
import dominio.Usuario;
import persistencia.ClienteDAO;
import persistencia.UsuarioDAO;
import utils.CUD;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;

public class clientReadingForm extends JDialog {
    private JPanel mainPanel;
    private JTextField textName;
    private JButton btnCrear;
    private JButton btnModificar;
    private JButton btnEliminar;
    private JTable tableClientes;

    private ClienteDAO clienteDAO;
    private UsuarioDAO usuarioDAO;
    private DefaultTableModel tableModel;

    public clientReadingForm(Window parent) {
        super(parent, "Gestión de Clientes", ModalityType.APPLICATION_MODAL);

        clienteDAO = new ClienteDAO();
        usuarioDAO = new UsuarioDAO();
        initComponents();

        setContentPane(mainPanel);
        setSize(800, 400);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        btnCrear.addActionListener(e -> openClientForm(CUD.CREATE, new Usuario()));
        btnModificar.addActionListener(e -> updateSelectedCliente());
        btnEliminar.addActionListener(e -> deleteSelectedCliente());

        // Quitar el ActionListener que había aquí
        // textName.addActionListener(e -> loadTableData(textName.getText().trim()));

        // Añadir DocumentListener para búsqueda en tiempo real
        textName.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                loadTableData(textName.getText().trim());
            }

            public void removeUpdate(DocumentEvent e) {
                loadTableData(textName.getText().trim());
            }

            public void changedUpdate(DocumentEvent e) {
                loadTableData(textName.getText().trim());
            }
        });

        loadTableData("");
    }

    private void initComponents() {
        mainPanel = new JPanel(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Buscar:"));
        textName = new JTextField(20);
        topPanel.add(textName);
        btnCrear = new JButton("Crear");
        topPanel.add(btnCrear);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"ID", "Nombre", "Teléfono", "Dirección"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableClientes = new JTable(tableModel);
        tableClientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(tableClientes);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnModificar = new JButton("Modificar");
        btnEliminar = new JButton("Eliminar");
        bottomPanel.add(btnModificar);
        bottomPanel.add(btnEliminar);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadTableData(String filtro) {
        try {
            ArrayList<Cliente> clientes = clienteDAO.searchCliente(filtro);
            tableModel.setRowCount(0);

            for (Cliente cliente : clientes) {
                Usuario usuario = usuarioDAO.getById(cliente.getUserId());
                if (usuario != null) {
                    tableModel.addRow(new Object[]{
                            cliente.getClienteId(),
                            usuario.getName(),
                            cliente.getTelefono(),
                            cliente.getDireccion()
                    });
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar clientes: " + ex.getMessage(),
                    "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Cliente getSelectedCliente() {
        int selectedRow = tableClientes.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente de la tabla", "Aviso", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        int clienteId = (int) tableModel.getValueAt(selectedRow, 0);
        try {
            return clienteDAO.getById(clienteId);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al obtener cliente: " + ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private void openClientForm(CUD cud, Usuario usuario) {
        ClientForm form = new ClientForm(this, cud, usuario);
        form.setVisible(true);
        loadTableData(textName.getText().trim());
    }

    private void updateSelectedCliente() {
        Cliente cliente = getSelectedCliente();
        if (cliente != null) {
            try {
                Usuario usuario = usuarioDAO.getById(cliente.getUserId());
                if (usuario != null) {
                    openClientForm(CUD.UPDATE, usuario);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al cargar datos del usuario", "ERROR", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSelectedCliente() {
        Cliente cliente = getSelectedCliente();
        if (cliente != null) {
            int option = JOptionPane.showConfirmDialog(this,
                    "¿Está seguro de eliminar al cliente?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                try {
                    if (clienteDAO.delete(cliente.getClienteId())) {
                        JOptionPane.showMessageDialog(this, "Cliente eliminado correctamente.");
                        loadTableData(textName.getText().trim());
                    } else {
                        JOptionPane.showMessageDialog(this, "No se pudo eliminar al cliente.", "ERROR", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error al eliminar cliente: " + ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}
