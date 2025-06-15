package Interface;

import dominio.Destino;
import persistencia.DestinoDAO;
import utils.CUD;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import javax.swing.filechooser.FileNameExtensionFilter;

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

    public FormularioDestinoGUI(Window owner, CUD cud, Destino destino) {
        super(owner, "Formulario Destino", ModalityType.APPLICATION_MODAL);

        $$$setupUI$$$(); // Esto lo genera automáticamente IntelliJ IDEA
        setContentPane(mainPanel);
        setSize(600, 500);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        configurarEventos();

        // Aquí puedes usar el objeto destino para cargar datos, si es necesario
        // Ejemplo: si destino no es null, carga los datos en los campos
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

        guardarButton.addActionListener(e -> guardarDestino());

        cargarButton.addActionListener(e ->
                JOptionPane.showMessageDialog(FormularioDestinoGUI.this, "Cargar destino (por implementar)")
        );
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

            Destino destino = new Destino();
            destino.setNombre(nombre);
            destino.setPais(pais);
            destino.setDescripcion(descripcion);
            destino.setImagen(imagenBytes);

            DestinoDAO destinoDAO = new DestinoDAO();
            Destino creado = destinoDAO.create(destino);

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

    private void limpiarCampos() {
        nombreField.setText("");
        paisField.setText("");
        descripcionArea.setText("");
        imagenLabel.setIcon(null);
        imagenLabel.setText("Imagen no seleccionada");
        imagenRuta = null;
    }

    private void $$$setupUI$$$() {
        // Método generado automáticamente por IntelliJ IDEA para el diseño del formulario
    }
}