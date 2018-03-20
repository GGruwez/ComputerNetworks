package exceptions;

public class UnknownStatusCodeException extends Exception{

	public UnknownStatusCodeException(int code){
		this.code = code;
		System.out.println("'" + code + "' is not a legal statuscode...");
	}
	
	public UnknownStatusCodeException(){
		this.code = 0;
		System.out.println("This is not a legal statuscode...");
	}
	
	private final int code;
	
	private static final long serialVersionUID = 2003001L;
}
