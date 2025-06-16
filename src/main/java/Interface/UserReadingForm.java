package Interface;

import dominio.Usuario;
import persistencia.UsuarioDAO;
import utils.CUD;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class UserReadingForm extends JDialog {
    private JPanel mainPanel;
    private JTextField txtName;
    private JButton btnCreate;
    private JTable tableUsers;
    private JButton btnUpdate;
    private JButton btnDelete;

    private UsuarioDAO userDAO;
    private MainForm mainForm;

    public UserReadingForm(MainForm mainForm) {
        this.mainForm = mainForm;
        this.userDAO = new UsuarioDAO();

        setContentPane(mainPanel);
        setModal(true);
        setTitle("Gestión de Usuarios");
        pack();
        setLocationRelativeTo(mainForm);

        // Asegura que se puedan seleccionar filas
        tableUsers.setRowSelectionAllowed(true);
        tableUsers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Cargar todos los usuarios al iniciar
        loadAllUsers();

        // Buscar usuarios al escribir
        txtName.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String query = txtName.getText().trim();
                if (!query.isEmpty()) {
                    search(query);
                } else {
                    loadAllUsers();
                }
            }
        });

        // Crear usuario
        btnCreate.addActionListener(e -> {
            System.out.println("Botón CREAR presionado");
            UserWriteForm form = new UserWriteForm(mainForm, CUD.CREATE, new Usuario());
            form.setVisible(true);
            loadAllUsers();
        });

        // Actualizar usuario
        btnUpdate.addActionListener(e -> {
            System.out.println("Botón MODIFICAR presionado");
            Usuario user = getUserFromTableRow();
            if (user != null) {
                System.out.println("Usuario seleccionado para MODIFICAR: " + user.getId());
                UserWriteForm form = new UserWriteForm(mainForm, CUD.UPDATE, user);
                form.setVisible(true);
                loadAllUsers();
            }
        });

        // Eliminar usuario
        btnDelete.addActionListener(e -> {
            System.out.println("Botón ELIMINAR presionado");
            Usuario user = getUserFromTableRow();
            if (user != null) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "¿Estás seguro de que deseas eliminar este usuario?",
                        "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    System.out.println("Usuario confirmado para ELIMINAR: " + user.getId());
                    UserWriteForm form = new UserWriteForm(mainForm, CUD.DELETE, user);
                    form.setVisible(true);
                    loadAllUsers();
                }
            }
        });
    }

    private void loadAllUsers() {
        try {
            ArrayList<Usuario> users = userDAO.getAll();
            createTable(users);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void search(String query) {
        try {
            ArrayList<Usuario> users = userDAO.search(query);
            createTable(users);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void createTable(ArrayList<Usuario> users) {
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        model.addColumn("ID");
        model.addColumn("Nombre");
        model.addColumn("Email");
        model.addColumn("Estatus");

        for (Usuario user : users) {
            model.addRow(new Object[]{
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getStrEstatus()
            });
        }

        tableUsers.setModel(model);
        hideCol(0);
    }

    private void hideCol(int col) {
        tableUsers.getColumnModel().getColumn(col).setMinWidth(0);
        tableUsers.getColumnModel().getColumn(col).setMaxWidth(0);
        tableUsers.getTableHeader().getColumnModel().getColumn(col).setMinWidth(0);
        tableUsers.getTableHeader().getColumnModel().getColumn(col).setMaxWidth(0);
    }

    private Usuario getUserFromTableRow() {
        try {
            int selectedRow = tableUsers.getSelectedRow();
            if (selectedRow != -1) {
                int id = (int) tableUsers.getModel().getValueAt(selectedRow, 0);
                System.out.println("Fila seleccionada. ID: " + id);
                Usuario usuario = userDAO.getById(id);
                if (usuario != null) {
                    System.out.println("Usuario obtenido desde BD: " + usuario.getName());
                } else {
                    System.out.println("Usuario con ID no encontrado en BD.");
                }
                return usuario;
            } else {
                JOptionPane.showMessageDialog(this, "Selecciona una fila.", "Validación", JOptionPane.WARNING_MESSAGE);
                return null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
}