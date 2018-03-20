package inner;

import java.net.*;
import java.io.*;

/**
 * A busy client is a client communicating with a server aka a connection.
 * @author Gilles
 *
 */
public class BusyClient implements Runnable { //implements runnable nodig om te multithreaden

	private final Socket socket;
	private final Server server;
	private BufferedInputStream input;
	private DataOutputStream output;
	private Boolean isStopped;
	private static final int TIMEOUT_MS = 10000;
	private long lastRequest;
	
	/**
	 * Construct a BusyClient. A BusyClient is defined by a server
	 *  (which is defined by port and serverSocket) and a clientSocket.
	 * @param server
	 * @param socket
	 */
	public BusyClient(Server server, Socket socket){
		this.server = server;
		this.socket = socket;
	}
	
	/**
	 * Runs the connection between server and client.
	 */
	@Override
	public void run(){
		
		Socket socket = this.getClientSocket();
		//open clientSocket
		startLoop();
		
		try {
			//server input = clientsocket input
			 setInput(new BufferedInputStream(socket.getInputStream()));
			//server output = clientsocket output - what client receives as output
			setOutput(new DataOutputStream(socket.getOutputStream()));	
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		//activate requestHandler
		RequestHandler handler = new RequestHandler(this);
		
		//handler loop
		while(!loopIsStopped()){
			try {
				//only handle if more than 1 byte input is available
				if (getInput().available() > 0){
					handler.handle();
					setLastRequest(System.currentTimeMillis());
				}
				//close if one of two sockets is closed or if inactive for too long
				if (socket.isClosed() || getServer().getServerSocket().isClosed() || (System.currentTimeMillis()-getLastRequest() > TIMEOUT_MS)){
					System.out.println("Closed a client connection");
					stopLoop();
				}
				
				
			} catch (Exception e){
				System.out.println("Error during handling");
				e.printStackTrace();
			}
		}
		
		//terminate service
		close();
		
	}
	
	/**
	 * Closes a client connection.
	 */
	public void close(){
		try{
			getClientSocket().close();
			stopLoop();
			Thread.currentThread().interrupt();
			return;
		}catch (IOException e){
			System.out.println("Error closing socket");
		}
		
	}
	
	/**
	 * Returns the time of the last request
	 */
	public long getLastRequest(){
		return this.lastRequest;
	}
	
	/**
	 * Sets the time of the last request
	 */
	public void setLastRequest(long lastRequest){
		this.lastRequest = lastRequest;
	}
	
	/**
	 * Returns the connection's client socket. 
	 */
	public Socket getClientSocket(){
		return this.socket;
	}
	
	/**
	 * Returns the connection's server.
	 */
	public Server getServer(){
		return this.server;
	}
	
	/**
	 * Returns the connection's input stream.
	 * @return
	 */
	public BufferedInputStream getInput(){
		return this.input;
	}
	
	/**
	 * Sets the connection's input stream.
	 * @param input
	 */
	public void setInput(BufferedInputStream input){
		this.input = input;
	}
	
	/**
	 * Return the connection's output stream.
	 * @return
	 */
	public DataOutputStream getOutput(){
		return this.output;
	}
	
	/**
	 * Set the connection's output stream.
	 */
	public void setOutput(DataOutputStream output){
		this.output = output;
	}
	
	/**
	 * Checks whether the service loop has been stopped. 
	 */
	public Boolean loopIsStopped(){
		return this.isStopped;
	}
	
	/**
	 * Starts the service loop. 
	 */
	private void startLoop(){
		this.isStopped = false;
	}
	
	/**
	 * Stops the service loop.
	 */
	public void stopLoop(){
		this.isStopped = true;
	}
	
	
}
