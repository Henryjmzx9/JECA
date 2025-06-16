package Interface;

import dominio.Reserva;
import persistencia.ReservaDAO;
import utils.CBOption;
import utils.CUD;
import utils.EstadoReserva;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;

public class ReservaReadingForm extends JDialog {
    private JPanel mainPanel;
    private JButton btnCrear;
    private JButton btnEliminar;
    private JComboBox<CBOption> cbEstado;
    private JButton btnModificar;
    private JTable tableReserva;

    private ReservaDAO reservaDAO;
    private DefaultTableModel tableModel;

    public ReservaReadingForm(Window parent) {
        super(parent, "Gestión de Reservas", ModalityType.APPLICATION_MODAL);

        reservaDAO = new ReservaDAO();

        initComponents();

        setContentPane(mainPanel);
        setSize(900, 450);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        btnCrear.addActionListener(e -> openReservaForm(CUD.CREATE, null));
        btnModificar.addActionListener(e -> updateSelectedReserva());
        btnEliminar.addActionListener(e -> deleteSelectedReserva());

        cbEstado.addActionListener(e -> loadTableData());

        loadTableData();
    }

    private void initComponents() {
        mainPanel = new JPanel(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Filtrar por estado:"));

        cbEstado = new JComboBox<>();
        cbEstado.addItem(new CBOption("Todos", "ALL"));
        cbEstado.addItem(new CBOption("Pendiente", EstadoReserva.PENDIENTE));
        cbEstado.addItem(new CBOption("Confirmada", EstadoReserva.CONFIRMADA));
        cbEstado.addItem(new CBOption("Cancelada", EstadoReserva.CANCELADA));

        topPanel.add(cbEstado);

        btnCrear = new JButton("Crear");
        topPanel.add(btnCrear);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
                new String[]{"ID", "Cliente", "Paquete", "Estado", "Fecha Reserva"},
                0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableReserva = new JTable(tableModel);
        tableReserva.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(tableReserva);
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
            CBOption selectedEstado = (CBOption) cbEstado.getSelectedItem();
            ArrayList<Reserva> reservas;

            if (selectedEstado != null && !"ALL".equals(selectedEstado.getValue())) {
                reservas = reservaDAO.searchByEstado((EstadoReserva) selectedEstado.getValue());
            } else {
                reservas = reservaDAO.getAll();
            }

            tableModel.setRowCount(0);
            for (Reserva r : reservas) {
                String cliente = (r.getCliente() != null && r.getCliente().getNombre() != null)
                        ? r.getCliente().getNombre()
                        : "N/A";
                String paquete = (r.getPaquete() != null && r.getPaquete().getNombre() != null)
                        ? r.getPaquete().getNombre()
                        : "N/A";

                tableModel.addRow(new Object[]{
                        r.getReservaId(),
                        cliente,
                        paquete,
                        r.getEstado().getValor(),
                        r.getFechaReserva()
                });
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar reservas: " + ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openReservaForm(CUD cud, Reserva reserva) {
        ReservaForm form = new ReservaForm();
        form.setVisible(true);
        loadTableData();
    }

    private Reserva getSelectedReserva() {
        int selectedRow = tableReserva.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una reserva de la tabla", "Aviso", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        int id = (int) tableModel.getValueAt(selectedRow, 0);
        try {
            return reservaDAO.getById(id);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al obtener reserva: " + ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private void updateSelectedReserva() {
        Reserva reserva = getSelectedReserva();
        if (reserva != null) {
            openReservaForm(CUD.UPDATE, reserva);
        }
    }

    private void deleteSelectedReserva() {
        Reserva reserva = getSelectedReserva();
        if (reserva != null) {
            int option = JOptionPane.showConfirmDialog(this,
                    "¿Está seguro de eliminar la reserva ID '" + reserva.getReservaId() + "'?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                try {
                    if (reservaDAO.delete(reserva.getReservaId())) {
                        JOptionPane.showMessageDialog(this, "Reserva eliminada correctamente.");
                        loadTableData();
                    } else {
                        JOptionPane.showMessageDialog(this, "No se pudo eliminar la reserva.", "ERROR", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error al eliminar reserva: " + ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}