package inner;

import java.net.*;
import java.util.concurrent.*;
import java.io.*;


public class Server implements Runnable {
	
	//fields
    private int portNumber;
    private ServerSocket serverSocket;
    private Thread runningThread;
    private boolean isClosed = false;
    private ExecutorService threadPool;



	
	public Server(int port) throws Exception{
			this.setPort(port);
	}
	
	
	@Override
	public void run(){
		
		
		//create a cached thread pool, a cached thread pool keeps released threads for 60sec
        //if the threads are not reclaimed after that the resources are released
        this.threadPool = Executors.newCachedThreadPool();
        
        //initialize the serverSocket
        try {
            this.serverSocket = new ServerSocket(getPort());
        } catch (IOException e) {
            e.getMessage();
        }
		
		
		//accept incoming connections, create a processing object and submit to threadpool!
		while(!isClosed){
			try{
				Socket clientSocket = serverSocket.accept();
				BusyClient busyClient = new BusyClient(this, clientSocket);
				threadPool.submit(busyClient);
			} catch (IOException e) {
				System.out.println("Connection issue");
			}
		}
		//close our server if we exited the loop
		this.closeServer();
	}
	

	/*private void requestHandler(Socket clientSocket) throws Exception{
		try {
			//input from client
			BufferedReader in = new BufferedReader(new
                    InputStreamReader(clientSocket.getInputStream()));
			//output to client
            DataOutputStream out = new DataOutputStream
                    (clientSocket.getOutputStream());
            
            
            String inputLine, outputLine;
            
            inputLine = in.readLine();
			
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
	}*/
	
	
	public void closeServer(){
        this.isClosed = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }

    
	
	private void setPort(int port){
		//port moet groter zijn dan 1024 voor non-root users!
		if (port < 1024) {
			throw new IllegalArgumentException("Invalid port");
		} else {
			this.portNumber = port;
		}
	}
	
	private int getPort(){
		return this.portNumber;
	}
	
	private ServerSocket getServerSocket(){
		return this.serverSocket;
	}
	
	private ExecutorService getThreadPool(){
		return this.threadPool;
	}
	
	
}