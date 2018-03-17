package inner;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.util.ArrayList;


public class Server {

    public ArrayList<Handler> clients = new ArrayList<>();
    final int portNumber = 1000;
	
	public Server() throws Exception{
			
			ServerSocket serverSocket = new ServerSocket(portNumber);
			System.out.println("Waiting for client");
			
			
			while (true){	
				Socket clientSocket = serverSocket.accept();
				
				if (clientSocket != null){
					
				//clients.add(clientSocket);	
				Handler h = new Handler(clientSocket);
                Thread thread = new Thread(h);
                thread.start();
				}
		
		}	
	}
}