package errors

class MethodCallError extends Exception{
	MethodCallError( message){
		super(message)
	}
	MethodCallError(Throwable cause){
		super(cause)
	}
	MethodCallError(String message,Throwable cause){
		super(message,cause)
	}
}
