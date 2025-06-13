package utils;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImageLoader extends JFrame {

    private JLabel imageLabel;
    private File selectedFile;

    public ImageLoader() {
        setTitle("Cargar Imagen del Disco Duro");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Cambiado para no cerrar la aplicación principal
        setSize(600, 400);
        setLocationRelativeTo(null);

        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(new JScrollPane(imageLabel));

        JButton loadButton = new JButton("Cargar Imagen");
        loadButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            // Filtrar para mostrar solo archivos de imagen
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                    "Imágenes (JPG, PNG, GIF)", "jpg", "jpeg", "png", "gif"));

            int result = fileChooser.showOpenDialog(ImageLoader.this);

            if (result == JFileChooser.APPROVE_OPTION) {
                selectedFile = fileChooser.getSelectedFile();

                try {
                    BufferedImage image = ImageIO.read(selectedFile);
                    if (image != null) {
                        // Mostrar la imagen seleccionada en el JLabel
                        ImageIcon icon = new ImageIcon(image.getScaledInstance(
                                imageLabel.getWidth(), imageLabel.getHeight(), java.awt.Image.SCALE_SMOOTH));
                        imageLabel.setIcon(icon);
                        imageLabel.setText(""); // Eliminar texto si había
                    } else {
                        JOptionPane.showMessageDialog(ImageLoader.this,
                                "El archivo seleccionado no es una imagen válida.",
                                "Error de Carga", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(ImageLoader.this,
                            "Error al cargar la imagen: " + ex.getMessage(),
                            "Error de Carga", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(loadButton);
        add(buttonPanel, "South");
    }

    public File getSelectedFile() {
        return selectedFile;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ImageLoader().setVisible(true);
        });
    }
}
