package inner;

import java.net.*;
import java.io.*;

/**
 * A busy client is a client communicating with a server// connection.
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
	
	public BusyClient(Server server, Socket socket){
		this.server = server;
		this.socket = socket;
		//this.requestHandler = requestHandler;
	}
	
	@Override
	public void run(){
		
		Socket socket = this.getClientSocket();
		startLoop();
		
		//initialize connection
		try {
			//reader
			 setInput(new BufferedInputStream(socket.getInputStream()));
			//writer
			setOutput(new DataOutputStream(socket.getOutputStream()));	
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		//activate requestHandler - hoe hier precies de handler implementeren, requesthandler in deze class implementeren?
		RequestHandler handler = new RequestHandler(this);
		while(!loopIsStopped()){
			try {
				if (getInput().available() > 0)handler.handle();
				if (socket.isClosed() || getServer().getServerSocket().isClosed())stopLoop();
			} catch (Exception e){
				e.printStackTrace();
			}
		}
		
		//terminate service
		close();
		
	}
	
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
	
	public Socket getClientSocket(){
		return this.socket;
	}
	
	public Server getServer(){
		return this.server;
	}
	
	public BufferedInputStream getInput(){
		return this.input;
	}
	
	public void setInput(BufferedInputStream input){
		this.input = input;
	}
	
	public DataOutputStream getOutput(){
		return this.output;
	}
	
	public void setOutput(DataOutputStream output){
		this.output = output;
	}
	
	public Boolean loopIsStopped(){
		return this.isStopped;
	}
	
	private void startLoop(){
		this.isStopped = false;
	}
	
	public void stopLoop(){
		this.isStopped = true;
	}
	
	
}
