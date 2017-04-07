package frontEnd;

import backEnd.Flight;
import backEnd.FlightCatalogue;
import backEnd.Ticket;
import backEnd.UserInfo;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Client
{
    private Socket socket;
    private BufferedReader stringIn;
    private PrintWriter stringOut;
    private ObjectOutputStream objectOut;
    private ObjectInputStream objectIn;

    private String clientId;
    private String clientType;
    private String mail;
    private String tid;
    private String ty;
    
    private JFrame loginGUI;
    private JFrame clientGUI;

    private ArrayList<Flight> flights;
    private String refreshQuery;

    public Client(String server, int port)
    {
        try
        {
            flights = null;
            loginGUI = null;
            clientGUI = null;
            refreshQuery = null;
            mail = "";
            tid = "";
            ty = "";

            socket = new Socket(server, port);
            stringIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            stringOut = new PrintWriter((socket.getOutputStream()), true);
            objectOut = new ObjectOutputStream(socket.getOutputStream());
            objectIn = new ObjectInputStream(socket.getInputStream());

            loginGUI = makeLoginGUI();
            loginGUI.setVisible(true);
        }catch(IOException err1)
        {   err1.printStackTrace();     }
    }

    //creates the login GUI
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
                        if(valid.equals("yes")) {
                            clientId = idField.getText().trim();
                            clientType = ty;
                            if(clientType.equals("Passenger"))
                                clientGUI = makePassengerGUI();
                            else
                                clientGUI = makeAdminGUI();

                            clientGUI.setVisible(true);
                            loginGUI.dispose();
                        }
                        else {
                            String error = "User does not exist.\nPlease try again.";
                            JOptionPane.showMessageDialog(null, error, "Error", JOptionPane.PLAIN_MESSAGE);
                        }
                    }catch (IOException err4)
                    {   err4.printStackTrace();    }
                }
                else if(action.getSource() == signUp)
                {   makeSignUpGUI();    }
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

    //creates passenger GUI
    private JFrame makePassengerGUI()
    {
        JFrame passengerFrame = new JFrame();
        passengerFrame.setTitle("Passenger: " + clientId);
        passengerFrame.setLayout(new GridLayout(1, 2));
        passengerFrame.setSize(850, 500);

        DefaultListModel<String> listModel = new DefaultListModel<String>();
        JList<String> listArea = new JList<String>(listModel);

        JButton search = new JButton("Search");
        JButton refresh = new JButton("Refresh");
        JButton book = new JButton("Book");
        JTextField dateField = new JTextField(15);
        JTextField sourceField = new JTextField(15);
        JTextField destField = new JTextField(15);

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

        class passengerButtonListener implements ActionListener
        {
            @Override
            public void actionPerformed(ActionEvent action)
            {
                if(action.getSource() == search)
                {
                    search.setEnabled(false);
                    flightNum.setText("");
                    source.setText("");
                    destination.setText("");
                    date.setText("");
                    time.setText("");
                    duration.setText("");
                    availSeats.setText("");
                    price.setText("");
                    listModel.removeAllElements();
                    flights = null;
                    String date = "";
                    String source = "";
                    String dest = "";
                    String error = "";
                    if(dateField.getText().trim().length() != 0) {
                        date = dateField.getText().trim();
                        for(int i = 0; i < date.length(); i++) {
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
                        refreshQuery = query;
                        stringOut.println("searchflights");
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
                    search.setEnabled(true);
                }
                else if(action.getSource() == book)
                {
                    book.setEnabled(false);
                	JTextField[] allFields = {flightNum, source, destination, date, time, duration, availSeats, price};
                	boolean isEmpty = false;

                	for(int i = 0; i < allFields.length; i++){
                		if(allFields[i].getText().trim().equals(""))
                			isEmpty = true;
                	}
                	if(!isEmpty){
                		Booking booking = new Booking(clientId,Integer.parseInt(flightNum.getText()));
                		stringOut.println("Booking");
                		try {
							objectOut.writeObject(booking);
							String line = stringIn.readLine();
							if(line.equals("Booking successful")){
								Ticket ticket = (Ticket)objectIn.readObject();
								int res = JOptionPane.showOptionDialog(null, "Booking Successful.\n", "Booking info", 
										  JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new 
										  String[]{"Print Ticket","Cancel"}, "default");
								
								if(res == 0){
									ticket.print();
								}
							}
							else {
                                JOptionPane.showMessageDialog(null, "Flight is full", "Error", JOptionPane.PLAIN_MESSAGE);
							}
						} catch (IOException e) {
							e.printStackTrace();
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}
                	}
                	else
                		JOptionPane.showMessageDialog(null, "Please select a flight to book", "Input Error", JOptionPane.PLAIN_MESSAGE);
                    book.setEnabled(true);
                }
                else if(action.getSource() == refresh)
                {
                    refresh.setEnabled(false);
                    if(refreshQuery != null) {
                        flightNum.setText("");
                        source.setText("");
                        destination.setText("");
                        date.setText("");
                        time.setText("");
                        duration.setText("");
                        availSeats.setText("");
                        price.setText("");
                        listModel.removeAllElements();
                        flights = null;
                        stringOut.println("searchflights");
                        stringOut.println(refreshQuery);
                        try{
                            String line = stringIn.readLine();
                            FlightCatalogue catalogue = null;
                            if(line.equals("catalogincoming"))
                                catalogue = (FlightCatalogue) objectIn.readObject();
                            flights = catalogue.getFlights();
                            if(flights.size() != 0) {
                                for(int i = 0; i < flights.size(); i++)
                                    listModel.addElement(flights.get(i).toString());
                            }
                            else
                                JOptionPane.showMessageDialog(null, "No Flights Found");
                        }catch(Exception errx)
                        {   errx.printStackTrace(); }
                    }
                    refresh.setEnabled(true);
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
                    }catch(IOException err2) {
                        System.out.println(err2.getMessage());
                        err2.printStackTrace();
                    }
                    System.exit(0);
                }
            }
        });
        return passengerFrame;
    }

    //creates admin GUI
    private JFrame makeAdminGUI()
    {
        JFrame adminFrame = new JFrame();
        adminFrame.setTitle("Admin: " + clientId);
        adminFrame.setLayout(new GridLayout(1, 4));
        adminFrame.setSize(1700, 600);

        //Search Flights and Book fields
        DefaultListModel<String> listModelFlights = new DefaultListModel<String>();
        JList<String> listAreaFlights = new JList<String>(listModelFlights);
        listAreaFlights.setVisibleRowCount(8);
        JScrollPane scrollFlights = new JScrollPane(listAreaFlights);
        JButton searchF = new JButton("Search");
        JButton refreshF = new JButton("Refresh");
        JButton bookF = new JButton("Book");
        JButton deleteF = new JButton("Delete");
        JTextField dateField = new JTextField(15);
        JTextField sourceField = new JTextField(15);
        JTextField destField = new JTextField(15);

        //add Flights fields
        JButton addF = new JButton("Add One Flight");
        JButton addFlights = new JButton("Add Flights");
        JTextField fileName = new JTextField(10);

        //Search tickets fields
        DefaultListModel<String> listModelTickets = new DefaultListModel<String>();
        JList<String> listAreaTickets = new JList<String>(listModelTickets);
        listAreaTickets.setVisibleRowCount(8);
        JScrollPane scrollTickets = new JScrollPane(listAreaTickets);
        JButton searchT = new JButton("Search Tickets");
        JButton deleteT = new JButton("Delete Ticket");
        JTextField fnumField = new JTextField(10);
        JTextField emailField = new JTextField(10);

        //Search Users fields
        DefaultListModel<String> listModelUsers = new DefaultListModel<String>();
        JList<String> listAreaUsers = new JList<String>(listModelUsers);
        listAreaUsers.setVisibleRowCount(8);
        JScrollPane scrollUsers = new JScrollPane(listAreaUsers);
        JButton searchU = new JButton("Search Users");
        JButton deleteU = new JButton("Delete User");
        JTextField eField = new JTextField(10);
        JTextField typeField = new JTextField(10);

        //Flight Info
        JTextField flightNum = new JTextField(5);
        flightNum.setEditable(false);
        JTextField source = new JTextField(15);
        source.setEditable(false);
        JTextField destination = new JTextField(15);
        destination.setEditable(false);
        JTextField date = new JTextField(7);
        date.setEditable(false);
        JTextField time = new JTextField(5);
        time.setEditable(false);
        JTextField duration = new JTextField(5);
        duration.setEditable(false);
        JTextField availSeats = new JTextField(5);
        availSeats.setEditable(false);
        JTextField price = new JTextField(5);
        price.setEditable(false);

        //col 1 - flights
        JPanel col1 = new JPanel();
        col1.setLayout(new GridLayout(2, 1));
        JPanel oneTop = new JPanel();
        oneTop.setLayout(new BorderLayout());
        JPanel oneTopN = new JPanel();
        JLabel oneTopTitle = new JLabel("Search Flights");
        oneTopTitle.setFont(new Font("Calibri", Font.BOLD, 20));
        oneTopN.add(oneTopTitle, Component.CENTER_ALIGNMENT);
        JPanel oneTopC = new JPanel();
        oneTopC.setLayout(new GridLayout(4, 1));
        JPanel one1 = new JPanel(new FlowLayout());
        JPanel one2 = new JPanel(new FlowLayout());
        JPanel one3 = new JPanel(new FlowLayout());
        JPanel one4 = new JPanel(new FlowLayout());
        one1.add(new JLabel("Date (YYYY-MM-DD)                 "));
        one1.add(dateField);
        one2.add(new JLabel("Source (City, Country)            "));
        one2.add(sourceField);
        one3.add(new JLabel("Destination (City, Country)    "));
        one3.add(destField);
        one4.add(searchF);
        one4.add(refreshF);
        oneTopC.add(one1,Component.LEFT_ALIGNMENT);
        oneTopC.add(one2, Component.LEFT_ALIGNMENT);
        oneTopC.add(one3, Component.LEFT_ALIGNMENT);
        oneTopC.add(one4, Component.LEFT_ALIGNMENT);
        oneTop.add("North", oneTopN);
        oneTop.add("Center", oneTopC);
        col1.add(oneTop);
        col1.add(scrollFlights);

        //col 2 - flights
        JPanel col2 = new JPanel();
        col2.setLayout(new GridLayout(10, 1));
        JLabel secondTitle1 = new JLabel("Add Flights");
        secondTitle1.setFont(new Font("Calibri", Font.BOLD, 20));
        JLabel secondTitle2 = new JLabel("Flight Information");
        secondTitle2.setFont(new Font("Calibri", Font.BOLD, 20));
        JPanel second1 = new JPanel(new FlowLayout());
        JPanel second2 = new JPanel(new FlowLayout());
        JPanel second3 = new JPanel(new FlowLayout());
        JPanel second4 = new JPanel(new FlowLayout());
        JPanel second5 = new JPanel(new FlowLayout());
        JPanel second6 = new JPanel(new FlowLayout());
        JPanel second7 = new JPanel(new FlowLayout());
        JPanel second8 = new JPanel(new FlowLayout());
        JPanel second9 = new JPanel(new FlowLayout());
        JPanel second10 = new JPanel(new FlowLayout());
        second1.add(secondTitle1, Component.CENTER_ALIGNMENT);
        second2.add(addF);
        second3.add(new JLabel("File Name"));
        second3.add(fileName);
        second3.add(addFlights);
        second4.add(secondTitle2, Component.CENTER_ALIGNMENT);
        second5.add(new JLabel("Flight Number"));
        second5.add(flightNum);
        second5.add(new JLabel("Date"));
        second5.add(date);
        second6.add(new JLabel("Time"));
        second6.add(time);
        second6.add(new JLabel("Duration"));
        second6.add(duration);
        second7.add(new JLabel("Seats Left"));
        second7.add(availSeats);
        second7.add(new JLabel("Price"));
        second7.add(price);
        second8.add(new JLabel("Source        "));
        second8.add(source);
        second9.add(new JLabel("Destination"));
        second9.add(destination);
        second10.add(bookF);
        second10.add(deleteF);
        col2.add(second1);
        col2.add(second2);
        col2.add(second3);
        col2.add(second4);
        col2.add(second5);
        col2.add(second6);
        col2.add(second7);
        col2.add(second8);
        col2.add(second9);
        col2.add(second10);

        //col3 - tickets
        JPanel col3 = new JPanel();
        col3.setLayout(new GridLayout(2, 1));
        JPanel threeTop = new JPanel();
        threeTop.setLayout(new BorderLayout());
        JPanel threeTopN = new JPanel();
        JLabel threeTopTitle = new JLabel("Search Tickets");
        threeTopTitle.setFont(new Font("Calibri", Font.BOLD, 20));
        threeTopN.add(threeTopTitle, Component.CENTER_ALIGNMENT);
        JPanel threeTopC = new JPanel();
        threeTopC.setLayout(new GridLayout(3, 1));
        JPanel three1 = new JPanel(new FlowLayout());
        JPanel three2 = new JPanel(new FlowLayout());
        JPanel three3 = new JPanel(new FlowLayout());
        three1.add(new JLabel("Flight Number"));
        three1.add(fnumField);
        three2.add(new JLabel("Email ID           "));
        three2.add(emailField);
        three3.add(searchT);
        three3.add(deleteT);
        threeTopC.add(three1,Component.LEFT_ALIGNMENT);
        threeTopC.add(three2, Component.LEFT_ALIGNMENT);
        threeTopC.add(three3, Component.LEFT_ALIGNMENT);
        threeTop.add("North", threeTopN);
        threeTop.add("Center", threeTopC);
        col3.add(threeTop);
        col3.add(scrollTickets);

        //col4 - users
        JPanel col4 = new JPanel();
        col4.setLayout(new GridLayout(2, 1));
        JPanel fourTop = new JPanel();
        fourTop.setLayout(new BorderLayout());
        JPanel fourTopN = new JPanel();
        JLabel fourTopTitle = new JLabel("Search Users");
        fourTopTitle.setFont(new Font("Calibri", Font.BOLD, 20));
        fourTopN.add(fourTopTitle, Component.CENTER_ALIGNMENT);
        JPanel fourTopC = new JPanel();
        fourTopC.setLayout(new GridLayout(3, 1));
        JPanel four1 = new JPanel(new FlowLayout());
        JPanel four2 = new JPanel(new FlowLayout());
        JPanel four3 = new JPanel(new FlowLayout());
        four1.add(new JLabel("Email ID     "));
        four1.add(eField);
        four2.add(new JLabel("User Type  "));
        four2.add(typeField);
        four3.add(searchU);
        four3.add(deleteU);
        fourTopC.add(four1,Component.LEFT_ALIGNMENT);
        fourTopC.add(four2, Component.LEFT_ALIGNMENT);
        fourTopC.add(four3, Component.LEFT_ALIGNMENT);
        fourTop.add("North", fourTopN);
        fourTop.add("Center", fourTopC);
        col4.add(fourTop);
        col4.add(scrollUsers);

        //mainframe
        adminFrame.add(col1);
        adminFrame.add(col2);
        adminFrame.add(col3);
        adminFrame.add(col4);

        class adminButtonListener implements ActionListener
        {
            @Override
            public void actionPerformed(ActionEvent action)
            {
                if(action.getSource() == searchF)
                {
                    searchF.setEnabled(false);
                    flightNum.setText("");
                    source.setText("");
                    destination.setText("");
                    date.setText("");
                    time.setText("");
                    duration.setText("");
                    availSeats.setText("");
                    price.setText("");
                    listModelFlights.removeAllElements();
                    flights = null;
                    String date = "";
                    String source = "";
                    String dest = "";
                    String error = "";
                    if(dateField.getText().trim().length() != 0) {
                        date = dateField.getText().trim();
                        for(int i = 0; i < date.length(); i++) {
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
                        refreshQuery = query;
                        stringOut.println("searchflights");
                        stringOut.println(query);
                        try{
                            String line = stringIn.readLine();
                            FlightCatalogue catalogue = null;
                            if(line.equals("catalogincoming"))
                                catalogue = (FlightCatalogue) objectIn.readObject();
                            flights = catalogue.getFlights();
                            for(int i = 0; i < flights.size(); i++)
                                listModelFlights.addElement(flights.get(i).toString());
                        }catch(Exception errx)
                        {   errx.printStackTrace(); }
                    }
                    else
                        JOptionPane.showMessageDialog(null, error, "Input Error", JOptionPane.PLAIN_MESSAGE);
                    searchF.setEnabled(true);
                }
                else if(action.getSource() == bookF)
                {
                    bookF.setEnabled(false);
                    JTextField[] allFields = {flightNum, source, destination, date, time, duration, availSeats, price};
                    boolean isEmpty = false;

                    for(int i = 0; i < allFields.length; i++){
                        if(allFields[i].getText().equals(""))
                            isEmpty = true;
                    }
                    if(!isEmpty){
                        Booking booking = new Booking(clientId,Integer.parseInt(flightNum.getText()));
                        stringOut.println("Booking");
                        try {
                            objectOut.writeObject(booking);
                            String line = stringIn.readLine();
                            if(line.equals("Booking successful")){
                                Ticket ticket = (Ticket)objectIn.readObject();
                                int res = JOptionPane.showOptionDialog(null, "Booking Successful.\n", "Booking info",
                                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new
                                                String[]{"Print Ticket","Cancel"}, "default");

                                if(res == 0){
                                    ticket.print();
                                }
                            }
                            else {
                                JOptionPane.showMessageDialog(null, "Flight is full", "Error", JOptionPane.PLAIN_MESSAGE);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    else
                        JOptionPane.showMessageDialog(null, "Please select a flight to book", "Input Error", JOptionPane.PLAIN_MESSAGE);
                    bookF.setEnabled(true);
                }
                else if(action.getSource() == refreshF)
                {
                    refreshF.setEnabled(false);
                    if(refreshQuery != null) {
                        flightNum.setText("");
                        source.setText("");
                        destination.setText("");
                        date.setText("");
                        time.setText("");
                        duration.setText("");
                        availSeats.setText("");
                        price.setText("");
                        listModelFlights.removeAllElements();
                        flights = null;
                        stringOut.println("searchflights");
                        stringOut.println(refreshQuery);
                        try{
                            String line = stringIn.readLine();
                            FlightCatalogue catalogue = null;
                            if(line.equals("catalogincoming"))
                                catalogue = (FlightCatalogue) objectIn.readObject();
                            flights = catalogue.getFlights();
                            if(flights.size() != 0) {
                                for(int i = 0; i < flights.size(); i++)
                                    listModelFlights.addElement(flights.get(i).toString());
                            }
                            else
                                JOptionPane.showMessageDialog(null, "No Flights Found");
                        }catch(Exception errx)
                        {   errx.printStackTrace(); }
                    }
                    refreshF.setEnabled(true);
                }
                else if(action.getSource() == addFlights)
                {
                    addFlights.setEnabled(false);
                    String file = "";
                    String error = "";
                    String[] arr;
                    ArrayList<Flight> flights = new ArrayList<>();

                    if(fileName.getText().trim().length() != 0)
                        file = fileName.getText().trim();
                    if(file.equals(""))
                        error += "File name cannot be empty!\n";
                    else {
                        try {
                            Scanner scan = new Scanner(new File(file));
                            String line = "";
                            while(scan.hasNextLine())
                            {
                                line = scan.nextLine();
                                arr = line.split(";", line.length());
                                if(arr.length == 9)
                                {
                                    int nm = Integer.parseInt(arr[0]);
                                    String src = arr[1];
                                    String dst = arr[2];
                                    String dt = arr[3];
                                    String tm = arr[4];
                                    String dur = arr[5];
                                    int ts = Integer.parseInt(arr[6]);
                                    int as = Integer.parseInt(arr[7]);
                                    double pr = Double.parseDouble(arr[8]);
                                    Flight x = new Flight(nm, src, dst, dt, tm, dur, ts, as, pr);
                                    flights.add(x);
                                }
                            }
                        }catch (IOException errx)
                        {error += "File input error\n";}
                    }
                    if(error.equals("")) {
                        FlightCatalogue catalog = new FlightCatalogue(flights);
                        try {
                            stringOut.println("addmultipleflights");
                            objectOut.writeObject(catalog);
                        }catch(IOException errx) {
                            error += "Error uploading flights to server\n";
                            errx.printStackTrace();
                        }
                    }
                    else
                        JOptionPane.showMessageDialog(null, error, "Input Data Error", JOptionPane.PLAIN_MESSAGE);
                    addFlights.setEnabled(true);
                }
                else if(action.getSource() == addF)
                    makeFlightGUI();
                else if(action.getSource() == deleteF)
                {
                    deleteF.setEnabled(false);
                	if(!flightNum.getText().trim().equals("")){
                		stringOut.println("Delete Flight");
                		try {
							objectOut.writeObject(flightNum.getText());
							String line = stringIn.readLine();
                            JOptionPane.showMessageDialog(null, line);
                            flightNum.setText("");
                            source.setText("");
                            destination.setText("");
                            date.setText("");
                            time.setText("");
                            duration.setText("");
                            availSeats.setText("");
                            price.setText("");
                            listModelFlights.removeAllElements();
						} catch (IOException e)
                        {   e.printStackTrace();    }
                	}
                    deleteF.setEnabled(true);
                }
                else if(action.getSource() == searchU)
                {
                    searchU.setEnabled(false);
                	listModelUsers.removeAllElements();
                	if(eField.getText().equals("") && typeField.getText().equals(""))
                		stringOut.println("Search all users");
                	else if(!eField.getText().trim().equals("") && typeField.getText().trim().equals("")){
                		stringOut.println("Search email");
                        stringOut.println(eField.getText().trim());
                	}
                	else if(eField.getText().trim().equals("") && !typeField.getText().trim().equals("")){
                		stringOut.println("Search types");
                        stringOut.println(typeField.getText().trim());
                	}
                	else{
                		stringOut.println("Search condition");
                        stringOut.println(eField.getText().trim() + " AND " + typeField.getText().trim());
                	}
                	try {
						if((stringIn.readLine()).equals("Search Successfull")){
							ArrayList<UserInfo> results = (ArrayList<UserInfo>)objectIn.readObject();
							for(int i = 0; i < results.size(); i++){
								String show = results.get(i).getMail() + " - " + results.get(i).getFirst()+ " " +
								results.get(i).getLast() + " - " + results.get(i).getType();
								listModelUsers.addElement(show);
							}
						}
						else
							JOptionPane.showMessageDialog(null, "No Users Found");
					} catch (IOException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
                    searchU.setEnabled(true);
                }
                else if(action.getSource() == deleteU)
                {
                    deleteU.setEnabled(false);
                	if(!mail.equals("") && !ty.equals("")) {
                	    if(ty.equals("Passenger")) {
                            stringOut.println("Delete user");
                            try {
                                objectOut.writeObject(mail);
                                String line = stringIn.readLine();
                                JOptionPane.showMessageDialog(null, "User Deleted");
                                listModelUsers.removeAllElements();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        else
                            JOptionPane.showMessageDialog(null, "Cannot delete an Admin");
                	}
                	else
                		JOptionPane.showMessageDialog(null, "User must be selected");
                    deleteU.setEnabled(true);
                }
                else if(action.getSource() == searchT)
                {
                    searchT.setEnabled(false);
                    tid = "";
                    listModelTickets.removeAllElements();
                    if(fnumField.getText().equals("") && emailField.getText().equals(""))
                        stringOut.println("Search all tickets");
                    else if(!fnumField.getText().trim().equals("") && emailField.getText().trim().equals("")){
                        stringOut.println("Search fnum-tickets");
                        stringOut.println("f.FlightNumber = " + fnumField.getText().trim());
                    }
                    else if(fnumField.getText().trim().equals("") && !emailField.getText().trim().equals("")){
                        stringOut.println("Search email-tickets");
                        stringOut.println("t.ClientEmail = '" + emailField.getText().trim() + "'");
                    }
                    else{
                        stringOut.println("Search condition-tickets");
                        stringOut.println("t.FlightNumber = " + fnumField.getText().trim() + " AND t.ClientEmail = '" + emailField.getText().trim() + "'");
                    }
                    try {
                        if((stringIn.readLine()).equals("Search Successfull")){
                            ArrayList<Ticket> results = (ArrayList<Ticket>)objectIn.readObject();
                            for(int i = 0; i < results.size(); i++){
                                String show = results.get(i).toString();
                                listModelTickets.addElement(show);
                            }
                        }
                        else
                            JOptionPane.showMessageDialog(null, "No Tickets Found");
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    searchT.setEnabled(true);
                }
                else if(action.getSource() == deleteT)
                {
                    deleteT.setEnabled(false);
                    if(!tid.equals("")){
                        stringOut.println("Delete ticket");
                        try {
                            stringOut.println(tid);
                            String line = stringIn.readLine();
                            JOptionPane.showMessageDialog(null, line);
                            listModelTickets.removeAllElements();
                        } catch (IOException e)
                        {  e.printStackTrace(); }
                    }
                    else
                        JOptionPane.showMessageDialog(null, "Ticket must be selected");
                    deleteT.setEnabled(true);
                }
            }
        }

        class adminListListenerFlight implements ListSelectionListener
        {
            @Override
            public void valueChanged(ListSelectionEvent e)
            {
                int index = listAreaFlights.getSelectedIndex();
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

        class adminListListenerTicket implements ListSelectionListener
        {
            @Override
            public void valueChanged(ListSelectionEvent e)
            {
                int index = listAreaTickets.getSelectedIndex();
                if (index >= 0) {
                    String line = new String(listModelTickets.get(index).toCharArray());
                    String [] arr = line.split(" - ");
                        tid = arr[2];
                }
            }
        }

        class adminListListenerUser implements ListSelectionListener
        {
            @Override
            public void valueChanged(ListSelectionEvent e)
            {
            	int index = listAreaUsers.getSelectedIndex();
                if (index >= 0) {
                	String line = new String(listModelUsers.get(index).toCharArray());
                	String [] arr = line.split(" - ");
                	mail = arr[0];
                	ty = arr[2];
                }
            }
        }

        adminButtonListener adminButtonListener = new adminButtonListener();
        searchF.addActionListener(adminButtonListener);
        refreshF.addActionListener(adminButtonListener);
        bookF.addActionListener(adminButtonListener);
        deleteF.addActionListener(adminButtonListener);
        addF.addActionListener(adminButtonListener);
        addFlights.addActionListener(adminButtonListener);
        searchT.addActionListener(adminButtonListener);
        deleteT.addActionListener(adminButtonListener);
        searchU.addActionListener(adminButtonListener);
        deleteU.addActionListener(adminButtonListener);

        adminListListenerFlight fListListener = new adminListListenerFlight();
        listAreaFlights.addListSelectionListener(fListListener);
        adminListListenerTicket tListListener = new adminListListenerTicket();
        listAreaTickets.addListSelectionListener(tListListener);
        adminListListenerUser uListListener = new adminListListenerUser();
        listAreaUsers.addListSelectionListener(uListListener);

        adminFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        adminFrame.addWindowListener(new java.awt.event.WindowAdapter() {
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
                    }catch(IOException err2) {
                        System.out.println(err2.getMessage());
                        err2.printStackTrace();
                    }
                    System.exit(0);
                }
            }
        });
        return adminFrame;
    }

    //creates the sign up GUI
    private void makeSignUpGUI()
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
                    if(f.length() > 20 || f.length() < 1) {
                        if(f.length() > 20)
                            error += "Your first name cannot be more than 40 characters.\n";
                        else
                            error += "Your first name cannot be empty.\n";
                    }
                    if(l.length() > 20 || l.length() < 1) {
                        if(l.length() > 20)
                            error += "Your last name cannot be more than 40 characters.\n";
                        else
                            error += "Your last name cannot be empty.\n";
                    }
                    if(e.length() > 40 || e.length() < 1) {
                        if(e.length() > 40)
                            error += "Your e-mail address cannot be more than 40 characters.\n";
                        else
                            error += "Your e-mail address cannot be empty.\n";
                    }
                    if((!e.contains(".com") && !e.contains(".ca")) || !e.contains("@"))
                        error += "Your e-mail address needs to be in this format: abc@def.com or abc@def.ca\n";
                    if(p.length() > 20 || p.length() < 1) {
                        if(p.length() > 20)
                            error += "Your password cannot be more than 40 characters.\n";
                        else
                            error += "Your password cannot be empty.\n";
                    }

                    if(error.equals("")) {
                        UserInfo info = new UserInfo(f, l, e, p, t);
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
                if(result == JOptionPane.YES_OPTION) {
                    stringOut.println("over");
                    try {
                        stringOut.close();
                        stringIn.close();
                        objectIn.close();
                        objectOut.close();
                        socket.close();
                    } catch (IOException err2) {
                        System.out.println(err2.getMessage());
                        err2.printStackTrace();
                    }
                    System.exit(0);
                }
            }
        });
    }

    private void makeFlightGUI()
    {
        JFrame flightFrame = new JFrame();
        flightFrame.setTitle("Add Flight");
        flightFrame.setLayout(new GridLayout(10, 1));
        flightFrame.setSize(375, 400);

        JButton confirmFlight = new JButton("Confirm");
        JTextField num = new JTextField(10);
        JTextField src = new JTextField(10);
        JTextField dst = new JTextField(10);
        JTextField date = new JTextField(10);
        JTextField time = new JTextField(10);
        JTextField dur = new JTextField(10);
        JTextField tseats = new JTextField(10);
        JTextField aseats = new JTextField(10);
        JTextField price = new JTextField(10);

        JPanel p1 = new JPanel(new FlowLayout());
        JPanel p2 = new JPanel(new FlowLayout());
        JPanel p3 = new JPanel(new FlowLayout());
        JPanel p4 = new JPanel(new FlowLayout());
        JPanel p5 = new JPanel(new FlowLayout());
        JPanel p6 = new JPanel(new FlowLayout());
        JPanel p7 = new JPanel(new FlowLayout());
        JPanel p8 = new JPanel(new FlowLayout());
        JPanel p9 = new JPanel(new FlowLayout());
        JPanel p10 = new JPanel(new FlowLayout());

        p1.add(new JLabel("Flight Number   "));
        p1.add(num);
        p2.add(new JLabel("Source                "));
        p2.add(src);
        p3.add(new JLabel("Destination        "));
        p3.add(dst);
        p4.add(new JLabel("Date                    "));
        p4.add(date);
        p5.add(new JLabel("Time                    "));
        p5.add(time);
        p6.add(new JLabel("Duration             "));
        p6.add(dur);
        p7.add(new JLabel("Total Seats        "));
        p7.add(tseats);
        p8.add(new JLabel("Available Seats "));
        p8.add(aseats);
        p9.add(new JLabel("Price                   "));
        p9.add(price);
        p10.add(confirmFlight);

        flightFrame.add(p1);
        flightFrame.add(p2);
        flightFrame.add(p3);
        flightFrame.add(p4);
        flightFrame.add(p5);
        flightFrame.add(p6);
        flightFrame.add(p7);
        flightFrame.add(p8);
        flightFrame.add(p9);
        flightFrame.add(p10);

        class flightListener implements ActionListener
        {
            @Override
            public void actionPerformed(ActionEvent action)
            {
                if(action.getSource() == confirmFlight)
                {
                    confirmFlight.setEnabled(false);
                    String error = "";
                    String one = num.getText().trim();
                    String two = src.getText().trim();
                    String three = dst.getText().trim();
                    String four = date.getText().trim();
                    String five = time.getText().trim();
                    String six = dur.getText().trim();
                    String seven = tseats.getText().trim();
                    String eight = aseats.getText().trim();
                    String nine = price.getText().trim();

                    try {
                        if(Integer.parseInt(one) <= 0)
                            error += "Flight Number has to be a positive integer.\n";
                        if(two.length() > 40 || two.length() < 1)
                            error += "Source cannot be empty and less than 40 characters.\n";
                        if(three.length() > 40 || three.length() < 1)
                            error += "Destination cannot be empty and less than 40 characters.\n";
                        if(four.length() != 0) {
                            for(int i = 0; i < four.length(); i++) {
                                char a = four.charAt(i);
                                if(i == 4 || i == 7) {
                                    if(a != '-') {
                                        error += "The date has to be in this format: YYYY-MM-DD\n";
                                        break;
                                    }
                                }
                                else {
                                    if(a < 48 || a > 57) {
                                        error += "The date has to be in this format: YYYY-MM-DD\n";
                                        break;
                                    }
                                }
                                if(i > 10) {
                                    error += "The date has to be in this format: YYYY-MM-DD\n";
                                    break;
                                }
                            }
                        }
                        if(four.length() == 0)
                            error += "Date cannot be empty.\n";
                        if(five.length() != 5 || five.charAt(2) != ':')
                            error += "Time has to be in this format: HH:MM\n";
                        if(six.length() != 5 || six.charAt(2) != ':')
                            error += "Duration has to be in this format: HH:MM\n";
                        if(Integer.parseInt(seven) < 1)
                            error += "Total seats has to be more than 0.\n";
                        if(Integer.parseInt(eight) < 0)
                            error += "Available seats has to 0 or more.\n";
                        if(Double.parseDouble(nine) < 0)
                            error += "Price seats has to 0 or more.\n";
                    }catch(Exception errx)
                    {   error += "Error in input format.\n";   }
                    if(error.equals("")) {
                        stringOut.println("addoneflight");
                        String query = Integer.parseInt(one) + ", '" + two + "', '" + three + "', '" + four + "', '" + five + "', '" + six + "', "
                                + Integer.parseInt(seven) + ", " + Integer.parseInt(eight) + ", " + Double.parseDouble(nine);
                        stringOut.println(query);
                        flightFrame.dispose();
                    }
                    else
                        JOptionPane.showMessageDialog(null, error, "Input Data Error", JOptionPane.PLAIN_MESSAGE);
                    confirmFlight.setEnabled(true);
                }
            }
        }

        flightListener flightListener = new flightListener();
        confirmFlight.addActionListener(flightListener);

        flightFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        flightFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(WindowEvent e)
            {
                JFrame frame = (JFrame)e.getSource();
                int result = JOptionPane.showConfirmDialog(frame, "Are you sure you don't want to add a flight?", "Exit Flight Adder", JOptionPane.YES_NO_OPTION);
                if(result == JOptionPane.YES_OPTION)
                    flightFrame.dispose();
            }
        });
        //flightFrame.pack();
        flightFrame.setVisible(true);
    }

    public static void main(String[] args) { Client client = new Client("192.168.1.66", 8099);    }
}