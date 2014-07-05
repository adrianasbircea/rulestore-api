package ruleml.api.exception;

public class PreconditionRequiredException extends Exception {

	public PreconditionRequiredException() {
		super("Preconditions Required");
	}
	
	public PreconditionRequiredException(String message) {
		super(message);
	}
}
