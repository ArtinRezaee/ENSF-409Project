package backEnd;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread
{
	private ServerSocket serverSocket;
	private DatabaseConnector db;
	
	public Server()
	{
		try {
			serverSocket = new ServerSocket(8099);
			db =  new DatabaseConnector();
			System.out.println("Server is running...");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run(){
		while(true){
			try {
				Socket socket = serverSocket.accept();
				WorkerThread wk = new WorkerThread(socket, db);
				wk.start();
			} catch (IOException e)
			{	e.printStackTrace();	}
		}
	}

	public static void main(String[] args) {	
		Server server = new Server();	
		server.start();
	}
}
