package main;
import inner.*;

public class ServerMain {

	public static void main(String[] args) throws Exception {
		Server server = new Server(6969);
		new Thread(server).start();
		server.close();

		
	}
	
	public Server server;
	
}
