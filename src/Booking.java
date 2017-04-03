// Created by crist on 2017-04-01

public class Booking {

    /** object of class Flight **/
    private Flight flight;

    /** member functions of class Booking **/
    private String fName;
    private String lName;
    private String type;

    /** constructor **/
    public Booking(Flight flight, String fName, String lName, String type) {
        this.flight = flight;
        this.fName = fName;
        this.lName = lName;
        this.type = type;
    }

    /** methods for class Booking **/
    public double CalculateTotalPrice(double grossAmount){
        // TODO
        double calcTotalPrice;
        calcTotalPrice = grossAmount*1.07;
        return calcTotalPrice;
    }
}
