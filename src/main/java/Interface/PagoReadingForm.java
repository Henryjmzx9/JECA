package Interface;

import dominio.Pago;
import dominio.Reserva;
import persistencia.PagoDAO;
import persistencia.ReservaDAO;
import utils.CBOption;
import utils.CUD;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import java.sql.SQLException;
import java.util.ArrayList;

public class PagoReadingForm extends JDialog {
    private JPanel mainPanel;
    private JButton btnModificar;
    private JButton btnEliminar;
    private JTable tablePago;
    private JButton btnCrear;
    private JComboBox<CBOption> cbId;

    private PagoDAO pagoDAO;
    private ReservaDAO reservaDAO;
    private DefaultTableModel tableModel;

    public PagoReadingForm(Window parent) {
        super(parent, "Gestión de Pagos", ModalityType.APPLICATION_MODAL);

        pagoDAO = new PagoDAO();
        reservaDAO = new ReservaDAO();

        initComponents();

        setContentPane(mainPanel);
        setSize(850, 450);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        btnCrear.addActionListener(e -> openPagoForm(CUD.CREATE, null));
        btnModificar.addActionListener(e -> updateSelectedPago());
        btnEliminar.addActionListener(e -> deleteSelectedPago());
        cbId.addActionListener(e -> loadTableData());

        loadTableData();
    }

    private void initComponents() {
        mainPanel = new JPanel(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Filtrar por reserva:"));

        cbId = new JComboBox<>();
        cbId.addItem(new CBOption("Todas", 0));

        try {
            ArrayList<Reserva> reservas = reservaDAO.getAll();
            for (Reserva r : reservas) {
                String label = "Reserva #" + r.getReservaId();
                cbId.addItem(new CBOption(label, r.getReservaId()));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error cargando reservas: " + ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }

        topPanel.add(cbId);

        btnCrear = new JButton("Crear");
        topPanel.add(btnCrear);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
                new String[]{"ID", "Reserva ID", "Método Pago", "Monto", "Fecha Pago"},
                0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablePago = new JTable(tableModel);
        tablePago.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(tablePago);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnModificar = new JButton("Modificar");
        btnEliminar = new JButton("Eliminar");
        bottomPanel.add(btnModificar);
        bottomPanel.add(btnEliminar);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadTableData() {
        try {
            CBOption selectedReserva = (CBOption) cbId.getSelectedItem();
            ArrayList<Pago> pagos;

            if (selectedReserva != null && (int) selectedReserva.getValue() != 0) {
                pagos = pagoDAO.getByReservaId((int) selectedReserva.getValue());
            } else {
                pagos = pagoDAO.getAll();
            }

            tableModel.setRowCount(0);
            for (Pago p : pagos) {
                tableModel.addRow(new Object[]{
                        p.getPagoId(),
                        p.getReserva().getReservaId(),
                        p.getMetodoPago().getNombreMetodo(),
                        p.getMonto(),
                        p.getFechaPago()
                });
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar pagos: " + ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void openPagoForm(CUD cud, Pago pago) {
        new PagoForm(this, cud, pago != null ? pago : new Pago());
        loadTableData();
    }

    private Pago getSelectedPago() {
        int selectedRow = tablePago.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un pago de la tabla", "Aviso", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        int id = (int) tableModel.getValueAt(selectedRow, 0);
        try {
            return pagoDAO.getById(id);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al obtener pago: " + ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private void updateSelectedPago() {
        Pago pago = getSelectedPago();
        if (pago != null) {
            openPagoForm(CUD.UPDATE, pago);
        }
    }

    private void deleteSelectedPago() {
        Pago pago = getSelectedPago();
        if (pago != null) {
            int option = JOptionPane.showConfirmDialog(this,
                    "¿Está seguro de eliminar el pago ID '" + pago.getPagoId() + "'?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                try {
                    if (pagoDAO.delete(pago.getPagoId())) {
                        JOptionPane.showMessageDialog(this, "Pago eliminado correctamente.");
                        loadTableData();
                    } else {
                        JOptionPane.showMessageDialog(this, "No se pudo eliminar el pago.", "ERROR", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error al eliminar pago: " + ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}