package Interface;

import dominio.Destino;
import persistencia.DestinoDAO;
import utils.CUD;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;

public class FormularioDestinoGUI extends JDialog {
    public JPanel mainPanel;
    public JTextField nombreField;
    public JTextField paisField;
    public JTextArea descripcionArea;
    public JLabel imagenLabel;
    public JButton seleccionarImagenButton;
    public JButton guardarButton;
    public JButton cargarButton;

    private String imagenRuta;
    private CUD cud;
    private Destino destino;

    public FormularioDestinoGUI(Window owner, CUD cud, Destino destino) {
        super(owner, "Formulario Destino", ModalityType.APPLICATION_MODAL);

        $$$setupUI$$$(); // Método generado por el diseñador de formularios de IntelliJ
        setContentPane(mainPanel);
        setSize(600, 500);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        this.cud = cud;
        this.destino = destino;

        configurarEventos();
        configurarFormulario();
    }

    private void configurarEventos() {
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                int confirm = JOptionPane.showConfirmDialog(
                        FormularioDestinoGUI.this,
                        "¿Seguro que quieres salir?",
                        "Confirmar salida",
                        JOptionPane.YES_NO_OPTION
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    dispose();
                }
            }
        });

        seleccionarImagenButton.addActionListener(e -> seleccionarImagen());
        guardarButton.addActionListener(e -> {
            if (cud == CUD.CREATE) {
                guardarDestino();
            } else if (cud == CUD.UPDATE) {
                actualizarDestino();
            }
        });

        cargarButton.addActionListener(e ->
                JOptionPane.showMessageDialog(FormularioDestinoGUI.this, "Función de carga por implementar.")
        );
    }

    private void configurarFormulario() {
        if (cud == CUD.CREATE) {
            // nada adicional
        } else if (cud == CUD.UPDATE || cud == CUD.DELETE) {
            nombreField.setText(destino.getNombre());
            paisField.setText(destino.getPais());
            descripcionArea.setText(destino.getDescripcion());

            byte[] imagen = destino.getImagen();
            if (imagen != null) {
                ImageIcon icon = new ImageIcon(imagen);
                Image imgEscalada = icon.getImage().getScaledInstance(200, 150, Image.SCALE_SMOOTH);
                imagenLabel.setIcon(new ImageIcon(imgEscalada));
                imagenLabel.setText("");
            }

            if (cud == CUD.UPDATE) {
                // Permitir editar
            } else if (cud == CUD.DELETE) {
                // Deshabilitar campos
                nombreField.setEnabled(false);
                paisField.setEnabled(false);
                descripcionArea.setEnabled(false);
                seleccionarImagenButton.setEnabled(false);
                guardarButton.setVisible(false);

                eliminarDestino(); // Ejecutar eliminación directa
            }
        }
    }

    private void seleccionarImagen() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Imágenes", "jpg", "png", "jpeg", "gif"));
        int option = fileChooser.showOpenDialog(FormularioDestinoGUI.this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            imagenRuta = selectedFile.getAbsolutePath();
            ImageIcon icon = new ImageIcon(new ImageIcon(imagenRuta).getImage().getScaledInstance(200, 150, Image.SCALE_SMOOTH));
            imagenLabel.setIcon(icon);
            imagenLabel.setText("");
        }
    }

    private void guardarDestino() {
        String nombre = nombreField.getText().trim();
        String pais = paisField.getText().trim();
        String descripcion = descripcionArea.getText().trim();

        if (nombre.isEmpty() || pais.isEmpty() || descripcion.isEmpty() || imagenRuta == null) {
            JOptionPane.showMessageDialog(this, "Por favor, completa todos los campos y selecciona una imagen.");
            return;
        }

        try {
            byte[] imagenBytes = Files.readAllBytes(new File(imagenRuta).toPath());

            Destino nuevoDestino = new Destino();
            nuevoDestino.setNombre(nombre);
            nuevoDestino.setPais(pais);
            nuevoDestino.setDescripcion(descripcion);
            nuevoDestino.setImagen(imagenBytes);

            DestinoDAO destinoDAO = new DestinoDAO();
            Destino creado = destinoDAO.create(nuevoDestino);

            if (creado != null) {
                JOptionPane.showMessageDialog(this, "Destino guardado exitosamente con ID: " + creado.getDestinoId());
                limpiarCampos();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo guardar el destino.");
            }

        } catch (IOException | SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void actualizarDestino() {
        String nombre = nombreField.getText().trim();
        String pais = paisField.getText().trim();
        String descripcion = descripcionArea.getText().trim();

        if (nombre.isEmpty() || pais.isEmpty() || descripcion.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, completa todos los campos.");
            return;
        }

        try {
            destino.setNombre(nombre);
            destino.setPais(pais);
            destino.setDescripcion(descripcion);

            if (imagenRuta != null) {
                byte[] imagenBytes = Files.readAllBytes(new File(imagenRuta).toPath());
                destino.setImagen(imagenBytes);
            }

            DestinoDAO destinoDAO = new DestinoDAO();
            boolean actualizado = destinoDAO.update(destino);

            if (actualizado) {
                JOptionPane.showMessageDialog(this, "Destino actualizado correctamente.");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo actualizar el destino.");
            }

        } catch (IOException | SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al actualizar: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void eliminarDestino() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Estás seguro de eliminar este destino?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                DestinoDAO destinoDAO = new DestinoDAO();
                boolean eliminado = destinoDAO.delete(destino.getDestinoId());

                if (eliminado) {
                    JOptionPane.showMessageDialog(this, "Destino eliminado correctamente.");
                } else {
                    JOptionPane.showMessageDialog(this, "No se pudo eliminar el destino.");
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al eliminar: " + ex.getMessage());
            }
            dispose();
        } else {
            dispose();
        }
    }

    private void limpiarCampos() {
        nombreField.setText("");
        paisField.setText("");
        descripcionArea.setText("");
        imagenLabel.setIcon(null);
        imagenLabel.setText("Imagen no seleccionada");
        imagenRuta = null;
    }

    private void $$$setupUI$$$() {
        // Método generado por IntelliJ IDEA (GUI Designer)
    }
}