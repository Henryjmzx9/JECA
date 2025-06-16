import Interface.LoginForm;
import Interface.MainForm;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {


        // Utiliza el Event Dispatch Thread (EDT) para asegurar la seguridad en la ejecución de la interfaz gráfica
        SwingUtilities.invokeLater(() -> {
            // Crea e instancia la ventana principal de la aplicación
            MainForm mainForm = new MainForm();
            mainForm.setVisible(true); // Se muestra maximizada (ya configurado en MainForm)

            // Crea e instancia la ventana de inicio de sesión, pasándole la ventana principal como referencia
           LoginForm loginForm = new LoginForm(mainForm);
            loginForm.setVisible(true); // Se muestra primero, solicitando credenciales
        });
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        System.out.println("Holaaaaaaaaa!");
        System.out.println("hola menoles!");
        System.out.println("hellouuuuuu!");
        int a =1;
        int b =2;
        int resultad= a+b;
        System.out.println(resultad);
        System.out.println("Hola, mundo!");
    }
}