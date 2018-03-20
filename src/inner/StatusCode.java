package inner;

import exceptions.UnknownStatusCodeException;

public enum StatusCode {

		ERROR200, ERROR400, ERROR404, ERROR500, ERROR304;
	
		public String getStatusString() throws UnknownStatusCodeException{
			if (this == StatusCode.ERROR200){
				return "200 OK";
			}else if (this == StatusCode.ERROR304){
				return "304 Not Modified";
			}else if (this == StatusCode.ERROR404){
				return "404 Not Found";
			}else if (this == StatusCode.ERROR400){
				return "400 Bad Request";
			}else if (this == StatusCode.ERROR500){
				return "500 Server Error";
			}else{
				throw new UnknownStatusCodeException(this.toInt());
			}
		}
		
		public int toInt() throws UnknownStatusCodeException{
			if (this == StatusCode.ERROR200){
				return 200;
			}else if (this == StatusCode.ERROR304){
				return 304;
			}else if (this == StatusCode.ERROR404){
				return 404;
			}else if (this == StatusCode.ERROR400){
				return 400;
			}else if (this == StatusCode.ERROR500){
				return 500;
			}else{
				throw new UnknownStatusCodeException();
			}
		}
	
}
