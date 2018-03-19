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
	public String host = "localhost";
	public String stringRequest;
	public int HTTP;
	public int code;
	public Response response;

	
	public RequestHandler(BusyClient busyClient){
		this.busyClient = busyClient;
	}
	
	public void handle() {
		try {
			//extract request
			byte[] request = new byte[10000000];
			busyClient.getInput().read(request);
			this.stringRequest = new String(request);
		}
		catch (IOException e) {
			this.code = 500;
			System.out.println("Server error");
		}
		
		
		if (this.stringRequest.contains("GET")) {
			executeGET();
		}
		else if (this.stringRequest.contains("HEAD")) {
			executeHEAD();
		}
		else if (this.stringRequest.contains("PUT")) {
			executePUT();
		}
		else if (this.stringRequest.contains("POST")) {
			executePOST();
		}
		else if (!this.containsHost() /*&& version = 1.1*/ ) {
			this.code = 400;
		}
		else {
			this.code = 400;
		}
	}
	
	

	private void executeGET(){
		
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
	
	private Boolean containsHost(){
		if (this.stringRequest.contains("\r\nHost:"))
			return true;
		else
			return false;

	}
	
	private String getHost(){
		return this.host;
	}
	
	public Response getResponse(){
		return this.response;
	}
	
	

}
