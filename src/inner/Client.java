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
				displayAndStoreResponseHTTP_1_1(fileName, RequestType.GET, true, request.getPath());
		        
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
				displayAndStoreResponseHTTP_1_1(fileName, RequestType.GET, true, request.getPath());
		        
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
				displayAndStoreResponseHTTP_1_1(fileName, RequestType.GET, true, request.getPath());
		        
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
				displayAndStoreResponseHTTP_1_1(fileName, RequestType.GET, true, request.getPath());
		        
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
		
		private void displayAndStoreResponseHTTP_1_1(String fileName, RequestType requestType, Boolean storeOnly, String path) throws IOException{
			//BufferedReader reader = getSocketReader();
			
	        //String output = "";
			List<Byte> output = new ArrayList<Byte>();
	        
	        String extension = "html";
	        Boolean startStoring = !(requestType == RequestType.GET);
	        
	        if (path.split("\\.").length > 1){
	        	extension = path.split("\\.")[path.split("\\.").length-1];
	        }
	        
	        /*
	        // Writer
            PrintStream serverInput = new PrintStream(getSocket().getOutputStream(),true);
            // Reader
            BufferedInputStream reader = new BufferedInputStream(getSocket().getInputStream());
            
            OutputStream writer = new FileOutputStream(new File(DOWNLOAD_DESTINATION, fileName + "." + extension));

            
    		byte[] b = new byte[1];
    		int length;
    		List<Byte> bytes = new ArrayList<Byte>();

    		while ((length = reader.read(b)) != -1) {
    			writer.write(b, 0, length);
    			String string = new String(b);
    			System.out.println(string);
    		}
	        */
	        
	        DataOutputStream writer = new DataOutputStream(new FileOutputStream(new File(DOWNLOAD_DESTINATION, fileName + "." + extension)));
	        BufferedInputStream reader = new BufferedInputStream(getSocket().getInputStream());
	        
	        /*
	        while (true) {	
	        	//final String line = reader.readLine();
	        	final String line = reader.readLine();
	        	
	        	// Print server output
	        	if (!storeOnly)System.out.println(line);
	        	
	        	// Check if the end of the file was reached (we assume every html file ends with a closing HTML tag)
	        	if (line == null || line.contains("</HTML>") || line.contains("</html>")) break;
	        	
	        	// Store server output
	        	//if (startStoring)output += line;
	        	byte[] byteArray = line.getBytes();
	        	if (startStoring){
	        		for (byte b : byteArray){
	        			output.add(b);
	        			
	        		}
	        		writer.write(byteArray, 0, byteArray.length);
	        		System.out.println(byteArray.length);
	        	}
	        	
	        	// Determine document extension
	        	if (!extensionFound && line.contains("Content-Type")){
	        		extension = line.split("/")[line.split("/").length-1];
	        		extensionFound = true;
	        		if (!extension.equals("html"))storeOnly=true;
	        		writer = new FileOutputStream(new File(DOWNLOAD_DESTINATION, fileName + "." + extension));
	        	}
	        	
	        	// Check if the end of the header was reached
	        	if (!startStoring && line.isEmpty()) startStoring = true;
	        	
	        }
	        */
	        
	        /*
	        Boolean done = false;
	        Boolean headerFound = false;
	        String line;
	        while ((line = reader.readLine()) != null && !line.contains("</HTML>") && !line.contains("</html>")){
	        	
	        	byte[] byteArray = line.getBytes();
	        	writer.write(byteArray, 0, byteArray.length);
	        	
	        	System.out.println(line);
	        	
	        	if (line.isEmpty()) {
	        		System.out.println("--------------------------------------");
	        		writer = new DataOutputStream(new FileOutputStream(new File(DOWNLOAD_DESTINATION, fileName + "." + extension)));
	        		headerFound = true;
	        		break;
	        	}
	        }
	        
	        char[] charLine = new char[1];
	        while (headerFound && reader.read(charLine) != -1){
	        	
	        	System.out.print(charLine);
	        	String charToString = new String(charLine);
	        	
	        	
	        	byte[] charByteArray = charToString.getBytes();
	        	writer.write(charByteArray, 0, charByteArray.length);
	        	writer.flush();
	        }
	        */
	        
	        byte[] bytes = new byte[1024];
	        Boolean headerFound = false;
	        while (reader.read(bytes) != -1){
	        	
	        	String line = new String(bytes);
	        	
	        	if (!storeOnly)System.out.print(line);
	        	
	        	if (!headerFound && line.indexOf("\r\n\r\n") != -1){
	        		headerFound = true;
	        		
	        	};
	        	
	        }
            
            
        	/*
	        // Parse for images
        	List<String> imageURLs = Parser.findImageURLs(output);
        	for (String imageURL : imageURLs){
        		System.out.println(imageURL);
        	}
        	*/
        	
        	/*
        	// Store server output
        	BufferedWriter writer = new BufferedWriter(new FileWriter(new File(DOWNLOAD_DESTINATION, fileName + "." + extension)));
	        writer.write(output);
	        */
	        
	        reader.close();
	        writer.close();
	        
		}
		
		private final Socket socket;
		private final PrintStream socketWriter;
		private final BufferedReader socketReader;
		private final String host;
		
		private final static String DOWNLOAD_DESTINATION = "./src/clientDownloads";
		
}
