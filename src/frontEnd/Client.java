package frontEnd;

import backEnd.FlightCatalogue;
import backEnd.Flight;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.net.Socket;
import java.io.*;

public class Client implements Runnable
{
    private Socket socket;
    private BufferedReader socketIn;
    private PrintWriter socketOut;

    private String id;
    private String password;
    private String type;
    protected FlightCatalogue catalogue;
    protected ArrayList<Flight> flights;

    public Client(String server, int port)
    {
        /*try
        {
            socket = new Socket(server, port);
            socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            socketOut = new PrintWriter((socket.getOutputStream()), true);
            makeLoginGUI();
        }catch(IOException err1)
        {
            System.err.println(err1.getMessage());
            err1.printStackTrace();
        }*/
        makeLoginGUI();
    }

    //creates the Login GUI
    private void makeLoginGUI()
    {
        JFrame loginFrame = new JFrame();
        loginFrame.setTitle("Login");
        loginFrame.setSize(350, 200);
        loginFrame.setLayout(new BorderLayout());

        JTextField idField = new JTextField(15);
        JTextField passwordField = new JTextField(15);

        JButton signIn = new JButton("Log In");
        signIn.setFont(new Font("Calibri", Font.BOLD, 15));
        JButton signUp = new JButton("Sign Up");
        signUp.setFont(new Font("Calibri", Font.BOLD, 15));

        JRadioButton admin = new JRadioButton("Admin");
        admin.setFont(new Font("Calibri", Font.BOLD, 15));
        JRadioButton passenger = new JRadioButton("Passenger");
        passenger.setFont(new Font("Calibri", Font.BOLD, 15));
        passenger.setSelected(true);

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(admin);
        buttonGroup.add(passenger);

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

        //button listener class for the login screen
        class loginListener implements ActionListener
        {
            public void actionPerformed(ActionEvent action)
            {
                if(action.getSource() == signIn)
                {

                }
                else if(action.getSource() == signUp)
                {
                    signUpClient();
                }
            }
        }

        loginListener loginListener = new loginListener();
        signIn.addActionListener(loginListener);
        signUp.addActionListener(loginListener);

        Container container = loginFrame.getContentPane();
        container.add("North", top);
        container.add("Center", mid);
        container.add("South", bot);

        loginFrame.setVisible(true);
        loginFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        loginFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(WindowEvent e)
            {
                JFrame frame = (JFrame)e.getSource();
                int result = JOptionPane.showConfirmDialog(frame, "Are you sure you want to exit the application?", "Exit Application", JOptionPane.YES_NO_OPTION);
                if(result == JOptionPane.YES_OPTION)
                {
                    System.exit(0);
                    /*try{
                        socketIn.close();
                        socketOut.close();
                        socket.close();
                    }catch(IOException err2)
                    {
                        System.out.println(err2.getMessage());
                        err2.printStackTrace();
                    }
                    System.exit(0);
                    */
                }

            }
        });
    }

    //creates the Sign-Up GUI
    private void signUpClient()
    {
        JFrame signUpFrame = new JFrame();
        signUpFrame.setSize(350, 300);
        signUpFrame.setTitle("Sign-Up");
        signUpFrame.setLayout(new BorderLayout());

        JButton confirm = new JButton("Confirm");
        confirm.setFont(new Font("Calibri", Font.BOLD, 15));

        JTextField first = new JTextField(15);
        JTextField last = new JTextField(15);
        JTextField email = new JTextField(15);
        JTextField passField = new JTextField(15);

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
        main4.add(passField);

        main.add(main1);
        main.add(main2);
        main.add(main3);
        main.add(main4);

        JPanel bot = new JPanel(new FlowLayout());
        bot.add(confirm);

        //button listener for Sign-Up GUI
        class signUpListener implements ActionListener
        {
            public void actionPerformed(ActionEvent action)
            {
                if(action.getSource() == confirm)
                {
                    String error = "";
                    if(first.getText().trim().length() > 20 || first.getText().trim().length() < 1)
                    {
                        if(first.getText().trim().length() > 20)
                            error += "Your first name cannot be more than 20 characters.\n";

                        else
                            error += "Your first name cannot be empty.\n";
                    }
                    if(last.getText().trim().length() > 20 || last.getText().trim().length() < 1)
                    {
                        if(last.getText().trim().length() > 20)
                            error += "Your last name cannot be more than 20 characters.\n";
                        else
                            error += "Your last name cannot be empty.\n";
                    }
                    if(email.getText().trim().length() > 50 || email.getText().trim().length() < 1)
                    {
                        if(email.getText().trim().length() > 50)
                            error += "Your e-mail address cannot be more than 50 characters.\n";
                        else
                            error += "Your e-mail address cannot be empty.\n";
                    }
                    if(!email.getText().trim().contains(".com") || !email.getText().trim().contains("@"))
                        error += "Your e-mail address needs to be in this format: abc@def.com\n";
                    if(passField.getText().trim().length() < 1)
                    {
                        error += "Your password cannot be empty.\n";
                    }

                    if(error.equals(""))
                    {

                    }
                    else
                    {
                        JOptionPane.showMessageDialog(null, error, "Input Data Error", JOptionPane.PLAIN_MESSAGE);
                    }

                }
            }
        }

        signUpListener signUpListener = new signUpListener();
        confirm.addActionListener(signUpListener);

        JLabel top = new JLabel("Sign-Up Form", SwingConstants.CENTER);
        top.setFont(new Font("Calibri", Font.BOLD, 20));
        Container container = signUpFrame.getContentPane();
        container.add("North", top);
        container.add("Center", main);
        container.add("South", bot);

        signUpFrame.setVisible(true);
        signUpFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        signUpFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(WindowEvent e)
            {
                JFrame frame = (JFrame)e.getSource();
                int result = JOptionPane.showConfirmDialog(frame, "Are you sure you don't want to sign up?", "Exit Sign-Up", JOptionPane.YES_NO_OPTION);
                if(result == JOptionPane.YES_OPTION)
                    signUpFrame.dispose();
            }
        });
    }

    @Override
    public void run()
    {

    }

    protected void login() {
        //TODO
    }

    protected void book(){
        //TODO
    }

    /* Not sure if we even need all this search methods????
    protected void search(){

    }
    protected void search(String source){

    }
    protected void search(String dest){

    }
    protected void search(int id){

    }
    protected void search(String date){

    }*/

    protected void search(String source, String dest, String date, int id){
        //TODO
    }
    protected void refresh(){
        //TODO
    }

    public static void main(String[] args) { Client client = new Client("localhost", 3309);    }
}

