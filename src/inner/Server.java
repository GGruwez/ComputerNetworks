package inner;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.io.*;


public class Server implements Runnable {
	
	
    public int          portNumber   = 6969;
    public ServerSocket serverSocket = null;
    public Thread       runningThread= null;
    public boolean      isClosed    = false;


	
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
		while(!isClosed){
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
			/*//input from client
			BufferedReader in = new BufferedReader(new
                    InputStreamReader(clientSocket.getInputStream()));
			//output to client
            DataOutputStream out = new DataOutputStream
                    (clientSocket.getOutputStream());
            
            
            String inputLine, outputLine;
            
            inputLine = in.readLine();*/
			
			InputStream  input  = clientSocket.getInputStream();
	        OutputStream output = clientSocket.getOutputStream();
	        long time = System.currentTimeMillis();

	        int responseDocument = 19999;

	        int responseHeader = 10000;

	        output.write(responseHeader);
	        output.write(responseDocument);
	        output.close();
	        input.close();
	        System.out.println("Request processed: " + time);
            
            
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	
	public void close(){
        this.isClosed = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }

    
	
	public void setPort(int port){
		//port moet groter zijn dan 1024 voor non-root users!
		if (port < 1024) {
			throw new IllegalArgumentException("Invalid port");
		} else {
			this.portNumber = port;
		}
	}
	
	public int getPort(){
		return this.portNumber;
	}
	
	
}