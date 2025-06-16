package Interface;

import dominio.Usuario;
import utils.Rol;

import javax.swing.*;

public class MainForm extends JFrame {

    private Usuario userAutenticate;
    private JPanel panel1;

    public Usuario getUserAutenticate() {
        return userAutenticate;
    }

    public void setUserAutenticate(Usuario userAutenticate) {
        this.userAutenticate = userAutenticate;
        configureMenuAccess(); // Configurar acceso basado en el rol al asignar el usuario
    }

    public MainForm() {
        setTitle("Sistema en Java de escritorio");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        createMenu();
    }

    // Menú principal y opciones
    private JMenuBar menuBar;
    private JMenu menuPerfil;
    private JMenu menuMantenimiento;
    private JMenuItem itemReservas;
    private JMenuItem itemPagos;
    private JMenuItem itemUsers;

    private void createMenu() {
        // Barra de menú
        menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        // Menú "Perfil"
        menuPerfil = new JMenu("Perfil");
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
            this.setUserAutenticate(null); // Eliminar el usuario actual
            LoginForm loginForm = new LoginForm(this);
            loginForm.setVisible(true); // Mostrar el LoginForm para un nuevo inicio de sesión
        });

        JMenuItem itemSalir = new JMenuItem("Salir");
        menuPerfil.add(itemSalir);
        itemSalir.addActionListener(e -> System.exit(0));

        // Menú "Mantenimientos"
        menuMantenimiento = new JMenu("Mantenimientos");
        menuBar.add(menuMantenimiento);

        // Opción: Usuarios
        itemUsers = new JMenuItem("Usuarios");
        menuMantenimiento.add(itemUsers);
        itemUsers.addActionListener(e -> {
            UserReadingForm userReadingForm = new UserReadingForm(this);
            userReadingForm.setVisible(true);
        });

        // Opción: Clientes
        JMenuItem itemClients = new JMenuItem("Clientes");
        menuMantenimiento.add(itemClients);
        itemClients.addActionListener(e -> {
            clientReadingForm clientReadingForm = new clientReadingForm(this);
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

        // Opción: Reservas
        itemReservas = new JMenuItem("Reservas");
        menuMantenimiento.add(itemReservas);
        itemReservas.addActionListener(e -> {
            ReservaReadingForm reservaForm = new ReservaReadingForm(this);
            reservaForm.setVisible(true);
        });

        // Opción: Pagos
        itemPagos = new JMenuItem("Pagos");
        menuMantenimiento.add(itemPagos);
        itemPagos.addActionListener(e -> {
            PagoReadingForm pagoForm = new PagoReadingForm(this);
            pagoForm.setVisible(true);
        });
    }

    private void configureMenuAccess() {
        if (userAutenticate != null) {
            Rol rol = userAutenticate.getRol();

            // Habilitar todas las opciones al principio
            itemReservas.setEnabled(true);
            itemPagos.setEnabled(true);
            itemUsers.setEnabled(true);
            menuMantenimiento.setEnabled(true);

            switch (rol) {
                case Administrador -> {
                    itemReservas.setEnabled(false);
                    itemPagos.setEnabled(false);
                }
                case Agente -> {
                    itemUsers.setEnabled(false);
                }
                case Cliente -> {
                    itemReservas.setEnabled(false);
                    itemPagos.setEnabled(false);
                    itemUsers.setEnabled(false);
                    menuMantenimiento.setEnabled(false); // Deshabilita todo el menú de Mantenimientos
                }
            }
        }
    }
}
