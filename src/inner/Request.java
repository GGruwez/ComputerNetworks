package inner;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import exceptions.UnknownHTTPVersionException;
import exceptions.UnknownRequestException;

public class Request {

		public Request(String typeString, String path, String portString, String versionString) throws UnknownRequestException, UnknownHTTPVersionException, URISyntaxException{
			path = new URI(Client.convertToReadableURL(path)).getPath();
			if (path.length() == 0){
				path = "/";
			}
			
			this.path = path;
			this.version = extractVersion(versionString);
			this.type = extractType(typeString);
			List<String> content = new ArrayList<String>();
			setContent(content);
		}
		
		public Request(RequestType type, String path, int port, HTTPVersion version) throws URISyntaxException{
			path = new URI(Client.convertToReadableURL(path)).getPath();
			if (path.length() == 0){
				path = "/";
			}
			
			this.path = path;
			this.version = version;
			this.type = type;
			List<String> content = new ArrayList<String>();
			setContent(content);
		}
		
		public RequestType getRequestType(){
			return this.type;
		}
		
		public String getPath(){
			return this.path;
		}
		
		public HTTPVersion getVersion(){
			return this.version;
		}
		
		public List<String> getContent(){
			if (canHaveContent()){
				return this.content;
			}else{
				return null;
			}
			
		}
		
		public void setContent(List<String> content){
			if (canHaveContent())this.content = content;
		}
		
		private Boolean canHaveContent(){
			return (getRequestType() == RequestType.POST || getRequestType() == RequestType.PUT);
		}
		
		private static RequestType extractType(String typeString) throws UnknownRequestException{
			
			if (typeString.equals("HEAD")){
				return RequestType.HEAD;
			}else if(typeString.equals("GET")){
				return RequestType.GET;
			}else if(typeString.equals("POST")){
				return RequestType.POST;
			}else if(typeString.equals("PUT")){
				return RequestType.PUT;
			}else{
				throw new UnknownRequestException(typeString);
			}
		}
		
		private static HTTPVersion extractVersion(String versionString) throws UnknownHTTPVersionException{
			if (versionString.equals("HTTP/1.0")){
				return HTTPVersion.HTTP_1_0;
			}else if(versionString.equals("HTTP/1.1")){
				return HTTPVersion.HTTP_1_1;
			}else{
				throw new UnknownHTTPVersionException(versionString);
			}
		}
		
		private final String path;
		private final HTTPVersion version;
		private final RequestType type;
		private List<String> content;
}
