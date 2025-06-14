package Interface;

import dominio.Usuario;

import javax.swing.*;

public class MainForm extends JFrame {

    private Usuario UserAutenticate;

    public Usuario getUserAutenticate() {
        return UserAutenticate;
    }

    public void setUserAutenticate(Usuario userAutenticate) {
        UserAutenticate = userAutenticate;
    }
}
