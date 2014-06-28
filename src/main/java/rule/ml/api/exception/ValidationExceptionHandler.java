package rule.ml.api.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.hibernate.validator.method.MethodConstraintViolationException;

public class ValidationExceptionHandler implements ExceptionMapper<MethodConstraintViolationException>
{
    @Override
    public Response toResponse(MethodConstraintViolationException exception)
    {
        return Response.status(Status.BAD_REQUEST).entity("Fill all fields").build();
    }
}
