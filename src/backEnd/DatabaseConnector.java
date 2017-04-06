 package backEnd;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;

public class DatabaseConnector
{
	//Source of connection to the database
	private Connection connection;
	
	//SQL statements to be executed
	private Statement statement;
	private PreparedStatement pStat;
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
	public void delete(String table, int id ){
		try {
			statement = connection.createStatement();
			if(table.equals("flights"))
				statement.executeUpdate("DELETE FROM " + table + " WHERE FlightNumber = " + id);
			else if(table.equals("tickets")){
				statement.executeUpdate("DELETE FROM " + table + " WHERE TicketID = " + id);
				this.incrementFlightSeats(id);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Deletes the specified tuple that meets the provided condition from the table. Designed for tables clients
	 * @param Email condition to delete the tuples based on
	 */
	public void deleteClient(String Email){
		try {
			statement = connection.createStatement();
			statement.executeUpdate("DELETE FROM clients" + " WHERE Email = '" + Email + "'");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method to decrement the available seats of a flight when a ticket is sold
	 * @param id of the flight
	 */
	public void decrementFlightSeats(int id){
		try {
			statement = connection.createStatement();
			String stmnt = "UPDATE flights SET AvailableSeats = AvailableSeats-1 WHERE FlightNumber = " + id;
			statement.executeUpdate(stmnt);
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
			String stmnt = "UPDATE flights SET AvailableSeats = AvailableSeats+1 WHERE FlightNumber = " + id;
			statement.executeUpdate(stmnt);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
}
