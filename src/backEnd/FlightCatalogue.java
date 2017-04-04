// Created by crist on 2017-04-01
package backEnd;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class FlightCatalogue
{
	
	DatabaseConnector dbc = new DatabaseConnector();
	/**
	 * ArrayList of flights that are currently in the database
	 */
    private ArrayList<Flight> flights = new ArrayList<>();

    /**
     * Table in the database
     */
    private ResultSet s;
    
    /**
     * Constructor
     */
    public FlightCatalogue() {
    	s = dbc.search("flights", "");
    	try {
			while(s.next()){
				flights.add(new Flight(s.getInt(0),s.getString(1),s.getString(2),s.getString(3),s.getString(4),s.getInt(5),s.getInt(6),s.getDouble(7), s.getString(8)));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	
    }

    /**
     * Function to return the catalog
     * @return returns an ArrayList of flights
     */
    public ArrayList<Flight> getFlights() {
        return flights;
    }

    /**
     * Method to refresh the catalog
     */
    public void refresh(){
    	ArrayList<Flight> flights = new ArrayList<>();
    	try {
			while(s.next()){
				flights.add(new Flight(s.getInt(0),s.getString(1),s.getString(2),s.getString(3),s.getString(4),s.getInt(5),s.getInt(6),s.getDouble(7), s.getString(8)));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	this.flights = flights;
    }
}
