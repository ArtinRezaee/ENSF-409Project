package backEnd;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.ArrayList;
import frontEnd.Booking;
import java.util.Random;
import java.util.Iterator;

public class DatabaseConnector
{
	//Source of connection to the database
	private Connection connection;
	
	//SQL statements to be executed
	private Statement statement;
	private ResultSet result;
	
	//Constructor that creates a database and populates it
	public DatabaseConnector(){
		try {
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/409database", "root", "1234");
			statement = connection.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method to add a flights from a text file
	 * @param fileName
	 */
	public void addFlights(String fileName){
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
			String line;
			
			while((line = br.readLine()) != null){
				String[] result = line.split(";", line.length());
				String values = "";
				
				for(int i = 0; i < result.length-1; i++){
					values += "'" + result[i] + "',";
				}
				values += "'" + result[result.length-1] + "'";
				insert("flights", values);
				
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method to add a flights from a text file
	 * @param catalog that has all the flight on the database
	 */
	public void addFlights(FlightCatalogue catalog){
		
		ArrayList<Flight> flights = catalog.getFlights();
		
		for(int i=0; i<flights.size(); i++){
			String query = flights.get(i).getNum() + ", '" + flights.get(i).getSrc() + "', '" + flights.get(i).getDest()+ "', '"
							+ flights.get(i).getDate() + "', '" + flights.get(i).getTime() + "', '" + flights.get(i).getDur() +
							"', " + flights.get(i).getTotalSeats() + ", " + flights.get(i).getAvailSeats() + ", " + 
							flights.get(i).getPrice();
			
			insert("flights" , query);
		}
	}
	
	
	public void insert(String table, String values){
		try {
			
			statement = connection.createStatement();
			String stmnt = "INSERT INTO " + table + " VALUES(" + values + ")";
			statement.executeUpdate(stmnt);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
		
	/**
	 * Selects all the tuples from the specified table that meet the provided condition
	 * @param table table to select from
	 * @param condition condition to select tuples based on
	 * @return returns a table with the results
	 */
	public ResultSet search(String table, String condition){
		ResultSet result = null;
		try {
			statement = connection.createStatement();
			String query;
			if(!condition.equals(""))
				query = "SELECT * FROM " + table + " WHERE " + condition;
			else
				query = "SELECT * FROM " + table;
			result = statement.executeQuery(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * Method to delete flights or tickets
	 * @param table the table that we want to delete
	 * @param id the primary key related to that table
	 */
	public synchronized Boolean delete(String table, int id ){
		try {
			statement = connection.createStatement();
			if(table.equals("flights"))
				statement.executeUpdate("DELETE FROM " + table + " WHERE FlightNumber = " + id);
			else if(table.equals("tickets")){
				ResultSet set = statement.executeQuery("SELECT FlightNumber FROM tickets WHERE TicketID = " + id);
				set.next();
				int fn = set.getInt("FlightNumber");
				this.incrementFlightSeats(fn);
				statement.executeUpdate("DELETE FROM " + table + " WHERE TicketID = " + id);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * Deletes the specified tuple that meets the provided condition from the table. Designed for tables clients
	 * @param Email condition to delete the tuples based on
	 */
	public synchronized Boolean deleteClient(String Email){
		try {
			statement = connection.createStatement();
			statement.executeUpdate("DELETE FROM clients" + " WHERE Email = '" + Email + "'");
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * Method to decrement the available seats of a flight when a ticket is sold
	 * @param id of the flight
	 */
	public void decrementFlightSeats(int id){
		try {
			statement = connection.createStatement();
			String query = "UPDATE flights SET AvailableSeats = AvailableSeats-1 WHERE FlightNumber = " + id;
			statement.executeUpdate(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Method to increment the available seats of a flight when a ticket is canceled
	 * @param id of the flight
	 */
	public void incrementFlightSeats(int id){
		try {
			statement = connection.createStatement();
			String query = "UPDATE flights SET AvailableSeats = AvailableSeats+1 WHERE FlightNumber = " + id;
			statement.executeUpdate(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

	public synchronized Ticket bookIt(Booking book)
	{
		Ticket ticket = null;
		try{
			int flightID = book.getFlightNumber();
			String email = book.getMail();
			Random randomNum = new Random();
			int ticketID =randomNum.nextInt(1000000);

			ResultSet set1 = this.search("clients","Email = '" + email + "'");
			String passengerFirstName = "";
			String passengerLastName = "";
			if(set1.next()) {
				passengerFirstName = set1.getString("FirstName");
				passengerLastName = set1.getString("LastName");
			}
			ResultSet set2 = this.search("flights","FlightNumber = " + flightID);
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
			if(seats != 0)
			{
				this.insert("tickets", "'" + flightID + "', '" + email +"', '" +ticketID +"'");

				ticket = new Ticket(passengerFirstName, passengerLastName, flightFrom, flightTo,
						flightDate,flightTime ,flightDuration,flightCost, ticketID, flightID);
				this.decrementFlightSeats(flightID);
			}
		}
		catch(SQLException e)
		{	e.printStackTrace();	}

		return ticket;
	}
}
