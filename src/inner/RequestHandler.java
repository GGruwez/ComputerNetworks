package inner;
import java.net.*;
import java.text.SimpleDateFormat;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Output: responses
 * @author Gilles
 *
 */
public class RequestHandler {
	
	public BusyClient busyClient;
	public static final String HOST = "localhost";
	public int HTTP;
	public int code;
	public Response response;

	
	public RequestHandler(BusyClient busyClient){
		this.busyClient = busyClient;
	}
	
	public void handle() {
		
		BusyClient busyClient = getBusyClient();
		
		try {
			//extract request header and body
			byte[] request = new byte[2048];
			int index, offset, length;
			String line;
			String header = "";
			String message = "";
			int contentLength = 0;
			int readBytes = -1;
			Boolean headerFound = false;
			
			System.out.println("HANDLING STARTED");
			
			while (readBytes < contentLength && busyClient.getInput().available()>0){
						
				busyClient.getInput().read(request);
				
				offset = 0;
				line = new String(request);
				length = line.length();
				
				index = line.indexOf("\r\n\r\n");
				
				if (index != -1 && !headerFound){
					header += line.substring(0, index);
					offset = index+4;
					readBytes = length - index - 4;
					headerFound = true;
				}
				
				if (!headerFound){
					header += line;
				}else{
					message += line.substring(offset);
					readBytes += length;
				}
				
				
			}
			System.out.println(header);
			System.out.println("---------------------");
			System.out.println(message);
			System.out.println("HANDLING FINISHED");
		}
		catch (IOException e) {
			this.code = 500;
			System.out.println("Server error");
		}
		
	}
	
	

	private void executeGET(){
		String fileName = null;
		
	}
	
	private void executeHEAD(){
		
	}
	
	private void executePUT(){
		
	}
	
	private void executePOST(){
		
	}
	
	public int getCode(){
		return this.code;
	}
	
	public BusyClient getBusyClient(){
		return this.busyClient;
	}
	
	private String getHost(){
		return HOST;
	}
	
	public Response getResponse(){
		return this.response;
	}
	
	

}
