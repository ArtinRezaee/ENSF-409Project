package backEnd;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;

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
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/409dbs?autoReconnect=true&useSSL=false", "root", "1234");
			statement = connection.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
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
	
	public void insert(String table, String values){
		try {
			
			statement = connection.createStatement();
			String stmnt = "INSERT " + "INTO " + table+ " VALUES("+ values +")";
			System.out.println(stmnt);
			statement.executeUpdate(stmnt);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method to delete a tuple from the database.Only for flights and clients tables
	 * @param table Table that the tuple should be deleted from
	 * @param id id of the flight or client
	 */
	public void delete(String table, int id ){
		try {
			statement = connection.createStatement();
			statement.executeUpdate("DELETE FROM " + table + " WHERE id=" + id );
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Function to delete a booked ticket from table ticket
	 * @param flightNum the flight number on the ticket
	 * @param clientId the clientId of the passenger or Admin
	 * @param seatNum The seat number of the client
	 */
	public void voidTicket(int flightNum, int clientId, int seatNum){
		try {
			statement = connection.createStatement();
			statement.executeUpdate("DELETE FROM ticket WHERE fNumber=" + flightNum + " AND clientId=" + clientId + 
									" AND seatNum=" + seatNum);
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

	//method to add user to database - table clients
	public void addUser(String f, String l, String e, String p, String t)
	{
		try{
			String query = "INSERT INTO client (Email, FirstName, LastName, Password, Type) VALUES (?,?,?,?,?)";
			pStat = connection.prepareStatement(query);
			pStat.setString(1, e);
			pStat.setString(2, f);
			pStat.setString(3, l);
			pStat.setString(4, p);
			pStat.setString(5, t);
			pStat.executeUpdate();
		}catch(SQLException err)
		{	err.printStackTrace(); }
	}

	public Boolean searchUser(String id, String pass, String type)
	{
		try{
			String query = "SELECT * FROM client WHERE Email = ? AND Password = ? AND Type = ?";
			pStat = connection.prepareStatement(query);
			pStat.setString(1, id);
			pStat.setString(2, pass);
			pStat.setString(3, type);
			result = pStat.executeQuery();
		}catch(SQLException err)
		{	err.printStackTrace(); }

		if(result == null)
			return false;
		else
			return true;
	}

	//TODO: ADD function to modify different tables in database
	/*public static void main(String[] args){
		DatabaseConnector database = new DatabaseConnector();
		database.addFlights("flightCatalog.txt");
	}*/
}
