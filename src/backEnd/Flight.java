package backEnd;

import java.io.Serializable;

public class Flight implements Serializable
{
    // Member functions of class Flight
    private int flightNumber;
    private String source;
    private String destination;
    private String date;
    private String time;
    private String duration;
    private int totalSeats;
    private int availableSeats;
    private double price;

    static final long serialVersionUID = 3123567;

    public Flight(int flightNumber, String source, String destination, String date, String time,
                  String duration, int totalSeats, int availableSeats, double price) {
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

    public int getNum()     {   return flightNumber;    }
    public String getSrc()  {   return source;    }
    public String getDest() {   return destination;    }
    public String getDate() {   return date;    }
    public String getTime() {   return time;    }
    public String getDur()  {   return duration;    }
    public int getTotalSeats() {   return totalSeats;    }
    public int getAvailSeats() {   return availableSeats;    }
    public double getPrice()    {   return price;   }

    public String toString()
    {
        return(source + " to " + destination + " on " + date);
    }
}
