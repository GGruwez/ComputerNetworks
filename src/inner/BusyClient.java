package inner;

import java.net.*;
import java.io.*;

/**
 * A busy client is a client communicating with a server// connection.
 * @author Gilles
 *
 */
public class BusyClient implements Runnable { //implements runnable nodig om te multithreaden

	public Socket socket;
	public Server server;
	public BufferedInputStream input;
	public DataOutputStream output;
	public Boolean isStopped;
	public RequestHandler requestHandler;
	
	public BusyClient(Server server, Socket socket){
		this.server = server;
		this.socket = socket;
		//this.requestHandler = requestHandler;
	}
	
	@Override
	public void run(){
		
		Socket socket = this.getClientSocket();
		start();
		
		//initialize connection
		try {
			//reader
			this.input = new BufferedInputStream(socket.getInputStream());
			//writer
			this.output = new DataOutputStream(socket.getOutputStream());	
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		//activate requestHandler - hoe hier precies de handler implementeren, requesthandler in deze class implementeren?
		while(!isStopped){
			try {
				new RequestHandler(this).handle();
			} catch (Exception e){
				e.printStackTrace();
			}
		}
		
		//terminate service
		try {
	         this.socket.close();
	         this.output.close();
	         this.input.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
	public Socket getClientSocket(){
		return this.socket;
	}
	
	public Server getServer(){
		return this.server;
	}
	
	public BufferedInputStream getInput(){
		return this.input;
	}
	
	public DataOutputStream getOutput(){
		return this.output;
	}
	
	public Boolean isStopped(){
		return this.isStopped;
	}
	
	private void start(){
		this.isStopped = false;
	}
	
	public void stop(){
		this.isStopped = true;
	}
	
	
}
