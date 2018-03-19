package main;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import inner.*;

public class ServerMain {

	public static void main(String[] args) throws IOException {
		
		
		Server server = new Server(2000);
		server.start();
		
	}
	
	
}
