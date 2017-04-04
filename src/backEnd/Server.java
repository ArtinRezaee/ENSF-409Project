package backEnd;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Handler;
import java.util.concurrent.*;

import com.sun.org.apache.bcel.internal.generic.NEW;
import frontEnd.NewUserInfo;

public class Server
{
	private ServerSocket serverSocket;
	private Socket socket;
	private BufferedReader stringIn;
	private PrintWriter stringOut;
	private ObjectInputStream objectIn;
	private ObjectOutputStream objectOut;

	private DatabaseConnector db;
	private ResultSet results;

	private final ExecutorService pool = Executors.newFixedThreadPool(3);
	
	
	public Server()
	{
		try {
			serverSocket = new ServerSocket(8099);
			System.out.println("Server is running...");
			db = new DatabaseConnector();

			socket = serverSocket.accept();
			System.out.println("Client connected..");

			stringIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			stringOut = new PrintWriter(socket.getOutputStream(),true);
			objectIn = new ObjectInputStream(socket.getInputStream());
			objectOut = new ObjectOutputStream(socket.getOutputStream());


			communicate();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void communicate()
	{
		String line = "";
		while(!line.equals("over"))
		{
			try
			{
				line = stringIn.readLine();
				if(line.equals("adduser"))
				{
					NewUserInfo info = (NewUserInfo)objectIn.readObject();
					db.insert("client", "'"+info.first+"', '"+ info.last +"', '" + info.email +"', '" +info.type + "', '"+ info.password +"'");
				}
				else if(line.equals("checklogin"))
				{
					String id = stringIn.readLine();
					String pass = stringIn.readLine();
					String type = stringIn.readLine();
					ResultSet s = db.search("client", "Email = '"+ id + "' AND " + "Password = '" + pass + "' AND " + "type = '" + type +"'");
					if(!s.next())
						stringOut.println("yes");
					else
						stringOut.println("no");
				}

			}catch(Exception e)
			{	e.printStackTrace();	}
		}

	}

	public static void main(String[] args) {	Server server = new Server();	}
}
