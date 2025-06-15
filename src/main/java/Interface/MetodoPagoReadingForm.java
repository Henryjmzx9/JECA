package Interface;

import dominio.MetodoPago;
import persistencia.MetodoPagoDAO;
import utils.CUD;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class MetodoPagoReadingForm extends JDialog {
    private JPanel mainPanel;
    private JTextField txtNombre;
    private JButton btnCrear;
    private JTable tableMetodoPago;
    private JButton btnModificar;
    private JButton btnEliminar;

    private MetodoPagoDAO metodoPagoDAO;
    private MainForm mainForm;

    public MetodoPagoReadingForm(MainForm mainForm) {
        this.mainForm = mainForm;
        metodoPagoDAO = new MetodoPagoDAO();

        setContentPane(mainPanel);
        setModal(true);
        setTitle("Gestión de Métodos de Pago");
        pack();
        setLocationRelativeTo(mainForm);

        txtNombre.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (!txtNombre.getText().trim().isEmpty()) {
                    search(txtNombre.getText());
                } else {
                    tableMetodoPago.setModel(new DefaultTableModel());
                }
            }
        });

        btnCrear.addActionListener(e -> {
            MetodoPagoForm form = new MetodoPagoForm(this.mainForm, CUD.CREATE, new MetodoPago());
            form.setVisible(true);
            tableMetodoPago.setModel(new DefaultTableModel());
        });

        btnModificar.addActionListener(e -> {
            MetodoPago metodo = getMetodoPagoFromTableRow();
            if (metodo != null) {
                MetodoPagoForm form = new MetodoPagoForm(this.mainForm, CUD.UPDATE, metodo);
                form.setVisible(true);
                tableMetodoPago.setModel(new DefaultTableModel());
            }
        });

        btnEliminar.addActionListener(e -> {
            MetodoPago metodo = getMetodoPagoFromTableRow();
            if (metodo != null) {
                MetodoPagoForm form = new MetodoPagoForm(this.mainForm, CUD.DELETE, metodo);
                form.setVisible(true);
                tableMetodoPago.setModel(new DefaultTableModel());
            }
        });
    }

    private void search(String query) {
        try {
            ArrayList<MetodoPago> metodos = metodoPagoDAO.search(query);
            createTable(metodos);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void createTable(ArrayList<MetodoPago> metodos) {
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        model.addColumn("ID");
        model.addColumn("Nombre Método de Pago");

        Object[] row = new Object[2];
        for (MetodoPago metodo : metodos) {
            row[0] = metodo.getMetodoPagoId();
            row[1] = metodo.getNombreMetodo();
            model.addRow(row);
        }

        tableMetodoPago.setModel(model);
        hideCol(0);
    }

    private void hideCol(int col) {
        tableMetodoPago.getColumnModel().getColumn(col).setMaxWidth(0);
        tableMetodoPago.getColumnModel().getColumn(col).setMinWidth(0);
        tableMetodoPago.getTableHeader().getColumnModel().getColumn(col).setMaxWidth(0);
        tableMetodoPago.getTableHeader().getColumnModel().getColumn(col).setMinWidth(0);
    }

    private MetodoPago getMetodoPagoFromTableRow() {
        try {
            int selectedRow = tableMetodoPago.getSelectedRow();
            if (selectedRow != -1) {
                int id = (int) tableMetodoPago.getValueAt(selectedRow, 0);
                MetodoPago metodo = metodoPagoDAO.getById(id);
                if (metodo == null || metodo.getMetodoPagoId() == 0) {
                    JOptionPane.showMessageDialog(null, "No se encontró el método de pago.", "Validación", JOptionPane.WARNING_MESSAGE);
                    return null;
                }
                return metodo;
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