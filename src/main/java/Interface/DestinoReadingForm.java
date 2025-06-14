package Interface;

import persistencia.DestinoDAO;
import dominio.Destino;
import utils.CUD;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class DestinoReadingForm extends JDialog {
    private JPanel mainPanel;
    private JTextField txtName;
    private JButton btnCreate;
    private JTable tableDestinos;
    private JButton btnUpdate;
    private JButton btnDelete;

    private DestinoDAO destinoDAO;
    private MainForm mainForm;

    public DestinoReadingForm(MainForm mainForm) {
        this.mainForm = mainForm;
        destinoDAO = new DestinoDAO();

        setContentPane(mainPanel);
        setModal(true);
        setTitle("Buscar Destino");
        pack();
        setLocationRelativeTo(mainForm);

        txtName.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (!txtName.getText().trim().isEmpty()) {
                    search(txtName.getText());
                } else {
                    DefaultTableModel emptyModel = new DefaultTableModel();
                    tableDestinos.setModel(emptyModel);
                }
            }
        });

        btnCreate.addActionListener(s -> {
            FormularioDestinoGUI destinoForm = new FormularioDestinoGUI(this.mainForm, CUD.CREATE, new Destino());
            destinoForm.setVisible(true);
            DefaultTableModel emptyModel = new DefaultTableModel();
            tableDestinos.setModel(emptyModel);
        });

        btnUpdate.addActionListener(s -> {
            Destino destino = getDestinoFromTableRow();
            if (destino != null) {
                FormularioDestinoGUI destinoForm = new FormularioDestinoGUI(this.mainForm, CUD.UPDATE, destino);
                destinoForm.setVisible(true);
                DefaultTableModel emptyModel = new DefaultTableModel();
                tableDestinos.setModel(emptyModel);
            }
        });

        btnDelete.addActionListener(s -> {
            Destino destino = getDestinoFromTableRow();
            if (destino != null) {
                FormularioDestinoGUI destinoForm = new FormularioDestinoGUI(this.mainForm, CUD.DELETE, destino);
                destinoForm.setVisible(true);
                DefaultTableModel emptyModel = new DefaultTableModel();
                tableDestinos.setModel(emptyModel);
            }
        });
    }

    private void search(String query) {
        try {
            ArrayList<Destino> destinos = destinoDAO.search(query);
            createTable(destinos);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void createTable(ArrayList<Destino> destinos) {
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 4) return ImageIcon.class;
                return String.class;
            }
        };

        model.addColumn("Id");
        model.addColumn("Nombre");
        model.addColumn("País");
        model.addColumn("Descripción");
        model.addColumn("Imagen");

        this.tableDestinos.setModel(model);

        // Ajustar altura de las filas
        tableDestinos.setRowHeight(80);

        Object[] row = new Object[5];
        for (Destino destino : destinos) {
            row[0] = destino.getDestinoId();
            row[1] = destino.getNombre();
            row[2] = destino.getPais();
            row[3] = destino.getDescripcion();

            byte[] imagenBytes = destino.getImagen();
            if (imagenBytes != null) {
                ImageIcon icon = new ImageIcon(imagenBytes);
                Image imgEscalada = icon.getImage().getScaledInstance(120, 80, Image.SCALE_SMOOTH);
                row[4] = new ImageIcon(imgEscalada);
            } else {
                row[4] = null;
            }

            model.addRow(row.clone());
        }

        hideCol(0);

        // Renderizador para centrar la imagen
        tableDestinos.getColumnModel().getColumn(4).setCellRenderer(new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = new JLabel();
                if (value instanceof ImageIcon) {
                    label.setIcon((ImageIcon) value);
                }
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setVerticalAlignment(SwingConstants.CENTER);
                return label;
            }
        });

        // Ajustar ancho mínimo de columna de imagen
        tableDestinos.getColumnModel().getColumn(4).setMinWidth(130);
        tableDestinos.getColumnModel().getColumn(4).setMaxWidth(130);
    }

    private void hideCol(int col) {
        this.tableDestinos.getColumnModel().getColumn(col).setMaxWidth(0);
        this.tableDestinos.getColumnModel().getColumn(col).setMinWidth(0);
        this.tableDestinos.getTableHeader().getColumnModel().getColumn(col).setMaxWidth(0);
        this.tableDestinos.getTableHeader().getColumnModel().getColumn(col).setMinWidth(0);
    }

    private Destino getDestinoFromTableRow() {
        Destino destino = null;
        try {
            int filaSelect = this.tableDestinos.getSelectedRow();
            int id = 0;

            if (filaSelect != -1) {
                id = (int) this.tableDestinos.getValueAt(filaSelect, 0);
            } else {
                JOptionPane.showMessageDialog(null, "Selecciona una fila de la tabla.", "Validación", JOptionPane.WARNING_MESSAGE);
                return null;
            }

            destino = destinoDAO.getById(id);

            if (destino.getDestinoId() == 0) {
                JOptionPane.showMessageDialog(null, "No se encontró ningún destino.", "Validación", JOptionPane.WARNING_MESSAGE);
                return null;
            }

            return destino;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
}
