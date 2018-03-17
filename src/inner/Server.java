package inner;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.util.ArrayList;


public class Server {

    final int portNumber = 1000;
	
	public Server(int portNumber) throws Exception{
			
	    	//public ArrayList<Handler> clients = new ArrayList<>();
		
			if (portNumber < 0) {
				throw new IllegalArgumentException("Invalid port");
			}

			ServerSocket serverSocket = new ServerSocket(portNumber);
			System.out.println("Waiting for client");
			
			/*
			 *  Accept incoming connections, create thread for each one of them,
			 *	while listening to more incoming connections
			 *  As stated in the course multi-threading can be done with thread pools
		 	 * https://docs.oracle.com/javase/tutorial/essential/concurrency/pools.html
			 * https://softwareengineering.stackexchange.com/questions/173575/what-is-a-thread-pool
			 */
			while (true){	
				Socket clientSocket = serverSocket.accept();
				
				if (clientSocket != null){
					
				//clients.add(clientSocket);	
				Handler handler = new Handler(clientSocket);
                Thread thread = new Thread(handler);
                thread.start();
				}
		
		}	
	}
	
	public int getPort(){
		return this.portNumber;
	}
	
	
}