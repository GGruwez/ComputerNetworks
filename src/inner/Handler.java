package inner;
import java.net.*;
import java.io.*;


public class Handler implements Runnable {
	
	Socket socket;
	public Handler(Socket socket){
		this.socket = socket;
	}
	
	@Override
	public void run(){
		try {
			//input from client
			BufferedReader in = new BufferedReader(new
                    InputStreamReader(socket.getInputStream()));
			//output to client
            DataOutputStream out = new DataOutputStream
                    (socket.getOutputStream());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

}
