package ruleml.api.authentification;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.core.Headers;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.util.Base64;

import ruleml.api.database.ExistDAO;
import ruleml.api.service.RuleService;

/**
 * This interceptor verifies the access permissions for a user based on username 
 * and password provided in request.
 * 
 * @author Adriana
 */
@Provider
public class SecurityInterceptor implements ContainerRequestFilter {

	private static final String AUTHORIZATION_PROPERTY = "Authorization";
	private static final String AUTHENTICATION_SCHEME = "Basic";
	private static final ServerResponse ACCESS_DENIED = new ServerResponse("Access denied for this resource", 401, new Headers<Object>());;
	private static final ServerResponse SERVER_ERROR = new ServerResponse("INTERNAL SERVER ERROR", 500, new Headers<Object>());;
	
	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		
		
		InputStream entityStream = requestContext.getEntityStream();
		StringWriter writer = new StringWriter();
		IOUtils.copy(entityStream, writer);
		RuleService.xmlContent = writer.toString();
//		System.out.println("Path " + requestContext.getUriInfo().getPath() +
//				" Base URI " + requestContext.getUriInfo().getBaseUri() + 
//				" Matched URIs " + requestContext.getUriInfo().getRequestUri());
//		
//		final MultivaluedMap<String, String> headers = requestContext.getHeaders();
//		MultivaluedMap<String,String> pathParameters = requestContext.getUriInfo().getQueryParameters();
//		Object[] array = pathParameters.keySet().toArray();
//		for (int i = 0; i < array.length; i++) {
//			System.out.println("-- " + array[i] + " " + pathParameters.get(array[i]));
//		}
//        System.out.println("filter");
//        //Fetch authorization header
//        final List<String> authorization = headers.get(AUTHORIZATION_PROPERTY);
//         
//        //If no authorization information present; block access
//        if(authorization == null || authorization.isEmpty())
//        {
////            requestContext.abortWith(ACCESS_DENIED);
////            return;
//        }
//         
//        //Get encoded username and password
//        final String encodedUserPassword = authorization.get(0).replaceFirst(AUTHENTICATION_SCHEME + " ", "");
//         
//        //Decode username and password
//        String usernameAndPassword = null;
//        try {
//            usernameAndPassword = new String(Base64.decode(encodedUserPassword));
//        } catch (IOException e) {
//            requestContext.abortWith(SERVER_ERROR);
//            return;
//        }
//
//        //Split username and password tokens
//        final StringTokenizer tokenizer = new StringTokenizer(usernameAndPassword, ":");
//        final String username = tokenizer.nextToken();
//        final String password = tokenizer.nextToken();
//         
//        //Verifying Username and password
//        System.out.println(username);
//        System.out.println(password);
//        if (!isUserAllowed(username, password)) {
////        	requestContext.abortWith(ACCESS_DENIED);
////        	return;
//        }
	}
	
	/**
	 * Checks if the user is registered to the API.
	 * 
	 * @param username 	The user's username.
	 * @param password	The password.
	 * 
	 * @return <code>true</code> if the user can access the API.
	 */
	private boolean isUserAllowed(final String username, final String password)
    {
        boolean isAllowed = true;
         
        //TODO: 
        //Step 1. Fetch password from database and match with password in argument
        //If both match then get the defined role for user from database and continue; else return isAllowed [false]
        //Access the database and do this part yourself
        //String userRole = userMgr.getUserRole(username);
        return isAllowed;
    }

}
