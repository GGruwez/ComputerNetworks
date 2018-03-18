package inner;
import java.net.*;
import java.io.*;

/**
 * Output: responses
 * @author Gilles
 *
 */
public class RequestHandler implements Runnable {
	
	public BusyClient busyClient;
	public String host = "localhost";
	public String stringRequest;
	public int HTTP;
	public int code = 200;

	
	public RequestHandler(BusyClient busyClient){
		this.busyClient = busyClient;
	}
	
	@Override
	public void run() {
		try {
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
	
	private String makeHeader(){
		String header = " ";
		return header;
	}
	
	

}
