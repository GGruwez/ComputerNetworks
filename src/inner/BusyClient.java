package inner;

import java.net.*;
import java.io.*;

/**
 * A busy client is a client communicating with a server aka a connection.
 * @author Gilles
 *
 */
public class BusyClient implements Runnable { //implements runnable nodig om te multithreaden

	public final Socket socket;
	public final Server server;
	public BufferedInputStream input;
	public DataOutputStream output;
	public Boolean isStopped;
	public RequestHandler requestHandler;
	
	/**
	 * Construct a BusyClient. A BusyClient is defined by a server
	 *  (which is defined by port and serverSocket) and a clientSocket.
	 * @param server
	 * @param socket
	 */
	public BusyClient(Server server, Socket socket){
		this.server = server;
		this.socket = socket;
		//this.requestHandler = requestHandler;
	}
	
	/**
	 * Initializes the connection between server and client.
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
				if (getInput().available() > 0)handler.handle();
				//close if one of two sockets is closed
				if (socket.isClosed() || getServer().getServerSocket().isClosed())stopLoop();
			} catch (Exception e){
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
			System.out.println("Closed a client connection");
			Thread.currentThread().interrupt();
			return;
		}catch (IOException e){
			System.out.println("Error closing socket");
		}
		
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
