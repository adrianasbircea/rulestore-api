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
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {

	/**
	 * An html message explaining that the URL is not well-formed.
	 */
    @Override
    public Response toResponse(NotFoundException exception) {
    	return Response.status(Status.NOT_FOUND).build();
    }
}
