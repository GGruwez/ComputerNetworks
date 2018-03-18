package inner;

import java.net.*;
import java.util.concurrent.*;
import java.io.*;


public class Server {
	
	//fields
    private int portNumber = 1997;
    private ServerSocket serverSocket;
    private boolean isClosed = false;
    private ExecutorService threadPool;



	
	public Server(int port) {
		this.portNumber = port;	
	}
	
	/**
	 * start server..
	 */
	public void start(){
		
		//create threadpool
		//cached threadpool heeft betere performance want reuses threads wanneer idle voor 60s
        this.threadPool = Executors.newCachedThreadPool();
        
        //initialize the serverSocket
        try {
            this.serverSocket = new ServerSocket(getPort());
        } catch (IOException e) {
            e.getMessage();
        }
		
		
		//accept incoming connections, create busyClient for every connection and submit to threadpool!
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
	

	
	public void closeServer(){
        this.isClosed = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
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