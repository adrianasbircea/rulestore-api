package ruleml.api.http;

public enum HTTPStatusCodes {
	/**
	 * The HTTP 201 Created status.
	 */
	CREATED(201, "Created"),
	/**
	 * The HTTP 500 Internal Server Error status.
	 */
	INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
	/**
	 * The HTTP 404 Not Found status.
	 */
	NOT_FOUND(404, "Not Found"),
	/**
	 * The HTTP 501 Not Implemented status.
	 */
	NOT_IMPLEMENTED(501, "Not Implemented");
	
	/**
	 * The HTTP status code.
	 */
	private final int statusCode;
	/**
	 * The HTTP status description.
	 */
    private final String description;
    
    HTTPStatusCodes(int code, String description) {
    	this.statusCode = code;
    	this.description = description;
    	
    }
    
    public int getStatusCode() {
		return statusCode;
	}
    
    public String getDescription() {
		return description;
	}
}
