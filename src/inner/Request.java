package inner;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import exceptions.UnknownHTTPVersionException;
import exceptions.UnknownRequestException;

/**
 * Class for constructing proper requests.
 * @author anthonyrathe
 *
 */
public class Request {

		/** 
		 * Constructor for requests of any of the four supported types.
		 * @param typeString
		 * @param path
		 * @param portString
		 * @param versionString
		 * @throws UnknownRequestException
		 * @throws UnknownHTTPVersionException
		 * @throws URISyntaxException
		 */
		public Request(String typeString, String path, String portString, String versionString) throws UnknownRequestException, UnknownHTTPVersionException, URISyntaxException{
			path = new URI(Parser.convertToReadableURL(path)).getPath();
			if (path.length() == 0){
				path = "/";
			}
			
			this.path = path;
			this.version = Parser.extractVersion(versionString);
			this.type = Parser.extractType(typeString);
			List<String> content = new ArrayList<String>();
			setContent(content);
		}
		
		/**
		 * Constructor for requests of any of the four supported types.
		 * @param type
		 * @param path
		 * @param port
		 * @param version
		 * @throws URISyntaxException
		 */
		public Request(RequestType type, String path, int port, HTTPVersion version) throws URISyntaxException{
			path = new URI(Parser.convertToReadableURL(path)).getPath();
			if (path.length() == 0){
				path = "/";
			}
			
			this.path = path;
			this.version = version;
			this.type = type;
			List<String> content = new ArrayList<String>();
			setContent(content);
		}
		
		/*
		 * GETTERS AND SETTERS
		 */
		
		/**
		 * Return the type of this request.
		 */
		public RequestType getRequestType(){
			return this.type;
		}
		
		/**
		 * Returns the path of this request.
		 * @return
		 */
		public String getPath(){
			return this.path;
		}
		
		/**
		 * Returns this request's HTTP version. 
		 * @return
		 */
		public HTTPVersion getVersion(){
			return this.version;
		}
		
		/**
		 * Returns this request's content.
		 * @return
		 */
		public List<String> getContent(){
			if (canHaveContent()){
				return this.content;
			}else{
				return null;
			}
			
		}
		
		/**
		 * Sets the given content as content of this request.
		 * @param content
		 */
		public void setContent(List<String> content){
			if (canHaveContent())this.content = content;
		}
		
		/*
		 * METHODS
		 */
		
		/**
		 * Method that returns whether or not the request requires a message body.
		 * @return
		 */
		private Boolean canHaveContent(){
			return (getRequestType() == RequestType.POST || getRequestType() == RequestType.PUT);
		}
		
		
		/*
		 * VARIABLES
		 */
		
		private final String path;
		private final HTTPVersion version;
		private final RequestType type;
		private List<String> content;
}
