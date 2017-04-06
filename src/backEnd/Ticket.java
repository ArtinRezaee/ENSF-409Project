package backEnd;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;

public class Ticket implements Serializable
{
   private String fName, lName, source , destination, time, date, duration;
	private double totalPrice;

   private int flightNumber;
   private String email;
   private int ticketId;

   private ArrayList<Object> allFields;

   private static int TICKETS = 1;
   static final long serialVersionUID = 1234689;

    /** constructor **/
    public Ticket(String f,String l,String s,String d,String date,String t, String dur, double tPrice, int ticketId, int flightId)
	{
		allFields = new ArrayList<Object>();
		fName = f;
		lName = l;
		source = s;
		destination = d;
		time = t;
		duration = dur;
		this.date = date;
		totalPrice = tPrice;
		this.ticketId = ticketId;
		flightNumber = flightId;

		allFields.add(this.ticketId);
		allFields.add(flightNumber);
		allFields.add(fName);
		allFields.add(lName);
		allFields.add(source);
		allFields.add(destination);
		allFields.add(this.date);
		allFields.add(time);
		allFields.add(duration);
		allFields.add(totalPrice);

		TICKETS++;
	}

	/** constructor **/
	public Ticket(String e, int fn, int tid)
	{
		flightNumber = fn;
		ticketId = tid;
		email = e;
	}

    /** methods to print the ticket **/
    public void print(){
    	try {
			PrintWriter writer = new PrintWriter("Ticket"+TICKETS+"_"+fName+"_"+lName+".txt", "UTF-8");
			String[] options = {"Ticket ID: ", "Flight Number: ", "First Name: ", "Last Name: ", "Source: ", "Destination: ", "Date of Departure: ",
								"Time of Departure: ","Flight Duration: ", "Price including 7% tax: $"};
			int j = 0;
	    	Iterator i = allFields.iterator();
	    	writer.println("TICKET INFO");
	    	while(i.hasNext()){
	    		writer.print(options[j]);
	    		writer.println(i.next());
	    		j++;
	    	}
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    }

    public String toString()
	{
		return (flightNumber + " - " + email + " - " + ticketId);
	}
}
