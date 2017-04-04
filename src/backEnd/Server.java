package backEnd;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Handler;
import java.util.concurrent.*;

import com.sun.org.apache.bcel.internal.generic.NEW;
import frontEnd.NewUserInfo;

public class Server {
	
	private ServerSocket serverSocket;
	private Socket socket;
	private BufferedReader inString;
	private PrintWriter outString;
	private ObjectInputStream objectIn;
	private ObjectOutputStream objectOut;

	private final ExecutorService pool = Executors.newFixedThreadPool(3);
	
	
	public Server(){
		try {
			serverSocket = new ServerSocket(8099);
			System.out.println("Server is running...");
			socket = serverSocket.accept();
			System.out.println("Client connected..");
			inString = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			outString = new PrintWriter(socket.getOutputStream(),true);
			objectIn = new ObjectInputStream(socket.getInputStream());
			objectOut = new ObjectOutputStream(socket.getOutputStream());


			communicate();

		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void communicate()
	{
		try
		{
			NewUserInfo info = (NewUserInfo)objectIn.readObject();
			System.out.println(info.first + " " + info.last + " " + info.email + " " + info.password);

		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {	Server server = new Server();	}
}
