package main;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import exceptions.UnknownHTTPVersionException;
import exceptions.UnknownRequestException;
import inner.*;

public class ServerMain {

	public static void main(/*String[] args*/) throws UnknownRequestException, URISyntaxException, UnknownHTTPVersionException, IOException {
		
		
		Server server = new Server(1997);
		server.start();
		
		System.out.println(Parser.parseRequestHeader("GET www.tinyos.net/ 80 HTTP/1.1"));
		
	}
	
	
}
