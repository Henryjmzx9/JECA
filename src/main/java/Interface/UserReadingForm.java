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
    private ArrayList<Usuario> Users;

    public UserReadingForm(MainForm mainForm) {
        this.mainForm = mainForm;
        userDAO = new UsuarioDAO();
        setContentPane(mainPanel);
        setModal(true);
        setTitle("Gestión de Usuarios");
        pack();
        setLocationRelativeTo(mainForm);

        txtName.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (!txtName.getText().trim().isEmpty()) {
                    search(txtName.getText());
                } else {
                    tableUsers.setModel(new DefaultTableModel());
                }
            }
        });

        btnCreate.addActionListener(e -> {
            UserWriteForm form = new UserWriteForm(this.mainForm, CUD.CREATE, new Usuario());
            form.setVisible(true);
            tableUsers.setModel(new DefaultTableModel());
        });

        btnUpdate.addActionListener(e -> {
            Usuario user = getUserFromTableRow();
            if (user != null) {
                UserWriteForm form = new UserWriteForm(this.mainForm, CUD.UPDATE, user);
                form.setVisible(true);
                tableUsers.setModel(new DefaultTableModel());
            }
        });

        btnDelete.addActionListener(e -> {
            Usuario user = getUserFromTableRow();
            if (user != null) {
                UserWriteForm form = new UserWriteForm(this.mainForm, CUD.DELETE, user);
                form.setVisible(true);
                tableUsers.setModel(new DefaultTableModel());
            }
        });
    }

    private void search(String query) {
        try {
            ArrayList<Usuario> users = userDAO.search(query);
            createTable(users);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
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

        Object[] row = new Object[4];
        for (int i = 0; i < users.size(); i++) {
            Usuario user = users.get(i);
            row[0] = user.getId();
            row[1] = user.getName();
            row[2] = user.getEmail();
            row[3] = user.getStrEstatus();
            model.addRow(row);
        }

        tableUsers.setModel(model);
        hideCol(0);
    }

    private void hideCol(int col) {
        tableUsers.getColumnModel().getColumn(col).setMaxWidth(0);
        tableUsers.getColumnModel().getColumn(col).setMinWidth(0);
        tableUsers.getTableHeader().getColumnModel().getColumn(col).setMaxWidth(0);
        tableUsers.getTableHeader().getColumnModel().getColumn(col).setMinWidth(0);
    }

    private Usuario getUserFromTableRow() {
        try {
            int selectedRow = tableUsers.getSelectedRow();
            if (selectedRow != -1) {
                int id = (int) tableUsers.getValueAt(selectedRow, 0);
                Usuario user = userDAO.getById(id);
                if (user.getId() == 0) {
                    JOptionPane.showMessageDialog(null, "No se encontró el usuario.", "Validación", JOptionPane.WARNING_MESSAGE);
                    return null;
                }
                return user;
            } else {
                JOptionPane.showMessageDialog(null, "Selecciona una fila.", "Validación", JOptionPane.WARNING_MESSAGE);
                return null;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
}

