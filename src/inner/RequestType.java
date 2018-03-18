package inner;

/**
 * An enumeration for all four request types.
 * @author anthonyrathe
 *
 */
public enum RequestType {
	HEAD, GET, PUT, POST;
	
	public String toString(){
		if (this==RequestType.HEAD){
			return "HEAD";
		}else if (this==RequestType.GET){
			return "GET";
		}else if (this==RequestType.PUT){
			return "PUT";
		}else if (this==RequestType.POST){
			return "POST";
		}else{
			return null;
		}
	}
}
