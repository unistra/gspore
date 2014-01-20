package errors

class SporeError extends Exception{
	
	SporeError(message){
		super(message)
	}
	SporeError(Throwable cause){
		super(cause)
	}
	SporeError(String message,Throwable cause){
		super(message,cause)
	}
}
