package main;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

import exceptions.SocketClosedException;
import exceptions.UnknownHTTPVersionException;
import exceptions.UnknownRequestException;
import inner.Client;
import inner.Request;

public class ClientMain {
	public static void main(String[] args) throws UnknownRequestException, UnknownHTTPVersionException, UnknownHostException, URISyntaxException, IOException, SocketClosedException{
		
		try{
			Client client = new Client(args[1], args[2]);
			Request request = new Request(args[0], args[1], args[2], args[3]);
			
			client.execute(request, true);
			
			/*
			 * After delivering the response, the client closes the connection (if not the server) (making HTTP a stateless protocol, 
			 * i.e. not maintaining any connection information between transactions).
			 */
			
			client.close();
		}catch (Exception exception){
			System.out.println("An exception occurred: ");
			exception.printStackTrace();
		}
		
		
	}
}
