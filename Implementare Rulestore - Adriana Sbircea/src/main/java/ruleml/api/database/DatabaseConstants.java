package ruleml.api.database;

public interface DatabaseConstants {
	/**
	 * XML Repository from the database.
	 */
	static final String XML_RPC_URI = "xmldb:exist://localhost:8080/exist/xmlrpc";
	/**
	 * Property name for name.
	 */
	static final String NAME_PROPERTY = "name";
	/**
	 *  Property name for language.
	 */
	static final String LANGUAGE_PROPERTY = "lang";
	/**
	 * Property name for description/metadata
	 */
	static final String DESCRIPTION_PROPERTY = "description";
	/**
	 * The root of the eXist collections.
	 */
	static final String ROOT_NAME = "/db/";

	/**
	 * Prefix for the repositories ID
	 */
	static final String REPOSITORY_NAME_PREFIX = "repos_";
	
}
