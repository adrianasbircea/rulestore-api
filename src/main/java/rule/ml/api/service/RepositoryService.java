package rule.ml.api.service;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.hibernate.validator.method.MethodConstraintViolationException;
import org.jboss.resteasy.plugins.validation.hibernate.ValidateRequest;

import com.sun.istack.NotNull;

import rule.ml.api.database.ExistDAO;
import rule.ml.api.exception.BadRequestExceptionMapper;
import rule.ml.api.http.HTTPStatusCodes;
import rule.ml.api.repository.Repository;
import rule.ml.api.repository.Store;
import rule.ml.api.util.Constants;
/**
 * Class which handles the {@link Request}s for repositories (Obtaining one or 
 * more repositories, update repositories, delete repositories). 
 * 
 * @author Adriana
 */
@Path("/")
public class RepositoryService {
	/**
	 * Handles HTTP {@link Request}s like:
	 * - GET /[store_id]/repositories 
	 * - GET /[store_id]/repositories?t=[token]
	 *   
	 * @param storeID 	The ID of the store.
	 * @param token		The authentication code necessary to access the store.
	 * 
	 * @return A HTTP {@link Response} containing the repositories or one of the errors:
	 * - HTTP 404 Not Found if the store does not contain any repository
	 * - HTTP 400 Bad Request if the URL contains invalid parameters.
	 * - HTTP 405 Method Not Allowed if a resource is requested using an HTTP 
	 * 	 method different from the necessary one.
	 */
	@GET
	@Path("/{storeID}/repositories")
	@Consumes (MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public Response getAllRepositories(
			@PathParam("storeID") String storeID,
			@DefaultValue("") @QueryParam("token") String token) {
		try {
			Store store = new Store();
			// Set all the repositories for the current store
			List<Repository> repositories = ExistDAO.getRepositories(storeID);
			store.setRepos(repositories);
			// Set the store object to the response
			return Response.status(200).entity(store).build();
		} catch (Exception e) {
			throw new NotFoundException();
		}
		
	}
	
	/**
	 * Handles HTTP {@link Request}s like:
	 * - GET /[store_id]/repositories/[repository_id]
	 * - GET /[store_id]/repositories/[repository_id]/?t=[token]
	 *   
	 * @param storeID 		The ID of the store.
	 * @param repositoryID 	The repository ID.
	 * @param token			The authentication code necessary to access the store.
	 * 
	 * @return A HTTP {@link Response} containing the repository with the given ID or one of the errors:
	 * - HTTP 404 Not Found if the store does not contain any repository
	 * - HTTP 400 Bad Request if the URL contains invalid parameters.
	 * - HTTP 405 Method Not Allowed if a resource is requested using an HTTP 
	 * 	 method different from the necessary one.
	 */
	@GET
	@Consumes (MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	@Path("/{storeID}/repositories/{repositoryID}")
	public Response getRepositoryByID(
			@PathParam("storeID") String storeID,
			@PathParam("repositoryID") String repositoryID,
			@DefaultValue("") @QueryParam("token") String token) {

		String response = "Store ID: " +  storeID + "repository ID " + repositoryID + " authentication: " + token;

		return Response.status(200).entity(response).build();
	}
	
	/**
	 * Handles HTTP {@link Request}s like:
	 * - GET /[store_id]/repositories/name/[name]
	 * - GET /[store_id]/repositories/name/[name]?t=[token]
	 *   
	 * @param storeID 			The ID of the store.
	 * @param repositoryName 	The repository(ies) name.
	 * @param token				The authentication code necessary to access the store.
	 * 
	 * @return A HTTP {@link Response} containing the repository(ies) with the given name or one of the errors:
	 * - HTTP 404 Not Found if the store does not contain any repository
	 * - HTTP 400 Bad Request if the URL contains invalid parameters.
	 * - HTTP 405 Method Not Allowed if a resource is requested using an HTTP 
	 * 	 method different from the necessary one.
	 */
	@GET
	@Path("/{storeID}/repositories/name/{repositoryName}")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public Response getRepositoryByName(
			@PathParam("storeID") String storeID,
			@PathParam("repositoryName") String repositoryName,
			@DefaultValue("") @QueryParam("token") String token) {
		String response = "Store ID: " +  storeID + "repository name " + repositoryName + " authentication: " + token;

		return Response.status(200).entity(response).build();
	}
	
	/**
	 * Creates a new (empty) repository with the following content:
	 * - a generated id, [repository_id],
	 * - a name [name]
	 * - a [metadata] description
	 * - NO rulesets. Rulesets can be added by new specific API requests
	 * - NO rules. Rules can be added by new specific API requests.
	 * 
	 * Handles HTTP {@link Request}s like:
	 * - POST /[store_id]/repositories
	 * - POST /[store_id]/repositories?t=[token]
	 *   
	 * @param storeID 			The ID of the store.
	 * @param token				The authentication code necessary to access the store.
	 * 
	 * @return A HTTP {@link Response} containing the repository location and 
	 * 		   201 Created status or one of the errors:
	 * - HTTP 404 Not Found if the store with the given ID is not found
	 * - HTTP 400 Bad Request if the URL contains invalid parameters.
	 * - HTTP 401 Unauthorized 
	 * - HTTP 500 Internal Server Error if the  repository could not be created
	 * - HTTP 511 Network Authentication Required  ?! when the user is not authentificated
	 * TODO ? - cod de eroare atunci cand userul nu are acces la store-ul respectiv sau cand resursa nu poate fi creata
	 */
	@POST
	@Consumes (MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	@Path("/{storeID}/repositories")
	@ValidateRequest
	public Response createNewRepository(
			@PathParam("storeID") String storeID,
			@DefaultValue("") @QueryParam("token") String token,
			@NotNull @QueryParam("name") String name,
			@NotNull @QueryParam("description") String description) {
		if (name == null || description == null) {
			throw new NotFoundException();
		}
		HTTPStatusCodes statusCode = HTTPStatusCodes.CREATED;
		String newReposRelativeURI = null;
		try {
			newReposRelativeURI = ExistDAO.createNewRepository(storeID);
			if (newReposRelativeURI != null) {
				
			} else {
				System.out.println("??????");
				statusCode = HTTPStatusCodes.INTERNAL_SERVER_ERROR;
			}
		} catch (FileNotFoundException e) {
			statusCode = HTTPStatusCodes.NOT_FOUND;
		}

		ResponseBuilder builder = Response.status(statusCode.getStatusCode())/*.entity(BAD_REQUEST_MSG)*/;
		if (newReposRelativeURI != null) {
			builder.header("Location", Constants.urlPrefix + newReposRelativeURI);
		}
		
    	return builder.build();
	}
	

	/**
	 * For JSON format requests, send an 501 Not Implemented message. 
	 * 
	 * @return A {@link Response} containing a 501 Not Implemented status code.
	 */
	@GET
	@POST
	@PUT
	@DELETE
	@Path("/{storeID}/repositories")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes (MediaType.APPLICATION_JSON)
	public Response getJSONRepresentation() {
		return Response.status(HTTPStatusCodes.NOT_IMPLEMENTED.getStatusCode()).
				entity(HTTPStatusCodes.NOT_IMPLEMENTED.getDescription()).build();
	}
}
