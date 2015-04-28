package errors

class MethodError extends Exception{
	MethodError(message){
		super(message)
	}
	MethodError(Throwable cause){
		super(cause)
	}
	MethodError(String message,Throwable cause){
		super(message,cause)
	}
}
