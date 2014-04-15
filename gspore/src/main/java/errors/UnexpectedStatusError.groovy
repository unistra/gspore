package errors

class UnexpectedStatusError extends Exception {

	public UnexpectedStatusError() {
	}

	public UnexpectedStatusError(String message) {
		super(message);
	}

	public UnexpectedStatusError(Throwable cause) {
		super(cause);
	}

	public UnexpectedStatusError(String message, Throwable cause) {
		super(message, cause);
	}

	public UnexpectedStatusError(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
