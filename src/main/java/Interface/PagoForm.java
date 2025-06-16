package Interface;

import dominio.Paquete;
import dominio.Reserva;
import persistencia.MetodoPagoDAO;
import persistencia.PaqueteDAO;
import persistencia.ReservaDAO;
import utils.CBOption;

import javax.swing.*;
import java.util.List;

public class PagoForm extends JFrame {
    private JPanel mainPanel;
    private JComboBox<CBOption> cbReserva;
    private JComboBox<CBOption> cbMPago;
    private JTextField txtMonto;
    private JButton btnGuardar;
    private JButton btnSalir;

    private ReservaDAO reservaDAO;
    private MetodoPagoDAO metodoPagoDAO;
    private PaqueteDAO paqueteDAO;

    public PagoForm() {
        setContentPane(mainPanel);
        setTitle("Registro de Pago");
        setSize(500, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        reservaDAO = new ReservaDAO();
        metodoPagoDAO = new MetodoPagoDAO();
        paqueteDAO = new PaqueteDAO();

        initCBReservas();
        initCBMetodoPago();
        initActions();

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

    private void initActions() {
        // Al cambiar la reserva, cargar automáticamente el precio del paquete
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

        // Botón Guardar
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

                // Crear pago
                persistencia.PagoDAO pagoDAO = new persistencia.PagoDAO();
                dominio.Pago pago = new dominio.Pago();
                pago.setReservaId((int) selectedReserva.getValue());
                pago.setMetodoPagoId((int) selectedMetodoPago.getValue());
                pago.setMonto(monto);
                pago.setFechaPago(new java.util.Date());

                pagoDAO.create(pago);

                JOptionPane.showMessageDialog(this, "Pago guardado correctamente.", "ÉXITO", JOptionPane.INFORMATION_MESSAGE);
                dispose();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Monto inválido.", "ERROR", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al guardar pago: " + ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        // Botón Salir
        btnSalir.addActionListener(e -> dispose());
    }
}