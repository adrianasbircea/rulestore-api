package ruleml.api.database;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.NotFoundException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.exist.xmldb.EXistResource;
import org.exist.xmldb.XQueryService;
import org.exist.xquery.functions.xmldb.XMLDBURIFunctions;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.CollectionManagementService;
import org.xmldb.api.modules.XMLResource;

import ruleml.api.exception.PreconditionRequiredException;
import ruleml.api.repository.Name;
import ruleml.api.repository.Repository;
import ruleml.api.repository.Rule;
import ruleml.api.repository.Ruleset;
import ruleml.api.util.IDUtil;

public class ExistDAO {

	/**
	 * Name of the properties file for a repository/ruleset.
	 */
	private static final String COLLECTION_PROPERTIES_FILE = "info";
	/**
	 * The user used to login into the DB.
	 */
	private static final String DB_USER = "admin";
	/**
	 * The password used to login into the DB.
	 */
	private static final String DB_PASSWORD = "root";
	private static Resource currentRule;

	/**
	 * Creates a new repository.
	 * 
	 * @param storeID The ID of the store which will hold the new repository.
	 * 
	 * @return the newly created repository URI or <code>null</code> if the repository could not be created.
	 * 
	 * @throws FileNotFoundException When the store with the given ID could not found.
	 */
	public static String createNewRepository(String storeID, String name, String description, String language) throws Exception {
		String newRepositoryURI = null;
		Collection store = null;
		Collection newRepos = null;

		try {    
			DBConnection.initConnection();
			// Get the store with the given ID
			store = DatabaseManager.getCollection(DatabaseConstants.XML_RPC_URI + DatabaseConstants.ROOT_NAME + storeID, DB_USER, DB_PASSWORD);
			if (store != null) {
				// Create a new collection which will represent the new repository
				String reposID = IDUtil.generateID();
				newRepos = getOrCreateCollection(DatabaseConstants.ROOT_NAME + storeID + "/" + reposID);
				if (newRepos != null) {
					newRepositoryURI = storeID + "/repositories/" + reposID;
					if (language == null) {
						language = "en";
					}

					// Create an xml document with info for the current collection
					createXMLInfoResource(newRepos, COLLECTION_PROPERTIES_FILE, createDOMInfo(name, description, language));
				}

				return newRepositoryURI;
			} else {
				throw new NotFoundException("Store with ID " + storeID + " could not be found.");
			}
		} finally {
			// Don't forget to clean up!
			if(newRepos != null) {
				newRepos.close(); 
			}

			if(store != null) {
				store.close(); 
			}
		}

	}

	private static Element createDOMInfo(String name, String description,
			String language) throws SAXException, IOException, ParserConfigurationException {
		Document document = DocumentBuilderFactory
				.newInstance()
				.newDocumentBuilder()
				.parse(new ByteArrayInputStream("<info/>".getBytes()));
		Element root =  document
				.getDocumentElement();

		Element nameElem = document.createElement(DatabaseConstants.NAME_PROPERTY);
		nameElem.setTextContent(name);
		root.appendChild(nameElem);

		Element descElem = document.createElement(DatabaseConstants.DESCRIPTION_PROPERTY);
		descElem.setTextContent(description);
		root.appendChild(descElem);

		Element langElem = document.createElement(DatabaseConstants.LANGUAGE_PROPERTY);
		langElem.setTextContent(language);
		root.appendChild(langElem);

		return root;
	}

	/**
	 * Creates an XML resource over the given DOM Node.
	 * 
	 * @param col The collection.
	 * @param name	The name of the XML file.
	 * @param element	The DOM element.
	 * 
	 * @return The newly created resource.
	 * 
	 * @throws XMLDBException If the resource could not be created.
	 */
	private static XMLResource createXMLInfoResource(Collection col, String name, Element element) throws XMLDBException {
		// create new XMLResource; an id will be assigned to the new 
		//resource
		XMLResource res = null;
		try {
			res = (XMLResource)col.createResource(name, "XMLResource");
			res.setContentAsDOM(element);
			col.storeResource(res);
			return res;
		} finally {
			if (res != null) {
				((EXistResource)res).freeResources();
			}
		}
	}

	/**
	 * Create or return a collection with the given URI
	 * 
	 * @param collectionUri The URI of the collection.
	 * 
	 * @return The collection.
	 * 
	 * @throws XMLDBException When something goes wrong.
	 */
	private static Collection getOrCreateCollection(String collectionUri) throws 
	XMLDBException {
		return getOrCreateCollection(collectionUri, 0);
	}

	/**
	 * Create or return a collection with the given URI
	 * 
	 * @param collectionUri The URI of the collection.
	 * @param pathSegmentOffset The offset of the segment from the given path.
	 * 
	 * @return The collection.
	 * 
	 * @throws XMLDBException When something goes wrong.
	 */
	private static Collection getOrCreateCollection(String collectionUri, int 
			pathSegmentOffset) throws XMLDBException {

		Collection col = DatabaseManager.getCollection(DatabaseConstants.XML_RPC_URI + collectionUri, DB_USER, DB_PASSWORD);
		if(col == null) {
			if(collectionUri.startsWith("/")) {
				collectionUri = collectionUri.substring(1);
			}
			String pathSegments[] = collectionUri.split("/");
			if(pathSegments.length > 0) {
				StringBuilder path = new StringBuilder();
				for(int i = 0; i <= pathSegmentOffset; i++) {
					path.append("/" + pathSegments[i]);
				}
				Collection start = DatabaseManager.getCollection(DatabaseConstants.XML_RPC_URI + path, DB_USER, DB_PASSWORD);
				if(start == null) {
					//collection does not exist, so create
					String parentPath = path.substring(0, path.lastIndexOf("/"
							));
					Collection parent = DatabaseManager.getCollection(DatabaseConstants.XML_RPC_URI + 
							parentPath, DB_USER, DB_PASSWORD);
					CollectionManagementService mgt = 
							(CollectionManagementService) parent.getService("CollectionManagementService", "1.0");
					col = mgt.createCollection(pathSegments[pathSegmentOffset]);
					col.close();
					parent.close();
				} else {
					start.close();
				}
			}
			return getOrCreateCollection(collectionUri, ++pathSegmentOffset);
		} else {
			return col;
		}
	}

	/**
	 * Obtain all the repositories from the given store.
	 * 
	 * @param storeID The ID of the store.
	 * 
	 * @throws Exception When a resource cannot be retrieved.
	 */
	public static List<Repository> getAllRepositories(String storeID) throws Exception {
		List<Repository> repos = new ArrayList<Repository>();
		List<Collection> collections = new ArrayList<Collection>();
		try {    
			// Initialize the database connection
			DBConnection.initConnection();
			// Get the store collection
			Collection col = DatabaseManager.getCollection(DatabaseConstants.XML_RPC_URI + "/db/" + storeID, DB_USER, DB_PASSWORD);
			if (col == null) {
				throw new NotFoundException("Collection could not be found");
			}
			collections.add(col);
			String[] childCollections = col.listChildCollections();
			for (int i = 0; i < childCollections.length; i++) {
				String reposID = childCollections[i];
				Collection currentRepos = col.getChildCollection(reposID);
				collections.add(currentRepos);
				Repository repository = new Repository();
				repository.setId(reposID);

				// Query the info.xml document 
				// Instantiate a XQuery service 
				XQueryService service = (XQueryService) currentRepos.getService("XQueryService", 
						"1.0"); 
				service.setProperty("indent", "yes"); 

				// Get the name
				Name nameElem = null;
				String xQuery = "for $x in doc('" + COLLECTION_PROPERTIES_FILE + "')//info//name " 
						+ "return data($x)"; 
				// Execute the query, print the result 
				ResourceSet result = service.query(xQuery); 
				ResourceIterator it = result.getIterator(); 
				if (it.hasMoreResources()) { 
					Resource r = it.nextResource();
					Object content = r.getContent();
					if (content instanceof String && ((String) content).length() > 0) {
						nameElem = new Name();
						nameElem.setName((String) content);
					}
				}

				// Get the description
				xQuery = "for $x in doc('" + COLLECTION_PROPERTIES_FILE + "')//info//description " 
						+ "return data($x)"; 
				// Execute the query, print the result 
				result = service.query(xQuery); 
				it = result.getIterator(); 
				if (it.hasMoreResources()) { 
					Resource r = it.nextResource();
					Object content = r.getContent();
					if (content instanceof String && ((String) content).length() > 0) {
						repository.setDescription((String) content);
					}
				}

				// Get the language
				xQuery = "for $x in doc('" + COLLECTION_PROPERTIES_FILE + "')//info//lang " 
						+ "return data($x)"; 
				// Execute the query, print the result 
				result = service.query(xQuery); 
				it = result.getIterator(); 
				if (it.hasMoreResources()) { 
					Resource r = it.nextResource();
					Object content = r.getContent();
					if (content instanceof String && ((String) content).length() > 0 &&
							nameElem != null) {
						nameElem.setLanguage((String) content);
					}
				}

				if (nameElem != null) {
					repository.setName(nameElem);
				}
				repos.add(repository);
			}
		} finally {
			// Clean up!
			if(!collections.isEmpty()) {
				for (int i = 0; i < collections.size(); i++) {
					try { 
						if (collections.get(i) != null) {
							collections.get(i).close(); 
						}
					} catch(XMLDBException ex) {
						throw ex;
					}
				}
			}
		}

		System.out.println("return from get all repos");
		return repos;
	}

	/**
	 * Obtain the repository with the given ID from the given store.
	 * 
	 * @param storeID 		The ID of the store.
	 * @param repositoryID 	The ID of the repository to search.
	 * 
	 * @throws Exception When a resource cannot be retrieved.
	 */
	public static Repository getRepositoryWithID(String storeID,
			String repositoryID) throws Exception {
		Repository repos = null;
		List<Collection> collections = new ArrayList<Collection>();
		try {    
			// Initialize the database connection
			DBConnection.initConnection();
			// Get the store collection
			Collection col = DatabaseManager.getCollection(
					DatabaseConstants.XML_RPC_URI + "/db/" + storeID, DB_USER, DB_PASSWORD);
			if (col == null) {
				throw new NotFoundException("Collection could not be found");
			}
			collections.add(col);
			String[] childCollections = col.listChildCollections();
			for (int i = 0; i < childCollections.length; i++) {
				String reposID = childCollections[i];
				// Found the searched repository
				if (repositoryID.equals(reposID)) {
					Collection currentRepos = col.getChildCollection(reposID);
					collections.add(currentRepos);
					repos = new Repository();
					repos.setId(reposID);

					// Query the info.xml document 
					// Instantiate a XQuery service 
					XQueryService service = (XQueryService) currentRepos.getService("XQueryService", 
							"1.0"); 
					service.setProperty("indent", "yes"); 

					// Get the name
					Name nameElem = null;
					String xQuery = "for $x in doc('" + COLLECTION_PROPERTIES_FILE + "')//info//name " 
							+ "return data($x)"; 
					// Execute the query, print the result 
					ResourceSet result = service.query(xQuery); 
					ResourceIterator it = result.getIterator(); 
					if (it.hasMoreResources()) { 
						Resource r = it.nextResource();
						Object content = r.getContent();
						if (content instanceof String && ((String) content).length() > 0) {
							nameElem = new Name();
							nameElem.setName((String) content);
						}
					}
					// Get the description
					xQuery = "for $x in doc('" + COLLECTION_PROPERTIES_FILE + "')//info//description " 
							+ "return data($x)"; 
					// Execute the query, print the result 
					result = service.query(xQuery); 
					it = result.getIterator(); 
					if (it.hasMoreResources()) { 
						Resource r = it.nextResource();
						Object content = r.getContent();
						if (content instanceof String && ((String) content).length() > 0) {
							repos.setDescription((String) content);
						}
					}

					// Get the language
					xQuery = "for $x in doc('" + COLLECTION_PROPERTIES_FILE + "')//info//lang " 
							+ "return data($x)"; 
					// Execute the query, print the result 
					result = service.query(xQuery); 
					it = result.getIterator(); 
					if (it.hasMoreResources()) { 
						Resource r = it.nextResource();
						Object content = r.getContent();
						if (content instanceof String && ((String) content).length() > 0 &&
								nameElem != null) {
							nameElem.setLanguage((String) content);
						}
					}
					
					if (nameElem != null) {
						repos.setName(nameElem);
					}
					
					break;
				}
			}
		} finally {
			// Clean up!
			if(!collections.isEmpty()) {
				for (int i = 0; i < collections.size(); i++) {
					try { 
						if (collections.get(i) != null) {
							collections.get(i).close(); 
						}
					} catch(XMLDBException ex) {
						throw ex;
					}
				}
			}
		}

		return repos;
	}

	public static List<Repository> getRepositoriesWithName(String storeID,
			String repositoryName, String lang) throws Exception {
		List<Repository> repos = new ArrayList<Repository>();
		List<Collection> collections = new ArrayList<Collection>();
		try {    
			// Initialize the database connection
			DBConnection.initConnection();
			// Get the store collection
			Collection col = DatabaseManager.getCollection(DatabaseConstants.XML_RPC_URI + "/db/" + storeID, DB_USER, DB_PASSWORD);
			if (col == null) {
				throw new NotFoundException("Collection could not be found");
			}
			collections.add(col);
			String[] childCollections = col.listChildCollections();
			for (int i = 0; i < childCollections.length; i++) {
				String reposID = childCollections[i];
				Collection currentRepos = col.getChildCollection(reposID);
				collections.add(currentRepos);
				Repository repository = new Repository();
				repository.setId(reposID);

				// Query the info.xml document 
				// Instantiate a XQuery service 
				XQueryService service = (XQueryService) currentRepos.getService("XQueryService", 
						"1.0"); 
				service.setProperty("indent", "yes"); 

				// Get the name
				String currentName = null;
				String xQuery = "for $x in doc('" + COLLECTION_PROPERTIES_FILE + "')//info//name " 
						+ "return data($x)"; 
				// Execute the query, print the result 
				ResourceSet result = service.query(xQuery); 
				ResourceIterator it = result.getIterator(); 
				if (it.hasMoreResources()) { 
					Resource r = it.nextResource();
					Object content = r.getContent();
					if (content instanceof String && ((String) content).length() > 0) {
						currentName = (String) content;
					}
				}

				// Get the language
				String language = null;
				xQuery = "for $x in doc('" + COLLECTION_PROPERTIES_FILE + "')//info//lang " 
						+ "return data($x)"; 
				// Execute the query, print the result 
				result = service.query(xQuery); 
				it = result.getIterator(); 
				if (it.hasMoreResources()) { 
					Resource r = it.nextResource();
					Object content = r.getContent();
					if (content instanceof String && ((String) content).length() > 0) {
						language = (String) content;
					}
				}
				
				boolean continueLoop = true;
				if (currentName != null) {
					if (/*(language == null && "en".equals(lang) || language.equals(lang)) &&*/ 
							currentName.equals(repositoryName)) {
						continueLoop = false;
						Name nameElem = new Name();
						nameElem.setName(currentName);
						nameElem.setLanguage(language != null ? language : "en");
						repository.setName(nameElem);
					}
				}
				
				if (continueLoop) {
					continue;
				}
				
				// Get the description
				xQuery = "for $x in doc('" + COLLECTION_PROPERTIES_FILE + "')//info//description " 
						+ "return data($x)"; 
				// Execute the query, print the result 
				result = service.query(xQuery); 
				it = result.getIterator(); 
				if (it.hasMoreResources()) { 
					Resource r = it.nextResource();
					Object content = r.getContent();
					if (content instanceof String && ((String) content).length() > 0) {
						repository.setDescription((String) content);
					}
				}

				repos.add(repository);
			}
		} finally {
			// Clean up!
			if(!collections.isEmpty()) {
				for (int i = 0; i < collections.size(); i++) {
					try { 
						if (collections.get(i) != null) {
							collections.get(i).close(); 
						}
					} catch(XMLDBException ex) {
						throw ex;
					}
				}
			}
		}

		return repos;
	}

	public static List<Ruleset> getAllRulesets(String storeID, String repositoryID) throws Exception {
		List<Ruleset> rulesets = new ArrayList<Ruleset>();
		List<Collection> collections = new ArrayList<Collection>();
		try {    
			// Initialize the database connection
			DBConnection.initConnection();
			// Get the store collection
			Collection col = DatabaseManager.getCollection(DatabaseConstants.XML_RPC_URI + "/db/" + storeID, DB_USER, DB_PASSWORD);
			if (col == null) {
				throw new NotFoundException("Collection could not be found");
			}
			collections.add(col);
			String[] childCollections = col.listChildCollections();
			for (int i = 0; i < childCollections.length; i++) {
				String reposID = childCollections[i];
				Collection currentRepos = col.getChildCollection(reposID);
				if (repositoryID.equals(reposID)) {
					String[] reposChildCollections = currentRepos.listChildCollections();
					collections.add(currentRepos);
					for (int j = 0; j < reposChildCollections.length; j++) {
						String currentRuleSetID = reposChildCollections[j];
						System.out.println("ruleset name " + currentRuleSetID);
						Collection currentRuleset = currentRepos.getChildCollection(currentRuleSetID);
						System.out.println("found ruleset " + currentRuleset);
						if (currentRuleset != null) {
							collections.add(currentRuleset);
							Ruleset ruleset = new Ruleset();
							ruleset.setId(currentRuleSetID);
							System.out.println("current rulesets col " + currentRuleset);
							// Query the info.xml document 
							// Instantiate a XQuery service 
							XQueryService service = (XQueryService) currentRuleset.getService("XQueryService", 
									"1.0"); 
							service.setProperty("indent", "yes"); 
							
							// Get the name
							Name nameElem = null;
							String xQuery = "for $x in doc('" + COLLECTION_PROPERTIES_FILE + "')//info//name " 
									+ "return data($x)"; 
							// Execute the query, print the result 
							System.out.println("query " + xQuery);
							ResourceSet result = service.query(xQuery); 
							ResourceIterator it = result.getIterator(); 
							System.out.println(" " + it.hasMoreResources());
							if (it.hasMoreResources()) { 
								Resource r = it.nextResource();
								Object content = r.getContent();
								if (content instanceof String && ((String) content).length() > 0) {
									System.out.println("nameee");
									nameElem = new Name();
									nameElem.setName((String) content);
								}
							}
							
							// Get the description
							xQuery = "for $x in doc('" + COLLECTION_PROPERTIES_FILE + "')//info//description " 
									+ "return data($x)"; 
							// Execute the query, print the result 
							result = service.query(xQuery); 
							it = result.getIterator(); 
							if (it.hasMoreResources()) { 
								Resource r = it.nextResource();
								Object content = r.getContent();
								if (content instanceof String && ((String) content).length() > 0) {
									ruleset.setDescription((String) content);
								}
							}
							
							// Get the language
							xQuery = "for $x in doc('" + COLLECTION_PROPERTIES_FILE + "')//info//lang " 
									+ "return data($x)"; 
							// Execute the query, print the result 
							result = service.query(xQuery); 
							it = result.getIterator(); 
							if (it.hasMoreResources()) { 
								Resource r = it.nextResource();
								Object content = r.getContent();
								if (content instanceof String && ((String) content).length() > 0 &&
										nameElem != null) {
									nameElem.setLanguage((String) content);
								}
							}
							
							if (nameElem != null) {
								ruleset.setName(nameElem);
							}
							rulesets.add(ruleset);
						}
					}
				}
			}
		} finally {
			// Clean up!
			if(!collections.isEmpty()) {
				for (int i = 0; i < collections.size(); i++) {
					try { 
						if (collections.get(i) != null) {
							collections.get(i).close(); 
						}
					} catch(XMLDBException ex) {
						throw ex;
					}
				}
			}
		}
		
		return rulesets;
	}

	/**
	 * Creates a new repository.
	 * 
	 * @param storeID The ID of the store which will hold the new repository.
	 * 
	 * @return the newly created repository URI or <code>null</code> if the repository could not be created.
	 * 
	 * @throws FileNotFoundException When the store with the given ID could not found.
	 */
	public static String createNewRuleset(String storeID, String reposID, String name, String description, String language) throws Exception {
		String newRepositoryURI = null;
		Collection repository = null;
		Collection newRuleset = null;
	
		try {    
			DBConnection.initConnection();
			// Get the store with the given ID
			String relPathToRepository = DatabaseConstants.ROOT_NAME + storeID + "/" + reposID;
			repository = DatabaseManager.getCollection(DatabaseConstants.XML_RPC_URI + relPathToRepository, DB_USER, DB_PASSWORD);
			if (repository != null) {

				// Create a new collection which will represent the new repository
				String rulesetID = IDUtil.generateID();
				newRuleset = getOrCreateCollection(relPathToRepository + "/" + rulesetID);
				if (newRuleset != null) {
					newRepositoryURI = storeID + "/repositories/" + reposID + "/rulesets/" + rulesetID;
					if (language == null) {
						language = "en";
					}

					// Create an xml document with info for the current collection
					createXMLInfoResource(newRuleset, COLLECTION_PROPERTIES_FILE, createDOMInfo(name, description, language));
				}

				return newRepositoryURI;
			} else {
				throw new NotFoundException("Store with ID " + storeID + " could not be found.");
			}
		} finally {
			// Don't forget to clean up!
			if(newRuleset != null) {
				newRuleset.close(); 
			}
	
			if(repository != null) {
				repository.close(); 
			}
		}
	}

	/**
	 * Obtain the ruleset with the given ID.
	 * 
	 * @param storeID 		The ID of the store.
	 * @param repositoryID 	The ID of the repository.
	 * @param rulesetID		The ID of the ruleset.
	 * 
	 * @throws Exception When a resource cannot be retrieved.
	 */
	public static Ruleset getRulesetWithID(String storeID,
			String repositoryID, String rulesetID) throws Exception {
		Ruleset ruleset = null;
		List<Collection> collections = new ArrayList<Collection>();
		Collection repository = null;
		try {    
			// Initialize the database connection
			DBConnection.initConnection();
			// Get the store collection
			String relPathToRepository = DatabaseConstants.ROOT_NAME + storeID + "/" + repositoryID;
			repository = DatabaseManager.getCollection(DatabaseConstants.XML_RPC_URI + relPathToRepository, DB_USER, DB_PASSWORD);
			if (repository == null) {
				throw new NotFoundException("Collection could not be found");
			}
			collections.add(repository);
			String[] childCollections = repository.listChildCollections();
			for (int i = 0; i < childCollections.length; i++) {
				String currentRulesetID = childCollections[i];
				// Found the searched repository
				if (rulesetID.equals(currentRulesetID)) {
					Collection currentRuleset = repository.getChildCollection(currentRulesetID);
					collections.add(currentRuleset);
					ruleset = new Ruleset();
					ruleset.setId(currentRulesetID);

					// Query the info.xml document 
					// Instantiate a XQuery service 
					XQueryService service = (XQueryService) currentRuleset.getService("XQueryService", 
							"1.0"); 
					service.setProperty("indent", "yes"); 

					// Get the name
					Name nameElem = null;
					String xQuery = "for $x in doc('" + COLLECTION_PROPERTIES_FILE + "')//info//name " 
							+ "return data($x)"; 
					// Execute the query, print the result 
					ResourceSet result = service.query(xQuery); 
					ResourceIterator it = result.getIterator(); 
					if (it.hasMoreResources()) { 
						Resource r = it.nextResource();
						Object content = r.getContent();
						if (content instanceof String && ((String) content).length() > 0) {
							nameElem = new Name();
							nameElem.setName((String) content);
						}
					}
					// Get the description
					xQuery = "for $x in doc('" + COLLECTION_PROPERTIES_FILE + "')//info//description " 
							+ "return data($x)"; 
					// Execute the query, print the result 
					result = service.query(xQuery); 
					it = result.getIterator(); 
					if (it.hasMoreResources()) { 
						Resource r = it.nextResource();
						Object content = r.getContent();
						if (content instanceof String && ((String) content).length() > 0) {
							ruleset.setDescription((String) content);
						}
					}

					// Get the language
					xQuery = "for $x in doc('" + COLLECTION_PROPERTIES_FILE + "')//info//lang " 
							+ "return data($x)"; 
					// Execute the query, print the result 
					result = service.query(xQuery); 
					it = result.getIterator(); 
					if (it.hasMoreResources()) { 
						Resource r = it.nextResource();
						Object content = r.getContent();
						if (content instanceof String && ((String) content).length() > 0 &&
								nameElem != null) {
							nameElem.setLanguage((String) content);
						}
					}
					
					if (nameElem != null) {
						ruleset.setName(nameElem);
					}
				}
			}
		} finally {
			// Clean up!
			if(!collections.isEmpty()) {
				for (int i = 0; i < collections.size(); i++) {
					try { 
						if (collections.get(i) != null) {
							collections.get(i).close(); 
						}
					} catch(XMLDBException ex) {
						throw ex;
					}
				}
			}
		}

		return ruleset;
	}

	public static List<Ruleset> getRulesetsWithName(String storeID,
			String reposID, String rulesetName, String lang) throws Exception {
		List<Ruleset> rulesets = new ArrayList<Ruleset>();
		List<Collection> collections = new ArrayList<Collection>();
		try {    
			// Initialize the database connection
			DBConnection.initConnection();
			// Get the store collection
			String relPathToRepository = DatabaseConstants.ROOT_NAME + storeID + "/" + reposID;
			Collection repository = DatabaseManager.getCollection(DatabaseConstants.XML_RPC_URI + relPathToRepository, DB_USER, DB_PASSWORD);
			if (repository == null) {
				throw new NotFoundException("Collection could not be found");
			}
			collections.add(repository);
			String[] childCollections = repository.listChildCollections();
			for (int i = 0; i < childCollections.length; i++) {
				String rulesetID = childCollections[i];
				Collection currentRuleset = repository.getChildCollection(rulesetID);
				collections.add(currentRuleset);
				Ruleset ruleset = new Ruleset();
				ruleset.setId(rulesetID);

				// Query the info.xml document 
				// Instantiate a XQuery service 
				XQueryService service = (XQueryService) currentRuleset.getService("XQueryService", 
						"1.0"); 
				service.setProperty("indent", "yes"); 

				// Get the name
				String currentName = null;
				String xQuery = "for $x in doc('" + COLLECTION_PROPERTIES_FILE + "')//info//name " 
						+ "return data($x)"; 
				// Execute the query, print the result 
				ResourceSet result = service.query(xQuery); 
				ResourceIterator it = result.getIterator(); 
				if (it.hasMoreResources()) { 
					Resource r = it.nextResource();
					Object content = r.getContent();
					if (content instanceof String && ((String) content).length() > 0) {
						currentName = (String) content;
					}
				}

				// Get the language
				String language = null;
				xQuery = "for $x in doc('" + COLLECTION_PROPERTIES_FILE + "')//info//lang " 
						+ "return data($x)"; 
				// Execute the query, print the result 
				result = service.query(xQuery); 
				it = result.getIterator(); 
				if (it.hasMoreResources()) { 
					Resource r = it.nextResource();
					Object content = r.getContent();
					if (content instanceof String && ((String) content).length() > 0) {
						language = (String) content;
					}
				}
				
				boolean continueLoop = true;
				if (currentName != null) {
					if (/*(language == null && "en".equals(lang) || language.equals(lang)) &&*/ 
							currentName.equals(rulesetName)) {
						continueLoop = false;
						Name nameElem = new Name();
						nameElem.setName(currentName);
						nameElem.setLanguage(language != null ? language : "en");
						ruleset.setName(nameElem);
					}
				}
				if (continueLoop) {
					continue;
				}
				
				// Get the description
				xQuery = "for $x in doc('" + COLLECTION_PROPERTIES_FILE + "')//info//description " 
						+ "return data($x)"; 
				// Execute the query, print the result 
				result = service.query(xQuery); 
				it = result.getIterator(); 
				if (it.hasMoreResources()) { 
					Resource r = it.nextResource();
					Object content = r.getContent();
					if (content instanceof String && ((String) content).length() > 0) {
						ruleset.setDescription((String) content);
					}
				}

				rulesets.add(ruleset);
			}
		} finally {
			// Clean up!
			if(!collections.isEmpty()) {
				for (int i = 0; i < collections.size(); i++) {
					try { 
						if (collections.get(i) != null) {
							collections.get(i).close(); 
						}
					} catch(XMLDBException ex) {
						throw ex;
					}
				}
			}
		}

		return rulesets;
	}

	public static List<Ruleset> getAllRulesetsMatchString(String storeID,
			String reposID, String searchString) throws Exception {
		List<Ruleset> rulesets = new ArrayList<Ruleset>();
		List<Collection> collections = new ArrayList<Collection>();
		try {    
			// Initialize the database connection
			DBConnection.initConnection();
			// Get the store collection
			String relPathToRepository = DatabaseConstants.ROOT_NAME + storeID + "/" + reposID;
			Collection repository = DatabaseManager.getCollection(DatabaseConstants.XML_RPC_URI + relPathToRepository, DB_USER, DB_PASSWORD);
			if (repository == null) {
				throw new NotFoundException("Collection could not be found");
			}
			collections.add(repository);
			String[] childCollections = repository.listChildCollections();
			for (int i = 0; i < childCollections.length; i++) {
				String rulesetID = childCollections[i];
				Collection currentRuleset = repository.getChildCollection(rulesetID);
				collections.add(currentRuleset);
				Ruleset ruleset = new Ruleset();
				ruleset.setId(rulesetID);

				// Query the info.xml document 
				// Instantiate a XQuery service 
				XQueryService service = (XQueryService) currentRuleset.getService("XQueryService", 
						"1.0"); 
				service.setProperty("indent", "yes"); 

				// Get the name
				String currentName = null;
				String xQuery = "for $x in doc('" + COLLECTION_PROPERTIES_FILE + "')//info//name " 
						+ "return data($x)"; 
				// Execute the query, print the result 
				ResourceSet result = service.query(xQuery); 
				ResourceIterator it = result.getIterator(); 
				if (it.hasMoreResources()) { 
					Resource r = it.nextResource();
					Object content = r.getContent();
					if (content instanceof String && ((String) content).length() > 0) {
						currentName = (String) content;
					}
				}

				// Get the language
				String language = null;
				xQuery = "for $x in doc('" + COLLECTION_PROPERTIES_FILE + "')//info//lang " 
						+ "return data($x)"; 
				// Execute the query, print the result 
				result = service.query(xQuery); 
				it = result.getIterator(); 
				if (it.hasMoreResources()) { 
					Resource r = it.nextResource();
					Object content = r.getContent();
					if (content instanceof String && ((String) content).length() > 0) {
						language = (String) content;
					}
				}
				
				boolean continueLoop = true;
				
				// Get the description
				String description = null;
				xQuery = "for $x in doc('" + COLLECTION_PROPERTIES_FILE + "')//info//description " 
						+ "return data($x)"; 
				// Execute the query, print the result 
				result = service.query(xQuery); 
				it = result.getIterator(); 
				if (it.hasMoreResources()) { 
					Resource r = it.nextResource();
					Object content = r.getContent();
					if (content instanceof String && ((String) content).length() > 0) {
						description = (String) content;
						ruleset.setDescription(description);
					}
				}
				
				
				if (description != null) {
					if (/*(language == null && "en".equals(lang) || language.equals(lang)) &&*/ 
							description.contains(searchString)) {
						continueLoop = false;
						Name nameElem = new Name();
						nameElem.setName(currentName);
						nameElem.setLanguage(language != null ? language : "en");
						ruleset.setName(nameElem);
						ruleset.setDescription(description);
					}
				}
				
				if (continueLoop) {
					continue;
				}

				rulesets.add(ruleset);
			}
		} finally {
			// Clean up!
			if(!collections.isEmpty()) {
				for (int i = 0; i < collections.size(); i++) {
					try { 
						if (collections.get(i) != null) {
							collections.get(i).close(); 
						}
					} catch(XMLDBException ex) {
						throw ex;
					}
				}
			}
		}

		return rulesets;
//		Pattern pattern = Pattern.compile(searchString);
//		Matcher matcher = pattern.matcher(sear);
//		if (matcher.find()) {
//		    System.out.println(matcher.group(0)); //prints /{item}/
//		} else {
//		    System.out.println("Match not found");
//		}
	}

	public static Ruleset deleteRuleset(String storeID, String repositoryID,
			String rulesetID) throws Exception {

		Ruleset ruleset = null;
		List<Collection> collections = new ArrayList<Collection>();
		Collection repository = null;
		try {    
			// Initialize the database connection
			DBConnection.initConnection();
			// Get the store collection
			String relPathToRepository = DatabaseConstants.ROOT_NAME + storeID + "/" + repositoryID;
			repository = DatabaseManager.getCollection(DatabaseConstants.XML_RPC_URI + relPathToRepository, DB_USER, DB_PASSWORD);
			if (repository == null) {
				throw new NotFoundException("Collection could not be found");
			}
			collections.add(repository);
			String[] childCollections = repository.listChildCollections();
			for (int i = 0; i < childCollections.length; i++) {
				String currentRulesetID = childCollections[i];
				// Found the searched repository
				if (rulesetID.equals(currentRulesetID)) {
					List<Rule> currentRules = null;
					
					Collection currentRuleset = repository.getChildCollection(currentRulesetID);
					ruleset = new Ruleset();
					ruleset.setId(currentRulesetID);

					// Query the info.xml document 
					// Instantiate a XQuery service 
					XQueryService service = (XQueryService) currentRuleset.getService("XQueryService", 
							"1.0"); 
					service.setProperty("indent", "yes"); 

					// Get the name
					Name nameElem = null;
					String xQuery = "for $x in doc('" + COLLECTION_PROPERTIES_FILE + "')//info//name " 
							+ "return data($x)"; 
					// Execute the query, print the result 
					ResourceSet result = service.query(xQuery); 
					ResourceIterator it = result.getIterator(); 
					if (it.hasMoreResources()) { 
						Resource r = it.nextResource();
						Object content = r.getContent();
						if (content instanceof String && ((String) content).length() > 0) {
							nameElem = new Name();
							nameElem.setName((String) content);
						}
					}
					// Get the description
					xQuery = "for $x in doc('" + COLLECTION_PROPERTIES_FILE + "')//info//description " 
							+ "return data($x)"; 
					// Execute the query, print the result 
					result = service.query(xQuery); 
					it = result.getIterator(); 
					if (it.hasMoreResources()) { 
						Resource r = it.nextResource();
						Object content = r.getContent();
						if (content instanceof String && ((String) content).length() > 0) {
							ruleset.setDescription((String) content);
						}
					}

					// Get the language
					xQuery = "for $x in doc('" + COLLECTION_PROPERTIES_FILE + "')//info//lang " 
							+ "return data($x)"; 
					// Execute the query, print the result 
					result = service.query(xQuery); 
					it = result.getIterator(); 
					if (it.hasMoreResources()) { 
						Resource r = it.nextResource();
						Object content = r.getContent();
						if (content instanceof String && ((String) content).length() > 0 &&
								nameElem != null) {
							nameElem.setLanguage((String) content);
						}
					}
					
					if (nameElem != null) {
						ruleset.setName(nameElem);
					}
					
					
					// Copy all rules from the ruleset to the repository
					String[] rules = currentRuleset.listResources();
					// Se obtin toate resursele si se retine continutul 
					for (int j = 0; j < rules.length; j++) {
						if (j == 0) {
							currentRules = new ArrayList<Rule>();
						}
						String ruleID = rules[j];
						//========================================================
						Rule rule = new Rule();
						
						nameElem = null;
						xQuery = "for $x in doc('" + ruleID + "')//rule//name " 
								+ "return data($x)"; 
						// Execute the query, print the result 
						result = service.query(xQuery); 
						it = result.getIterator(); 
						if (it.hasMoreResources()) { 
							Resource r = it.nextResource();
							Object content = r.getContent();
							if (content instanceof String && ((String) content).length() > 0) {
								nameElem = new Name();
								nameElem.setName((String) content);
							}
						}
						// Get the description
						xQuery = "for $x in doc('" + ruleID + "')//rule//description " 
								+ "return data($x)"; 
						// Execute the query, print the result 
						result = service.query(xQuery); 
						it = result.getIterator(); 
						if (it.hasMoreResources()) { 
							Resource r = it.nextResource();
							Object content = r.getContent();
							if (content instanceof String && ((String) content).length() > 0) {
								rule.setDescription((String) content);
							}
						}

						// Get the language
						xQuery = "for $x in doc('" + ruleID + "')//rule//lang " 
								+ "return data($x)"; 
						// Execute the query, print the result 
						result = service.query(xQuery); 
						it = result.getIterator(); 
						if (it.hasMoreResources()) { 
							Resource r = it.nextResource();
							Object content = r.getContent();
							if (content instanceof String && ((String) content).length() > 0 &&
									nameElem != null) {
								nameElem.setLanguage((String) content);
							}
						}
						
						if (nameElem != null) {
							rule.setName(nameElem);
						}
						
						//========================================================
						
						currentRules.add(rule);
						
					}					
					if (currentRules != null) {
						ruleset.setRules(currentRules);
					}
					// Delete the ruleset
					
					CollectionManagementService mgt = 
							(CollectionManagementService) repository.getService("CollectionManagementService", "1.0");
					mgt.removeCollection(rulesetID);
					
					break;
				}
				
			}
			
			
		} finally {
			// Clean up!
			if(!collections.isEmpty()) {
				for (int i = 0; i < collections.size(); i++) {
					try { 
						if (collections.get(i) != null) {
							collections.get(i).close(); 
						}
					} catch(XMLDBException ex) {
						throw ex;
					}
				}
			}
		}

		if (ruleset == null) {
			throw new PreconditionRequiredException();
		}
		
		return ruleset;
		
	}

	public static String createNewRule(String storeID, String reposID,
			String name, String description, String language, String xmlContent) throws Exception {
		String newRepositoryURI = null;
		Collection repository = null;
		XMLResource newRule = null;
	
		try {    
			DBConnection.initConnection();
			// Get the store with the given ID
			String relPathToRepository = DatabaseConstants.ROOT_NAME + storeID + "/" + reposID;
			repository = DatabaseManager.getCollection(DatabaseConstants.XML_RPC_URI + relPathToRepository, DB_USER, DB_PASSWORD);
			if (repository != null) {

				// Create a new collection which will represent the new repository
				String ruleID = IDUtil.generateID();
				newRule = createXMLResource(repository, ruleID, name, description, language, xmlContent);
				System.out.println("new rule " + newRule);
				if (newRule != null) {
					newRepositoryURI = storeID + "/repositories/" + reposID + "/rules/" + ruleID;
				}

				return newRepositoryURI;
			} else {
				throw new NotFoundException("Store with ID " + storeID + " could not be found.");
			}
		} finally {
			if(repository != null) {
				repository.close(); 
			}
		}
	}

	private static XMLResource createXMLResource(Collection collection, String ruleID,
			String name, String description, String language, String xmlContent) throws Exception {
		
		Document document = DocumentBuilderFactory
				.newInstance()
				.newDocumentBuilder()
				.parse(new ByteArrayInputStream("<rule/>".getBytes()));
		Element root =  document
				.getDocumentElement();

		Element nameElem = document.createElement(DatabaseConstants.NAME_PROPERTY);
		nameElem.setTextContent(name);
		root.appendChild(nameElem);

		Element descElem = document.createElement(DatabaseConstants.DESCRIPTION_PROPERTY);
		descElem.setTextContent(description);
		root.appendChild(descElem);

		Element langElem = document.createElement(DatabaseConstants.LANGUAGE_PROPERTY);
		langElem.setTextContent(language);
		root.appendChild(langElem);
		
		Node importNode = document.importNode(getNodeElements(xmlContent), true);
		Element content = document.createElement(DatabaseConstants.RULE_CONTENT);
		content.appendChild(importNode);
		root.appendChild(content);
		XMLResource res = null;
		try {
			res = (XMLResource)collection.createResource(ruleID, "XMLResource");
			res.setContentAsDOM(root);
			collection.storeResource(res);
			return res;
		} finally {
			if (res != null) {
				((EXistResource)res).freeResources();
			}
		}
	}
	
	private static Node getNodeElements(String xmlContent) throws Exception {
		Document document = DocumentBuilderFactory
				.newInstance()
				.newDocumentBuilder()
				.parse(new ByteArrayInputStream(xmlContent.getBytes()));
		
		return document.getDocumentElement();
	}

	public static List<Rule> getAllRules(String storeID, String reposID) throws Exception {
		List<Rule> rules = new ArrayList<Rule>();
		List<Collection> collections = new ArrayList<Collection>();
		List<XMLResource> resources = new ArrayList<XMLResource>();
		try {    
			// Initialize the database connection
			DBConnection.initConnection();
			// Get the store collection
			String relPathToRepository = DatabaseConstants.ROOT_NAME + storeID + "/" + reposID;
			Collection repository = DatabaseManager.getCollection(DatabaseConstants.XML_RPC_URI + relPathToRepository, DB_USER, DB_PASSWORD);
			if (repository == null) {
				throw new NotFoundException("Collection could not be found");
			}
			collections.add(repository);
			String[] childResources = repository.listResources();
			for (int i = 0; i < childResources.length; i++) {
				String ruleID = childResources[i];

				if (!COLLECTION_PROPERTIES_FILE.equals(ruleID)) {
					XMLResource currentRuleset = (XMLResource) repository.getResource(ruleID);
					resources.add(currentRuleset);
					Rule rule = new Rule();
					rule.setId(ruleID);

					// Query the info.xml document 
					// Instantiate a XQuery service 
					XQueryService service = (XQueryService) repository.getService("XQueryService", 
							"1.0"); 
					service.setProperty("indent", "yes"); 

					// Get the name
					String currentName = null;
					String xQuery = "for $x in doc('" + ruleID + "')//rule//name " 
							+ "return data($x)"; 
					// Execute the query, print the result 
					ResourceSet result = service.query(xQuery); 
					ResourceIterator it = result.getIterator(); 
					if (it.hasMoreResources()) { 
						Resource r = it.nextResource();
						Object content = r.getContent();
						if (content instanceof String && ((String) content).length() > 0) {
							currentName = (String) content;
						}
					}

					// Get the language
					String language = null;
					xQuery = "for $x in doc('" + ruleID + "')//rule//lang " 
							+ "return data($x)"; 
					// Execute the query, print the result 
					result = service.query(xQuery); 
					it = result.getIterator(); 
					if (it.hasMoreResources()) { 
						Resource r = it.nextResource();
						Object content = r.getContent();
						if (content instanceof String && ((String) content).length() > 0) {
							language = (String) content;
						}
					}


					if (currentName != null) {
						Name nameElem = new Name();
						nameElem.setName(currentName);
						nameElem.setLanguage(language != null ? language : "en");
						rule.setName(nameElem);
					}

					// Get the description
					String description = null;
					xQuery = "for $x in doc('" + ruleID + "')//rule//description " 
							+ "return data($x)"; 
					// Execute the query, print the result 
					result = service.query(xQuery); 
					it = result.getIterator(); 
					if (it.hasMoreResources()) { 
						Resource r = it.nextResource();
						Object content = r.getContent();
						if (content instanceof String && ((String) content).length() > 0) {
							description = (String) content;
							rule.setDescription(description);
						}
					}

					if (description != null) {
						rule.setDescription(description);
					}

					rules.add(rule);
				}

			}
		} finally {
			// Clean up!
			if(!collections.isEmpty()) {
				for (int i = 0; i < collections.size(); i++) {
					try { 
						if (collections.get(i) != null) {
							collections.get(i).close(); 
						}
					} catch(XMLDBException ex) {
						throw ex;
					}
				}
			}

		}

		return rules;
	}

	public static Rule getRuleWithID(String storeID, String repositoryID,
			String ruleID) throws Exception {
		Rule rule = null;
		List<Collection> collections = new ArrayList<Collection>();
		Collection repository = null;
		try {    
			// Initialize the database connection
			DBConnection.initConnection();
			// Get the store collection
			String relPathToRepository = DatabaseConstants.ROOT_NAME + storeID + "/" + repositoryID;
			repository = DatabaseManager.getCollection(DatabaseConstants.XML_RPC_URI + relPathToRepository, DB_USER, DB_PASSWORD);
			if (repository == null) {
				throw new NotFoundException("Collection could not be found");
			}
			collections.add(repository);
			String[] childResources = repository.listResources();
			for (int i = 0; i < childResources.length; i++) {
				String currentRuleID = childResources[i];
				// Found the searched repository
				if (ruleID.equals(currentRuleID)) {
					rule = new Rule();
					rule.setId(currentRuleID);

					// Query the info.xml document 
					// Instantiate a XQuery service 
					XQueryService service = (XQueryService) repository.getService("XQueryService", 
							"1.0"); 
					service.setProperty("indent", "yes"); 

					// Get the name
					Name nameElem = null;
					String xQuery = "for $x in doc('" + ruleID + "')//rule//name " 
							+ "return data($x)"; 
					// Execute the query, print the result 
					ResourceSet result = service.query(xQuery); 
					ResourceIterator it = result.getIterator(); 
					if (it.hasMoreResources()) { 
						Resource r = it.nextResource();
						Object content = r.getContent();
						if (content instanceof String && ((String) content).length() > 0) {
							nameElem = new Name();
							nameElem.setName((String) content);
						}
					}
					// Get the description
					xQuery = "for $x in doc('" + ruleID + "')//rule//description " 
							+ "return data($x)"; 
					// Execute the query, print the result 
					result = service.query(xQuery); 
					it = result.getIterator(); 
					if (it.hasMoreResources()) { 
						Resource r = it.nextResource();
						Object content = r.getContent();
						if (content instanceof String && ((String) content).length() > 0) {
							rule.setDescription((String) content);
						}
					}

					// Get the language
					xQuery = "for $x in doc('" + ruleID + "')//rule//lang " 
							+ "return data($x)"; 
					// Execute the query, print the result 
					result = service.query(xQuery); 
					it = result.getIterator(); 
					if (it.hasMoreResources()) { 
						Resource r = it.nextResource();
						Object content = r.getContent();
						if (content instanceof String && ((String) content).length() > 0 &&
								nameElem != null) {
							nameElem.setLanguage((String) content);
						}
					}
					
					if (nameElem != null) {
						rule.setName(nameElem);
					}
					
					
					xQuery = "for $x in doc('" + ruleID + "')//rule/content/*" 
							+ "return $x"; 
					// Execute the query, print the result 
					result = service.query(xQuery); 
					it = result.getIterator(); 
					if (it.hasMoreResources()) { 
						Resource r = it.nextResource();
						Object content = r.getContent();
						if (content instanceof String && ((String) content).length() > 0) {
							rule.setXmlContent((String) content);
						}
					}
				}
			}
		} finally {
			// Clean up!
			if(!collections.isEmpty()) {
				for (int i = 0; i < collections.size(); i++) {
					try { 
						if (collections.get(i) != null) {
							collections.get(i).close(); 
						}
					} catch(XMLDBException ex) {
						throw ex;
					}
				}
			}
		}

		return rule;
	}

	public static List<Rule> getRulesWithName(String storeID, String reposID,
			String ruleName, String lang) throws Exception {
		List<Rule> rules = new ArrayList<Rule>();
		List<Collection> collections = new ArrayList<Collection>();
		try {    
			// Initialize the database connection
			DBConnection.initConnection();
			// Get the store collection
			String relPathToRepository = DatabaseConstants.ROOT_NAME + storeID + "/" + reposID;
			Collection repository = DatabaseManager.getCollection(DatabaseConstants.XML_RPC_URI + relPathToRepository, DB_USER, DB_PASSWORD);
			if (repository == null) {
				throw new NotFoundException("Collection could not be found");
			}
			collections.add(repository);
			String[] childResources = repository.listResources();
			for (int i = 0; i < childResources.length; i++) {
				String ruleID = childResources[i];
				Rule rule = new Rule();
				rule.setId(ruleID);

				// Query the info.xml document 
				// Instantiate a XQuery service 
				XQueryService service = (XQueryService) repository.getService("XQueryService", 
						"1.0"); 
				service.setProperty("indent", "yes"); 

				// Get the name
				String currentName = null;
				String xQuery = "for $x in doc('" + ruleID + "')//rule//name " 
						+ "return data($x)"; 
				// Execute the query, print the result 
				ResourceSet result = service.query(xQuery); 
				ResourceIterator it = result.getIterator(); 
				if (it.hasMoreResources()) { 
					Resource r = it.nextResource();
					Object content = r.getContent();
					if (content instanceof String && ((String) content).length() > 0) {
						currentName = (String) content;
					}
				}

				// Get the language
				String language = null;
				xQuery = "for $x in doc('" + ruleID + "')//rule//lang " 
						+ "return data($x)"; 
				// Execute the query, print the result 
				result = service.query(xQuery); 
				it = result.getIterator(); 
				if (it.hasMoreResources()) { 
					Resource r = it.nextResource();
					Object content = r.getContent();
					if (content instanceof String && ((String) content).length() > 0) {
						language = (String) content;
					}
				}
				
				boolean continueLoop = true;
				if (currentName != null) {
					if (/*(language == null && "en".equals(lang) || language.equals(lang)) &&*/ 
							currentName.equals(ruleName)) {
						continueLoop = false;
						Name nameElem = new Name();
						nameElem.setName(currentName);
						nameElem.setLanguage(language != null ? language : "en");
						rule.setName(nameElem);
					}
				}
				if (continueLoop) {
					continue;
				}
				
				// Get the description
				xQuery = "for $x in doc('" + ruleID + "')//rule//description " 
						+ "return data($x)"; 
				// Execute the query, print the result 
				result = service.query(xQuery); 
				it = result.getIterator(); 
				if (it.hasMoreResources()) { 
					Resource r = it.nextResource();
					Object content = r.getContent();
					if (content instanceof String && ((String) content).length() > 0) {
						rule.setDescription((String) content);
					}
				}

				rules.add(rule);
			}
		} finally {
			// Clean up!
			if(!collections.isEmpty()) {
				for (int i = 0; i < collections.size(); i++) {
					try { 
						if (collections.get(i) != null) {
							collections.get(i).close(); 
						}
					} catch(XMLDBException ex) {
						throw ex;
					}
				}
			}
		}

		return rules;
	}

	public static List<Rule> getAllRulesMatchString(String storeID,
			String reposID, String searchString) throws Exception {
		List<Rule> rules = new ArrayList<Rule>();
		List<Collection> collections = new ArrayList<Collection>();
		try {    
			// Initialize the database connection
			DBConnection.initConnection();
			// Get the store collection
			String relPathToRepository = DatabaseConstants.ROOT_NAME + storeID + "/" + reposID;
			Collection repository = DatabaseManager.getCollection(DatabaseConstants.XML_RPC_URI + relPathToRepository, DB_USER, DB_PASSWORD);
			if (repository == null) {
				throw new NotFoundException("Collection could not be found");
			}
			collections.add(repository);
			String[] childResources = repository.listResources();
			for (int i = 0; i < childResources.length; i++) {
				String ruleID = childResources[i];
				Rule rule = new Rule();
				rule.setId(ruleID);

				// Query the info.xml document 
				// Instantiate a XQuery service 
				XQueryService service = (XQueryService) repository.getService("XQueryService", 
						"1.0"); 
				service.setProperty("indent", "yes"); 

				// Get the name
				String currentName = null;
				String xQuery = "for $x in doc('" + ruleID + "')//rule//name " 
						+ "return data($x)"; 
				// Execute the query, print the result 
				ResourceSet result = service.query(xQuery); 
				ResourceIterator it = result.getIterator(); 
				if (it.hasMoreResources()) { 
					Resource r = it.nextResource();
					Object content = r.getContent();
					if (content instanceof String && ((String) content).length() > 0) {
						currentName = (String) content;
					}
				}

				// Get the language
				String language = null;
				xQuery = "for $x in doc('" + ruleID + "')//rule//lang " 
						+ "return data($x)"; 
				// Execute the query, print the result 
				result = service.query(xQuery); 
				it = result.getIterator(); 
				if (it.hasMoreResources()) { 
					Resource r = it.nextResource();
					Object content = r.getContent();
					if (content instanceof String && ((String) content).length() > 0) {
						language = (String) content;
					}
				}
				
				boolean continueLoop = true;
				
				// Get the description
				String description = null;
				xQuery = "for $x in doc('" + ruleID + "')//rule//description " 
						+ "return data($x)"; 
				// Execute the query, print the result 
				result = service.query(xQuery); 
				it = result.getIterator(); 
				if (it.hasMoreResources()) { 
					Resource r = it.nextResource();
					Object content = r.getContent();
					if (content instanceof String && ((String) content).length() > 0) {
						description = (String) content;
						rule.setDescription(description);
					}
				}
				
				
				if (description != null) {
					if (/*(language == null && "en".equals(lang) || language.equals(lang)) &&*/ 
							description.contains(searchString)) {
						continueLoop = false;
						Name nameElem = new Name();
						nameElem.setName(currentName);
						nameElem.setLanguage(language != null ? language : "en");
						rule.setName(nameElem);
						rule.setDescription(description);
					}
				}
				
				if (continueLoop) {
					continue;
				}

				rules.add(rule);
			}
		} finally {
			// Clean up!
			if(!collections.isEmpty()) {
				for (int i = 0; i < collections.size(); i++) {
					try { 
						if (collections.get(i) != null) {
							collections.get(i).close(); 
						}
					} catch(XMLDBException ex) {
						throw ex;
					}
				}
			}
		}

		return rules;
	}

	public static Object deleteRule(String storeID, String reposID,
			String rulesetID, String ruleID) throws Exception {
		List<Collection> collections = new ArrayList<Collection>();
		Collection repository = null;
		try {    
			// Initialize the database connection
			DBConnection.initConnection();
			// Get the store collection
			String relPathToRepository = DatabaseConstants.ROOT_NAME + storeID + "/" + reposID;
			repository = DatabaseManager.getCollection(DatabaseConstants.XML_RPC_URI + relPathToRepository, DB_USER, DB_PASSWORD);
			if (rulesetID != null) {
				Ruleset ruleset = null;
				String relPathToRuleset = DatabaseConstants.ROOT_NAME + storeID + "/" + reposID + "/" + rulesetID;
				Collection rulesetCol = DatabaseManager.getCollection(
						DatabaseConstants.XML_RPC_URI + relPathToRuleset, DB_USER, DB_PASSWORD);
				if (repository == null || rulesetCol == null) {
					throw new NotFoundException("Collection could not be found");
				}
				collections.add(repository);
				collections.add(rulesetCol);

				// Get information about the current ruleset
				ruleset = new Ruleset();
				ruleset.setId(rulesetID);

				// Query the info.xml document 
				// Instantiate a XQuery service 
				XQueryService service = (XQueryService) rulesetCol.getService("XQueryService", 
						"1.0"); 
				service.setProperty("indent", "yes"); 

				// Get the name
				Name nameElem = null;
				String xQuery = "for $x in doc('" + COLLECTION_PROPERTIES_FILE + "')//info//name " 
						+ "return data($x)"; 
				// Execute the query, print the result 
				ResourceSet result = service.query(xQuery); 
				ResourceIterator it = result.getIterator(); 
				if (it.hasMoreResources()) { 
					Resource r = it.nextResource();
					Object content = r.getContent();
					if (content instanceof String && ((String) content).length() > 0) {
						nameElem = new Name();
						nameElem.setName((String) content);
					}
				}
				// Get the description
				xQuery = "for $x in doc('" + COLLECTION_PROPERTIES_FILE + "')//info//description " 
						+ "return data($x)"; 
				// Execute the query, print the result 
				result = service.query(xQuery); 
				it = result.getIterator(); 
				if (it.hasMoreResources()) { 
					Resource r = it.nextResource();
					Object content = r.getContent();
					if (content instanceof String && ((String) content).length() > 0) {
						ruleset.setDescription((String) content);
					}
				}

				// Get the language
				xQuery = "for $x in doc('" + COLLECTION_PROPERTIES_FILE + "')//info//lang " 
						+ "return data($x)"; 
				// Execute the query, print the result 
				result = service.query(xQuery); 
				it = result.getIterator(); 
				if (it.hasMoreResources()) { 
					Resource r = it.nextResource();
					Object content = r.getContent();
					if (content instanceof String && ((String) content).length() > 0 &&
							nameElem != null) {
						nameElem.setLanguage((String) content);
					}
				}

				if (nameElem != null) {
					ruleset.setName(nameElem);
				}
				
				Rule rule = new Rule();
				rule.setId(ruleID);

				// Query the info.xml document 
				// Instantiate a XQuery service 
				service = (XQueryService) rulesetCol.getService("XQueryService", 
						"1.0"); 
				service.setProperty("indent", "yes"); 

				// Get the name
				nameElem = null;
				xQuery = "for $x in doc('" + ruleID + "')//rule//name " 
						+ "return data($x)"; 
				// Execute the query, print the result 
				result = service.query(xQuery); 
				it = result.getIterator(); 
				if (it.hasMoreResources()) { 
					Resource r = it.nextResource();
					Object content = r.getContent();
					if (content instanceof String && ((String) content).length() > 0) {
						nameElem = new Name();
						nameElem.setName((String) content);
					}
				}
				// Get the description
				xQuery = "for $x in doc('" + ruleID + "')//rule//description " 
						+ "return data($x)"; 
				// Execute the query, print the result 
				result = service.query(xQuery); 
				it = result.getIterator(); 
				if (it.hasMoreResources()) { 
					Resource r = it.nextResource();
					Object content = r.getContent();
					if (content instanceof String && ((String) content).length() > 0) {
						rule.setDescription((String) content);
					}
				}

				// Get the language
				xQuery = "for $x in doc('" + ruleID + "')//rule//lang " 
						+ "return data($x)"; 
				// Execute the query, print the result 
				result = service.query(xQuery); 
				it = result.getIterator(); 
				if (it.hasMoreResources()) { 
					Resource r = it.nextResource();
					Object content = r.getContent();
					if (content instanceof String && ((String) content).length() > 0 &&
							nameElem != null) {
						nameElem.setLanguage((String) content);
					}
				}
				
				if (nameElem != null) {
					rule.setName(nameElem);
				}
				ruleset.addRule(rule);
				return ruleset;
			} else {
				Repository repositoryObj = new Repository();
				repositoryObj.setId(reposID);
				XQueryService service = (XQueryService) repository.getService("XQueryService", 
						"1.0"); 
				service.setProperty("indent", "yes"); 

				// Get the name
				Name nameElem = null;
				String xQuery = "for $x in doc('" + COLLECTION_PROPERTIES_FILE + "')//info//name " 
						+ "return data($x)"; 
				// Execute the query, print the result 
				ResourceSet result = service.query(xQuery); 
				ResourceIterator it = result.getIterator(); 
				if (it.hasMoreResources()) { 
					Resource r = it.nextResource();
					Object content = r.getContent();
					if (content instanceof String && ((String) content).length() > 0) {
						nameElem = new Name();
						nameElem.setName((String) content);
					}
				}
				// Get the description
				xQuery = "for $x in doc('" + COLLECTION_PROPERTIES_FILE + "')//info//description " 
						+ "return data($x)"; 
				// Execute the query, print the result 
				result = service.query(xQuery); 
				it = result.getIterator(); 
				if (it.hasMoreResources()) { 
					Resource r = it.nextResource();
					Object content = r.getContent();
					if (content instanceof String && ((String) content).length() > 0) {
						repositoryObj.setDescription((String) content);
					}
				}

				// Get the language
				xQuery = "for $x in doc('" + COLLECTION_PROPERTIES_FILE + "')//info//lang " 
						+ "return data($x)"; 
				// Execute the query, print the result 
				result = service.query(xQuery); 
				it = result.getIterator(); 
				if (it.hasMoreResources()) { 
					Resource r = it.nextResource();
					Object content = r.getContent();
					if (content instanceof String && ((String) content).length() > 0 &&
							nameElem != null) {
						nameElem.setLanguage((String) content);
					}
				}

				if (nameElem != null) {
					repositoryObj.setName(nameElem);
				}
				
				Rule rule = new Rule();
				rule.setId(ruleID);

				// Query the info.xml document 
				// Instantiate a XQuery service 
				service = (XQueryService) repository.getService("XQueryService", 
						"1.0"); 
				service.setProperty("indent", "yes"); 

				// Get the name
				nameElem = null;
				xQuery = "for $x in doc('" + ruleID + "')//rule//name " 
						+ "return data($x)"; 
				// Execute the query, print the result 
				result = service.query(xQuery); 
				it = result.getIterator(); 
				if (it.hasMoreResources()) { 
					Resource r = it.nextResource();
					Object content = r.getContent();
					if (content instanceof String && ((String) content).length() > 0) {
						nameElem = new Name();
						nameElem.setName((String) content);
					}
				}
				// Get the description
				xQuery = "for $x in doc('" + ruleID + "')//rule//description " 
						+ "return data($x)"; 
				// Execute the query, print the result 
				result = service.query(xQuery); 
				it = result.getIterator(); 
				if (it.hasMoreResources()) { 
					Resource r = it.nextResource();
					Object content = r.getContent();
					if (content instanceof String && ((String) content).length() > 0) {
						rule.setDescription((String) content);
					}
				}

				// Get the language
				xQuery = "for $x in doc('" + ruleID + "')//rule//lang " 
						+ "return data($x)"; 
				// Execute the query, print the result 
				result = service.query(xQuery); 
				it = result.getIterator(); 
				if (it.hasMoreResources()) { 
					Resource r = it.nextResource();
					Object content = r.getContent();
					if (content instanceof String && ((String) content).length() > 0 &&
							nameElem != null) {
						nameElem.setLanguage((String) content);
					}
				}
				
				if (nameElem != null) {
					rule.setName(nameElem);
				}
				
				ArrayList<Rule> rules = new ArrayList<Rule>();
				rules.add(rule);
//				repositoryObj.setRules(rules);
				
				System.out.println("repositoryObj " + repositoryObj);
				return repositoryObj;
			}
		} finally {
			// Clean up!
			if(!collections.isEmpty()) {
				for (int i = 0; i < collections.size(); i++) {
					try { 
						if (collections.get(i) != null) {
							collections.get(i).close(); 
						}
					} catch(XMLDBException ex) {
						throw ex;
					}
				}
			}
		}
	}

	public static Ruleset addRuleToRuleset(String storeID, String reposID,
			String rulesetID, String ruleID) throws Exception {
		
		Ruleset ruleset = null;
		
		List<Collection> collections = new ArrayList<Collection>();
		Collection repository = null;
		Collection rulesetCol = null;
		try {    
			// Initialize the database connection
			DBConnection.initConnection();
			// Get the store collection
			String relPathToRepository = DatabaseConstants.ROOT_NAME + storeID + "/" + reposID;
			repository = DatabaseManager.getCollection(DatabaseConstants.XML_RPC_URI + relPathToRepository, DB_USER, DB_PASSWORD);
			String relPathToRuleset = DatabaseConstants.ROOT_NAME + storeID + "/" + reposID + "/" + rulesetID;
			rulesetCol = DatabaseManager.getCollection(DatabaseConstants.XML_RPC_URI + relPathToRuleset, DB_USER, DB_PASSWORD);
			if (repository == null || rulesetCol == null) {
				throw new NotFoundException("Collection could not be found");
			}
			collections.add(repository);
			collections.add(rulesetCol);
			
			// Get information about the current ruleset
			ruleset = new Ruleset();
			ruleset.setId(rulesetID);

			// Query the info.xml document 
			// Instantiate a XQuery service 
			XQueryService service = (XQueryService) rulesetCol.getService("XQueryService", 
					"1.0"); 
			service.setProperty("indent", "yes"); 

			// Get the name
			Name nameElem = null;
			String xQuery = "for $x in doc('" + COLLECTION_PROPERTIES_FILE + "')//info//name " 
					+ "return data($x)"; 
			// Execute the query, print the result 
			ResourceSet result = service.query(xQuery); 
			ResourceIterator it = result.getIterator(); 
			if (it.hasMoreResources()) { 
				Resource r = it.nextResource();
				Object content = r.getContent();
				if (content instanceof String && ((String) content).length() > 0) {
					nameElem = new Name();
					nameElem.setName((String) content);
				}
			}
			// Get the description
			xQuery = "for $x in doc('" + COLLECTION_PROPERTIES_FILE + "')//info//description " 
					+ "return data($x)"; 
			// Execute the query, print the result 
			result = service.query(xQuery); 
			it = result.getIterator(); 
			if (it.hasMoreResources()) { 
				Resource r = it.nextResource();
				Object content = r.getContent();
				if (content instanceof String && ((String) content).length() > 0) {
					ruleset.setDescription((String) content);
				}
			}

			// Get the language
			xQuery = "for $x in doc('" + COLLECTION_PROPERTIES_FILE + "')//info//lang " 
					+ "return data($x)"; 
			// Execute the query, print the result 
			result = service.query(xQuery); 
			it = result.getIterator(); 
			if (it.hasMoreResources()) { 
				Resource r = it.nextResource();
				Object content = r.getContent();
				if (content instanceof String && ((String) content).length() > 0 &&
						nameElem != null) {
					nameElem.setLanguage((String) content);
				}
			}
			
			if (nameElem != null) {
				ruleset.setName(nameElem);
			}
			
			currentRule = repository.getResource(ruleID);
			
			if (currentRule == null) {
				throw new PreconditionRequiredException();
			}
			
			// Store the rule to the ruleset
			rulesetCol.storeResource(currentRule);
			
			List<Rule> rulesFromRuleset = new ArrayList<Rule>();
			
			String[] childResources = repository.listResources();
			for (int i = 0; i < childResources.length; i++) {
				nameElem = null;
				String currentRuleID = childResources[i];

				if (!COLLECTION_PROPERTIES_FILE.equals(currentRuleID)) {
					Rule rule = new Rule();
					rule.setId(currentRuleID);

					// Query the info.xml document 
					// Instantiate a XQuery service 
					service = (XQueryService) rulesetCol.getService("XQueryService", 
							"1.0"); 
					service.setProperty("indent", "yes"); 

					// Get the name
					String currentName = null;
					xQuery = "for $x in doc('" + currentRuleID + "')//rule//name " 
							+ "return data($x)"; 
					// Execute the query, print the result 
					result = service.query(xQuery); 
					it = result.getIterator(); 
					if (it.hasMoreResources()) { 
						Resource r = it.nextResource();
						Object content = r.getContent();
						if (content instanceof String && ((String) content).length() > 0) {
							currentName = (String) content;
						}
					}

					// Get the language
					String language = null;
					xQuery = "for $x in doc('" + currentRuleID + "')//rule//lang " 
							+ "return data($x)"; 
					// Execute the query, print the result 
					result = service.query(xQuery); 
					it = result.getIterator(); 
					if (it.hasMoreResources()) { 
						Resource r = it.nextResource();
						Object content = r.getContent();
						if (content instanceof String && ((String) content).length() > 0) {
							language = (String) content;
						}
					}


					if (currentName != null) {
						nameElem = new Name();
						nameElem.setName(currentName);
						nameElem.setLanguage(language != null ? language : "en");
						rule.setName(nameElem);
					}

					// Get the description
					String description = null;
					xQuery = "for $x in doc('" + currentRuleID + "')//rule//description " 
							+ "return data($x)"; 
					// Execute the query, print the result 
					result = service.query(xQuery); 
					it = result.getIterator(); 
					if (it.hasMoreResources()) { 
						Resource r = it.nextResource();
						Object content = r.getContent();
						if (content instanceof String && ((String) content).length() > 0) {
							description = (String) content;
							rule.setDescription(description);
						}
					}

					if (description != null) {
						rule.setDescription(description);
					}

					rulesFromRuleset.add(rule);
				}

			}
			
			ruleset.setRules(rulesFromRuleset);
					
			return ruleset;
		} finally {
			// Clean up!
			if(!collections.isEmpty()) {
				for (int i = 0; i < collections.size(); i++) {
					try { 
						if (collections.get(i) != null) {
							collections.get(i).close(); 
						}
					} catch(XMLDBException ex) {
						throw ex;
					}
				}
			}
		}
		
		
	}
}
