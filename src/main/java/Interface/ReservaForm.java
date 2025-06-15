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

    private JComboBox<CBOption> cbClientes;
    private JComboBox<CBOption> cbPaquetes;
    private JComboBox<CBOption> cbEstado;

    private ClienteDAO clienteDAO;
    private UsuarioDAO usuarioDAO;
    private PaqueteDAO paqueteDAO;

    private Reserva reserva;

    public ReservaForm() {
        clienteDAO = new ClienteDAO();
        usuarioDAO = new UsuarioDAO();
        paqueteDAO = new PaqueteDAO();
        reserva = new Reserva();

        cbClientes = new JComboBox<>();
        cbPaquetes = new JComboBox<>();
        cbEstado = new JComboBox<>();

        initCBClientes();
        initCBPaquetes();
        initCBEstado();
    }

    private void initCBClientes() {
        try {
            DefaultComboBoxModel<CBOption> model = new DefaultComboBoxModel<>();
            model.addElement(new CBOption("Seleccione un cliente", 0));

            List<Cliente> clientes = clienteDAO.searchCliente("");
            for (Cliente c : clientes) {
                Usuario u = usuarioDAO.getById(c.getUserId());
                String nombre = u.getNombre();
                model.addElement(new CBOption(nombre, c.getClienteId()));
            }
            cbClientes.setModel(model);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error cargando clientes: " + ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initCBPaquetes() {
        try {
            DefaultComboBoxModel<CBOption> model = new DefaultComboBoxModel<>();
            model.addElement(new CBOption("Seleccione un paquete", 0));

            List<Paquete> paquetes = paqueteDAO.search("");
            for (Paquete p : paquetes) {
                model.addElement(new CBOption(p.getNombre(), p.getPaqueteId()));
            }
            cbPaquetes.setModel(model);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error cargando paquetes: " + ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initCBEstado() {
        try {
            DefaultComboBoxModel<CBOption> model = new DefaultComboBoxModel<>();
            model.addElement(new CBOption("Seleccione un estado", null));
            model.addElement(new CBOption("Pendiente", EstadoReserva.PENDIENTE));
            model.addElement(new CBOption("Confirmada", EstadoReserva.CONFIRMADA));
            model.addElement(new CBOption("Cancelada", EstadoReserva.CANCELADA));
            cbEstado.setModel(model);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error cargando estados: " + ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
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
            for (int i = 0; i < cbEstado.getItemCount(); i++) {
                CBOption opt = cbEstado.getItemAt(i);
                if (opt.getValue() == reserva.getEstado()) {
                    cbEstado.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private boolean getValuesControls() {
        CBOption selectedCliente = (CBOption) cbClientes.getSelectedItem();
        CBOption selectedPaquete = (CBOption) cbPaquetes.getSelectedItem();
        CBOption selectedEstado = (CBOption) cbEstado.getSelectedItem();

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

    // método para acceder al JComboBox de clientes
    public JComboBox<CBOption> getCbClientes() {
        return cbClientes;
    }

    public JComboBox<CBOption> getCbPaquetes() {
        return cbPaquetes;
    }

    public JComboBox<CBOption> getCbEstado() {
        return cbEstado;
    }
}
