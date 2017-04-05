package frontEnd;

import backEnd.FlightCatalogue;
import backEnd.Flight;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.net.Socket;
import java.io.*;
import java.lang.String;

public class Client
{
    private Socket socket;
    private BufferedReader stringIn;
    private PrintWriter stringOut;
    private ObjectOutputStream objectOut;
    private ObjectInputStream objectIn;

    private String clientId;
    private String clientPassword;
    private String clientType;

    private ArrayList<Flight> flights;

    private JFrame loginGUI;
    private JFrame clientGUI;

    private String refreshQuery = "";

    public Client(String server, int port)
    {
        try
        {
            socket = new Socket(server, port);
            stringIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            stringOut = new PrintWriter((socket.getOutputStream()), true);
            objectOut = new ObjectOutputStream(socket.getOutputStream());
            objectIn = new ObjectInputStream(socket.getInputStream());

            loginGUI = makeLoginGUI();
            loginGUI.setVisible(true);
        }catch(IOException err1)
        {
            System.err.println(err1.getMessage());
            err1.printStackTrace();
        }
    }

    //creates the Login GUI
    private JFrame makeLoginGUI()
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
                            clientId = idField.getText().trim();
                            clientPassword = passwordField.getText().trim();
                            clientType = ty;
                            if(clientType.equals("Passenger"))
                                clientGUI = makePassengerGUI();
                            else
                                clientGUI = makeAdminGUI();

                            clientGUI.setVisible(true);
                            loginGUI.dispose();
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
                    makeSignUpClient();
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
                        objectIn.close();
                        objectOut.close();
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

        return loginFrame;
    }

    private JFrame makePassengerGUI()
    {
        JFrame passengerFrame = new JFrame();
        passengerFrame.setTitle("Access Level: Passenger");
        passengerFrame.setLayout(new GridLayout(1, 2));
        passengerFrame.setSize(775, 400);

        DefaultListModel<String> listModel = new DefaultListModel<String>();
        JList<String> listArea = new JList<String>(listModel);

        JButton search = new JButton("Search");
        JButton refresh = new JButton("Refresh");
        JTextField dateField = new JTextField(15);
        JTextField sourceField = new JTextField(15);
        JTextField destField = new JTextField(15);

        JButton book = new JButton("Book");
        JTextField flightNum = new JTextField(15);
        flightNum.setEditable(false);
        JTextField source = new JTextField(15);
        source.setEditable(false);
        JTextField destination = new JTextField(15);
        destination.setEditable(false);
        JTextField date = new JTextField(15);
        date.setEditable(false);
        JTextField time = new JTextField(15);
        time.setEditable(false);
        JTextField duration = new JTextField(15);
        duration.setEditable(false);
        JTextField availSeats = new JTextField(15);
        availSeats.setEditable(false);
        JTextField price = new JTextField(15);
        price.setEditable(false);
        JPanel right = new JPanel();
        right.setLayout(new BorderLayout());

        JPanel rightTop = new JPanel();
        JLabel rightTitle = new JLabel("Flight Information");
        rightTitle.setFont(new Font("Calibri", Font.BOLD, 20));
        rightTop.add(rightTitle, Component.CENTER_ALIGNMENT);

        JPanel rightCenter = new JPanel();
        rightCenter.setLayout(new GridLayout(9, 1));
        JPanel right1 = new JPanel(new FlowLayout());
        JPanel right2 = new JPanel(new FlowLayout());
        JPanel right3 = new JPanel(new FlowLayout());
        JPanel right4 = new JPanel(new FlowLayout());
        JPanel right5 = new JPanel(new FlowLayout());
        JPanel right6 = new JPanel(new FlowLayout());
        JPanel right7 = new JPanel(new FlowLayout());
        JPanel right8 = new JPanel(new FlowLayout());
        JPanel right9 = new JPanel(new FlowLayout());
        right1.add(new JLabel("Flight Number"));
        right1.add(flightNum);
        right2.add(new JLabel("Source            "));
        right2.add(source);
        right3.add(new JLabel("Destination     "));
        right3.add(destination);
        right4.add(new JLabel("Date                 "));
        right4.add(date);
        right5.add(new JLabel("Time                 "));
        right5.add(time);
        right6.add(new JLabel("Duration          "));
        right6.add(duration);
        right7.add(new JLabel("Seats Left       "));
        right7.add(availSeats);
        right8.add(new JLabel("Price                "));
        right8.add(price);
        right9.add(book);
        rightCenter.add(right1);
        rightCenter.add(right2);
        rightCenter.add(right3);
        rightCenter.add(right4);
        rightCenter.add(right5);
        rightCenter.add(right6);
        rightCenter.add(right7);
        rightCenter.add(right8);
        rightCenter.add(right9);

        right.add("North", rightTop);
        right.add("Center", rightCenter);

        JPanel left = new JPanel();
        left.setLayout(new GridLayout(2, 1));
        left.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 0));

        JPanel leftTop = new JPanel();
        leftTop.setLayout(new BorderLayout());

        JPanel leftTopN = new JPanel();
        JLabel lefttopTitle = new JLabel("Search Flights");
        lefttopTitle.setFont(new Font("Calibri", Font.BOLD, 20));
        leftTopN.add(lefttopTitle, Component.CENTER_ALIGNMENT);

        JPanel leftTopC = new JPanel();
        leftTopC.setLayout(new GridLayout(4, 1));
        JPanel left1 = new JPanel(new FlowLayout());
        JPanel left2 = new JPanel(new FlowLayout());
        JPanel left3 = new JPanel(new FlowLayout());
        JPanel left4 = new JPanel(new FlowLayout());
        left1.add(new JLabel("Date (YYYY-MM-DD)                 "));
        left1.add(dateField);
        left2.add(new JLabel("Source (City, Country)            "));
        left2.add(sourceField);
        left3.add(new JLabel("Destination (City, Country)    "));
        left3.add(destField);
        left4.add(search);
        left4.add(refresh);

        leftTopC.add(left1,Component.LEFT_ALIGNMENT);
        leftTopC.add(left2, Component.LEFT_ALIGNMENT);
        leftTopC.add(left3, Component.LEFT_ALIGNMENT);
        leftTopC.add(left4, Component.LEFT_ALIGNMENT);

        leftTop.add("North", leftTopN);
        leftTop.add("Center", leftTopC);

        listArea.setVisibleRowCount(8);
        JScrollPane listPanel = new JScrollPane(listArea);
        left.add(leftTop);
        left.add(listPanel);

        passengerFrame.add(left);
        passengerFrame.add(right);

        JLabel rightLabel = new JLabel("Flight Information", SwingConstants.CENTER);
        rightLabel.setFont(new Font("Calibri", Font.BOLD, 20));

        class passengerButtonListener implements ActionListener
        {
            @Override
            public void actionPerformed(ActionEvent action)
            {
                if(action.getSource() == search)
                {
                    listModel.removeAllElements();
                    flights = null;
                    String date = "";
                    String source = "";
                    String dest = "";
                    String error = "";
                    if(dateField.getText().trim().length() != 0)
                    {
                        date = dateField.getText().trim();
                        for(int i = 0; i < date.length(); i++)
                        {
                            char a = date.charAt(i);
                            if(i == 4 || i == 7) {
                                if(a != '-')
                                    error = "The date has to be in this format: YYYY-MM-DD\n";
                            }
                            else {
                                if(a < 48 || a > 57)
                                    error = "The date has to be in this format: YYYY-MM-DD\n";
                            }
                            if(i > 10)
                                error = "The date has to be in this format: YYYY-MM-DD\n";

                        }
                        if(!error.equals(""))
                            date = "";
                    }
                    if(sourceField.getText().trim().length() != 0)
                        source = sourceField.getText().trim();
                    if(destField.getText().trim().length() != 0)
                        dest = destField.getText().trim();

                    String query = "";
                    if(error.equals("")) {
                        if(!date.equals("")) {
                            query = "Date = '" + date + "'";
                            if(!source.equals(""))
                                query += " AND Source = '" + source + "'";
                            if(!dest.equals(""))
                                query += " AND Destination = '" + dest + "'";
                        }
                        else if(!source.equals("")) {
                            query = "Source = '" + source + "'";
                            if(!dest.equals(""))
                                query += " AND Destination = '" + dest + "'";
                        }
                        else if(!dest.equals("")) {
                            query = "Destination = '" + dest + "'";
                        }
                        stringOut.println("searchflights");
                        refreshQuery = query;
                        stringOut.println(query);
                        try{
                            String line = stringIn.readLine();
                            FlightCatalogue catalogue = null;
                            if(line.equals("catalogincoming"))
                                catalogue = (FlightCatalogue) objectIn.readObject();
                            flights = catalogue.getFlights();
                            for(int i = 0; i < flights.size(); i++)
                                listModel.addElement(flights.get(i).toString());
                        }catch(Exception errx)
                        {   errx.printStackTrace(); }
                    }
                    else
                        JOptionPane.showMessageDialog(null, error, "Input Error", JOptionPane.PLAIN_MESSAGE);
                }
                else if(action.getSource() == book)
                {

                }
                else if(action.getSource() == refresh) {
                    listModel.removeAllElements();
                    flights = null;
                    if(!refreshQuery.equals("")) {
                        stringOut.println("searchflights");
                        stringOut.println(refreshQuery);
                        try{
                            String line = stringIn.readLine();
                            FlightCatalogue catalogue = null;
                            if(line.equals("catalogincoming"))
                                catalogue = (FlightCatalogue) objectIn.readObject();
                            flights = catalogue.getFlights();
                            for(int i = 0; i < flights.size(); i++)
                                listModel.addElement(flights.get(i).toString());
                        }catch(Exception errx)
                        {   errx.printStackTrace(); }
                    }
                }
            }
        }

        class passengerListListener implements ListSelectionListener
        {
            @Override
            public void valueChanged(ListSelectionEvent e)
            {
                int index = listArea.getSelectedIndex();
                if (index >= 0)
                {
                    flightNum.setText(String.valueOf(flights.get(index).getNum()));
                    source.setText(flights.get(index).getSrc());
                    destination.setText(flights.get(index).getDest());
                    date.setText(flights.get(index).getDate());
                    time.setText(flights.get(index).getTime());
                    duration.setText(flights.get(index).getDur());
                    availSeats.setText(String.valueOf(flights.get(index).getAvailSeats()));
                    price.setText(String.valueOf(flights.get(index).getPrice()));
                }
            }
        }

        passengerButtonListener buttonListener = new passengerButtonListener();
        search.addActionListener(buttonListener);
        refresh.addActionListener(buttonListener);
        book.addActionListener(buttonListener);

        passengerListListener listListener = new passengerListListener();
        listArea.addListSelectionListener(listListener);

        passengerFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        passengerFrame.addWindowListener(new java.awt.event.WindowAdapter() {
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
                        objectIn.close();
                        objectOut.close();
                        socket.close();
                    }catch(IOException err2)
                    {
                        System.out.println(err2.getMessage());
                        err2.printStackTrace();
                    }
                    clientGUI.dispose();
                }
            }
        });

        return passengerFrame;
    }

    private JFrame makeAdminGUI()
    {

        return null;
    }

    //creates the Sign-Up GUI
    private void makeSignUpClient()
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
            @Override
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
                    if(f.length() > 20 || f.length() < 1)
                    {
                        if(f.length() > 20)
                            error += "Your first name cannot be more than 40 characters.\n";

                        else
                            error += "Your first name cannot be empty.\n";
                    }
                    if(l.length() > 20 || l.length() < 1)
                    {
                        if(l.length() > 20)
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
                        error += "Your e-mail address needs to be in this format: abc@def.com or abc@def.ca\n";
                    if(p.length() > 20 || p.length() < 1)
                    {
                        if(p.length() > 20)
                            error += "Your password cannot be more than 40 characters.\n";
                        else
                            error += "Your password cannot be empty.\n";
                    }

                    if(error.equals(""))
                    {
                        NewUserInfo info = new NewUserInfo(f, l, e, p, t);
                        try {
                            stringOut.println("adduser");
                            objectOut.writeObject(info);
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

    public static void main(String[] args) { Client client = new Client("localhost", 8099);    }
}