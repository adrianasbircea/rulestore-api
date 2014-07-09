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
import org.jboss.resteasy.spi.MethodNotAllowedException;
import org.xmldb.api.base.ErrorCodes;
import org.xmldb.api.base.XMLDBException;

import ruleml.api.database.ExistDAO;
import ruleml.api.exception.PreconditionRequiredException;
import ruleml.api.http.HTTPStatusCodes;
import ruleml.api.repository.Repository;
import ruleml.api.repository.Ruleset;
import ruleml.api.repository.Store;
import ruleml.api.util.ServiceUtil;
/**
 * Class which handles the {@link Request}s for rulesets (Obtaining one or 
 * more rulesets, update ruleset, delete ruleset). 
 * 
 * @author Adriana
 */
@Path("/")
public class RulesetService {
	/**
	 * Handles HTTP {@link Request}s like:
	 * - GET /[store_id]/repositories/[repository_id]/rulesets 
	 * - GET /[store_id]/repositories/[repository_id]/rulesets?t=[token]
	 *   
	 * @param storeID 	The ID of the store.
	 * @param reposID	The ID of the repository.
	 * @param token		The authentication code necessary to access the store.
	 * 
	 * @return A HTTP {@link Response} containing the rulesets or one of the errors:
	 * - HTTP 404 Not Found if the store does not contain any ruleset
	 * - HTTP 400 Bad Request if the URL contains invalid parameters.
	 * - HTTP 500 Internal Server Error if an error occurs while retrieving the data is encountered.
	 * 
	 * @throws Exception If one of the specified errors occurred. 
	 */
	@GET
	@Path("/{storeID}/repositories/{reposID}/rulesets")
	@Produces(MediaType.APPLICATION_XML)
	public Response getAllRulesets(
			@PathParam("storeID") String storeID,
			@PathParam("reposID") String reposID,
			@DefaultValue("") @QueryParam("token") String token) throws Exception {
		System.out.println("GET ALL RULESETS");
		try {
			// Obtain all the rulesets for the given repository
			Repository repos = ExistDAO.getRepositoryWithID(storeID, reposID);
			List<Ruleset> rulesets = ExistDAO.getAllRulesets(storeID, reposID);
			System.out.println("rulesets " + rulesets);
			if (rulesets.isEmpty() || repos == null) {
				throw new NotFoundException();
			}
			repos.setRulesets(rulesets);
			
			System.out.println("repos " + repos);
			// Set the store object to the response
			return Response.status(200).entity(repos).build();
		} catch (Exception e) {
			if (e instanceof XMLDBException) {
				throw ServiceUtil.getException(((XMLDBException) e));
			}
			throw e;
		}
	}

	/**
	 * Handles HTTP {@link Request}s like:
	 * - GET /[store_id]/repositories/[repository_id]/rulesets/[ruleset_id]
	 * - GET /[store_id]/repositories/[repository_id]/rulesets/[ruleset_id]?t=[token]
	 *   
	 * @param storeID 		The ID of the store.
	 * @param repositoryID 	The repository ID.
	 * @param rulesetID		The ID of the ruleset.
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
	@Path("/{storeID}/repositories/{reposID}/rulesets/{rulesetID}")
	public Response getRulesetByID(
			@PathParam("storeID") String storeID,
			@PathParam("reposID") String repositoryID,
			@PathParam("rulesetID") String rulesetID,
			@DefaultValue("") @QueryParam("token") String token) throws Exception {
		System.out.println("4");
		try {
			// Set all the repositories for the current store
			Ruleset ruleset = ExistDAO.getRulesetWithID(storeID, repositoryID, rulesetID);
			System.out.println("rulesets " + ruleset);
			if (ruleset == null) {
				throw new NotFoundException();
			}
			
			System.out.println("ruleset " + ruleset);
			// Set the store object to the response
			return Response.status(200).entity(ruleset).build();
		} catch (Exception e) {
			if (e instanceof XMLDBException) {
				throw ServiceUtil.getException(((XMLDBException) e));
			}
			throw e;
		}
	}
	
	/**
	 * Handles HTTP {@link Request}s like:
	 * - GET /[store_id]/repositories/[repository_id]/rulesets/name/[name]
	 * - GET /[store_id]/repositories/[repository_id]/rulesets/name/[name]?t=[token]
	 *   
	 * @param storeID 			The ID of the store.
	 * @param repositoryID	 	The repository's ID.
	 * @param rulesetName		The name of the ruleset.
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
	@Path("/{storeID}/repositories/{reposID}/rulesets/name/{rulesetName}")
	@Produces(MediaType.APPLICATION_XML)
	public Response getRulesetByName(
			@PathParam("storeID") String storeID,
			@PathParam("reposID") String reposID,
			@PathParam("rulesetName") String rulesetName,
			@DefaultValue("") @QueryParam("token") String token,
			@Context HttpHeaders headers) throws Exception {
		System.out.println("3");
		try {
			// Set all the repositories for the current store
			List<Locale> acceptableLanguages = headers.getAcceptableLanguages();
			String lang = "en";
			if (!acceptableLanguages.isEmpty()) { 
				Locale locale = acceptableLanguages.get(0);
				lang = locale.getLanguage();
			}
			Repository repository = ExistDAO.getRepositoryWithID(storeID, reposID);
			
			List<Ruleset> rulesets = ExistDAO.getRulesetsWithName(storeID, reposID, rulesetName, lang);
			System.out.println("repository " + repository + " rulesets " + rulesets);
			if (repository == null || rulesets.isEmpty()) {
				throw new NotFoundException();
			}
			
			repository.setRulesets(rulesets);
			// Set the store object to the response
			// TODO .language(lang) sa fie de string sau sa ii dau de Locale?
			return Response.status(200).entity(repository).language(lang).build();
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
	@Path("/{storeID}/repositories/{reposID}/rulesets")
	@ValidateRequest
	@Produces("application/xml")
	public Response createNewRuleset(
			@PathParam("storeID") String storeID,
			@PathParam("reposID") String reposID,
			@DefaultValue("") @QueryParam("token") String token,
			@NotNull @QueryParam("name") String name,
			@NotNull @QueryParam("description") String description,
			@DefaultValue("en-US") @QueryParam("lang") Locale lang) throws Exception {
		System.out.println("2");
		if (name == null || description == null) {
			throw new BadRequestException();
		}
		String newRulesetRelativeURI = null;
		try {
			newRulesetRelativeURI = ExistDAO.createNewRuleset(storeID, reposID, name, description, lang.getLanguage());
			if (newRulesetRelativeURI != null) {
				ResponseBuilder builder = Response.status(HTTPStatusCodes.CREATED.getStatusCode());
				builder.location(new URI(ServiceUtil.urlPrefix + newRulesetRelativeURI));
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
	public Response getJSONRepresentation() {
		return Response.status(HTTPStatusCodes.NOT_IMPLEMENTED.getStatusCode()).build();
	}

	/**
	 * Handles HTTP {@link Request}s like:
	 * - GET /[store_id]/repositories/[repository_id]/rulesets/search/[search_string]
	 * - GET /[store_id]/repositories/[repository_id]/rulesets/search/[search_string]?t=[token]
	 *   
	 * @param storeID 	The ID of the store.
	 * @param reposID	The ID of the repository.
	 * @param token		The authentication code necessary to access the store.
	 * 
	 * @return A HTTP {@link Response} containing the rulesets or one of the errors:
	 * - HTTP 404 Not Found if the store does not contain any ruleset
	 * - HTTP 400 Bad Request if the URL contains invalid parameters.
	 * - HTTP 500 Internal Server Error if an error occurs while retrieving the data is encountered.
	 * 
	 * @throws Exception If one of the specified errors occurred. 
	 */
	@GET
	@Path("/{storeID}/repositories/{reposID}/rulesets/search/{searchString}")
	@Produces(MediaType.APPLICATION_XML)
	public Response getRulesetsForSearchString(
			@PathParam("storeID") String storeID,
			@PathParam("reposID") String reposID,
			@PathParam("searchString") String searchString,
			@DefaultValue("") @QueryParam("token") String token) throws Exception {
		System.out.println("GET ALL RULESETS");
		try {
			// Obtain all the rulesets for the given repository
			Repository repos = ExistDAO.getRepositoryWithID(storeID, reposID);
			List<Ruleset> rulesets = ExistDAO.getAllRulesetsMatchString(storeID, reposID, searchString);
			System.out.println("rulesets " + rulesets);
			if (rulesets.isEmpty() || repos == null) {
				throw new NotFoundException();
			}
			repos.setRulesets(rulesets);
			
			System.out.println("repos " + repos);
			// Set the store object to the response
			return Response.status(200).entity(repos).build();
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
	@DELETE
	@Path("/{storeID}/repositories/{reposID}/rulesets/{rulesetID}")
	@Produces("application/xml")
	public Response deleteRuleset(
			@PathParam("storeID") String storeID,
			@PathParam("reposID") String repositoryID,
			@PathParam("rulesetID") String rulesetID,
			@DefaultValue("") @QueryParam("token") String token) throws Exception {
		System.out.println("delete");
		Ruleset deleted = null;
		try {
			deleted = ExistDAO.deleteRuleset(storeID, repositoryID, rulesetID);
			if (deleted != null) {
				ResponseBuilder builder = Response.status(200).entity(deleted);
				return builder.build();
			} else {
				throw new InternalServerErrorException("Resource could not be created");
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (e instanceof XMLDBException) {
				throw ServiceUtil.getException((XMLDBException) e);
			} else if (e instanceof NotFoundException ||
					e instanceof PreconditionRequiredException) {
				throw e;
			}
			
			throw new InternalServerErrorException(e);
		}
	}
	
	@GET
	@POST
	@PUT
	@DELETE
	@Path("{var:.*}")
	@Produces("application/xml")
	public Response getMethodNotAllowed() {
		throw new MethodNotAllowedException("");
	}
}
