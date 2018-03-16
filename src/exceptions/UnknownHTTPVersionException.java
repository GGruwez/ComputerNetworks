package exceptions;

public class UnknownHTTPVersionException extends Exception {

	public UnknownHTTPVersionException(String versionString){
		this.versionString = versionString;
		System.out.println("'" + versionString + "' is not a legal/supported HTTP version...");
	}
	
	public UnknownHTTPVersionException(){
		this.versionString = "Unknown";
		System.out.println("This is not a legal/supported HTTP version...");
	}
	
	public String getVersionString(){
		return this.versionString;
	}
	
	private final String versionString;
	
	private static final long serialVersionUID = 2003001L;
	
}
