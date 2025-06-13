package app;

import Interface.FormularioDestinoGUI;
import dominio.Destino;
import persistencia.DestinoDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.sql.SQLException;
import java.nio.file.Files;

public class FormularioDestinoWindow extends JFrame {
    private FormularioDestinoGUI formGUI;
    private String imagenRuta;

    public FormularioDestinoWindow() {
        formGUI = new FormularioDestinoGUI();
        setContentPane(formGUI.contentPane);

        setTitle("Gestión de Destinos");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        // Confirmación de cierre
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                int confirm = JOptionPane.showConfirmDialog(
                        FormularioDestinoWindow.this,
                        "¿Seguro que quieres salir?",
                        "Confirmar salida",
                        JOptionPane.YES_NO_OPTION
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    dispose();
                }
            }
        });

        // Evento seleccionar imagen con vista previa
        formGUI.seleccionarImagenButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("Imágenes", "jpg", "png", "jpeg", "gif"));
            int option = fileChooser.showOpenDialog(FormularioDestinoWindow.this);
            if (option == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                imagenRuta = selectedFile.getAbsolutePath();
                ImageIcon icon = new ImageIcon(new ImageIcon(imagenRuta).getImage().getScaledInstance(200, 150, Image.SCALE_SMOOTH));
                formGUI.imagenLabel.setIcon(icon);
                formGUI.imagenLabel.setText("");
            }
        });

        // Evento guardar
        formGUI.guardarButton.addActionListener(e -> {
            String nombre = formGUI.nombreField.getText().trim();
            String pais = formGUI.paisField.getText().trim();
            String descripcion = formGUI.descripcionArea.getText().trim();

            // Validaciones básicas
            if (nombre.isEmpty() || pais.isEmpty() || descripcion.isEmpty() || imagenRuta == null) {
                JOptionPane.showMessageDialog(FormularioDestinoWindow.this, "Por favor, completa todos los campos y selecciona una imagen.");
                return;
            }

            try {
                // Leer imagen como bytes
                byte[] imagenBytes = Files.readAllBytes(new File(imagenRuta).toPath());

                // Crear objeto destino
                Destino destino = new Destino();
                destino.setNombre(nombre);
                destino.setPais(pais);
                destino.setDescripcion(descripcion);
                destino.setImagen(imagenBytes);

                // Guardar en la base de datos
                DestinoDAO destinoDAO = new DestinoDAO();
                Destino creado = destinoDAO.create(destino);

                if (creado != null) {
                    JOptionPane.showMessageDialog(FormularioDestinoWindow.this, "Destino guardado exitosamente con ID: " + creado.getDestinoId());
                    limpiarCampos();
                } else {
                    JOptionPane.showMessageDialog(FormularioDestinoWindow.this, "No se pudo guardar el destino.");
                }

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(FormularioDestinoWindow.this, "Error al leer la imagen: " + ex.getMessage());
                ex.printStackTrace();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(FormularioDestinoWindow.this, "Error al guardar en la base de datos: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        // Evento cargar (para que no quede vacío)
        formGUI.cargarButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(FormularioDestinoWindow.this, "Cargar destino (por implementar)");
        });

        setVisible(true);
    }

    private void limpiarCampos() {
        formGUI.nombreField.setText("");
        formGUI.paisField.setText("");
        formGUI.descripcionArea.setText("");
        formGUI.imagenLabel.setIcon(null);
        formGUI.imagenLabel.setText("Imagen no seleccionada");
        imagenRuta = null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FormularioDestinoWindow::new);
    }
}