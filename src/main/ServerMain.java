package main;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import exceptions.UnknownHTTPVersionException;
import exceptions.UnknownRequestException;
import inner.*;

public class ServerMain {

	public static void main(String[] args) throws UnknownRequestException, URISyntaxException, UnknownHTTPVersionException, IOException {
		
		Server server = new Server(2000);
		server.start();
		
	}
	
	
}
