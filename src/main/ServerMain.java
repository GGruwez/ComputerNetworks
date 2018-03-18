package main;
import inner.*;

public class ServerMain {

	public static void main(String[] args) throws Exception {
		Server server = new Server(1000);
		new Thread(server).start();
	}
	
	
}
