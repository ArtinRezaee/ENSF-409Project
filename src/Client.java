import sun.security.krb5.internal.Ticket;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class Client extends JFrame implements Runnable
{
    private JRadioButton passenger ;
    private JRadioButton admin;
    private JButton signIn;
    private JButton signUp;
    private JTextField idField;
    private JTextField passwordField;

    private String id = null;
    private String password = null;

    public Client()
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

        //button listener class for the main login screen
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
                    addClient();
                }
            }
        }

        ButtonListener listener = new ButtonListener();
        signIn.addActionListener(listener);
        signUp.addActionListener(listener);

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

    @Override
    public void run()
    {
        Client client = new Client();
    }

    //check if the user id and password is correct
    private Boolean checkUserExists(String id, String password)
    {

        return null;
    }

    //checks if the id and password fields is empty
    //error message shown if fields are empty
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

    private void addClient()
    {
        SignUpGUI signup = new SignUpGUI();

    }

    /** Member functions and objects of class FLightCatalogue and Ticket**/
    protected FlightCatalogue catalogue;
    protected ArrayList<Ticket> ticket = new ArrayList<>();
    protected String type;

    /** second constructor **/
    public Client(ArrayList<Ticket> ticket, FlightCatalogue catalogue, String type) {
        this.ticket = ticket;
        this.catalogue = catalogue;
        this.type = type;
    }

    /** Methods for clas Client **/
    protected void CreateGUI() {
        //TODO
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

    }
    */

    protected void search(String source, String dest, String date, int id){
        //TODO
    }
    protected void refresh(){
        //TODO
    }
    public static void main(String[] args) { Client client = new Client();    }
    }
