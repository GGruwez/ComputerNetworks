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

import exceptions.SocketClosedException;
import exceptions.UnknownHTTPVersionException;
import exceptions.UnknownRequestException;

public class Client {

		/**
		 * Constructs a client object with given URL and port.
		 * @param URL
		 * @param port
		 * @throws URISyntaxException
		 * @throws UnknownHostException
		 * @throws IOException
		 */
		public Client(URI URL, int port) throws URISyntaxException, UnknownHostException, IOException{
			
			//create socket for this client
			Socket clientSocket = new Socket(URL.getHost(),port);

			// Writer
            PrintStream serverInput = new PrintStream(clientSocket.getOutputStream(),true);
            // Reader
            BufferedInputStream serverOutput = new BufferedInputStream(clientSocket.getInputStream());
            
            //initialize client's fields
            this.socket = clientSocket;
            this.socketWriter = serverInput;
            this.socketReader = serverOutput;
            this.host = convertToReadableURL(URL).getHost();
            this.port = port;
		}
		
		/**
		 * Constructs a client object.
		 * @param URL
		 * @param port
		 * @throws URISyntaxException
		 * @throws UnknownHostException
		 * @throws IOException
		 */
		public Client(String URL, String port) throws URISyntaxException, UnknownHostException, IOException{
			
			String host = getHostFromURL(URL);
			int portInt = Integer.parseInt(port);
			
			Socket clientSocket = new Socket(host, portInt);

			// Writer
            PrintStream serverInput = new PrintStream(clientSocket.getOutputStream(),true);
            // Reader
            BufferedInputStream serverOutput = new BufferedInputStream(clientSocket.getInputStream());
		
            this.socket = clientSocket;
            this.socketWriter = serverInput;
            this.socketReader = serverOutput;
            this.host = new URI(convertToReadableURL(URL)).getHost();
            this.port = portInt;
		}
		
		/**
		 * Closes client socket.
		 * 
		 * @throws IOException
		 */
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
		
		private BufferedInputStream getSocketReader(){
			return this.socketReader;
		}
		
		public String getHost(){
			return this.host;
		}
		
		public int getPort(){
			return this.port;
		}
		
		/*
		 * HELP METHODS
		 */
		
		public static List<String> askForMessageContent() throws IOException{
			BufferedReader contentReader = new BufferedReader(new InputStreamReader(System.in));
			System.out.print("Insert POST request content and press ENTER for every line: " );
			System.out.print("Finish with #END# ");
			
			Boolean reading = true;
			List<String> content = new ArrayList<String>();
			while (reading){
				String line = contentReader.readLine();
				if (line.equals("#END#")){
					reading = false;
				}else{
					content.add(line);
				}
			}
			
			contentReader.close();
			
			return content;
			
			
		}
		
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
		
		public void execute(Request request, Boolean displayToTerminal) throws UnknownRequestException, IOException, UnknownHTTPVersionException, URISyntaxException, SocketClosedException{
			if (getSocket().isClosed()){
				throw new SocketClosedException();
			}else{
				if (request.getRequestType() == RequestType.GET){
					executeGet(request, displayToTerminal);
				}else if (request.getRequestType() == RequestType.HEAD){
					executeHead(request, displayToTerminal);
				}else if (request.getRequestType() == RequestType.POST){
					executePost(request, displayToTerminal);
				} else if (request.getRequestType() == RequestType.PUT){
					executePut(request, displayToTerminal);
				}else{
					throw new UnknownRequestException();
				}
			}
		}
		
		/**
		 * Method for the execution of a GET request
		 * @param request
		 * @throws IOException
		 * @throws UnknownHTTPVersionException
		 * @throws URISyntaxException 
		 * @throws SocketClosedException 
		 * @throws UnknownRequestException 
		 */
		private void executeGet(Request request, Boolean displayToTerminal) throws IOException, UnknownHTTPVersionException, URISyntaxException, UnknownRequestException, SocketClosedException{
			
			// Check whether or not this is a GET request
			if (request.getRequestType() != RequestType.GET){
				throw new IOException("This is not a GET request");
			}
			
			// Create filename based on the url
			String fileName = "GET" + getHost() + request.getPath();
			fileName = fileName.replace('.', '_').replace('/', '_');
			
			if (request.getVersion() == HTTPVersion.HTTP_1_1){
				// Execute as an HTTP/1.1 request

				// Send request
				sendHTTP_1_1Request(request);
				
				// Print and store server output
				List<String> embeddedObjectsURLs = displayAndStoreResponse(fileName, RequestType.GET, !displayToTerminal, "http://" + getHost() + request.getPath(), false);
		        
				// Fetch and store all embedded objects
				for (String embeddedObjectURL : embeddedObjectsURLs){
					
		        	if (Parser.isLocalAddress(embeddedObjectURL, getHost())){
		        		// If image is local, reuse connection
		        		execute(new Request(RequestType.GET, Parser.getPath(embeddedObjectURL), getPort(), HTTPVersion.HTTP_1_1), false);
		        	}else{
		        		// If image is not local, make a new connection
		        		Client newClient = new Client(new URI(convertToReadableURL(embeddedObjectURL)), getPort());
		        		newClient.execute(new Request(RequestType.GET, Parser.getPath(embeddedObjectURL), getPort(), HTTPVersion.HTTP_1_1), false);
		        	}
		        }
				
			}else if (request.getVersion() == HTTPVersion.HTTP_1_0){
				// Execute as an HTTP/1.0 request
				
				// Send request
				sendHTTP_1_0Request(request);
				
				// Print and store server output
				List<String> embeddedObjectsURLs = displayAndStoreResponse(fileName, RequestType.GET, !displayToTerminal, "http://" + getHost() + request.getPath(), false);
		        
				// Fetch and store all embedded objects
				for (String embeddedObjectURL : embeddedObjectsURLs){
					// Always make a new connection for each embedded object
		        	Client newClient = new Client(new URI(convertToReadableURL(embeddedObjectURL)), getPort());
		        	newClient.execute(new Request(RequestType.GET, Parser.getPath(embeddedObjectURL), getPort(), HTTPVersion.HTTP_1_0), false);
				}
				
				close();
		        
			}else{
				throw new UnknownHTTPVersionException();
			}
			
			
	        
		}
		
		/**
		 * Executes HEAD request // HEAD = GET without actual body message.
		 * @param request
		 * @throws IOException
		 * @throws UnknownHTTPVersionException
		 */
		private void executeHead(Request request, Boolean displayToTerminal) throws IOException, UnknownHTTPVersionException{
			
			// Check whether or not this is a HEAD request
			if (request.getRequestType() != RequestType.HEAD){
				throw new IOException("This is not a HEAD request");
			}
			
			// Create filename based on URL
			String fileName = "HEAD" + getHost() + request.getPath();
			fileName = fileName.replace('.', '_').replace('/', '_');
			
			if (request.getVersion() == HTTPVersion.HTTP_1_1){
				// Execute as an HTTP/1.1 request

				// Send request
				sendHTTP_1_1Request(request);
		        
			}else if (request.getVersion() == HTTPVersion.HTTP_1_0){
				// Execute as an HTTP/1.0 request
				
				// Send request
				sendHTTP_1_0Request(request);
		        
			}else{
				throw new UnknownHTTPVersionException();
			}
			
			// Print and store server output
			displayAndStoreResponse(fileName, RequestType.HEAD, !displayToTerminal, request.getPath(), true);
			
	        
		}
		
		private void executePost(Request request, Boolean displayToTerminal) throws IOException, UnknownHTTPVersionException{
			
			// Get content of POST request
			List<String> content = askForMessageContent();
			request.setContent(content);
			
			// Check whether or not this is a POST request
			if (request.getRequestType() != RequestType.POST){
				throw new IOException("This is not a POST request");
			}
			
			// Create filename based on URL
			String fileName = "POST" + getHost() + request.getPath();
			fileName = fileName.replace('.', '_').replace('/', '_');
			
			if (request.getVersion() == HTTPVersion.HTTP_1_1){
				// Execute as an HTTP/1.1 request

				// Send request
				sendHTTP_1_1Request(request);
		        
			}else if (request.getVersion() == HTTPVersion.HTTP_1_0){
				// Execute as an HTTP/1.0 request
				
				// Send request
				sendHTTP_1_0Request(request);
		        
			}else{
				throw new UnknownHTTPVersionException();
			}
			
			// Print and store server output
			displayAndStoreResponse(fileName, RequestType.POST, !displayToTerminal, request.getPath(), true);
	        
		}
		
		private void executePut(Request request, Boolean displayToTerminal) throws IOException, UnknownHTTPVersionException{
			
			// Get content of PUT request
			List<String> content = askForMessageContent();
			request.setContent(content);
			
			// Check whether or not this is a PUT request
			if (request.getRequestType() != RequestType.PUT){
				throw new IOException("This is not a PUT request");
			}
			
			// Create filename based on URL
			String fileName = "PUT" + getHost() + request.getPath();
			fileName = fileName.replace('.', '_').replace('/', '_');
			
			if (request.getVersion() == HTTPVersion.HTTP_1_1){
				// Execute as an HTTP/1.1 request

				// Send request
				sendHTTP_1_1Request(request);
				
			}else if (request.getVersion() == HTTPVersion.HTTP_1_0){
				// Execute as an HTTP/1.0 request
				
				// Send request
				sendHTTP_1_0Request(request);
		        
			}else{
				throw new UnknownHTTPVersionException();
			}
			
			// Print and store server output
			displayAndStoreResponse(fileName, RequestType.PUT, !displayToTerminal, request.getPath(), true);
	        
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
			
			List<String> content;
			
			String commandLine1 = requestType + " " + path + " " + version;
			String commandLine2 = "Host: " + host;
			String commandLine3 = "Connection: Keep-alive";
	        writer.println(commandLine1);
	        writer.println(commandLine2);
	        writer.println(commandLine3);
	        
	        if ((content = request.getContent()) != null){
	        	
	        	int contentLength = content.size();
	        	for (String line : content){
	        		contentLength += line.length();
	        	}
	        	
	        	writer.println("Content-Type: text/html");
	        	writer.println("Content-Length: " + contentLength);
	        }
	        
	        writer.println();
	        writer.flush();

	        
	        if ((content = request.getContent()) != null){
	        	for (String line : content){
	        		writer.println(line);
	        	}
	        	writer.flush();
	        }
	        
			
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
		
		
		/**
		 * Method that displays and stores the HTTP response.
		 * Returns a list of all the image urls in the HTML file that was retrieved. 
		 * Returns an empty list if no image tags were found OR if the retrieved file was not an HTML file. 
		 * @param fileName
		 * @param requestType
		 * @param storeOnly
		 * @param 
		 * @return
		 * @throws IOException
		 */
		private List<String> displayAndStoreResponse(String fileName, RequestType requestType, Boolean storeOnly, String URL, Boolean headerOnly) throws IOException{
	        
	        String extension = "html";
	        
	        // Extract the base URL that will be passed to the parser for creating absolute image addresses
	        String baseURL = URL.substring(0,URL.lastIndexOf("/"));
	        
	        // Extract the extension of the requested file
	        int lastSlashIndex = URL.lastIndexOf("/");
	        int urlLength = URL.length();
	        if (URL.split("/").length > 0){
	        	URL = URL.split("/")[URL.split("/").length-1];
		        if (URL.split("\\.").length > 1 && lastSlashIndex < urlLength-1 && !headerOnly){
		        	extension = URL.split("\\.")[URL.split("\\.").length-1];
		        }
	        }
	        
	        
	        // Create reader and writer
	        DataOutputStream writer = new DataOutputStream(new FileOutputStream(new File(DOWNLOAD_DESTINATION, fileName + "." + extension)));
	        
	        BufferedInputStream reader = getSocketReader();
	        
	        byte[] bytes = new byte[2048];
	        Boolean headerFound = false;
	        int offset, length;
	        int contentLength = -1;
	        int nbOfReadBytes = 0;
	        String line = "";
	        while ((contentLength == -1 || nbOfReadBytes < contentLength) && (length = reader.read(bytes)) != -1){

		        offset = 0;
	        	line = new String(bytes, 0, length);
	        	
	        	if (contentLength == -1 && !headerFound){
	        		contentLength = Parser.parseForContentLength(line);
	        	}
	        	
	        	if (!storeOnly)System.out.print(line);
	        	
	        	if (!headerFound && (offset = line.indexOf("\r\n\r\n")) != -1){
	        		if (!headerOnly){
		        		length = length - offset - 4;
		        		offset = offset + 4;
		        		nbOfReadBytes = 0;
		        		writer = new DataOutputStream(new FileOutputStream(new File(DOWNLOAD_DESTINATION, fileName + "." + extension)));
	        		}else{
	        			length = offset;
	        			offset = 0;
	        		}
	        		headerFound = true;
	        	};
	        	
	        	nbOfReadBytes += length;
	        	writer.write(bytes, offset, length);
                writer.flush();
                
                if (headerFound && headerOnly)break;
	        	
	        }
            
	        // Parse for embedded objects if this is an html file
	        List<String> imageURLs = new ArrayList<String>();
	        if (extension.equals("html") || extension.equals("HTML")){
	        	imageURLs = Parser.findImageURLs(new File(DOWNLOAD_DESTINATION, fileName + "." + extension), baseURL);
	        	
	        }

	        writer.close();
	        return imageURLs;
	        
		}
		
		private final Socket socket;
		private final PrintStream socketWriter;
		private final BufferedInputStream socketReader;
		private final String host;
		private final int port;
		
		private final static String DOWNLOAD_DESTINATION = "./src/clientDownloads";
		
}
