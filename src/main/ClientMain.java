package main;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Arrays;

import exceptions.UnknownHTTPVersionException;
import exceptions.UnknownRequestException;
import inner.Client;
import inner.Request;

public class ClientMain {
	public static void main(String[] args) throws UnknownRequestException, UnknownHTTPVersionException, UnknownHostException, URISyntaxException, IOException{
		
		Client client = new Client(args[1], args[2]);
		Request request = new Request(args[0], args[1], args[2], args[3]);
		
		client.execute(request);
		client.close();
		
		/*
		for (String arg : args){
			System.out.println(arg);
		}
		*/
		
	}
}
