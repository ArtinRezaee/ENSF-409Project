package frontEnd;

import java.io.Serializable;

public class Booking implements Serializable{

	private String mail;
	private int flightNumber;
	
	public Booking(String mail, int fNum){
		this.mail = mail;
		this.flightNumber = fNum;
	}
	
	public String getMail(){return mail;}
	public int getFlightNumber(){return flightNumber;}
	
}
