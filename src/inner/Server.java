package inner;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class Server implements Runnable {
	
	
    public int          portNumber   = 1000;
    public ServerSocket serverSocket = null;
    public boolean      isStopped    = false;
    public Thread       runningThread= null;

	
	public Server(int port) throws Exception{
			this.setPort(port);
	}
	
	@Override
	public void run(){
		this.runningThread = Thread.currentThread();
		
		//open server
		try{
			this.serverSocket = new ServerSocket(this.portNumber);
		} catch (IOException e) {
			System.out.println("Cannot open server.");
		}
		
		/*
		 *  Accept incoming connections, create thread for each one of them,
		 *	while listening to more incoming connections
		 *  As stated in the course multi-threading can be done with thread pools
	 	 * https://docs.oracle.com/javase/tutorial/essential/concurrency/pools.html
		 * https://softwareengineering.stackexchange.com/questions/173575/what-is-a-thread-pool
		 */
		
		//accept incoming connections and try handle them
		while(true){
			try{
				Socket clientSocket = serverSocket.accept();
				if (clientSocket != null){
					requestHandler(clientSocket);
				}
			} catch (Exception e) {
				System.out.println("Handler issue");
			}
		}
	}
	
	
	private void requestHandler(Socket clientSocket) throws Exception{
		try {
			//input from client
			BufferedReader in = new BufferedReader(new
                    InputStreamReader(clientSocket.getInputStream()));
			//output to client
            DataOutputStream out = new DataOutputStream
                    (clientSocket.getOutputStream());
            
            String inputLine, outputLine;
            
            inputLine = in.readLine();
            
            
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	
	public void setPort(int port){
		if (port < 0) {
			throw new IllegalArgumentException("Invalid port");
		} else {
			this.portNumber = port;
		}
	}
	
	public int getPort(){
		return this.portNumber;
	}
	
	
}