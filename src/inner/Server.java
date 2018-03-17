package inner;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.util.ArrayList;


public class Server {

    public ArrayList<BusyClient> clients = new ArrayList<>();

	
	public Server{
		
		try{
			
			ServerSocket serverSocket = new ServerSocket(1000);
			System.out.println("Waiting for client");
			
			
			while (true){	
				Socket clientSocket = serverSocket.accept();
				BusyClient newClient = new BusyClient();
				clients.add(newClient);	
		}	
		
		}	
}
