package inner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Parser {
	
	public Parser(){
		
	}
	
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
	

}
