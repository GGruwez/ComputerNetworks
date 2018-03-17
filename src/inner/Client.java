package inner;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import exceptions.UnknownHTTPVersionException;
import exceptions.UnknownRequestException;

public class Client {

		public Client(URI URL, int port) throws URISyntaxException, UnknownHostException, IOException{
			
			Socket clientSocket = new Socket(URL.getHost(),port);

			// Writer
            PrintStream serverInput = new PrintStream(clientSocket.getOutputStream(),true);
            // Reader
            BufferedReader serverOutput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		
            this.socket = clientSocket;
            this.socketWriter = serverInput;
            this.socketReader = serverOutput;
            this.host = convertToReadableURL(URL).getHost();
		}
		
		public Client(String URL, String port) throws URISyntaxException, UnknownHostException, IOException{
			
			String host = getHostFromURL(URL);
			int portInt = Integer.parseInt(port);
			
			Socket clientSocket = new Socket(host, portInt);

			// Writer
            PrintStream serverInput = new PrintStream(clientSocket.getOutputStream(),true);
            // Reader
            BufferedReader serverOutput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		
            this.socket = clientSocket;
            this.socketWriter = serverInput;
            this.socketReader = serverOutput;
            this.host = new URI(convertToReadableURL(URL)).getHost();
		}
		
		public void close() throws IOException{
			if (!getSocket().isClosed()){
				getSocket().close();
			}
		}
		
		/*
		 * GETTERS
		 */
		
		private Socket getSocket(){
			return this.socket;
		}
		
		private PrintStream getSocketWriter(){
			return this.socketWriter;
		}
		
		private BufferedReader getSocketReader(){
			return this.socketReader;
		}
		
		public String getHost(){
			return this.host;
		}
		
		/*
		 * HELP METHODS
		 */
		
		public static String getHostFromURL(String URL) throws URISyntaxException{
			String host = convertToReadableURL(URL);
			host = new URI(host).getHost();
			return host;
		}
		
		public static String convertToReadableURL(String URL){
			if (URL.contains("http://")){
				return URL;
			}else{
				return "http://" + URL;
			}
		}
		
		public static URI convertToReadableURL(URI URL) throws URISyntaxException{
			String URLString = URL.toString();
			return new URI(convertToReadableURL(URLString));
		}
		
		/*
		 * REQUEST METHODS
		 */
		
		public void execute(Request request) throws UnknownRequestException, IOException, UnknownHTTPVersionException{
			if (request.getRequestType() == RequestType.GET){
				executeGet(request);
			}else if (request.getRequestType() == RequestType.HEAD){
				executeHead(request);
			}else if (request.getRequestType() == RequestType.POST){
				executePost(request);
			}else{
				throw new UnknownRequestException();
			}
		}
		
		/**
		 * Method for the execution of a GET request
		 * @param request
		 * @throws IOException
		 * @throws UnknownHTTPVersionException
		 */
		private void executeGet(Request request) throws IOException, UnknownHTTPVersionException{
			
			// Check whether or not this is a GET request
			if (request.getRequestType() != RequestType.GET){
				throw new IOException("This is not a GET request");
			}
			
			BufferedReader reader = getSocketReader();
			
			String fileName = "GET" + getHost() + request.getPath();
			fileName = fileName.replace('.', '_').replace('/', '_');
			
			if (request.getVersion() == HTTPVersion.HTTP_1_1){
				// Execute as an HTTP/1.1 request

				// Send request
				sendHTTP_1_1Request(request);
				
				// Print and store server output
				displayAndStoreResponse(fileName, RequestType.GET, false, "http://" + getHost() + request.getPath());
		        
			}else if (request.getVersion() == HTTPVersion.HTTP_1_0){
				// Execute as an HTTP/1.0 request
				
				// Send request
				sendHTTP_1_0Request(request);
				
				// Print and store server output
				displayAndStoreResponseHTTP_1_0(fileName);
		        
			}else{
				throw new UnknownHTTPVersionException();
			}
			
			
	        
		}
		
		private void executeHead(Request request) throws IOException, UnknownHTTPVersionException{
			
			// Check whether or not this is a HEAD request
			if (request.getRequestType() != RequestType.HEAD){
				throw new IOException("This is not a HEAD request");
			}
			
			BufferedReader reader = getSocketReader();
			String fileName = "GET" + getHost() + request.getPath();
			fileName = fileName.replace('.', '_').replace('/', '_');
			
			if (request.getVersion() == HTTPVersion.HTTP_1_1){
				// Execute as an HTTP/1.1 request

				// Send request
				sendHTTP_1_1Request(request);
				
				// Print and store server output
				displayAndStoreResponse(fileName, RequestType.GET, true, request.getPath());
		        
			}else if (request.getVersion() == HTTPVersion.HTTP_1_0){
				// Execute as an HTTP/1.0 request
				
				// Send request
				sendHTTP_1_0Request(request);
				
				// Print and store server output
				displayAndStoreResponseHTTP_1_0(fileName);
		        
			}else{
				throw new UnknownHTTPVersionException();
			}
			
			
	        
		}
		
		private void executePost(Request request) throws IOException, UnknownHTTPVersionException{
			
			// Check whether or not this is a POST request
			if (request.getRequestType() != RequestType.POST){
				throw new IOException("This is not a POST request");
			}
			
			BufferedReader reader = getSocketReader();
			String fileName = "GET" + getHost() + request.getPath();
			fileName = fileName.replace('.', '_').replace('/', '_');
			
			if (request.getVersion() == HTTPVersion.HTTP_1_1){
				// Execute as an HTTP/1.1 request

				// Send request
				sendHTTP_1_1Request(request);
				
				// Print and store server output
				displayAndStoreResponse(fileName, RequestType.GET, true, request.getPath());
		        
			}else if (request.getVersion() == HTTPVersion.HTTP_1_0){
				// Execute as an HTTP/1.0 request
				
				// Send request
				sendHTTP_1_0Request(request);
				
				// Print and store server output
				displayAndStoreResponseHTTP_1_0(fileName);
		        
			}else{
				throw new UnknownHTTPVersionException();
			}
			
	        
		}
		
		private void executePut(Request request) throws IOException, UnknownHTTPVersionException{
			
			// Check whether or not this is a PUT request
			if (request.getRequestType() != RequestType.PUT){
				throw new IOException("This is not a PUT request");
			}
			
			BufferedReader reader = getSocketReader();
			String fileName = "GET" + getHost() + request.getPath();
			fileName = fileName.replace('.', '_').replace('/', '_');
			
			if (request.getVersion() == HTTPVersion.HTTP_1_1){
				// Execute as an HTTP/1.1 request

				// Send request
				sendHTTP_1_1Request(request);
				
				// Print and store server output
				displayAndStoreResponse(fileName, RequestType.GET, true, request.getPath());
		        
			}else if (request.getVersion() == HTTPVersion.HTTP_1_0){
				// Execute as an HTTP/1.0 request
				
				// Send request
				sendHTTP_1_0Request(request);
				
				// Print and store server output
				displayAndStoreResponseHTTP_1_0(fileName);
		        
			}else{
				throw new UnknownHTTPVersionException();
			}
			
	        
		}
		
		/**
		 * Crude method for sending HTTP/1.1 requests to a server.
		 * Assumes a correctly formed request.
		 * @throws IOException 
		 */
		private void sendHTTP_1_1Request(Request request) throws IOException{
			
			if (request.getVersion() != HTTPVersion.HTTP_1_1){
				throw new IOException("This is not an HTTP/1.1 request");
			}
			String path = request.getPath();
			String requestType = request.getRequestType().toString();
			String version = request.getVersion().toString();
			
			String host = getHost();
			
			PrintStream writer = getSocketWriter();
			
			String commandLine1 = requestType + " " + path + " " + version;
			String commandLine2 = "Host: " + host;
	        writer.println(commandLine1);
	        writer.println(commandLine2);
	        writer.println();
	        writer.flush();
			
		}
		
		/**
		 * Crude method for sending HTTP/1.0 requests to a server.
		 * Assumes a correctly formed request.
		 * @throws IOException 
		 */
		private void sendHTTP_1_0Request(Request request) throws IOException{
			
			if (request.getVersion() != HTTPVersion.HTTP_1_0){
				throw new IOException("This is not an HTTP/1.0 request");
			}
			
			String path = request.getPath();
			String requestType = request.getRequestType().toString();
			String version = request.getVersion().toString();
			
			String host = getHost();
			
			PrintStream writer = getSocketWriter();
			
			String commandLine1 = requestType + " http://" + host + path + " " + version;
	        writer.println(commandLine1);
	        writer.println();
	        writer.flush();
			
		}
		
		private void displayAndStoreResponseHTTP_1_0(String fileName) throws IOException{
			BufferedReader reader = getSocketReader();
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(DOWNLOAD_DESTINATION, fileName)));
			
	        String output;
	        while((output = reader.readLine()) != null){
	        	// Print server output
	        	System.out.println(output);
	        	
	        	// Parse for images
	        	
	        	
	        	
	        	// Store server output
		        writer.write(output);
	        }
	        reader.close();
	        writer.close();
	        
		}
		
		/**
		 * Method that displays and stores the HTTP response.
		 * Returns a list of all the image urls in the HTML file that was retrieved. 
		 * Returns an empty list if no image tags were found OR if the retrieved file was not an HTML file. 
		 * @param fileName
		 * @param requestType
		 * @param storeOnly
		 * @param path
		 * @return
		 * @throws IOException
		 */
		private List<String> displayAndStoreResponse(String fileName, RequestType requestType, Boolean storeOnly, String URL) throws IOException{
	        
	        String extension = "html";
	        
	        // Extract the base URL that will be passed to the parser for creating absolute image addresses
	        String baseURL = URL.substring(0,URL.lastIndexOf("/"));
	        
	        // Extract the extension of the requested file
	        URL = URL.split("/")[URL.split("/").length-1];
	        if (URL.split("\\.").length > 1){
	        	extension = URL.split("\\.")[URL.split("\\.").length-1];
	        }
	        
	        // Create reader and writer
	        DataOutputStream writer = new DataOutputStream(new FileOutputStream(new File(DOWNLOAD_DESTINATION, fileName + "." + extension)));
	        BufferedInputStream reader = new BufferedInputStream(getSocket().getInputStream());
	        
	        byte[] bytes = new byte[2048];
	        Boolean headerFound = false;
	        int offset, length;
	        while ((length = reader.read(bytes)) != -1){

		        offset = 0;
	        	String line = new String(bytes, 0, length);
	        	

	        	if (!storeOnly)System.out.print(line);
	        	
	        	if (!headerFound && (offset = line.indexOf("\r\n\r\n")) != -1){
	        		headerFound = true;
	        		length = length - offset - 4;
	        		offset = offset + 4;
	        		writer = new DataOutputStream(new FileOutputStream(new File(DOWNLOAD_DESTINATION, fileName + "." + extension)));
	        	};
	        	
	        	writer.write(bytes, offset, length);
                writer.flush();
	        	
	        	
	        }
            
	        // Parse for images if this is an html file
	        List<String> imageURLs = new ArrayList<String>();
	        if (extension.equals("html") || extension.equals("HTML")){
	        	imageURLs = Parser.findImageURLs(new File(DOWNLOAD_DESTINATION, fileName + "." + extension), baseURL);
	        	for (String imageURL : imageURLs){
	        		System.out.println(imageURL);
	        	}
	        }
        	
	        
	        reader.close();
	        writer.close();
	        
	        return imageURLs;
	        
		}
		
		private final Socket socket;
		private final PrintStream socketWriter;
		private final BufferedReader socketReader;
		private final String host;
		
		private final static String DOWNLOAD_DESTINATION = "./src/clientDownloads";
		
}
