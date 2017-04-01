import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginGUI extends JFrame
{
    private JRadioButton passenger ;
    private JRadioButton admin;

    private JButton signIn;
    private JButton signUp;

    private JTextField idField;
    private JTextField passwordField;

    private LoginGUI()
    {
        setTitle("Login Window");
        setSize(265, 175);
        setLayout(new BorderLayout());

        idField = new JTextField(10);
        passwordField = new JTextField(10);

        signIn = new JButton("Log In");
        signIn.setFont(new Font("Calibri", Font.BOLD, 15));
        signUp = new JButton("Sign Up");
        signUp.setFont(new Font("Calibri", Font.BOLD, 15));

        admin = new JRadioButton("Admin");
        admin.setFont(new Font("Calibri", Font.BOLD, 15));
        passenger = new JRadioButton("Passenger");
        passenger.setFont(new Font("Calibri", Font.BOLD, 15));
        passenger.setSelected(true);

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(admin);
        buttonGroup.add(passenger);

        ButtonListener listener = new ButtonListener();
        signIn.addActionListener(listener);
        signUp.addActionListener(listener);

        JPanel top = new JPanel(new FlowLayout());
        top.add(admin);
        top.add(passenger);

        JPanel mid = new JPanel(new GridLayout(2, 1));

        JPanel mid1 = new JPanel(new FlowLayout());
        mid1.add(new JLabel("User Name"));
        mid1.add(idField);
        JPanel mid2 = new JPanel(new FlowLayout());
        mid2.add(new JLabel("Password "));
        mid2.add(passwordField);

        mid.add(mid1);
        mid.add(mid2);

        JPanel bot = new JPanel(new FlowLayout());
        bot.add(signIn);
        bot.add(signUp);

        Container container = getContentPane();
        container.add("North", top);
        container.add("Center", mid);
        container.add("South", bot);

        setVisible(true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(WindowEvent e)
            {
                JFrame frame = (JFrame)e.getSource();
                int result = JOptionPane.showConfirmDialog(frame, "Are you sure you want to exit the application?", "Exit Application", JOptionPane.YES_NO_OPTION);
                if(result == JOptionPane.YES_OPTION)
                    System.exit(0);
            }
        });
    }

    class ButtonListener implements ActionListener
    {
        public void actionPerformed(ActionEvent action)
        {
            if(action.getSource() == signIn)
            {
                if(checkInputs(idField.getText().trim(), passwordField.getText().trim()))
                {

                }
            }
            else if(action.getSource() == signUp)
            {
                SignUpGUI signup = new SignUpGUI();
            }
        }
    }

    private Boolean checkInputs(String id, String password)
    {
        String error = "";
        if(id.length() < 1)
            error += "Your user name cannot be empty.\n";
        if(password.length() < 1)
            error += "Your password cannot be empty.\n";

        if(error.equals(""))
            return true;
        else
        {
            JOptionPane.showMessageDialog(null, error, "Input Data Error", JOptionPane.PLAIN_MESSAGE);
            return false;
        }
    }

    public static void main(String[] args) { LoginGUI client = new LoginGUI();    }
}
