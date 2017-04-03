package backEnd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Handler;
import java.util.concurrent.*;

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
			inString = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			outString = new PrintWriter(socket.getOutputStream(),true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void communicate(){
		for (;;) {
			pool.execute(new Handler(socket));
		} 
	}
	
	class Handler implements Runnable {
		private final Socket socket;
		Handler(Socket socket) {
		this.socket = socket; }
		public void run()
		{ // read and service request }
		} 
	}
}
