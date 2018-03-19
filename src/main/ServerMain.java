package main;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import inner.*;

public class ServerMain {

	public static void main(String[] args) {
		
		
		Server server = new Server(1997);
		server.start();
		
	}
	
	
}
