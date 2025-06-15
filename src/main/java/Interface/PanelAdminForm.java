package Interface;

import javax.swing.*;

public class PanelAdminForm extends JFrame {
    private JPanel mainPanel;
    private JButton button1;
    private JButton button2;
    private JButton button3;
    private JButton button4;
    private JButton button5;
    private JButton button6;
    private JButton button7;

    public PanelAdminForm() {
        System.out.println("mainPanel es null? " + (mainPanel == null));
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
    }
}
