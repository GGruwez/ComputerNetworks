package inner;

import java.text.SimpleDateFormat;

import exceptions.UnknownHTTPVersionException;

public class Response {
	
	HTTPVersion version;
	int status;
	String reason;
	RequestHandler requestHandler;
	
	public Response(HTTPVersion version, int status, String reason){
		this.version = version;
		this.status = status;
		this.reason = reason;
		
	}

	
	private String makeHeader(){
		// \n --> LF
		// \r --> CR

		String header = " ";
		
		if (this.getRequestHandler().getCode() == 200) {
			this.status = 200;
			this.reason = "OK";
		}
		else if (this.getRequestHandler().getCode() == 404) {
			this.status = 404; 
			this.reason = "Not Found";
		}
		else if (this.getRequestHandler().getCode() == 400) {
			this.status = 400;
			this.reason = "Bad Request";
		}
		else if (this.getRequestHandler().getCode() == 500) {
			this.status = 500;
			this.reason = "Server Error";
		}
		else if (this.getRequestHandler().getCode() == 304) {
			this.status = 304;
			this.reason = "Not Modified";
		}
		
		
		header += "\r\n";
		
				
		//header must contain date
		long dateTime = System.currentTimeMillis();				
		SimpleDateFormat format = new SimpleDateFormat("E, dd MMM Y HH:mm:ss");
		header += "Date: " + format.format(dateTime) + " GMT\r\n\r\n";
		
		return header;
	}
	
	
	/**
	 * Static method for converting a string to a HTTPVersion
	 * @param versionString
	 * @return
	 * @throws UnknownHTTPVersionException
	 */
	private static HTTPVersion extractVersion(String versionString) throws UnknownHTTPVersionException{
		if (versionString.equals("HTTP/1.0")){
			return HTTPVersion.HTTP_1_0;
		}else if(versionString.equals("HTTP/1.1")){
			return HTTPVersion.HTTP_1_1;
		}else{
			throw new UnknownHTTPVersionException(versionString);
		}
	}
	
	public RequestHandler getRequestHandler(){
		return this.requestHandler;
	}
	
	
	
	
}
