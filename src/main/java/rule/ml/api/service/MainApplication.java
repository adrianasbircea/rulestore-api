package rule.ml.api.service;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import rule.ml.api.exception.BadRequestExceptionMapper;

/**
 * Main application. It creates the database connection and return the providers and
 * @Path annotated classes.
 * 
 * @author Adriana
 */
public class MainApplication extends Application {

	/**
	 * The classes that provides different responses.
	 */
	Set<Class<?>> classes = new HashSet<Class<?>>();
	
	public MainApplication() {
		// Start DB
		classes.add(RepositoryService.class);
		classes.add(BadRequestExceptionMapper.class);
		
	}
	@Override
	public Set<Class<?>> getClasses() {
		return super.getClasses();
	}
}
