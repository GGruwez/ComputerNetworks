package inner;

/**
 * An enumeration for both HTTP versions.
 * @author anthonyrathe
 *
 */
public enum HTTPVersion {
	HTTP_1_1, HTTP_1_0;
	
	public String toString(){
		if (this==HTTPVersion.HTTP_1_0){
			return "HTTP/1.0";
		}else if (this==HTTPVersion.HTTP_1_1){
			return "HTTP/1.1";
		}else{
			return null;
		}
	}
}
