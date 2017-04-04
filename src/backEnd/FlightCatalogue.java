// Created by crist on 2017-04-01
package backEnd;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class FlightCatalogue implements Serializable
{
	static final long serialVersionUID = 2;
    public ArrayList<Flight> flights;

    //Constructor
    public FlightCatalogue(ResultSet set)
	{
		flights = new ArrayList<>();
		try{
			while(set.next())
			{
				int num = set.getInt("FlightNumber");
				String src = set.getString("Source");
				String dest = set.getString("Destination");
				String date = set.getString("Date");
				String time = set.getString("Time");
				String duration = set.getString("Duration");
				int tseats = set.getInt("TotalSeats");
				int aseats = set.getInt("AvailableSeats");
				double price = set.getDouble("Price");
				Flight flight = new Flight(num, src, dest, date, time, duration, tseats, aseats, price);
				flights.add(flight);
			}
		}catch(SQLException e)
		{	e.printStackTrace();	}

    }
}
