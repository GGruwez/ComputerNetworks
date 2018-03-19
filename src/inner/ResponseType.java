package inner;

public enum ResponseType {
	HEAD, GET, PUT, POST;
	
	public String toString(){
		if (this==ResponseType.HEAD){
			return "HEAD";
		}else if (this==ResponseType.GET){
			return "GET";
		}else if (this==ResponseType.PUT){
			return "PUT";
		}else if (this==ResponseType.POST){
			return "POST";
		}else{
			return null;
		}
	}
}
