package exceptions;

public class UnknownRequestException extends Exception{
	
	public UnknownRequestException(String typeString){
		this.typeString = typeString;
		System.out.println("'" + typeString + "' is not a legal client command...");
	}
	
	public UnknownRequestException(){
		this.typeString = "Unknown";
		System.out.println("This is not a legal client command...");
	}
	
	public String getTypeString(){
		return this.typeString;
	}
	
	private final String typeString;
	
	private static final long serialVersionUID = 2003001L;
}
