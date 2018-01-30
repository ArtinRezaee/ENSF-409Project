package backEnd;

import frontEnd.Booking;
import java.io.*;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class WorkerThread extends Thread
{
    // Required Variables
	private Socket socket;
	private BufferedReader stringIn;
	private PrintWriter stringOut;
	private ObjectInputStream objectIn;
	private ObjectOutputStream objectOut;
	private DatabaseConnector db;
	private String id;
	private String pass;
	private String type;
	
    /**
     * Worker thread constructor
     * @param s, gets a socket to read and write to in order to communicate with the client and server
     * @param d, an instance of DatabaseConnector to fetch data from and wrtie tothe databse
     */
	public WorkerThread(Socket s, DatabaseConnector d){
		try {
			id = "";
			pass = "";
			type = "";
			socket = s;
			stringIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			stringOut = new PrintWriter(socket.getOutputStream(),true);
			objectIn = new ObjectInputStream(socket.getInputStream());
			objectOut = new ObjectOutputStream(socket.getOutputStream());
			db =  d;
		} catch (IOException e)
		{	e.printStackTrace();	}
	}
	
	/**
	 * Method to run the thread and start communication with the client. Backend logic and decision process of the program is implemented here
	 */
	public void run()
	{
		String line = "";
		while(!line.equals("over"))
		{
			try
			{
				line = stringIn.readLine();
				if(line.equals("adduser"))
				{
					UserInfo info = (UserInfo)objectIn.readObject();
					db.insert("clients", "'"+info.getMail()+"', '"+ info.getFirst() +"', '"
							  + info.getLast() +"', '" +info.getPass() + "', '"+ info.getType() +"'");
				}
				else if(line.equals("checklogin"))
				{
					String i = stringIn.readLine();
					String p = stringIn.readLine();
					String t = stringIn.readLine();
					ResultSet set = db.search("clients", "Email = '"+ i + "' AND Password = '" + p + "' AND Type = '" + t +"'");
					if(set.next())
					{
						stringOut.println("yes");
						id = i;
						pass = p;
						type = t;
						System.out.println(i + " connected");
					}
					else
						stringOut.println("no");
				}
				else if(line.equals("searchflights"))
				{
					String query = stringIn.readLine();
					ResultSet set = db.search("flights", query);
					FlightCatalogue catalogue = new FlightCatalogue(set);
					if(catalogue.checkFlights() == true) {
						try {
							stringOut.println("catalogincoming");
							objectOut.writeObject(catalogue);
						}catch(IOException err3)
						{   err3.printStackTrace(); }
					}
					else
						stringOut.println("No Flights Found");

				}
				else if(line.equals("Booking"))
				{
					Booking book = (Booking)objectIn.readObject();
					Ticket ticket = db.bookIt(book);
					if(ticket != null)
					{
						try {
							stringOut.println("Booking successful");
							objectOut.writeObject(ticket);
						}catch(IOException err3)
						{   err3.printStackTrace(); }
					}
					else
						stringOut.println("no Book");
				}
				else if(line.equals("addmultipleflights")) {
					FlightCatalogue catalog = (FlightCatalogue) objectIn.readObject();
					Boolean check = db.addFlights(catalog);
					if(check == true)
						stringOut.println("All flights added to database.");
					else
						stringOut.println("Error adding flights to database");
				}
				else if(line.equals("Delete Flight")){
					int id = Integer.parseInt((String)objectIn.readObject());
					Boolean check = db.delete("flights", id);
					if(check == true)
						stringOut.println("Flight deleted");
					else
						stringOut.println("Could not delete flight");
				}
				else if(line.equals("Search all users")){
					ResultSet set = db.search("clients", "");
					ArrayList<UserInfo> users = new ArrayList<UserInfo>();
					while(set.next()){
						UserInfo u = new UserInfo(set.getString("FirstName"),set.getString("LastName"),set.getString("Email"),
									 set.getString("Password"), set.getString("type"));
						users.add(u);
					}
					if(users.size() != 0){
						stringOut.println("Search Successfull");
						objectOut.writeObject(users);
					}
					else 
						stringOut.println("No results found");
				}
				else if(line.equals("Search email")){
					String in = stringIn.readLine();
					ResultSet set = db.search("clients", "Email = '" + in + "'");
					ArrayList<UserInfo> users = new ArrayList<UserInfo>();
					while(set.next()){
						UserInfo u = new UserInfo(set.getString("FirstName"),set.getString("LastName"),set.getString("Email"),
									 set.getString("Password"), set.getString("type"));
						users.add(u);
					}
					if(users.size() != 0){
						stringOut.println("Search Successfull");
						objectOut.writeObject(users);
					}
					else 
						stringOut.println("No results found");
				}
				else if(line.equals("Search types")){
					String in = stringIn.readLine();
					ResultSet set = db.search("clients", "Type = '" + in + "'");
					ArrayList<UserInfo> users = new ArrayList<UserInfo>();
					while(set.next()){
						UserInfo u = new UserInfo(set.getString("FirstName"),set.getString("LastName"),set.getString("Email"),
									 set.getString("Password"), set.getString("type"));
						users.add(u);
					}
					if(users.size() != 0){
						stringOut.println("Search Successfull");
						objectOut.writeObject(users);
					}
					else 
						stringOut.println("No results found");
				}
				else if(line.equals("Search condition")){
					String in = stringIn.readLine();
					String [] ins = in.split(" AND ");
					ResultSet set = db.search("clients", "Email = '" + ins[0] + "' AND Type = '" + ins[1] +"'");
					ArrayList<UserInfo> users = new ArrayList<UserInfo>();
					while(set.next()){
						UserInfo u = new UserInfo(set.getString("FirstName"),set.getString("LastName"),set.getString("Email"),
									 set.getString("Password"), set.getString("type"));
						System.out.println(u.toString());
						users.add(u);
					}
					if(users.size() != 0){
						stringOut.println("Search Successfull");
						objectOut.writeObject(users);
					}
					else 
						stringOut.println("No results found");
				}
				else if(line.equals("Delete user")){
					String mail = (String)objectIn.readObject();
					Boolean check = db.deleteClient(mail);
					if(check == true)
						stringOut.println("User deleted");
					else
						stringOut.println("Could not delete user");
				}
				else if(line.equals("addoneflight")){
					String query = stringIn.readLine();
					Boolean check = db.insert("flights", query);
					if(check == true)
						stringOut.println("Added the flight");
					else
						stringOut.println("Could not add the flight");
				}
				else if(line.equals("Search all tickets"))
				{
					ResultSet set = db.search("tickets", "");
					ArrayList<Ticket> tickets = new ArrayList<Ticket>();
					while(set.next()){
						Ticket t = new Ticket(set.getString("ClientEmail"), set.getInt("FlightNumber"), set.getInt("TicketID"));
						tickets.add(t);
					}
					if(tickets.size() != 0){
						stringOut.println("Search Successfull");
						objectOut.writeObject(tickets);
					}
					else
						stringOut.println("No results found");
				}
				else if(line.equals("Search fnum-tickets"))
				{
					String in = stringIn.readLine();
					ResultSet set = db.search("tickets", in);
					ArrayList<Ticket> tickets = new ArrayList<Ticket>();
					while(set.next()){
						Ticket t = new Ticket(set.getString("ClientEmail"), set.getInt("FlightNumber"), set.getInt("TicketID"));
						tickets.add(t);
					}
					if(tickets.size() != 0){
						stringOut.println("Search Successfull");
						objectOut.writeObject(tickets);
					}
					else
						stringOut.println("No results found");
				}
				else if(line.equals("Search email-tickets"))
				{
					String in = stringIn.readLine();
					ResultSet set = db.search("tickets", in);
					ArrayList<Ticket> tickets = new ArrayList<Ticket>();
					while(set.next()){
						Ticket t = new Ticket(set.getString("ClientEmail"), set.getInt("FlightNumber"), set.getInt("TicketID"));
						tickets.add(t);
					}
					if(tickets.size() != 0){
						stringOut.println("Search Successfull");
						objectOut.writeObject(tickets);
					}
					else
						stringOut.println("No results found");
				}
				else if(line.equals("Search condition-tickets"))
				{
					String in = stringIn.readLine();
					ResultSet set = db.search("tickets", in);
					ArrayList<Ticket> tickets = new ArrayList<Ticket>();
					while(set.next()){
						Ticket t = new Ticket(set.getString("ClientEmail"), set.getInt("FlightNumber"), set.getInt("TicketID"));
						tickets.add(t);
					}
					if(tickets.size() != 0){
						stringOut.println("Search Successfull");
						objectOut.writeObject(tickets);
					}
					else
						stringOut.println("No results found");
				}
				else if(line.equals("Delete ticket")){
					String tid = stringIn.readLine();
					Boolean check = db.delete("tickets", Integer.parseInt(tid));
					if(check == true)
						stringOut.println("Ticket deleted");
					else
						stringOut.println("Could not delete ticket");
				}
			}catch(IOException e){	
				e.printStackTrace();	
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		if(!id.equals(""))
			System.out.println(id + " disconnected");
		try{
			stringOut.close();
			objectOut.close();
			objectIn.close();
			stringIn.close();
			socket.close();
		}catch(IOException e)
		{	e.printStackTrace();	}
	}
}
