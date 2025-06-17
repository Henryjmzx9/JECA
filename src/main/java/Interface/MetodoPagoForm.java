package Interface;

import dominio.MetodoPago;
import persistencia.MetodoPagoDAO;
import utils.CUD;

import javax.swing.*;

public class MetodoPagoForm extends JDialog {
    private JPanel metodoPagoPanel;
    private JTextField txtNombreMetodo;
    private JButton btnOk;
    private JButton btnCancel;

    private MetodoPagoDAO metodoPagoDAO;
    private CUD cud;
    private MetodoPago metodoPago;

    public MetodoPagoForm(JFrame parent, CUD cud, MetodoPago metodoPago) {
        this.cud = cud;
        this.metodoPago = metodoPago != null ? metodoPago : new MetodoPago();
        this.metodoPagoDAO = new MetodoPagoDAO();

        setContentPane(metodoPagoPanel);
        setModal(true);
        init();
        pack();
        setLocationRelativeTo(parent);

        btnCancel.addActionListener(e -> dispose());
        btnOk.addActionListener(e -> ok());
    }

    private void init() {
        switch (cud) {
            case CREATE:
                setTitle("Agregar Método de Pago");
                btnOk.setText("Guardar");
                break;
            case UPDATE:
                setTitle("Modificar Método de Pago");
                btnOk.setText("Actualizar");
                break;
            case DELETE:
                setTitle("Eliminar Método de Pago");
                btnOk.setText("Eliminar");
                txtNombreMetodo.setEnabled(false); // deshabilita edición
                break;
        }

        setValuesControls(metodoPago);
    }

    private void setValuesControls(MetodoPago metodoPago) {
        if (metodoPago != null) {
            txtNombreMetodo.setText(metodoPago.getNombreMetodo());
        } else {
            txtNombreMetodo.setText("");
        }
    }

    private boolean getValuesControls() {
        if (cud == CUD.DELETE) return true; // no se necesita validar campos para eliminar

        String nombre = txtNombreMetodo.getText().trim();
        if (nombre.isEmpty()) {
            return false;
        }
        // Verificar campos vacíos
        if (txtNombreMetodo.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El campo 'Nombre' es obligatorio.", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        metodoPago.setNombreMetodo(nombre);
        return true;
    }

    private void ok() {
        try {
            boolean valoresCorrectos = getValuesControls();
            if (!valoresCorrectos) {
                JOptionPane.showMessageDialog(this,
                        "El campo Nombre Método es obligatorio.",
                        "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            boolean operacionExitosa = false;

            switch (cud) {
                case CREATE:
                    MetodoPago creado = metodoPagoDAO.create(metodoPago);
                    operacionExitosa = creado != null && creado.getMetodoPagoId() > 0;
                    break;
                case UPDATE:
                    operacionExitosa = metodoPagoDAO.update(metodoPago);
                    break;
                case DELETE:
                    operacionExitosa = metodoPagoDAO.delete(metodoPago.getMetodoPagoId());
                    break;
            }

            if (operacionExitosa) {
                JOptionPane.showMessageDialog(this,
                        "Operación realizada exitosamente.",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "No se pudo completar la operación.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}