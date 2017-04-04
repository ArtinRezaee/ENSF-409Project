// Created by crist on 2017-04-01
package backEnd;
public class Flight
{
    /** Member functions of class Flight **/
    private int flightNumber;
    private String source;
    private String destination;
    private String date;
    private String time;
    private String duration;
    private int totalSeats;
    private int availableSeats;
    private double price;

    /** Constructor **/
    public Flight(int flightNumber, String source, String destination, String date, String time,
                  String duration, int totalSeats, int availableSeats, double price)
    {
        this.flightNumber = flightNumber;
        this.source = source;
        this.destination = destination;
        this.date = date;
        this.time = time;
        this.duration = duration;
        this.totalSeats = totalSeats;
        this.availableSeats = availableSeats;
        this.price = price;
    }

    public String toString()
    {
        return(source + " " + destination + " " + date + " " + time);
    }
}
