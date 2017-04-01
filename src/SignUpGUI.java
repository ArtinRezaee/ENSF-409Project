import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SignUpGUI extends JFrame
{
    private JButton confirm;

    private JTextField first;
    private JTextField last;
    private JTextField email;
    private JTextField password;

    public SignUpGUI()
    {
        setTitle("Sign-Up");
        setLayout(new BorderLayout());

        ButtonListener listener = new ButtonListener();
        confirm = new JButton("Confirm");
        confirm.setFont(new Font("Calibri", Font.BOLD, 15));
        confirm.addActionListener(listener);

        first = new JTextField(15);
        last = new JTextField(15);
        email = new JTextField(15);
        password = new JTextField(15);

        JPanel main = new JPanel(new GridLayout(4, 1));
        JPanel main1 = new JPanel(new FlowLayout());
        JPanel main2 = new JPanel(new FlowLayout());
        JPanel main3 = new JPanel(new FlowLayout());
        JPanel main4 = new JPanel(new FlowLayout());

        main1.add(new JLabel("First Name"));
        main1.add(first);
        main2.add(new JLabel("Last Name"));
        main2.add(last);
        main3.add(new JLabel("E-mail        "));
        main3.add(email);
        main4.add(new JLabel("Password "));
        main4.add(password);

        main.add(main1);
        main.add(main2);
        main.add(main3);
        main.add(main4);

        JPanel bot = new JPanel(new FlowLayout());
        bot.add(confirm);

        JLabel top = new JLabel("Sign-Up Form", SwingConstants.CENTER);
        top.setFont(new Font("Calibri", Font.BOLD, 17));
        Container container = getContentPane();
        container.add("North", top);
        container.add("Center", main);
        container.add("South", bot);

        pack();
        setVisible(true);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(WindowEvent e)
            {
                JFrame frame = (JFrame)e.getSource();
                int result = JOptionPane.showConfirmDialog(frame, "Are you sure you don't want to sign up?", "Exit Sign-Up", JOptionPane.YES_NO_OPTION);
                if(result == JOptionPane.YES_OPTION)
                    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            }
        });
    }

    class ButtonListener implements ActionListener
    {
        public void actionPerformed(ActionEvent action)
        {
            if(action.getSource() == confirm)
            {
                if(checkInputs(first.getText().trim(), last.getText().trim(), email.getText().trim(), password.getText().trim()))
                {

                }
            }
        }
    }

    private Boolean checkInputs(String first, String last, String email, String password)
    {
        String error = "";
        if(first.length() > 20 || first.length() < 1)
        {
            if(first.length() > 20)
                error += "Your first name cannot be more than 20 characters.\n";

            else
                error += "Your first name cannot be empty.\n";
        }
        if(last.length() > 20 || last.length() < 1)
        {
            if(last.length() > 20)
                error += "Your last name cannot be more than 20 characters.\n";
            else
                error += "Your last name cannot be empty.\n";
        }
        if(email.length() > 50 || email.length() < 1)
        {
            if(email.length() > 50)
                error += "Your e-mail address cannot be more than 50 characters.\n";
            else
                error += "Your e-mail address cannot be empty.\n";
        }
        if(!email.contains(".com") || !email.contains("@"))
            error += "Your e-mail address needs to be in this format: abc@def.com\n";
        if(password.length() < 1)
        {
            error += "Your password cannot be empty.\n";
        }

        if(error.equals(""))
            return true;
        else
        {
            JOptionPane.showMessageDialog(null, error, "Input Data Error", JOptionPane.PLAIN_MESSAGE);
            return false;
        }
    }
}
