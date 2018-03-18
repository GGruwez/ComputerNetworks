package inner;

import java.net.*;
import java.io.*;

/**
 * A busyn
 * @author Gilles
 *
 */
public class BusyClient implements Runnable {

	public Socket socket;
	public Server server;
	
	public BusyClient(Server server, Socket socket){
		this.server = server;
		this.socket = socket;
	}
	
	@Override
	public void run(){
		
	}
}
