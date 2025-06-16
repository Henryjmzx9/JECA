package Interface;

import dominio.Paquete;
import dominio.Pago;
import dominio.Reserva;
import dominio.MetodoPago;
import persistencia.MetodoPagoDAO;
import persistencia.PaqueteDAO;
import persistencia.PagoDAO;
import persistencia.ReservaDAO;
import utils.CBOption;
import utils.CUD;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PagoForm extends JDialog {
    private JPanel mainPanel;
    private JComboBox<CBOption> cbReserva;
    private JComboBox<CBOption> cbMPago;
    private JTextField txtMonto;
    private JButton btnGuardar;
    private JButton btnSalir;

    private ReservaDAO reservaDAO;
    private MetodoPagoDAO metodoPagoDAO;
    private PaqueteDAO paqueteDAO;
    private PagoDAO pagoDAO;

    private CUD cud;
    private Pago pago;

    public PagoForm(Window parent, CUD cud, Pago pago) {
        super(parent, "Formulario de Pago", ModalityType.APPLICATION_MODAL);
        this.cud = cud;
        this.pago = pago;

        setContentPane(mainPanel);
        setSize(500, 300);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        reservaDAO = new ReservaDAO();
        metodoPagoDAO = new MetodoPagoDAO();
        paqueteDAO = new PaqueteDAO();
        pagoDAO = new PagoDAO();

        initCBReservas();
        initCBMetodoPago();
        initActions();

        if (cud == CUD.UPDATE && pago != null) {
            loadPagoData(pago);
        }

        setVisible(true);
    }

    private void initCBReservas() {
        try {
            DefaultComboBoxModel<CBOption> model = new DefaultComboBoxModel<>();
            model.addElement(new CBOption("Seleccione una reserva", 0));

            List<Reserva> reservas = reservaDAO.getAll();
            for (Reserva r : reservas) {
                String display = "Reserva #" + r.getReservaId() +
                        " - " + r.getCliente().getNombre() +
                        " / " + r.getPaquete().getNombre();
                model.addElement(new CBOption(display, r.getReservaId()));
            }
            cbReserva.setModel(model);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error cargando reservas: " + ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initCBMetodoPago() {
        try {
            DefaultComboBoxModel<CBOption> model = new DefaultComboBoxModel<>();
            model.addElement(new CBOption("Seleccione un método de pago", 0));

            metodoPagoDAO.getAll().forEach(mp -> {
                model.addElement(new CBOption(mp.getNombreMetodo(), mp.getMetodoPagoId()));
            });
            cbMPago.setModel(model);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error cargando métodos de pago: " + ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadPagoData(Pago pago) {
        // Selecciona la reserva correspondiente
        for (int i = 0; i < cbReserva.getItemCount(); i++) {
            CBOption item = cbReserva.getItemAt(i);
            if ((int) item.getValue() == pago.getReserva().getReservaId()) {
                cbReserva.setSelectedIndex(i);
                break;
            }
        }

        // Selecciona el método de pago correspondiente
        for (int i = 0; i < cbMPago.getItemCount(); i++) {
            CBOption item = cbMPago.getItemAt(i);
            if ((int) item.getValue() == pago.getMetodoPago().getMetodoPagoId()) {
                cbMPago.setSelectedIndex(i);
                break;
            }
        }

        txtMonto.setText(String.valueOf(pago.getMonto()));
    }

    private void initActions() {
        cbReserva.addActionListener(e -> {
            CBOption selected = (CBOption) cbReserva.getSelectedItem();
            if (selected != null && (int) selected.getValue() != 0) {
                try {
                    int reservaId = (int) selected.getValue();
                    Reserva reserva = reservaDAO.getById(reservaId);
                    Paquete paquete = paqueteDAO.getById(reserva.getPaquete().getPaqueteId());
                    txtMonto.setText(String.valueOf(paquete.getPrecio()));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error obteniendo precio: " + ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                txtMonto.setText("");
            }
        });

        btnGuardar.addActionListener(e -> {
            try {
                CBOption selectedReserva = (CBOption) cbReserva.getSelectedItem();
                CBOption selectedMetodoPago = (CBOption) cbMPago.getSelectedItem();

                if (selectedReserva == null || (int) selectedReserva.getValue() == 0) {
                    JOptionPane.showMessageDialog(this, "Debe seleccionar una reserva.", "VALIDACIÓN", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (selectedMetodoPago == null || (int) selectedMetodoPago.getValue() == 0) {
                    JOptionPane.showMessageDialog(this, "Debe seleccionar un método de pago.", "VALIDACIÓN", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (txtMonto.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Debe ingresar un monto.", "VALIDACIÓN", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                double monto = Double.parseDouble(txtMonto.getText());
                if (monto <= 0) {
                    JOptionPane.showMessageDialog(this, "El monto debe ser mayor a 0.", "VALIDACIÓN", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (cud == CUD.CREATE) {
                    Pago nuevoPago = new Pago();
                    nuevoPago.setReservaId((int) selectedReserva.getValue());
                    nuevoPago.setMetodoPagoId((int) selectedMetodoPago.getValue());
                    nuevoPago.setMonto(monto);
                    nuevoPago.setFechaPago(new java.util.Date());
                    pagoDAO.create(nuevoPago);
                    JOptionPane.showMessageDialog(this, "Pago creado correctamente.");
                } else if (cud == CUD.UPDATE && pago != null) {
                    pago.setReservaId((int) selectedReserva.getValue());
                    pago.setMetodoPagoId((int) selectedMetodoPago.getValue());
                    pago.setMonto(monto);
                    pagoDAO.update(pago);
                    JOptionPane.showMessageDialog(this, "Pago actualizado correctamente.");
                }

                dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Monto inválido.", "ERROR", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al guardar pago: " + ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        btnSalir.addActionListener(e -> dispose());
    }
}