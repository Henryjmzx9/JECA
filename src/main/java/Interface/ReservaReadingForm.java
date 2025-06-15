package Interface;

import dominio.Cliente;
import dominio.Paquete;
import dominio.Reserva;
import persistencia.ClienteDAO;
import persistencia.PaqueteDAO;
import persistencia.ReservaDAO;
import utils.CBOption;
import utils.CUD;

import javax.swing.*;
import java.awt.*;

public class ReservaReadingForm extends JDialog {
    private JPanel mainPanel;

    private JButton btnCrear;
    private JButton btnEditar;
    private JButton btnEliminar;
    private JTextField txtCliente;
    private JTextField txtPaquete;
    private JTextField txtReserva;
    private JTable table1;

    private ClienteDAO clienteDAO;
    private PaqueteDAO paqueteDAO;
    private ReservaDAO reservaDAO;
    private CUD cud;
    private Reserva reserva;

    public ReservaReadingForm(Window parent, CUD cud, Reserva reserva) {
        super(parent);
        this.cud = cud;
        this.reserva = reserva;
        this.clienteDAO = new ClienteDAO();
        this.paqueteDAO = new PaqueteDAO();
        this.reservaDAO = new ReservaDAO();

        setContentPane(mainPanel);
        setModal(true);
        setTitle("Formulario de Reserva");
        initTextFields();
        setValuesControls(reserva);
        pack();
        setLocationRelativeTo(parent);

        btnCrear.addActionListener(e -> guardar(CUD.CREATE));
        btnEditar.addActionListener(e -> guardar(CUD.UPDATE));
        btnEliminar.addActionListener(e -> guardar(CUD.DELETE));
    }

    private void initTextFields() {
        try {
            StringBuilder clientesText = new StringBuilder();
            for (Cliente c : clienteDAO.getAll()) {
                clientesText.append(c.getNombreCompleto()).append(" (ID: ").append(c.getClienteId()).append("), ");
            }
            txtCliente.setText(clientesText.toString());

            StringBuilder paquetesText = new StringBuilder();
            for (Paquete p : paqueteDAO.getAll()) {
                paquetesText.append(p.getNombre()).append(" (ID: ").append(p.getPaqueteId()).append("), ");
            }
            txtPaquete.setText(paquetesText.toString());

            txtReserva.setText("Pendiente, Confirmada, Cancelada");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error cargando datos: " + ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setValuesControls(Reserva r) {
        if (r == null) return;

        try {
            Cliente cliente = clienteDAO.getById(r.getClienteId());
            Paquete paquete = paqueteDAO.getById(r.getPaqueteId());

            if (cliente != null) {
                txtCliente.setText(cliente.getNombreCompleto() + " (ID: " + cliente.getClienteId() + ")");
            }

            if (paquete != null) {
                txtPaquete.setText(paquete.getNombre() + " (ID: " + paquete.getPaqueteId() + ")");
            }

            txtReserva.setText(r.getEstado() != null ? r.getEstado().toString() : "");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error asignando valores: " + ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void guardar(CUD accion) {
        try {
            int clienteId = extraerIdDesdeTexto(txtCliente.getText());
            int paqueteId = extraerIdDesdeTexto(txtPaquete.getText());
            String estado = txtReserva.getText().trim();

            if (reserva == null) reserva = new Reserva();
            reserva.setClienteId(clienteId);
            reserva.setPaqueteId(paqueteId);
            reserva.setEstado(estado);

            boolean ok = false;
            switch (accion) {
                case CREATE:
                    ok = reservaDAO.create(reserva) != null;
                    break;
                case UPDATE:
                    ok = reservaDAO.update(reserva);
                    break;
                case DELETE:
                    ok = reservaDAO.delete(reserva.getReservaId());
                    break;
            }

            if (ok) {
                JOptionPane.showMessageDialog(this, "Operación exitosa", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Error en la operación", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int extraerIdDesdeTexto(String texto) throws Exception {
        int start = texto.lastIndexOf("(ID: ");
        int end = texto.lastIndexOf(")");
        if (start != -1 && end != -1 && start < end) {
            String idStr = texto.substring(start + 5, end).trim();
            return Integer.parseInt(idStr);
        } else {
            throw new Exception("No se pudo extraer el ID del campo: " + texto);
        }
    }
}
