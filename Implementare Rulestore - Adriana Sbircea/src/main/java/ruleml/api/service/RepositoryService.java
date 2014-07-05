package ruleml.api.service;

import java.net.URI;
import java.util.List;
import java.util.Locale;

import javax.validation.constraints.NotNull;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.jboss.resteasy.plugins.validation.hibernate.ValidateRequest;
import org.xmldb.api.base.ErrorCodes;
import org.xmldb.api.base.XMLDBException;

import ruleml.api.database.ExistDAO;
import ruleml.api.http.HTTPStatusCodes;
import ruleml.api.repository.Repository;
import ruleml.api.repository.Store;
import ruleml.api.util.ServiceUtil;
/**
 * Class which handles the {@link Request}s for repositories (Obtaining one or 
 * more repositories, update repositories, etc). 
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
	 * - HTTP 500 Internal Server Error if an error occurs while retrieving the data is encountered.
	 * 
	 * @throws Exception If one of the specified errors occurred. 
	 */
	@GET
	@Path("/{storeID}/repositories")
	@Produces(MediaType.APPLICATION_XML)
	public Response getAllRepositories(
			@PathParam("storeID") String storeID,
			@DefaultValue("") @QueryParam("token") String token) throws Exception {
		System.out.println("??????????????????????");
		try {
			// Set all the repositories for the current store
			List<Repository> repositories = ExistDAO.getAllRepositories(storeID);
			System.out.println("----------");
			if (repositories.isEmpty()) {
				throw new NotFoundException();
			}
			Store store = new Store();
			store.setRepos(repositories);
			// Set the store object to the response
			return Response.status(200).entity(store).build();
		} catch (Exception e) {
			e.printStackTrace();
			
			if (e instanceof XMLDBException) {
				throw ServiceUtil.getException(((XMLDBException) e));
			}
			throw e;
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
	 * - HTTP 500 Internal Server Error if an error occurs while retrieving the data is encountered.
	 * 
	 * @throws Exception If one of the specified errors occurred. 
	 */
	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("/{storeID}/repositories/{repositoryID}")
	public Response getRepositoryByID(
			@PathParam("storeID") String storeID,
			@PathParam("repositoryID") String repositoryID,
			@DefaultValue("") @QueryParam("token") String token) throws Exception {
		System.out.println("4");
		try {
			// Set all the repositories for the current store
			Repository repository = ExistDAO.getRepositoryWithID(storeID, repositoryID);
			if (repository != null) {
				// Set the repository object to the response
				return Response.status(200).entity(repository).build();
			} else {
				throw new NotFoundException("Repository with ID " + repositoryID + " could not be found.");
			}
		} catch (Exception e) {
			if (e instanceof XMLDBException) {
				throw ServiceUtil.getException(((XMLDBException) e));
			}
			throw e;
		}
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
	 * @return A HTTP {@link Response} containing the repository with the given ID or one of the errors:
	 * - HTTP 404 Not Found if the store does not contain any repository
	 * - HTTP 400 Bad Request if the URL contains invalid parameters.
	 * - HTTP 500 Internal Server Error if an error occurs while retrieving the data is encountered.
	 * 
	 * @throws Exception If one of the specified errors occurred. 
	 */
	@GET
	@Path("/{storeID}/repositories/name/{repositoryName}")
	@Produces(MediaType.APPLICATION_XML)
	public Response getRepositoryByName(
			@PathParam("storeID") String storeID,
			@PathParam("repositoryName") String repositoryName,
			@DefaultValue("") @QueryParam("token") String token,
			@Context HttpHeaders headers) throws Exception {
		try {
			// Set all the repositories for the current store
			List<Locale> acceptableLanguages = headers.getAcceptableLanguages();
			String lang = "en";
			if (!acceptableLanguages.isEmpty()) { 
				Locale locale = acceptableLanguages.get(0);
				lang = locale.getLanguage();
			}
			//TODO   Ar trebui lang + locale.getCountry() ????
			List<Repository> repositories = ExistDAO.getRepositoriesWithName(storeID, repositoryName, lang);
			if (repositories.isEmpty()) {
				throw new NotFoundException();
			}
			Store store = new Store();
			store.setRepos(repositories);
			// Set the store object to the response
			// TODO .language(lang) sa fie de string sau sa ii dau de Locale?
			return Response.status(200).entity(store).language(lang).build();
		} catch (Exception e) {
			if (e instanceof XMLDBException) {
				throw ServiceUtil.getException(((XMLDBException) e));
			}
			throw e;
		}
	
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
	 * @param name				The name of the repository.
	 * @param description 		The description (metadata) of the repository.
	 * @param lang				The language of the repository. It is optionally.
	 * 
	 * @return A HTTP {@link Response} containing the repository location (HTTP 
	 * 			Header Location) and a 201 Created status code or one of the errors:
	 * - HTTP 404 Not Found if the store with the given ID is not found
	 * - HTTP 400 Bad Request if the URL contains invalid parameters.
	 * - HTTP 401 Unauthorized TODO
	 * - HTTP 500 Internal Server Error if the  repository could not be created
	 * 
	 */
	@POST
	@Path("/{storeID}/repositories")
	@ValidateRequest
	public Response createNewRepository(
			@PathParam("storeID") String storeID,
			@DefaultValue("") @QueryParam("token") String token,
			@NotNull @QueryParam("name") String name,
			@NotNull @QueryParam("description") String description,
			@DefaultValue("en-US") @QueryParam("lang") Locale lang) throws Exception {
		System.out.println("2");
		if (name == null || description == null) {
			throw new BadRequestException();
		}
		String newReposRelativeURI = null;
		try {
			//TODO ? poate lang.getLanguage() + 
			
			newReposRelativeURI = ExistDAO.createNewRepository(storeID, name, description, lang.getLanguage());
			if (newReposRelativeURI != null) {
				ResponseBuilder builder = Response.status(HTTPStatusCodes.CREATED.getStatusCode());
				builder.location(new URI(ServiceUtil.urlPrefix + newReposRelativeURI));
				return builder.build();
			} else {
				throw new InternalServerErrorException("Resource could not be created");
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (e instanceof XMLDBException) {
				throw ServiceUtil.getException((XMLDBException) e);
			} else if (e instanceof NotFoundException) {
				throw e;
			}
			
			throw new InternalServerErrorException(e);
		}

		
	}

	/**
	 * For JSON format requests, send a 501 Not Implemented message. 
	 * 
	 * @return A {@link Response} containing a 501 Not Implemented status code.
	 */
	@GET
	@POST
	@PUT
	@DELETE
	@Path("{var:.*}")
	@Produces({MediaType.APPLICATION_JSON, "application/prolog"})
	@Consumes ({MediaType.APPLICATION_JSON, "application/prolog"})
	public Response getOtherRepresentation() {
		return Response.status(HTTPStatusCodes.NOT_IMPLEMENTED.getStatusCode()).build();
	}
	
	/**
	 * For JSON format requests, send a 501 Not Implemented message. 
	 * 
	 * @return A {@link Response} containing a 501 Not Implemented status code.
	 */
	@GET
	@POST
	@PUT
	@DELETE
	@Path("/{storeID}/repositories/name/{name}")
	@Produces({MediaType.APPLICATION_JSON, "application/prolog"})
	@Consumes ({MediaType.APPLICATION_JSON, "application/prolog"})
	public Response getJSONRepresentationName() {
		System.out.println("1");
		return Response.status(HTTPStatusCodes.NOT_IMPLEMENTED.getStatusCode()).build();
	}
}
