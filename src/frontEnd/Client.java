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
    private BufferedReader stringIn;
    private PrintWriter stringOut;
    private ObjectOutputStream objOut;
    private ObjectInputStream objIn;

    private String id;
    private String password;
    private String type;
    protected FlightCatalogue catalogue;
    protected ArrayList<Flight> flights;

    public Client(String server, int port)
    {
        try
        {
            socket = new Socket(server, port);
            stringIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            stringOut = new PrintWriter((socket.getOutputStream()), true);
            objOut = new ObjectOutputStream(socket.getOutputStream());
            objIn = new ObjectInputStream(socket.getInputStream());

            makeLoginGUI();
        }catch(IOException err1)
        {
            System.err.println(err1.getMessage());
            err1.printStackTrace();
        }
    }

    //creates the Login GUI
    private void makeLoginGUI()
    {
        JFrame loginFrame = new JFrame();
        loginFrame.setTitle("Login");
        loginFrame.setSize(300, 200);
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
        mid1.add(new JLabel("E-mail        "));
        mid1.add(idField);
        JPanel mid2 = new JPanel(new FlowLayout());
        mid2.add(new JLabel("Password"));
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
                    stringOut.println("checklogin");
                    stringOut.println(idField.getText().trim());
                    stringOut.println(passwordField.getText().trim());
                    String ty;
                    if(passenger.isSelected())
                        ty = "Passenger";
                    else
                        ty = "Admin";
                    stringOut.println(ty);

                    try{
                        String valid = stringIn.readLine();
                        if(valid.equals("yes"))
                        {
                            //make user GUI
                            System.out.println("Login Success" + " " + ty);
                        }
                        else
                        {
                            String error = "User does not exist.\nPlease try again.";
                            JOptionPane.showMessageDialog(null, error, "Error", JOptionPane.PLAIN_MESSAGE);
                        }
                    }catch (IOException err4)
                    {   err4.printStackTrace();    }
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
                    stringOut.println("over");
                    try{
                        stringOut.close();
                        stringIn.close();
                        objIn.close();
                        objOut.close();
                        socket.close();
                    }catch(IOException err2)
                    {
                        System.out.println(err2.getMessage());
                        err2.printStackTrace();
                    }
                    System.exit(0);
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

        String[] ClientTypes = {"Passenger", "Administrator"};

        JTextField first = new JTextField(15);
        JTextField last = new JTextField(15);
        JTextField email = new JTextField(15);
        JTextField passField = new JTextField(15);
        JComboBox type = new JComboBox(ClientTypes);

        JPanel main = new JPanel(new GridLayout(5, 1));
        JPanel main1 = new JPanel(new FlowLayout());
        JPanel main2 = new JPanel(new FlowLayout());
        JPanel main3 = new JPanel(new FlowLayout());
        JPanel main4 = new JPanel(new FlowLayout());
        JPanel main5 = new JPanel(new FlowLayout());

        main1.add(new JLabel("First Name"));
        main1.add(first);
        main2.add(new JLabel("Last Name"));
        main2.add(last);
        main3.add(new JLabel("E-mail        "));
        main3.add(email);
        main4.add(new JLabel("Password "));
        main4.add(passField);
        main5.add(new JLabel("User Type"));
        main5.add(type);

        main.add(main1);
        main.add(main2);
        main.add(main3);
        main.add(main4);
        main.add(main5);

        JPanel bot = new JPanel(new FlowLayout());
        bot.add(confirm);

        //button listener for Sign-Up GUI
        class signUpListener implements ActionListener
        {
            public void actionPerformed(ActionEvent action)
            {
                if(action.getSource() == confirm)
                {
                    String f = first.getText().trim();
                    String l = last.getText().trim();
                    String e = email.getText().trim();
                    String p = passField.getText().trim();
                    String t = null;
                    if(type.getSelectedIndex() == 0)
                        t = "Passenger";
                    else
                        t = "Admin";

                    String error = "";
                    if(f.length() > 40 || f.length() < 1)
                    {
                        if(f.length() > 40)
                            error += "Your first name cannot be more than 40 characters.\n";

                        else
                            error += "Your first name cannot be empty.\n";
                    }
                    if(l.length() > 40 || l.length() < 1)
                    {
                        if(l.length() > 40)
                            error += "Your last name cannot be more than 40 characters.\n";
                        else
                            error += "Your last name cannot be empty.\n";
                    }
                    if(e.length() > 40 || e.length() < 1)
                    {
                        if(e.length() > 40)
                            error += "Your e-mail address cannot be more than 40 characters.\n";
                        else
                            error += "Your e-mail address cannot be empty.\n";
                    }
                    if((!e.contains(".com") && !e.contains(".ca")) || !e.contains("@"))
                        error += "Your e-mail address needs to be in this format: abc@def.com\n";
                    if(p.length() > 40 || p.length() < 1)
                    {
                        if(p.length() > 40)
                            error += "Your password cannot be more than 40 characters.\n";
                        else
                            error += "Your password cannot be empty.\n";
                    }

                    if(error.equals(""))
                    {
                        NewUserInfo info = new NewUserInfo(f, l, e, p, t);
                        try {
                            stringOut.println("adduser");
                            objOut.writeObject(info);
                            signUpFrame.dispose();
                        }catch(IOException err3)
                        {   err3.printStackTrace(); }
                    }
                    else
                        JOptionPane.showMessageDialog(null, error, "Input Data Error", JOptionPane.PLAIN_MESSAGE);
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

    public static void main(String[] args) { Client client = new Client("localhost", 8099);    }
}

