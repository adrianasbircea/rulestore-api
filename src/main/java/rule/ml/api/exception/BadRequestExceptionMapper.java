package rule.ml.api.exception;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Class that returns a HTTP 400 Bad request error code when the URL is not well-formed.
 *  
 * @author Adriana
 */
@Provider
public class BadRequestExceptionMapper implements ExceptionMapper<NotFoundException> {

	/**
	 * An html message explaining that the URL is not well-formed.
	 */
	public final String BAD_REQUEST_MSG = "<html><head><title>400 Bad Request</title></head></html>";
    @Override
    public Response toResponse(NotFoundException exception) {
    	ResponseBuilder builder = Response.status(Status.BAD_REQUEST)/*.entity(BAD_REQUEST_MSG)*/;
//    	builder.header("Content-Type", "text/html");
    	return builder.build();
    }
}
