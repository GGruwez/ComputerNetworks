package inner;

import java.net.*;
import java.io.*;

/**
 * A busy client is a client communicating with a server.
 * @author Gilles
 *
 */
public class BusyClient implements Runnable {

	private Socket socket;
	private Server server;
	private BufferedReader reader;
	private PrintWriter writer; //enkel tekst
	private Boolean isStopped;
	
	public BusyClient(Server server, Socket socket){
		this.server = server;
		this.socket = socket;
	}
	
	@Override
	public void run(){
		
		Socket socket = this.getClientSocket();
		
		//initialize connection
		try {
			InputStream input = socket.getInputStream();
			OutputStream output = socket.getOutputStream();
			this.reader = new BufferedReader(new InputStreamReader(input));
			this.writer = new PrintWriter(output);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		//activate requestHandler
		while(!isStopped){
			/*
			 * 1. get request type
			 * 2. handle request
			 */
			try {
				
				
			} catch (Exception e){
				e.printStackTrace();
			}
		}
		
		//terminate service
		try {
			 writer.close();
	         reader.close();
	         socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
	private Socket getClientSocket(){
		return this.socket;
	}
	
	private Server getServer(){
		return this.server;
	}
	
	private BufferedReader getReader(){
		return this.reader;
	}
	
	private PrintWriter getWriter(){
		return this.writer;
	}
	
	
}
