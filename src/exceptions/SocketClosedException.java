package exceptions;

public class SocketClosedException extends Exception{

	public SocketClosedException(){
		System.out.println("This socket was closed...");
	}
	
}
