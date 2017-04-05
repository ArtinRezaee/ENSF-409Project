package backEnd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;

import frontEnd.Booking;


public class WorkerThread extends Thread
{
	private Socket socket;
	private BufferedReader stringIn;
	private PrintWriter stringOut;
	private ObjectInputStream objectIn;
	private ObjectOutputStream objectOut;
	private DatabaseConnector db;
	private String id;
	private String pass;
	private String type;
	
	public WorkerThread(Socket s){
		try {
			id = "";
			pass = "";
			type = "";
			socket = s;
			stringIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			stringOut = new PrintWriter(socket.getOutputStream(),true);
			objectIn = new ObjectInputStream(socket.getInputStream());
			objectOut = new ObjectOutputStream(socket.getOutputStream());
			db =  new DatabaseConnector();
		} catch (IOException e)
		{	e.printStackTrace();	}
	}
	
	public void run(){
		String line = "";
		while(!line.equals("over"))
		{
			try
			{
				line = stringIn.readLine();
				if(line.equals("adduser"))
				{
					UserInfo info = (UserInfo)objectIn.readObject();
					System.out.println(info.getFirst());
					db.insert("clients", "'"+info.getMail()+"', '"+ info.getFirst() +"', '" 
							  + info.getLast() +"', '" +info.getPass() + "', '"+ info.getType() +"'");
				}
				else if(line.equals("checklogin"))
				{
					String i = stringIn.readLine();
					String p = stringIn.readLine();
					String t = stringIn.readLine();
					ResultSet set = db.search("clients", "Email = '"+ i + "' AND Password = '" + p + "' AND Type = '" + t +"'");
					if(set.next())
					{
						stringOut.println("yes");
						id = i;
						pass = p;
						type = t;
						System.out.println(i + " connected");
					}
					else
						stringOut.println("no");
				}
				else if(line.equals("searchflights"))
				{
					String query = stringIn.readLine();
					ResultSet set = db.search("flights", query);
					FlightCatalogue catalogue = new FlightCatalogue(set);
					try {
						stringOut.println("catalogincoming");
						objectOut.writeObject(catalogue);
					}catch(IOException err3)
					{   err3.printStackTrace(); }
				}
				else if(line.equals("book"))
				{
					Booking book = (Booking)objectIn.readObject();
					//TODO: get name and flight id from book. Add to database with a randomly generated ticket ID.
					//TODO: Create a ticket
				}

			}catch(IOException e){	
				e.printStackTrace();	
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		if(!id.equals(""))
			System.out.println(id + " disconnected");
		try{
			stringOut.close();
			objectOut.close();
			objectIn.close();
			stringIn.close();
			socket.close();
		}catch(IOException e)
		{	e.printStackTrace();	}
	}
}
