package backEnd;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;

public class Ticket
{
   private String fName, lName, source , destination, time, date;
   private  double totalPrice;
   private int ticketId;
   private ArrayList<Object> allFields;
   private static int TICKETS = 0;
    /** constructor **/
    public Ticket(String f,String l,String s,String d,String t, String date, double tPrice, int ticketId) {
    	allFields = new ArrayList<Object>();
    	fName = f;
    	lName = l;
    	source = s;
    	destination = d;
    	time = t;
    	this.date = date;
    	totalPrice = tPrice;
    	this.ticketId = ticketId;
    	allFields.add(fName);
    	allFields.add(lName);
    	allFields.add(source);
      	allFields.add(destination);
    	allFields.add(time);
    	allFields.add(this.date);
      	allFields.add(totalPrice);
    	allFields.add(this.ticketId);
    	TICKETS++;
    }

    /** methods to print the ticket **/
    public void print(){
    	
    	try {
			PrintWriter writer = new PrintWriter("Ticket"+TICKETS+"_"+ fName+ "_"+ lName +".txt", "UTF-8");	
			String[] options = {"First Name: ", "Last Name:", "Source: ", "Destination: ", "Time of Departure: ", 
								"Date of Departure: ", "Price: ", "Ticket ID: "};
			int j = 0;
	    	Iterator i = allFields.iterator();
	    	while(i.hasNext()){
	    		writer.print(options[j]);
	    		writer.println(i.next());
	    	}
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    }
}
