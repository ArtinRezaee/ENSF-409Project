import java.util.ArrayList;

/**
 * Created by crist on 2017-04-01.
 */
public class FlightCatalogue  {

    /** Arraylist with instances of class FLight**/
    private ArrayList<Flight> flights = new ArrayList<>();

    /** Constructor **/
    public FlightCatalogue(ArrayList<Flight> flights) {
        this.flights = flights;
    }

    /** Getter**/
    public ArrayList<Flight> getFlights() {
        return flights;
    }

    /**Methods for class FlightCatalogue **/
    public void createCatalogue(){
        //TODO
    }
}
