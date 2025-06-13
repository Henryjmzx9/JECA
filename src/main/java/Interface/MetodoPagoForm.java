package Interface;

import dominio.MetodoPago;
import persistencia.MetodoPagoDAO;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class MetodoPagoForm extends JFrame {
    private JPanel metodoPagoPanel;
    private JTextField nombreMetodoField;
    private JButton agregarButton;
    private JButton actualizarButton;
    private JButton eliminarButton;
    private JButton buscarButton;

    private MetodoPagoDAO dao;
    private MetodoPago metodoActual;

    public MetodoPagoForm() {
        // Inicializar panel raíz y componentes
        metodoPagoPanel = new JPanel(new BorderLayout(10, 10));
        nombreMetodoField = new JTextField(20);
        agregarButton = new JButton("Agregar");
        actualizarButton = new JButton("Actualizar");
        eliminarButton = new JButton("Eliminar");
        buscarButton = new JButton("Buscar");

        // Panel para botones
        JPanel botonesPanel = new JPanel();
        botonesPanel.setLayout(new FlowLayout());
        botonesPanel.add(agregarButton);
        botonesPanel.add(actualizarButton);
        botonesPanel.add(eliminarButton);
        botonesPanel.add(buscarButton);

        // Panel para campo de texto con etiqueta
        JPanel campoPanel = new JPanel(new FlowLayout());
        campoPanel.add(new JLabel("Nombre Método:"));
        campoPanel.add(nombreMetodoField);

        // Agregar componentes al panel raíz
        metodoPagoPanel.add(campoPanel, BorderLayout.NORTH);
        metodoPagoPanel.add(botonesPanel, BorderLayout.SOUTH);

        // Configurar JFrame
        setContentPane(metodoPagoPanel);
        setTitle("Gestión de Métodos de Pago");
        setSize(450, 150);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        dao = new MetodoPagoDAO();

        configurarEventos();
    }

    private void configurarEventos() {
        agregarButton.addActionListener(e -> agregarMetodoPago());
        actualizarButton.addActionListener(e -> actualizarMetodoPago());
        eliminarButton.addActionListener(e -> eliminarMetodoPago());
        buscarButton.addActionListener(e -> buscarMetodoPago());
    }

    private void agregarMetodoPago() {
        String nombre = nombreMetodoField.getText().trim();
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingresa un nombre para agregar.");
            return;
        }

        MetodoPago metodo = new MetodoPago();
        metodo.setNombreMetodo(nombre);

        try {
            MetodoPago creado = dao.create(metodo);
            if (creado != null) {
                JOptionPane.showMessageDialog(this, "Método de pago agregado con ID: " + creado.getMetodoPagoId());
                metodoActual = creado;
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo agregar.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al agregar: " + ex.getMessage());
        }
    }

    private void actualizarMetodoPago() {
        if (metodoActual == null) {
            JOptionPane.showMessageDialog(this, "Primero busca o agrega un método para actualizar.");
            return;
        }

        String nuevoNombre = nombreMetodoField.getText().trim();
        if (nuevoNombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingresa el nuevo nombre.");
            return;
        }

        metodoActual.setNombreMetodo(nuevoNombre);
        try {
            if (dao.update(metodoActual)) {
                JOptionPane.showMessageDialog(this, "Método actualizado.");
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo actualizar.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al actualizar: " + ex.getMessage());
        }
    }

    private void eliminarMetodoPago() {
        if (metodoActual == null) {
            JOptionPane.showMessageDialog(this, "Primero busca un método para eliminar.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "¿Eliminar este método?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (dao.delete(metodoActual.getMetodoPagoId())) {
                    JOptionPane.showMessageDialog(this, "Método eliminado.");
                    metodoActual = null;
                    nombreMetodoField.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "No se pudo eliminar.");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al eliminar: " + ex.getMessage());
            }
        }
    }

    private void buscarMetodoPago() {
        String nombre = nombreMetodoField.getText().trim();
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingresa un nombre para buscar.");
            return;
        }

        try {
            MetodoPago encontrado = dao.authenticate(nombre);
            if (encontrado != null) {
                metodoActual = encontrado;
                nombreMetodoField.setText(encontrado.getNombreMetodo());
                JOptionPane.showMessageDialog(this, "Método encontrado. Puedes actualizar o eliminar.");
            } else {
                JOptionPane.showMessageDialog(this, "Método no encontrado.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al buscar: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MetodoPagoForm().setVisible(true));
    }
}
