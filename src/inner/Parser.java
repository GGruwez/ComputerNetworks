package inner;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import exceptions.UnknownHTTPVersionException;
import exceptions.UnknownRequestException;
import exceptions.UnknownStatusCodeException;

/**
 * An abstract class containing all parsing-related methods
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
		
		//simple jsoup parser implementation
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
		url = url.trim();
		if (url.length() < 7 || (!url.substring(0,7).equals("http://") && !url.substring(0,7).equals("HTTP://"))) return url.substring(url.indexOf("/"));
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
	public static int parseForContentLength(String lines){
		// \n = LF
		// trim() : This method returns
		//a copy of the string, with leading and trailing whitespace omitted.
		for (String subline : lines.split("\n")){
			if (subline.contains("Content-Length") || subline.contains("Content-length") || subline.contains("content-length")){
				return Integer.parseInt(subline.split(":")[subline.split(":").length-1].replaceAll(" ", "").replaceAll("\n", "").replaceAll("\r", "").trim());
			}
		}
		
		return 0;
	}
	
	/**
	 * Method that extracts the host in an HTTP header.
	 * Returns nothing if none is found. 
	 */
	public static String parseForHost(String lines){
		for (String subline : lines.split("\n")){
			if (subline.split(":").length > 1 && (subline.contains("Host") || subline.contains("host"))){
				return subline.split(":")[1].replaceAll(" ", "");
			}
		}
		
		return "";
	}
	
	/**
	 * This method extracts the port number in an HTTP header.
	 * Returns -1 if none is found. 
	 */
	public static int parseForPort(String lines){
		for (String subline : lines.split("\n")){
			if (subline.contains("Host") || subline.contains("host")){
				if (subline.split(":").length == 3)return Integer.parseInt(cleanString(subline.split(":")[2].replaceAll(" ", "")));
			}
		}
		
		return -1;
	}
	
	/**
	 * Extract the modied since date from the HTTP header.
	 */
	public static Date parseForModifiedSince(String lines) throws ParseException{
		for (String subline : lines.split("\n")){
			if (subline.contains("If-Modified-Since") || subline.contains("If-modified-since") || subline.contains("If-Modified-since") || subline.contains("if-modified-since") || subline.contains("If-modified-Since")){
				subline = subline.split(":")[1] + ":" + subline.split(":")[2] + ":" + subline.split(":")[3];
				System.out.println(subline);
				SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);
				return format.parse(subline.trim());
			}
		}
		
		return null;
	}
	
	/**
	 * Return the date in a string.
	 */
	public static String toHTTPDate(Date date){
		SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);
		return format.format(date);
	}
	
	
	/**
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
	 * Returns whether the given extension is text or image based.
	 */
	public static String extractFileType(String extension){
		String[] text = {"html", "HTML", "php", "PHP", "txt", "TXT"};
		String[] image = {"jpg", "gif", "png", "JPG", "PNG", "GIF"};
		if (Arrays.asList(text).contains(extension)){
			return "text";
		}else if (Arrays.asList(image).contains(extension)){
			return "image";
		}else{
			return "unknown";
		}
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
	 * 
	 */
	public static StatusCode extractStatusCode(int code) throws UnknownStatusCodeException{
		if (code == 200){
			return StatusCode.ERROR200;
		}else if (code == 400){
			return StatusCode.ERROR400;
		}else if (code == 404){
			return StatusCode.ERROR404;
		}else if (code == 500){
			return StatusCode.ERROR500;
		}else if (code == 304){
			return StatusCode.ERROR304;
		}else{
			throw new UnknownStatusCodeException(code);
		}
	}
	
	/**
	 * Returns the request type for the header so the requestHandler can generate the proper response. 
	 * @return
	 * @throws URISyntaxException 
	 * @throws UnknownHTTPVersionException 
	 * @throws IOException 
	 */
	public static Request parseRequestHeader(String header, int port) throws UnknownRequestException, URISyntaxException, UnknownHTTPVersionException{
		RequestType type;
		String path;
		HTTPVersion version;
		
		//split header in lines
		String[] lines = header.split("\n");
		
		//split initial line in strings
		String[] initialLine = lines[0].split(" ");
		
		//get type, path, port, version
		type = extractType(cleanString(initialLine[0]));
		path = "http://localhost" + getPath(cleanString(initialLine[1]));
		version = extractVersion(cleanString(initialLine[2]));
		
		return new Request(type, path, port, version);
		
		
	}
	
	/**
	 * Removes all spaces, LF and CR.
	 */
	public static String cleanString(String string){
		return string.replaceAll("\n", "").replaceAll("\r", "").replaceAll(" ", "");
	}
	
	/**
	 * Returns the file name of a given object.
	 */
	public static String getFilename(String path){
		if (path.equals("/"))return "";
		
		String[] splitString = path.split("/");
		String filename = splitString[splitString.length-1];
		return filename.substring(0, filename.lastIndexOf("."));
		
	}
	
	/**
	 * Removes the filename from a given path.
	 */
	public static String getPathWithoutFilename(String path){
		return path.substring(0, path.lastIndexOf("/")+1);
	}


}
