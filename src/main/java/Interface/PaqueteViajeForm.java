package Interface;

import dominio.Destino;
import dominio.Paquete;
import persistencia.DestinoDAO;
import persistencia.PaqueteDAO;
import utils.CBOption;
import utils.CUD;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class PaqueteViajeForm extends JDialog {
    private JPanel mainPanel;
    private JTextField txtNombre;
    private JTextField txtPrecio;
    private JSpinner spnDuracionDias;
    private JComboBox cbDestino;
    private JButton guardarButton;
    private JButton btnCancel;
    private JTextArea textArea1;

    private PaqueteDAO paqueteDAO;
    private DestinoDAO destinoDAO;
    private CUD cud;
    private Paquete en;

    public PaqueteViajeForm(Window parent, CUD cud, Paquete paquete) {
        super(parent);
        this.cud = cud;
        this.en = paquete;
        paqueteDAO = new PaqueteDAO();
        destinoDAO = new DestinoDAO();

        setContentPane(mainPanel);
        setModal(true);
        init();
        pack();
        setLocationRelativeTo(parent);

        btnCancel.addActionListener(s -> this.dispose());
        guardarButton.addActionListener(s -> ok());
    }

    private void init() {
        initCBDestino();

        switch (this.cud) {
            case CREATE:
                setTitle("Crear Paquete de Viaje");
                guardarButton.setText("Guardar");
                break;
            case UPDATE:
                setTitle("Modificar Paquete de Viaje");
                guardarButton.setText("Guardar");
                break;
            case DELETE:
                setTitle("Eliminar Paquete de Viaje");
                guardarButton.setText("Eliminar");
                break;
        }

        setValuesControls(this.en);
    }

    private void initCBDestino() {
        try {
            DefaultComboBoxModel<CBOption> model = new DefaultComboBoxModel<>();
            model.addElement(new CBOption("Seleccione un destino", 0));

            List<Destino> destinos = destinoDAO.search("");
            for (Destino d : destinos) {
                String nombreCompleto = d.getNombre() + " - " + d.getPais();
                model.addElement(new CBOption(nombreCompleto, d.getDestinoId()));
            }
            cbDestino.setModel(model);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error cargando destinos: " + ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setValuesControls(Paquete paquete) {
        txtNombre.setText(paquete.getNombre());
        txtPrecio.setText(String.valueOf(paquete.getPrecio()));
        spnDuracionDias.setValue(paquete.getDuracionDias());
        textArea1.setText(paquete.getDescripcion());

        for (int i = 0; i < cbDestino.getItemCount(); i++) {
            CBOption opt = (CBOption) cbDestino.getItemAt(i);
            if ((int) opt.getValue() == paquete.getDestinoId()) {
                cbDestino.setSelectedIndex(i);
                break;
            }
        }

        if (this.cud == CUD.CREATE) {
            spnDuracionDias.setValue(1);
        }

        if (this.cud == CUD.DELETE) {
            txtNombre.setEditable(false);
            txtPrecio.setEditable(false);
            spnDuracionDias.setEnabled(false);
            cbDestino.setEnabled(false);
            textArea1.setEditable(false);
        }
    }

    private boolean getValuesControls() {
        // Validación del combo de destinos
        CBOption selectedDestino = (CBOption) cbDestino.getSelectedItem();
        int destinoId = selectedDestino != null ? (int) selectedDestino.getValue() : 0;

        // Verificar si el destino no fue seleccionado
        if (destinoId == 0) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un destino.", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Verificar campos vacíos
        if (txtNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El campo 'Nombre' es obligatorio.", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (txtPrecio.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El campo 'Precio' es obligatorio.", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Validación del precio como valor numérico
        double precio;
        try {
            precio = Double.parseDouble(txtPrecio.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El precio debe ser un número válido.", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Asignar valores al objeto `Paquete`
        this.en.setNombre(txtNombre.getText().trim());
        this.en.setPrecio(precio);
        this.en.setDuracionDias((int) spnDuracionDias.getValue());
        this.en.setDestinoId(destinoId);
        this.en.setDescripcion(textArea1.getText().trim());

        // Fechas calculadas
        this.en.setFechaInicio(LocalDate.now());
        this.en.setFechaFin(LocalDate.now().plusDays(this.en.getDuracionDias()));

        return true;
    }

    private void ok() {
        try {
            if (getValuesControls()) {
                boolean r = false;
                switch (this.cud) {
                    case CREATE:
                        Paquete p = paqueteDAO.create(this.en);
                        if (p != null && p.getPaqueteId() > 0) r = true;
                        break;
                    case UPDATE:
                        r = paqueteDAO.update(this.en);
                        break;
                    case DELETE:
                        r = paqueteDAO.delete(this.en.getPaqueteId());
                        break;
                }

                if (r) {
                    JOptionPane.showMessageDialog(null, "Operación realizada correctamente", "Información", JOptionPane.INFORMATION_MESSAGE);
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "No se logró realizar la operación", "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }
}
