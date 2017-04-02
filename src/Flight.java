/**
 * Created by crist on 2017-04-01.
 */
public class Flight {


    /** Member functions of class Flight **/
    private int flightNumber;
    private String source;
    private String destination;
    private String date;
    private int totalSeats;
    private int availableSeats;
    private String duration;
    private double price;

    /** Constructor **/
    public Flight(int flightNumber, String source, String destination, String date,
                  int totalSeats, int availableSeats, String duration, double price) {
        this.flightNumber = flightNumber;
        this.source = source;
        this.destination = destination;
        this.date = date;
        this.totalSeats = totalSeats;
        this.availableSeats = availableSeats;
        this.duration = duration;
        this.price = price;
    }
}
