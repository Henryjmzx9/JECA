package Interface;

import dominio.Paquete;
import persistencia.DestinoDAO;
import persistencia.PaqueteDAO;
import utils.CUD;

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
    private DestinoDAO destinoDAO;
    private DefaultTableModel tableModel;

    public ReadingPaqueteViajeForm(Window parent) {
        super(parent, "Gestión de Paquetes de Viaje", ModalityType.MODELESS); // ← AQUÍ el cambio de modalidad

        paqueteDAO = new PaqueteDAO();
        destinoDAO = new DestinoDAO();

        initComponents();

        setContentPane(mainPanel);
        setSize(900, 450);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Eventos
        btnCreate.addActionListener(e -> openPaqueteForm(CUD.CREATE, null));
        btnUpdate.addActionListener(e -> updateSelectedPaquete());
        btnDelete.addActionListener(e -> deleteSelectedPaquete());
        textName.addActionListener(e -> loadTableData(textName.getText().trim()));

        loadTableData(""); // Cargar datos iniciales
    }

    private void initComponents() {
        mainPanel = new JPanel(new BorderLayout(10, 10));

        // Panel superior de búsqueda y botón crear
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Buscar:"));
        textName = new JTextField(20);
        topPanel.add(textName);
        btnCreate = new JButton("Crear");
        topPanel.add(btnCreate);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Tabla de paquetes
        tableModel = new DefaultTableModel(
                new String[]{"ID", "Nombre", "Descripción", "Precio", "Duración (días)", "Fecha Inicio", "Fecha Fin", "Destino"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
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
                String nombreDestino = "Sin destino";
                if (p.getDestinoId() != 0) {
                    nombreDestino = destinoDAO.getNombreById(p.getDestinoId());
                }

                tableModel.addRow(new Object[]{
                        p.getPaqueteId(),
                        p.getNombre(),
                        p.getDescripcion(),
                        p.getPrecio(),
                        p.getDuracionDias(),
                        p.getFechaInicio(),
                        p.getFechaFin(),
                        nombreDestino
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
        form.setVisible(true); // Esto sí funcionará ahora
        loadTableData(textName.getText().trim()); // Recargar la tabla al cerrar el formulario
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