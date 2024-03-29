package inner;

import java.net.*;
import java.util.concurrent.*;
import java.io.*;


public class Server {
	
	//fields
    private final int portNumber;
    private ServerSocket serverSocket;
    private boolean isClosed;
    private ExecutorService threadPool;

	/**
	 * Server constructor..
	 */
	public Server(int port) {
		this.portNumber = port;	
	}
	
	/**
	 * start server..
	 * @throws IOException 
	 */
	public void start() throws IOException{
		
		open();
		
		//create threadpool
		//cached threadpool heeft betere performance want reuses threads wanneer idle voor 60s
        setThreadPool(Executors.newCachedThreadPool());
        
        //initialize the serverSocket
        setServerSocket(new ServerSocket(getPort()));
		
		
		//accept incoming connections, create busyClient for every connection and submit to threadpool!
		while(!isClosed()){
			try{
				Socket clientSocket = getServerSocket().accept();
				BusyClient busyClient = new BusyClient(this, clientSocket);
				getThreadPool().submit(busyClient);
				System.out.println("Client accepted");
			} catch (IOException e) {
				System.out.println("Connection issue");
			}
		}
		//close our server if we exited the loop
		this.closeServer();
	}
	

	/**
	 * This method closes our ServerSocket.
	 */
	public void closeServer(){
        close();
        try {
            getServerSocket().close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }

    
	/**
	 * Returns the port number of our server.
	 * @return
	 */
	public int getPort(){
		return this.portNumber;
	}
	
	/**
	 * Checks whether our server is closed.
	 * @return
	 */
	public boolean isClosed(){
		return this.isClosed;
	}
	
	/**
	 * Closes our server.
	 */
	public void close(){
		this.isClosed = true;
	}
	
	/**
	 * Opens our server.
	 */
	private void open(){
		this.isClosed = false;
	}
	
	/**
	 * Initializes the server's socket.
	 * @param serverSocket
	 */
	private void setServerSocket(ServerSocket serverSocket){
		this.serverSocket = serverSocket;
	}
	
	/**
	 * Returns server's socket.
	 * @return
	 */
	public ServerSocket getServerSocket(){
		return this.serverSocket;
	}
	
	/**
	 * Initializes threadpool.
	 * @param threadPool
	 */
	private void setThreadPool(ExecutorService threadPool){
		this.threadPool = threadPool;
	}
	
	/**
	 * Returns server's threadpool. 
	 */
	private ExecutorService getThreadPool(){
		return this.threadPool;
	}
	
	
}