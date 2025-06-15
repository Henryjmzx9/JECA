package Interface;

import dominio.Usuario;
import persistencia.UsuarioDAO;
import utils.Rol;

import javax.swing.*;
import java.awt.event.WindowAdapter; // Importa la clase WindowAdapter desde el paquete java.awt.event. WindowAdapter es una clase abstracta que implementa la interfaz WindowListener y proporciona implementaciones vacías para sus métodos. Se utiliza para extenderla y solo sobrescribir los métodos de los eventos de ventana que nos interesan.
import java.awt.event.WindowEvent;

public class LoginForm extends JDialog {
    private JPanel mainPanel;
    private JTextField TxtEmail;
    private JButton btnLogin;
    private JButton btnSalir;
    private JPasswordField txtPassword;

    public LoginForm() {
        initComponents(); // llamado generado por el GUI Designer

        // Imagen de fondo (esto es lo que me diste, ya colocado correctamente)
        int ancho = 600;
        int alto = 400;
        ImageIcon fondo = new ImageIcon(getClass().getResource("C:\\Users\\MINEDUCYT\\IdeaProjects\\JECA\\src\\main\\java\\utils\\img.png"));
        JLabel fondoLabel = new JLabel(fondo);
        fondoLabel.setBounds(0, 0, ancho, alto);
        getContentPane().add(fondoLabel);
    }

    private void initComponents() {
        // Aquí va el código generado por IntelliJ Swing UI Designer (.form)
    }


    private UsuarioDAO userDAO; // Declaración de una variable de instancia llamada 'userDAO' de tipo UserDAO. Esta variable se utilizará para interactuar con la capa de acceso a datos de los usuarios (por ejemplo, para autenticar usuarios).
    private MainForm mainForm;

    public LoginForm(MainForm mainForm){
        this.mainForm = mainForm; // Asigna la instancia del formulario principal a la variable 'mainForm' de esta clase.
        userDAO = new UsuarioDAO(); // Crea una nueva instancia de la clase UserDAO. Esta clase  se encarga de la lógica de acceso a datos de los usuarios (creación, lectura, autenticación, etc.).
        setContentPane(mainPanel); // Establece el panel principal ('mainPanel') como el contenido visible de este componente.
        setModal(true); // Establece un diálogo modal bloquea la interacción con otras ventanas de la aplicación hasta que se cierra.
        setTitle("Login"); // Establece el título de la ventana como "Login".
        pack(); // Ajusta el tamaño de la ventana para que quepan todos sus componentes preferidos.
        setLocationRelativeTo(mainForm); // Centra la ventana de inicio de sesión  relativa al formulario principal ('mainForm').

        btnSalir.addActionListener(e -> System.exit(0)); // Agrega un ActionListener al botón 'btnSalir'. Cuando se hace clic en este botón, la aplicación se cerrará (terminando la JVM).
        btnLogin.addActionListener(e-> login()); // Agrega un ActionListener al botón 'btnLogin'. Cuando se hace clic en este botón, se ejecutará el método 'login()' de esta clase,  para realizar la lógica de autenticación.
        addWindowListener(new WindowAdapter() { // Agrega un WindowListener a esta ventana para escuchar eventos de ventana.
            @Override
            public void windowClosing(WindowEvent e) { // Sobreescribe el método 'windowClosing'. Este método se llama cuando el usuario intenta cerrar la ventana (por ejemplo, haciendo clic en el botón de cerrar).
                System.exit(0); // Cuando la ventana se está cerrando, la aplicación también se cerrará (terminando la JVM).
            }
        });
    }
    private void login() {
        try{
            Usuario user = new Usuario(); // Crea una nueva instancia de la clase User para almacenar las credenciales del usuario.
            user.setEmail(TxtEmail.getText()); // Obtiene el texto ingresado en el campo de texto 'txtEmail'  y lo establece como el correo electrónico del objeto 'user'.
            user.setPasswordHash(new String(txtPassword.getPassword())); // Obtiene la contraseña ingresada en el campo de contraseña 'txtPassword' (como un array de caracteres), la convierte a un String y la establece como la contraseña hasheada del objeto 'user'.

            Usuario userAut = userDAO.authenticate(user.getEmail(), user.getPasswordHash());
            // Llama al método 'authenticate' del objeto 'userDAO' para verificar las credenciales del usuario contra la base de datos. El resultado (un objeto User si la autenticación es exitosa, o null si falla) se almacena en 'userAut'.

            // Verifica si la autenticación fue exitosa:
            // 1. 'userAut' no es null (se encontró un usuario).
            // 2. El ID del usuario autenticado es mayor que 0 (implica que es un usuario válido en la base de datos).
            // 3. El correo electrónico del usuario autenticado coincide con el correo electrónico ingresado.
            if(userAut != null && userAut.getId() > 0 && userAut.getEmail().equals(user.getEmail())) {
                this.mainForm.setUserAutenticate(userAut);
                this.dispose(); // cerrar LoginForm

                // Verificar si el rol es Administrador usando el enum
                if (userAut.getRol() == Rol.Administrador) {
                    PanelAdminForm adminForm = new PanelAdminForm();
                    adminForm.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null,
                            "Tu rol no tiene acceso al panel de administrador.",
                            "Acceso Denegado",
                            JOptionPane.WARNING_MESSAGE);
                }
            }


            else{
                // Si la autenticación falla, muestra un mensaje de diálogo de advertencia.
                JOptionPane.showMessageDialog(null,
                        "Email y password incorrecto", // El mensaje que se muestra al usuario.
                        "Login", // El título de la ventana de diálogo.
                        JOptionPane.WARNING_MESSAGE); // El tipo de icono que se muestra (advertencia).
            }
        }
        catch (Exception ex){
            // Captura cualquier excepción que pueda ocurrir durante el proceso de inicio de sesión (por ejemplo, error de base de datos).
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(), // Muestra el mensaje de la excepción.
                    "Sistem", // El título de la ventana de diálogo.
                    JOptionPane.ERROR_MESSAGE); // El tipo de icono que se muestra (error).
        }


    }
}
