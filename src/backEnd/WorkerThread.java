package backEnd;

import frontEnd.Booking;

import java.io.*;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;


public class WorkerThread extends Thread
{
	private Socket socket;
	private BufferedReader stringIn;
	private PrintWriter stringOut;
	private ObjectInputStream objectIn;
	private ObjectOutputStream objectOut;
	private DatabaseConnector db;
	private String id;
	private String pass;
	private String type;
	
	public WorkerThread(Socket s){
		try {
			id = "";
			pass = "";
			type = "";
			socket = s;
			stringIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			stringOut = new PrintWriter(socket.getOutputStream(),true);
			objectIn = new ObjectInputStream(socket.getInputStream());
			objectOut = new ObjectOutputStream(socket.getOutputStream());
			db =  new DatabaseConnector();
		} catch (IOException e)
		{	e.printStackTrace();	}
	}
	
	public void run(){
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
					try {
						stringOut.println("catalogincoming");
						objectOut.writeObject(catalogue);
					}catch(IOException err3)
					{   err3.printStackTrace(); }
				}
				else if(line.equals("Booking"))
				{
					synchronized(this){
						Booking book = (Booking)objectIn.readObject();
	
						int flightID = book.getFlightNumber();
						String email = book.getMail();
						Random randomNum = new Random();
						int ticketID =randomNum.nextInt(1000000);
	
						ResultSet set1 = db.search("clients","Email = '" + email + "'");
						String passengerFirstName = "";
						String passengerLastName = "";
						if(set1.next()) {
							passengerFirstName = set1.getString("FirstName");
							passengerLastName = set1.getString("LastName");
						}
						ResultSet set2 = db.search("flights","FlightNumber = " + flightID);
						String flightFrom="";
						String flightTo="";
						String flightDate="";
						String flightTime="";
						String flightDuration="";
						double flightCost = 0;
						int seats = 0;
						if(set2.next())
						{
							flightFrom = set2.getString("Source");
							flightTo = set2.getString("Destination");
							flightDate = set2.getString("Date");
							flightTime = set2.getString("Time");
							flightDuration = set2.getString("Duration");
							flightCost = set2.getDouble("Price")*1.07;
							seats = set2.getInt("AvailableSeats");
						}
						if(seats != 0) {
							db.insert("tickets", "'" + flightID + "', '" + email +"', '" +ticketID +"'");
	
							Ticket ticket = new Ticket(passengerFirstName, passengerLastName, flightFrom, flightTo,
															flightDate,flightTime ,flightDuration,flightCost, ticketID, flightID);
							db.decrementFlightSeats(flightID);
	
							try {
								stringOut.println("Booking successful");
								objectOut.writeObject(ticket);
							}catch(IOException err3)
							{   err3.printStackTrace(); }
						}
					}
				}
				else if(line.equals("addmultipleflights"))
				{
					FlightCatalogue catalog = (FlightCatalogue) objectIn.readObject();
					db.addFlights(catalog);
				}
				else if(line.equals("addoneflight"))
				{
					String query = stringIn.readLine();
					db.insert("flights", query);
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
