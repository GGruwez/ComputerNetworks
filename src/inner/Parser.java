package inner;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import exceptions.UnknownHTTPVersionException;
import exceptions.UnknownRequestException;

/**
 * An abstract class containing all parsing-related methods
 * @author anthonyrathe
 *
 */
public abstract class Parser {
	
	
	/**
	 * Method that returns absolute addresses of all image tags found in a given HTML file.
	 * These addresses are based on the given baseURL
	 * @param file
	 * @param baseURL
	 * @return
	 * @throws IOException
	 */
	public static List<String> findImageURLs(File file, String baseURL) throws IOException{
		List<String> imageURLs = new ArrayList<String>();
		
		Document doc = Jsoup.parse(file, "UTF-8", baseURL);
		Elements images = doc.getElementsByTag("img");
		for (Element image : images){
			String url = image.absUrl("src");
			imageURLs.add(url);
		}
		
		return imageURLs;
	}
	
	/**
	 * Method that checks if an absolute address is local to a given host
	 * @param address
	 * @param host
	 * @return
	 */
	public static Boolean isLocalAddress(String address, String host){
		return address.contains(host);
	}
	
	/**
	 * Extract the host address from a URL of the form 
	 * http://www.host.com/........
	 * @param url
	 * @return
	 */
	public static String getHost(String url){
		String resultString = url.substring(7);
		return resultString.substring(0,resultString.indexOf("/"));
	}
	
	/**
	 * Extract the path (i.e. that part of a URL that follows the host) from a URL of the form 
	 * http://www.host.com/.....path
	 * @param url
	 * @return
	 */
	public static String getPath(String url){
		String resultString = url.substring(7);
		return resultString.substring(resultString.indexOf("/"));
	}
	
	/**
	 * Method that extracts the Content-Length field of a HTTP header.
	 * Returns the value of the field if the field was contained in the provided string.
	 * Returns -1 if the field couldn't be found.
	 * @param line
	 * @return
	 */
	public static int parseForContentLength(String line){
		for (String subline : line.split("\n")){
			if (subline.contains("Content-Length") || subline.contains("Content-length") || subline.contains("content-length")){
				return Integer.parseInt(subline.split(":")[subline.split(":").length-1].replaceAll(" ", "").replaceAll("\n", "").replaceAll("\r", ""));
			}
		}
		
		return -1;
	}
	

	
	
	/*
	 * Returns the extension of a file at given URL.
	 * Returns an empty string if no extension was found.
	 * @param URL
	 * @return
	 */
	public static String extractExtension(String URL){
		int lastSlashIndex = URL.lastIndexOf("/");
        int urlLength = URL.length();
        if (URL.split("/").length > 0){
        	URL = URL.split("/")[URL.split("/").length-1];
	        if (URL.split("\\.").length > 1 && lastSlashIndex < urlLength-1){
	        	return URL.split("\\.")[URL.split("\\.").length-1];
	        }
        }
        return "";
	}
	
	/**
	 * Method that parses a URL and returns the host.
	 * @param URL
	 * @return
	 * @throws URISyntaxException
	 */
	public static String getHostFromURL(String URL) throws URISyntaxException{
		String host = convertToReadableURL(URL);
		host = new URI(host).getHost();
		return host;
	}
	
	/**
	 * Method that converts any URL to a URL that can be used with the URI method "getHost()"
	 * @param URL
	 * @return
	 */
	public static String convertToReadableURL(String URL){
		if (URL.contains("http://") || URL.contains("https://")){
			return URL;
		}else{
			return "http://" + URL;
		}
	}
	
	/**
	 * Method that converts any URL to a URL that can be used with the URI method "getHost()"
	 * @param URL
	 * @return
	 * @throws URISyntaxException
	 */
	public static URI convertToReadableURL(URI URL) throws URISyntaxException{
		String URLString = URL.toString();
		return new URI(convertToReadableURL(URLString));
	}
	
	/**
	 * Static method for converting a string to a RequestType.
	 * @param typeString
	 * @return
	 * @throws UnknownRequestException
	 */
	public static RequestType extractType(String typeString) throws UnknownRequestException{
		
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
	
	/**
	 * Static method for converting a string to a HTTPVersion
	 * @param versionString
	 * @return
	 * @throws UnknownHTTPVersionException
	 */
	public static HTTPVersion extractVersion(String versionString) throws UnknownHTTPVersionException{
		if (versionString.equals("HTTP/1.0")){
			return HTTPVersion.HTTP_1_0;
		}else if(versionString.equals("HTTP/1.1")){
			return HTTPVersion.HTTP_1_1;
		}else{
			throw new UnknownHTTPVersionException(versionString);
		}
	}
	
	/**
	 * Returns the request type for the so the requestHandler can generate the proper response. 
	 * @return
	 * @throws URISyntaxException 
	 * @throws UnknownHTTPVersionException 
	 * @throws IOException 
	 */
	public static Request parseRequestHeader(String header) throws UnknownRequestException, URISyntaxException, UnknownHTTPVersionException, IOException{
		RequestType type = null;
		String path = null;
		int port = 0;
		HTTPVersion version = null;
		
		//split header in lines
		String[] lines = header.split("\n");
		System.out.println(lines);
		
		//split initial line in strings
		String[] initialLine = lines[0].split(" ");
		System.out.println(initialLine);
		
		//get type, path, port, version
		if (initialLine.length==4){
			type = extractType(initialLine[0]);
			path = getPath(initialLine[1]);
			port = getPort(initialLine[2]);
			version = extractVersion(initialLine[3]);
		} 
		//request without port
		else if (initialLine.length==3){
			type = extractType(initialLine[0]);
			path = getPath(initialLine[1]);
			version = extractVersion(initialLine[2]);

		} else{
			throw new IOException();
		}
	
		
		
		return new Request(type, path, port, version);
		
		
	}
	
	public static int getPort(String port){
		return Integer.parseInt(port);
	}
	
	


}
