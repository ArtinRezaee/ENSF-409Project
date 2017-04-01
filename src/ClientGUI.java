
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ClientGUI extends JFrame
{
    private Container container;
    private buttonListener buttonListener;

    private JButton signIn;
    private JButton signUp;

    private JTextField idField;
    private JTextField passwordField;

    private ButtonGroup buttonGroup;
    private JRadioButton passenger;
    private JRadioButton admin;

    public ClientGUI()
    {
        setTitle("Login Screen");
        setLayout(new BorderLayout());
        buttonListener = new buttonListener();


    }

    class buttonListener implements ActionListener
    {
        public void actionPerformed(ActionEvent action)
        {
            if(action.getSource() == signIn)
            {

            }
            else if(action.getSource() == signUp)
            {

            }
        }
    }
}
