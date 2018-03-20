package inner;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import exceptions.UnknownHTTPVersionException;
import exceptions.UnknownRequestException;
import exceptions.UnknownStatusCodeException;

/**
 * Output: responses
 * @author Gilles
 *
 */
public class RequestHandler {
	
	public BusyClient busyClient;
	public static final String HOST = "localhost";
	public static final String SERVER_LOCATION = "./src/serverFiles";
	public static final String POST_PUT_FOLDER = "./src/postPut";
	public static final String ROOT_FILE = "/index.html";

	
	public RequestHandler(BusyClient busyClient){
		this.busyClient = busyClient;
	}
	
	public void handle() throws UnknownRequestException, URISyntaxException, UnknownHTTPVersionException, UnknownStatusCodeException, IOException {
		
		BusyClient busyClient = getBusyClient();
		
		try {
			//extract request header and body
			byte[] request;
			int index, offset, length;
			String line, modifiedLine;
			String header = "";
			String message = "";
			int contentLength = 0;
			int readBytes = -1;
			int endLength = 4;
			Boolean headerFound = false;
			Boolean hasAppendedLine = false;
			
			System.out.println("HANDLING STARTED");
			
			while (readBytes < contentLength || !headerFound){
				
				
				request = new byte[2048];
				// Make sure that if the request comes in in different lines, we wait properly
				if ((length = busyClient.getInput().available()) == 0){
					if (!hasAppendedLine){
						if (!headerFound){
							header += "\n";
						}else{
							message += "\n";
						}
						hasAppendedLine = true;
					}
				}else{
					busyClient.getInput().read(request);
					hasAppendedLine = false;
				}
				
				offset = 0;
				line = new String(request);
				
				
				index = line.indexOf("\r\n\r\n");
				//if (index == -1) index = line.indexOf("\r\n");
				if (index == -1 && line.replaceAll("\n", "%").replaceAll("\r", "/").trim().equals("/%")){
					index = 0;
					endLength = 2;
				}
				
				if (index != -1 && !headerFound){
					header += line.substring(0, index);
					offset = index + endLength;
					readBytes = length - index - endLength;
					headerFound = true;
					contentLength = Parser.parseForContentLength(header);
				}
				
				if (!headerFound){
					header += line;
				}else{
					message += line.substring(offset);
					readBytes += length;
				}
			}
			
			
			Request requestObject = null;
			try{
				requestObject = Parser.parseRequestHeader(header, getBusyClient().getServer().getPort());
			}catch(Exception e){
				e.printStackTrace();
				respondWithError(400);
				return;
			}
			
			// If the header request was valid, proceed with the handling
			String host = Parser.parseForHost(header);
			int port = Parser.parseForPort(header);
			Date date = Parser.parseForModifiedSince(header);
			
			executeRequest(requestObject, host.trim(), port, message, date);
			
			System.out.println("HANDLING FINISHED");
			
			
			
			
		}
		catch (Exception e) {
			e.printStackTrace();
			respondWithError(500);
			System.out.println("Server error");
		}
		
	}
	
	public void executeRequest(Request request, String host, int port, String message, Date date) throws IOException, UnknownStatusCodeException{
		if ((host.equals(getHost()) && (port == getBusyClient().getServer().getPort() || port == -1)) || request.getVersion() == HTTPVersion.HTTP_1_0 ){
			if (request.getRequestType() == RequestType.GET){
				System.out.println("EXECUTE GET REQUEST");
				executeGet(request, date);
			}else if (request.getRequestType() == RequestType.HEAD){
				System.out.println("EXECUTE HEAD REQUEST");
				executeHead(request, date);
			}else if (request.getRequestType() == RequestType.PUT){
				System.out.println("EXECUTE PUT REQUEST");
				executePut(request, date, message);
			}else if (request.getRequestType() == RequestType.POST){
				System.out.println("EXECUTE POST REQUEST");
				executePost(request, date, message);
			}else{
				respondWithError(400);
			}
		}else{
			respondWithError(400);
		}
		
	}

	private void executeGet(Request request, Date date) throws IOException, UnknownStatusCodeException{
		
		String path = request.getPath();
		if (path.equals("/"))path = ROOT_FILE;
		
		// Find the file that we require
		BufferedInputStream reader = null;
		File file = null;
		try{
			file = new File(SERVER_LOCATION+path);
			reader = new BufferedInputStream(new FileInputStream(file));
		}catch (FileNotFoundException e){
			e.getStackTrace();
			respondWithError(404);
			return;
		}
		
		// Check for modified-since
		// Sample request headers for debugging: 
		//		If-Modified-Since: Tue, 05 Jul 2016 23:27:52 GMT
		//		If-Modified-Since: Tue, 05 Jul 2018 23:27:52 GMT
		if (date != null){
			Date lastModified = new Date(file.lastModified());
			
			// If lastModified was before date, throw 304 Error
			if (date.after(lastModified)){
				String lastModifiedHeader = "Last-Modified: " + Parser.toHTTPDate(lastModified);
				respondWithError(304, lastModifiedHeader);
				reader.close();
				return;
			}
		}
		
		// Construct body
		byte[] body = new byte[0];
		int nbReadBytes;
		try{
			while ((nbReadBytes = reader.available()) > 0){
				byte[] data = new byte[nbReadBytes];
				reader.read(data);
				body = merge(body, data);
			}
		}catch (IOException e){
			e.getStackTrace();
			respondWithError(500);
			reader.close();
			return;
		}

		int contentLength = body.length;
		
		
		// Construct header
		List<String> headers = new ArrayList<String>();
		String extension = Parser.extractExtension(path).trim();
		headers.add("Date: " + Parser.toHTTPDate(new Date()));
		headers.add("Content-Length: " + contentLength);
		headers.add("Content-Type: " + Parser.extractFileType(extension) +"/" +extension);
		String header = null;
		try{
			header = constructHeader(StatusCode.ERROR200, headers);
		}catch(UnknownStatusCodeException e){
			e.getStackTrace();
			respondWithError(500);
			reader.close();
			return;
		}
		
		// Merge
		byte[] response = merge(header.getBytes(), body);
		System.out.println(new String(header));
		
		// Send response
		sendResponse(response);
		
		
		reader.close();
		
	}
	
	private void executeHead(Request request, Date date) throws IOException, UnknownStatusCodeException{
		String path = request.getPath();
		if (path.equals("/"))path = ROOT_FILE;
		
		// Find the file that we require and determine content-length
		File file = null;
		file = new File(SERVER_LOCATION+path);
		long contentLength = file.length();
		if (!file.exists()){
			respondWithError(404);
			return;
		}
		
		// Check for modified-since
		// Sample request headers for debugging: 
		//		If-Modified-Since: Tue, 05 Jul 2016 23:27:52 GMT
		//		If-Modified-Since: Tue, 05 Jul 2018 23:27:52 GMT
		if (date != null){
			Date lastModified = new Date(file.lastModified());
			
			// If lastModified was before date, throw 304 Error
			if (date.after(lastModified)){
				String lastModifiedHeader = "Last-Modified: " + Parser.toHTTPDate(lastModified);
				respondWithError(304, lastModifiedHeader);
				return;
			}
		}
		
		
		// Construct header
		List<String> headers = new ArrayList<String>();
		String extension = Parser.extractExtension(path).trim();
		headers.add("Date: " + Parser.toHTTPDate(new Date()));
		headers.add("Content-Length: " + contentLength);
		headers.add("Content-Type: " + Parser.extractFileType(extension) +"/" +extension);
		String header = null;
		try{
			header = constructHeader(StatusCode.ERROR200, headers);
		}catch(UnknownStatusCodeException e){
			e.getStackTrace();
			respondWithError(500);
			return;
		}
		
		System.out.println(new String(header));
		
		// Send response
		sendResponse(header.getBytes());
		
	}
	
	private void executePut(Request request, Date date, String message) throws IOException, UnknownStatusCodeException{
		
		// Create new filename if filename was not given
		String path = Parser.getPathWithoutFilename(request.getPath());
		String filename = Parser.getFilename(request.getPath());
		if (filename.equals(""))filename = "newFilename";
		
		
		// Write new file
		System.out.println(message);
		DataOutputStream writer = new DataOutputStream(new FileOutputStream(new File(POST_PUT_FOLDER+path, filename + ".txt")));
		writer.write(message.getBytes());
        writer.flush();
        writer.close();
        
        // Construct header
        List<String> headers = new ArrayList<String>();
        headers.add("Content-Location: " + path + filename + ".txt");
        headers.add("Date: " + Parser.toHTTPDate(new Date()));
        String header = constructHeader(StatusCode.ERROR200, headers);
        
        // Send response
     	sendResponse(header.getBytes());
        
	}
	
	private void executePost(Request request, Date date, String message) throws IOException, UnknownStatusCodeException{
		// Create new filename if filename was not given
		String path = Parser.getPathWithoutFilename(request.getPath());
		String filename = Parser.getFilename(request.getPath());
		if (filename.equals(""))filename = "newFilename";
		
		
		// Append file or create new one
		System.out.println(message);
		File file = new File(POST_PUT_FOLDER+path, filename + ".txt");
		if (!file.exists()){
			// Create new file and write to it
			DataOutputStream writer = null;
			try{
				writer = new DataOutputStream(new FileOutputStream(file));
			}catch (IOException e){
				e.printStackTrace();
				respondWithError(404);
				return;
			}
			
			writer.write(message.getBytes());
	        writer.flush();
	        writer.close();
		}else{
			// Append existing file
			Files.write(Paths.get(POST_PUT_FOLDER+path, filename + ".txt"), message.getBytes(), StandardOpenOption.APPEND);
		}
		
        
        // Construct header
        List<String> headers = new ArrayList<String>();
        headers.add("Content-Location: " + path + filename + ".txt");
        headers.add("Date: " + Parser.toHTTPDate(new Date()));
        String header = constructHeader(StatusCode.ERROR200, headers);
        
        // Send response
     	sendResponse(header.getBytes());
	}
	
	private void sendResponse(byte[] response) throws IOException{
		
		DataOutputStream writer = getBusyClient().getOutput();
		writer.write(response);
		
	}
	
	private String constructHeader(StatusCode status, List<String> headers) throws UnknownStatusCodeException{
		String result = "HTTP/1.1 " + status.getStatusString() + "\r\n";
		for (String header : headers){
			result += header + "\r\n";
		}
		result += "\r\n\r\n";
		return result;
	}
	
	/*
	 * ERROR RESPONSES
	 */
	private void respondWithError(int errorCode) throws UnknownStatusCodeException, IOException{
		respondWithError(errorCode, "");
	}
	
	private void respondWithError(int errorCode, String header) throws UnknownStatusCodeException, IOException{
		List<String> headers = new ArrayList<String>();
		headers.add("Date: " + Parser.toHTTPDate(new Date()));
		if (header.length()>0)headers.add(header);
		String finalHeader = constructHeader(Parser.extractStatusCode(errorCode), headers);
		sendResponse(finalHeader.getBytes());
	}
	
	public BusyClient getBusyClient(){
		return this.busyClient;
	}
	
	private String getHost(){
		return HOST;
	}
	
	/*
	 * HELP METHODS
	 */
	public static byte[] merge(byte[] array1, byte[] array2){
		byte[] combined = new byte[array1.length + array2.length];
		System.arraycopy(array1,0,combined,0,array1.length);
		System.arraycopy(array2,0,combined,array1.length,array2.length);
		return combined;
	}
	
	
	

}
