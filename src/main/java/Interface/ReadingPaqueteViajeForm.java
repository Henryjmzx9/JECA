package Interface;

import dominio.Paquete;
import persistencia.PaqueteDAO;
import utils.CUD;
import utils.CBOption;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;

public class ReadingPaqueteViajeForm extends JDialog {
    private JPanel mainPanel;
    private JTextField textName;
    private JButton btnCreate;
    private JTable tablepaquetes;
    private JButton btnUpdate;
    private JButton btnDelete;

    private PaqueteDAO paqueteDAO;
    private DefaultTableModel tableModel;

    public ReadingPaqueteViajeForm(Window parent) {
        super(parent, "Gestión de Paquetes de Viaje", ModalityType.APPLICATION_MODAL);

        paqueteDAO = new PaqueteDAO();

        initComponents();

        setContentPane(mainPanel);
        setSize(800, 400);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        btnCreate.addActionListener(e -> openPaqueteForm(CUD.CREATE, null));
        btnUpdate.addActionListener(e -> updateSelectedPaquete());
        btnDelete.addActionListener(e -> deleteSelectedPaquete());

        textName.addActionListener(e -> loadTableData(textName.getText().trim()));

        loadTableData("");
    }

    private void initComponents() {
        mainPanel = new JPanel(new BorderLayout(10, 10));

        // Panel superior con filtro y botón crear
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Buscar:"));
        textName = new JTextField(20);
        topPanel.add(textName);
        btnCreate = new JButton("Crear");
        topPanel.add(btnCreate);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Tabla con paquetes
        tableModel = new DefaultTableModel(
                new String[]{"ID", "Nombre", "Descripción", "Precio", "Duración (días)", "Fecha Inicio", "Fecha Fin", "Destino ID"},
                0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabla no editable
            }
        };
        tablepaquetes = new JTable(tableModel);
        tablepaquetes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(tablepaquetes);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Panel inferior con botones actualizar y eliminar
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnUpdate = new JButton("Actualizar");
        btnDelete = new JButton("Eliminar");
        bottomPanel.add(btnUpdate);
        bottomPanel.add(btnDelete);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadTableData(String filtro) {
        try {
            ArrayList<Paquete> paquetes = paqueteDAO.searchPaquete(filtro);
            tableModel.setRowCount(0);
            for (Paquete p : paquetes) {
                tableModel.addRow(new Object[]{
                        p.getPaqueteId(),
                        p.getNombre(),
                        p.getDescripcion(),
                        p.getPrecio(),
                        p.getDuracionDias(),
                        p.getFechaInicio(),
                        p.getFechaFin(),
                        p.getDestinoId()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar paquetes: " + ex.getMessage(),
                    "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openPaqueteForm(CUD cud, Paquete paquete) {
        PaqueteViajeForm form = new PaqueteViajeForm(this, cud, paquete != null ? paquete : new Paquete());
        form.setVisible(true);
        // Recargar tabla al cerrar el form
        loadTableData(textName.getText().trim());
    }

    private Paquete getSelectedPaquete() {
        int selectedRow = tablepaquetes.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un paquete de la tabla", "Aviso", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        int id = (int) tableModel.getValueAt(selectedRow, 0);
        try {
            return paqueteDAO.getById(id);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al obtener paquete: " + ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private void updateSelectedPaquete() {
        Paquete paquete = getSelectedPaquete();
        if (paquete != null) {
            openPaqueteForm(CUD.UPDATE, paquete);
        }
    }

    private void deleteSelectedPaquete() {
        Paquete paquete = getSelectedPaquete();
        if (paquete != null) {
            int option = JOptionPane.showConfirmDialog(this,
                    "¿Está seguro de eliminar el paquete '" + paquete.getNombre() + "'?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                try {
                    if (paqueteDAO.delete(paquete.getPaqueteId())) {
                        JOptionPane.showMessageDialog(this, "Paquete eliminado correctamente.");
                        loadTableData(textName.getText().trim());
                    } else {
                        JOptionPane.showMessageDialog(this, "No se pudo eliminar el paquete.", "ERROR", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error al eliminar paquete: " + ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}