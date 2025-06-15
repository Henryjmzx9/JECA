package Interface;

import dominio.Cliente;
import dominio.Paquete;
import dominio.Reserva;
import dominio.Usuario;
import persistencia.ClienteDAO;
import persistencia.PaqueteDAO;
import persistencia.UsuarioDAO;
import utils.CBOption;
import utils.EstadoReserva;

import javax.swing.*;
import java.util.List;

public class ReservaForm extends JFrame {
    private JPanel mainPanel;
    private JComboBox<CBOption> cbClientes;
    private JComboBox<CBOption> cbPaquetes;
    private JComboBox<CBOption> cbStatus;
    private JButton btnGuardar;
    private JButton btnSalir;

    private ClienteDAO clienteDAO;
    private UsuarioDAO usuarioDAO;
    private PaqueteDAO paqueteDAO;
    private Reserva reserva;

    public ReservaForm() {
        setContentPane(mainPanel);
        setTitle("Gestión de Reservas");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        clienteDAO = new ClienteDAO();
        usuarioDAO = new UsuarioDAO();
        paqueteDAO = new PaqueteDAO();
        reserva = new Reserva();

        initCBClientes();
        initCBPaquetes();
        initCBEstado();

        setVisible(true);
    }

    private void initCBClientes() {
        try {
            DefaultComboBoxModel<CBOption> model = new DefaultComboBoxModel<>();
            model.addElement(new CBOption("Seleccione un cliente", 0));

            List<Cliente> clientes = clienteDAO.searchCliente("");
            for (Cliente c : clientes) {
                Usuario u = usuarioDAO.getById(c.getUserId());
                if (u != null) { // Validación para evitar null
                    model.addElement(new CBOption(u.getName(), c.getClienteId()));
                }
            }
            cbClientes.setModel(model);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error cargando clientes: " + ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initCBPaquetes() {
        try {
            DefaultComboBoxModel<CBOption> model = new DefaultComboBoxModel<>();
            model.addElement(new CBOption("Seleccione un paquete", 0));

            // Corrección: Se agregó un segundo argumento en la llamada a search()
            List<Paquete> paquetes = paqueteDAO.search("", ""); // Ahora recibe dos argumentos
            for (Paquete p : paquetes) {
                model.addElement(new CBOption(p.getNombre(), p.getPaqueteId()));
            }
            cbPaquetes.setModel(model);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error cargando paquetes: " + ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initCBEstado() {
        try {
            DefaultComboBoxModel<CBOption> model = new DefaultComboBoxModel<>();
            model.addElement(new CBOption("Seleccione un estado", "N/A")); // Evitamos null
            model.addElement(new CBOption("Pendiente", EstadoReserva.PENDIENTE));
            model.addElement(new CBOption("Confirmada", EstadoReserva.CONFIRMADA));
            model.addElement(new CBOption("Cancelada", EstadoReserva.CANCELADA));
            cbStatus.setModel(model);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error cargando estados: " + ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean getValuesControls() {
        CBOption selectedCliente = (CBOption) cbClientes.getSelectedItem();
        CBOption selectedPaquete = (CBOption) cbPaquetes.getSelectedItem();
        CBOption selectedEstado = (CBOption) cbStatus.getSelectedItem();

        if (selectedCliente == null || (int) selectedCliente.getValue() == 0 ||
                selectedPaquete == null || (int) selectedPaquete.getValue() == 0 ||
                selectedEstado == null || selectedEstado.getValue() == null) {
            return false;
        }

        Cliente cliente = new Cliente((int) selectedCliente.getValue());
        Paquete paquete = new Paquete((int) selectedPaquete.getValue());
        EstadoReserva estado = (EstadoReserva) selectedEstado.getValue();

        reserva.setCliente(cliente);
        reserva.setPaquete(paquete);
        reserva.setEstado(estado);

        return true;
    }

    private void setValuesControls() {
        if (reserva.getCliente() != null) {
            for (int i = 0; i < cbClientes.getItemCount(); i++) {
                CBOption opt = cbClientes.getItemAt(i);
                if ((int) opt.getValue() == reserva.getCliente().getClienteId()) {
                    cbClientes.setSelectedIndex(i);
                    break;
                }
            }
        }

        if (reserva.getPaquete() != null) {
            for (int i = 0; i < cbPaquetes.getItemCount(); i++) {
                CBOption opt = cbPaquetes.getItemAt(i);
                if ((int) opt.getValue() == reserva.getPaquete().getPaqueteId()) {
                    cbPaquetes.setSelectedIndex(i);
                    break;
                }
            }
        }

        if (reserva.getEstado() != null) {
            for (int i = 0; i < cbStatus.getItemCount(); i++) {
                CBOption opt = cbStatus.getItemAt(i);
                if (opt.getValue() == reserva.getEstado()) {
                    cbStatus.setSelectedIndex(i);
                    break;
                }
            }
        }
    }
}
