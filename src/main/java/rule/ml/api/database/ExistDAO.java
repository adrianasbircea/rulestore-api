package rule.ml.api.database;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.exist.xmldb.EXistResource;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.CollectionManagementService;
import org.xmldb.api.modules.XMLResource;

import rule.ml.api.repository.Repository;

public class ExistDAO {

	/**
	 * Repository ID generator. 
	 */
	private static int repository_id_count = 0;

	/**
	 * Creates a new repository.
	 * 
	 * @param storeID The ID of the store which will hold the new repository.
	 * 
	 * @return the newly created repository URI or <code>null</code> if the repository could not be created.
	 * 
	 * @throws FileNotFoundException When the store with the given ID could not found.
	 */
	public static String createNewRepository(String storeID) throws FileNotFoundException {
		String newRepositoryURI = null;
		Collection store = null;
		Collection newRepos = null;

		try {    
			DBConnection.initConnection();
			// Get the store with the given ID
			store = DatabaseManager.getCollection(DatabaseConstants.URI + DatabaseConstants.ROOT_NAME + storeID);
			if (store != null) {
				// Create a new collection which will represent the new repository
				String reposID = String.valueOf(repository_id_count);
				String reposName = DatabaseConstants.REPOSITORY_NAME_PREFIX + repository_id_count++;

				newRepos = getOrCreateCollection(DatabaseConstants.ROOT_NAME + storeID + "/" + reposID);
				if (newRepos != null) {
					newRepositoryURI = storeID + "/repositories/" + reposID;
					newRepos.setProperty(DatabaseConstants.NAME_PROPERTY, reposName);
				}
			} else {
				throw new FileNotFoundException("Store with ID " + storeID + " could not be found.");
			}
		} catch (XMLDBException e) {
			// Do nothing, the resource URI will be null
		} finally {
			// Don't forget to clean up!
			if(newRepos != null) {
				try { 
					newRepos.close(); 
				} catch(XMLDBException xe) {
					// Do nothing, the resource URI will be null
				}
			}

			if(store != null) {
				try { 
					store.close(); 
				} catch(XMLDBException xe) {
					// Do nothing, the resource URI will be null
				}
			}
		}

		return newRepositoryURI;
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

		Collection col = DatabaseManager.getCollection(DatabaseConstants.URI + collectionUri);
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
				Collection start = DatabaseManager.getCollection(DatabaseConstants.URI + path);
				if(start == null) {
					//collection does not exist, so create
					String parentPath = path.substring(0, path.lastIndexOf("/"
							));
					Collection parent = DatabaseManager.getCollection(DatabaseConstants.URI + 
							parentPath, "admin", "root");
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
	public static List<Repository> getRepositories(String storeID) throws Exception {
		List<Repository> repos = new ArrayList<Repository>();
		List<Collection> collections = new ArrayList<Collection>();
		try {    
			// Initialize the database connection
			DBConnection.initConnection();
			// Get the store collection
			Collection col = DatabaseManager.getCollection(DatabaseConstants.URI + "/db/" + storeID);
			collections.add(col);
			String[] childCollections = col.listChildCollections();
			for (int i = 0; i < childCollections.length; i++) {
				String reposID = childCollections[i];
				Collection currentRepos = col.getChildCollection(reposID);
				collections.add(currentRepos);
				String reposName = currentRepos.getProperty(DatabaseConstants.NAME_PROPERTY);
				String reposLang = currentRepos.getProperty(DatabaseConstants.LANGUAGE_PROPERTY);
				String reposDescription = currentRepos.getProperty(DatabaseConstants.DESCRIPTION_PROPERTY);
				Repository repository = new Repository();
				repository.setId(reposID);
				repository.setName(reposName);
				repository.setLanguage(reposLang);
				repository.setDescription(reposDescription);
				repos.add(repository);
			}
		} finally {
			// Clean up!
			if(!collections.isEmpty()) {
				for (int i = 0; i < collections.size(); i++) {
					try { 
						collections.get(i).close(); 
					} catch(XMLDBException ex) {
						throw ex;
					}
				}
			}
		}

		return repos;
	}

	/**
	 * Usually, when asking a resource for its name, it will return a string like /db/collection_name.
	 * This method removes the root path and returns the collection name.
	 * 
	 * @param path The path of the resource.
	 * 
	 * @return The resource relative path.
	 */
	private static String removeRootCollectionFromPath(String path) {
		if (path.indexOf(DatabaseConstants.ROOT_NAME) != -1) {
			return path.substring(path.indexOf(DatabaseConstants.ROOT_NAME) + DatabaseConstants.ROOT_NAME.length());
		}

		return path;
	}
}
