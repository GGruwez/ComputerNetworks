package inner;

import java.util.ArrayList;
import java.util.List;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Parser {
	
	public Parser(){
		
	}
	
	public static List<String> findImageURLs(String string){
		List<String> imageURLs = new ArrayList<String>();
		
		Document doc = new Document(string);
		Elements images = doc.getElementsByTag("img");
		for (Element image : images){
			String url = image.attr("abs:src");
			imageURLs.add(url);
		}
		
		return imageURLs;
	}
	

}
