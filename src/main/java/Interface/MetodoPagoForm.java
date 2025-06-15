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
    private MetodoPago en;

    // Constructor correcto: recibe parent, cud y MetodoPago
    public MetodoPagoForm(JFrame parent, CUD cud, MetodoPago metodoPago) {
        this.cud = cud;
        this.en = metodoPago;
        metodoPagoDAO = new MetodoPagoDAO();

        setContentPane(metodoPagoPanel);
        setModal(true);
        init();
        pack();
        setLocationRelativeTo(parent); // ahora sí, porque parent viene como parámetro

        btnCancel.addActionListener(e -> this.dispose());
        btnOk.addActionListener(e -> ok());
    }

    private void init() {
        switch (this.cud) {
            case CREATE:
                setTitle("Agregar Método de Pago");
                btnOk.setText("Guardar");
                break;
            default:
                // Solo CREATE permitido en este formulario
                break;
        }

        setValuesControls(this.en);
    }

    private void setValuesControls(MetodoPago metodoPago) {
        txtNombreMetodo.setText("");
    }

    private boolean getValuesControls() {
        if (txtNombreMetodo.getText().trim().isEmpty()) {
            return false;
        }
        this.en.setNombreMetodo(txtNombreMetodo.getText().trim());
        return true;
    }

    private void ok() {
        try {
            boolean res = getValuesControls();

            if (res) {
                boolean r = false;

                if (this.cud == CUD.CREATE) {
                    MetodoPago mp = metodoPagoDAO.create(this.en);
                    if (mp.getMetodoPagoId() > 0) {
                        r = true;
                    }
                }

                if (r) {
                    JOptionPane.showMessageDialog(null,
                            "Método de pago registrado correctamente",
                            "Información", JOptionPane.INFORMATION_MESSAGE);
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(null,
                            "No se logró realizar la operación",
                            "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null,
                        "El campo Nombre Método es obligatorio",
                        "Validación", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }
}
