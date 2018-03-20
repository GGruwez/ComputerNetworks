package exceptions;

public class UnknownStatusCodeException extends Exception{

	public UnknownStatusCodeException(int code){
		System.out.println("'" + code + "' is not a legal statuscode...");
	}
	
	public UnknownStatusCodeException(){
		System.out.println("This is not a legal statuscode...");
	}
	
	
	private static final long serialVersionUID = 2003001L;
}
