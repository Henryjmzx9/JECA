package utils;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
public class GetBytesFromFileChooser extends JFrame {

    private JTextArea textArea; // Para mostrar información sobre los bytes
    private JLabel imageLabel;
    public GetBytesFromFileChooser() {
        setTitle("Obtener Bytes de Archivo con JFileChooser");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 300);
        setLocationRelativeTo(null);

        textArea = new JTextArea();
        textArea.setEditable(false);
        imageLabel = new JLabel("Selecciona un archivo de imagen para mostrarlo.");
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        JScrollPane scrollPane = new JScrollPane(imageLabel);

        add(scrollPane, BorderLayout.CENTER);

        JButton selectFileButton = new JButton("Seleccionar Archivo y Obtener Bytes");
        selectFileButton.addActionListener(e -> selectFileAndGetBytes());
        add(selectFileButton, BorderLayout.SOUTH);

        //scrollPane.add(imageLabel, BorderLayout.CENTER);
        setVisible(true);
    }

    private void selectFileAndGetBytes() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Selecciona un archivo para obtener sus bytes");

        // Opcional: Puedes establecer un filtro si quieres que el usuario solo elija ciertos tipos de archivos
        // Por ejemplo, para solo imágenes:
        // FileNameExtensionFilter imageFilter = new FileNameExtensionFilter(
        //         "Archivos de Imagen (JPG, PNG, GIF)", "jpg", "jpeg", "png", "gif");
        // fileChooser.setFileFilter(imageFilter);

        int userSelection = fileChooser.showOpenDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            if (selectedFile != null) {
                try {
                    byte[] fileBytes = readFileToBytes(selectedFile);
                    showImagenBytes(fileBytes);
                    textArea.setText("Archivo seleccionado: " + selectedFile.getAbsolutePath() + "\n");
                    textArea.append("Tamaño del archivo: " + selectedFile.length() + " bytes\n");
                    textArea.append("Bytes obtenidos: " + fileBytes.length + " bytes\n");

                    // Aquí tienes el array de bytes (fileBytes)
                    // Puedes hacer lo que necesites con ellos:
                    // - Guardarlos en una base de datos.
                    // - Enviarlos a través de una red.
                    // - Procesarlos de alguna manera (ej. si es una imagen, convertir a BufferedImage).

                    // Ejemplo: Mostrar los primeros 100 bytes (¡solo para demostración, puede ser ilegible!)
                    textArea.append("\nPrimeros 100 bytes (en hexadecimal): \n");
                    StringBuilder hexString = new StringBuilder();
                    for (int i = 0; i < Math.min(fileBytes.length, 100); i++) {
                        hexString.append(String.format("%02X ", fileBytes[i]));
                        if ((i + 1) % 16 == 0) {
                            hexString.append("\n"); // Nueva línea cada 16 bytes
                        }
                    }
                    textArea.append(hexString.toString());

                } catch (IOException ex) {
                    textArea.setText("Error al leer el archivo: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        } else {
            textArea.setText("Selección de archivo cancelada.");
        }
    }
    private void showImagenBytes(byte[] fileBytes){
        try{


            ByteArrayInputStream bis = new ByteArrayInputStream(fileBytes);

            // 3. Leer el InputStream como BufferedImage
            BufferedImage image = ImageIO.read(bis);

            if (image != null) {
                // 4. Crear un ImageIcon a partir de la BufferedImage
                ImageIcon icon = new ImageIcon(image);

                // Opcional: Escalar la imagen si es demasiado grande para el JLabel
                // Si la imagen es más grande que el JLabel, se puede escalar
                if (icon.getIconWidth() > imageLabel.getWidth() || icon.getIconHeight() > imageLabel.getHeight()) {
                    // Crear una nueva imagen escalada
                    java.awt.Image scaledImage = image.getScaledInstance(imageLabel.getWidth(), -1, java.awt.Image.SCALE_SMOOTH); // Escala a ancho fijo, altura proporcional
                    // O si quieres escalar a la altura
                    // Image scaledImage = image.getScaledInstance(-1, imageLabel.getHeight(), Image.SCALE_SMOOTH);
                    icon = new ImageIcon(scaledImage);
                }

                // 5. Asignar el ImageIcon al JLabel
                imageLabel.setIcon(icon);
                imageLabel.setText(""); // Limpia el texto si la imagen se cargó
                textArea.append("Imagen cargada y mostrada correctamente.\n");
            } else {
                imageLabel.setIcon(null);
                imageLabel.setText("No se pudo leer la imagen del archivo seleccionado. Asegúrate de que sea un formato de imagen válido.");
                textArea.append("Error: El archivo no es un formato de imagen válido.\n");
            }
        }
        catch(Exception ex){

        }
    }
    /**
     * Lee el contenido de un archivo y lo devuelve como un arreglo de bytes.
     * @param file El archivo a leer.
     * @return Un arreglo de bytes que representa el contenido del archivo.
     * @throws IOException Si ocurre un error de E/S al leer el archivo.
     */
    private byte[] readFileToBytes(File file) throws IOException {
        // Usamos try-with-resources para asegurar que los streams se cierren automáticamente
        try (FileInputStream fis = new FileInputStream(file);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[1024]; // Buffer para leer chunks
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            return bos.toByteArray();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GetBytesFromFileChooser::new);
    }
}
