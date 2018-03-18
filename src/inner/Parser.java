package inner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
	

}
