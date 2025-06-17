package Interface;

import dominio.Cliente;
import dominio.Paquete;
import dominio.Reserva;
import dominio.Usuario;
import persistencia.ClienteDAO;
import persistencia.PaqueteDAO;
import persistencia.ReservaDAO;
import persistencia.UsuarioDAO;
import utils.CBOption;
import utils.CUD;
import utils.EstadoReserva;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ReservaForm extends JDialog {
    private JPanel mainPanel;
    private JComboBox<CBOption> cbClientes;
    private JComboBox<CBOption> cbPaquetes;
    private JComboBox<CBOption> cbStatus;
    private JButton btnGuardar;
    private JButton btnSalir;

    private ClienteDAO clienteDAO;
    private UsuarioDAO usuarioDAO;
    private PaqueteDAO paqueteDAO;
    private ReservaDAO reservaDAO;
    private Reserva reserva;
    private CUD modo;

    public ReservaForm(Window parent, CUD cud, Reserva reserva) {
        super(parent, "Formulario de Reserva", ModalityType.APPLICATION_MODAL);

        this.reserva = reserva;
        this.modo = cud;

        clienteDAO = new ClienteDAO();
        usuarioDAO = new UsuarioDAO();
        paqueteDAO = new PaqueteDAO();
        reservaDAO = new ReservaDAO();

        setContentPane(mainPanel);
        setSize(500, 400);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initCBClientes();
        initCBPaquetes();
        initCBEstado();
        setValuesControls();
        initActions();

        setVisible(true);
    }

    private void initCBClientes() {
        try {
            DefaultComboBoxModel<CBOption> model = new DefaultComboBoxModel<>();
            model.addElement(new CBOption("Seleccione un cliente", 0));

            List<Cliente> clientes = clienteDAO.searchCliente("");
            for (Cliente c : clientes) {
                Usuario u = usuarioDAO.getById(c.getUserId());
                if (u != null) {
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

            List<Paquete> paquetes = paqueteDAO.search("", "");
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
            model.addElement(new CBOption("Seleccione un estado", "N/A"));
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

        // Validar cliente seleccionado
        if (selectedCliente == null || (int) selectedCliente.getValue() == 0) {
            JOptionPane.showMessageDialog(this, "Debes seleccionar un cliente.", "Validación", JOptionPane.WARNING_MESSAGE);
            cbClientes.requestFocus();
            return false;
        }

        // Validar paquete seleccionado
        if (selectedPaquete == null || (int) selectedPaquete.getValue() == 0) {
            JOptionPane.showMessageDialog(this, "Debes seleccionar un paquete.", "Validación", JOptionPane.WARNING_MESSAGE);
            cbPaquetes.requestFocus();
            return false;
        }

        // Validar estado seleccionado
        if (selectedEstado == null || selectedEstado.getValue() == null || "N/A".equals(selectedEstado.getValue())) {
            JOptionPane.showMessageDialog(this, "Debes seleccionar un estado.", "Validación", JOptionPane.WARNING_MESSAGE);
            cbStatus.requestFocus();
            return false;
        }

        // Lógica original para asignar los valores
        Cliente cliente = new Cliente((int) selectedCliente.getValue());
        Paquete paquete = new Paquete((int) selectedPaquete.getValue());
        EstadoReserva estado = (EstadoReserva) selectedEstado.getValue();

        reserva.setCliente(cliente);
        reserva.setPaquete(paquete);
        reserva.setEstado(estado);
        reserva.setFechaReserva(java.time.LocalDate.now());

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

    private void initActions() {
        btnGuardar.addActionListener(e -> {
            if (getValuesControls()) {
                try {
                    if (modo == CUD.CREATE) {
                        reservaDAO.create(reserva);
                    } else if (modo == CUD.UPDATE) {
                        reservaDAO.update(reserva);
                    }

                    JOptionPane.showMessageDialog(this, "Reserva guardada correctamente.");
                    dispose(); // Cierra el formulario

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error guardando reserva: " + ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnSalir.addActionListener(e -> dispose());
    }
}