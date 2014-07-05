package ruleml.api.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Class that returns a HTTP 400 Bad request error code when the URL is not well-formed.
 *  
 * @author Adriana
 */
@Provider
public class PreconditionsRequiredExceptionMapper implements ExceptionMapper<PreconditionRequiredException> {

	/**
	 * An html message explaining that the URL is not well-formed.
	 */
    @Override
    public Response toResponse(PreconditionRequiredException exception) {
    	return Response.status(428).build();
    }
}
