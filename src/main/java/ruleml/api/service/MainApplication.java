package ruleml.api.service;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import ruleml.api.exception.PreconditionsRequiredExceptionMapper;

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
		classes.add(PreconditionsRequiredExceptionMapper.class);
		
	}
	@Override
	public Set<Class<?>> getClasses() {
		return super.getClasses();
	}
}
