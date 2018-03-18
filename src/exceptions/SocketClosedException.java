package exceptions;

/**
 * Exception thrown when a socket is closed while the user is trying to use it.
 * @author anthonyrathe
 *
 */
public class SocketClosedException extends Exception{

	public SocketClosedException(){
		System.out.println("This socket was closed...");
	}
	
	private static final long serialVersionUID = 2003001L;
}
