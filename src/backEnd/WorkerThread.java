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

import frontEnd.NewUserInfo;

public class WorkerThread extends Thread{
	private Socket socket;
	private BufferedReader stringIn;
	private PrintWriter stringOut;
	private ObjectInputStream objectIn;
	private ObjectOutputStream objectOut;
	private DatabaseConnector db;
	
	public WorkerThread(Socket s){
		try {
			socket = s;
			stringIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			stringOut = new PrintWriter(socket.getOutputStream(),true);
			objectIn = new ObjectInputStream(socket.getInputStream());
			objectOut = new ObjectOutputStream(socket.getOutputStream());
			db =  new DatabaseConnector();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run(){
		String line = "";
		while(!line.equals("over"))
		{
			try
			{
				System.out.println("Hello");
				line = stringIn.readLine();
				if(line.equals("adduser"))
				{
					System.out.println("Here");
					NewUserInfo info = (NewUserInfo)objectIn.readObject();
					System.out.println(info.getFirst());
					db.insert("clients", "'"+info.getMail()+"', '"+ info.getFirst() +"', '" 
							  + info.getLast() +"', '" +info.getPass() + "', '"+ info.getType() +"'");
				}
				else if(line.equals("checklogin"))
				{
					String id = stringIn.readLine();
					String pass = stringIn.readLine();
					String type = stringIn.readLine();
					ResultSet set = db.search("clients", "Email = '"+ id + "' AND Password = '" + pass + "' AND Type = '" + type +"'");
					if(set.next())
						stringOut.println("yes");
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

				}

			}catch(IOException e){	
				e.printStackTrace();	
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}

	}

}
