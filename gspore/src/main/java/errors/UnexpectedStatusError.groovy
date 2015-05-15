package errors

class UnexpectedStatusError extends Exception {
	public UnexpectedStatusError(String message) {
		super(message);
	}
}
