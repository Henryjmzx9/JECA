package Interface;

import dominio.MetodoPago;
import dominio.Usuario;
import utils.CUD;

import javax.swing.*;

public class MainForm extends JFrame {

    private Usuario UserAutenticate;

    public Usuario getUserAutenticate() {
        return UserAutenticate;
    }

    public void setUserAutenticate(Usuario userAutenticate) {
        UserAutenticate = userAutenticate;
    }
    public MainForm() {
        setTitle("Sistema en Java de escritorio"); // Título de la ventana principal.
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Cierra la aplicación al salir.
        setLocationRelativeTo(null); // Centra la ventana en pantalla.
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Maximiza la ventana al iniciar.
        createMenu(); // Llama al método que crea y configura el menú.
    }
    private void createMenu() {
        // Barra de menú
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        // Menú "Perfil"
        JMenu menuPerfil = new JMenu("Perfil");
        menuBar.add(menuPerfil);

        JMenuItem itemChangePassword = new JMenuItem("Cambiar contraseña");
        menuPerfil.add(itemChangePassword);
        itemChangePassword.addActionListener(e -> {
            ChangePasswordForm changePassword = new ChangePasswordForm(this);
            changePassword.setVisible(true);
        });

        JMenuItem itemChangeUser = new JMenuItem("Cambiar de usuario");
        menuPerfil.add(itemChangeUser);
        itemChangeUser.addActionListener(e -> {
            LoginForm loginForm = new LoginForm(this);
            loginForm.setVisible(true);
        });

        JMenuItem itemSalir = new JMenuItem("Salir");
        menuPerfil.add(itemSalir);
        itemSalir.addActionListener(e -> System.exit(0));

        // Menú "Mantenimientos"
        JMenu menuMantenimiento = new JMenu("Mantenimientos");
        menuBar.add(menuMantenimiento);

        // Opción: Usuarios
        JMenuItem itemUsers = new JMenuItem("Usuarios");
        menuMantenimiento.add(itemUsers);
        itemUsers.addActionListener(e -> {
            UserReadingForm userReadingForm = new UserReadingForm(this);
            userReadingForm.setVisible(true);
        });
        // Opción: Clientes
        JMenuItem itemClients = new JMenuItem("Clientes");
        menuMantenimiento.add(itemClients);
        itemClients.addActionListener(e -> {
            clientReadingForm clientReadingForm = new clientReadingForm(this);  // este abre el listado de clientes
            clientReadingForm.setVisible(true);
        });



        // Opción: Destinos
        JMenuItem itemDestinos = new JMenuItem("Destinos");
        menuMantenimiento.add(itemDestinos);
        itemDestinos.addActionListener(e -> {
            DestinoReadingForm destinoReadingForm = new DestinoReadingForm(this);
            destinoReadingForm.setVisible(true);
        });
        JMenuItem itemMetodoPago = new JMenuItem("Método de pago");
        menuMantenimiento.add(itemMetodoPago);
        itemMetodoPago.addActionListener(e -> {
            MetodoPagoReadingForm metodoPagoReadingForm = new MetodoPagoReadingForm(this);
            metodoPagoReadingForm.setVisible(true);
        });
        JMenuItem itemPaquetes = new JMenuItem("Paquetes");
        menuMantenimiento.add(itemPaquetes);
        itemPaquetes.addActionListener(e -> {
            ReadingPaqueteViajeForm form = new ReadingPaqueteViajeForm(this);
            form.setVisible(true);
        });
        JMenuItem itemReservas = new JMenuItem("Reservas");
        menuMantenimiento.add(itemReservas);
        itemReservas.addActionListener(e -> {
            ReservaReadingForm reservaForm = new ReservaReadingForm(this, CUD.CREATE, null);
            reservaForm.setVisible(true);
        });
    }
}
