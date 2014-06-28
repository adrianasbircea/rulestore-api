package rule.ml.api.util;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;

import org.xmldb.api.base.ErrorCodes;
import org.xmldb.api.base.XMLDBException;

/**
 * Interface containing different constants.
 * 
 * @author Adriana
 */
public class ServiceUtil {
	/**
	 * Prefix for the resources URL.
	 */
	public static final String urlPrefix = "http://localhost:8081/ruleml/";
	
	/**
	 * Returns an exception for the error code from the given exception.
	 * 
	 * @param e The initial exception.
	 * 
	 * @return An exception depending on the error code from the given exception.
	 */
	public static Exception getException(XMLDBException e) {
		e.printStackTrace();
		int errorCode = e.errorCode;
		if (errorCode == ErrorCodes.VENDOR_ERROR ||
				errorCode == ErrorCodes.COLLECTION_CLOSED ||
				errorCode == ErrorCodes.UNKNOWN_ERROR) {
			// DB not working
			return new InternalServerErrorException();
		} else if (errorCode == ErrorCodes.NO_SUCH_COLLECTION ||
				errorCode == ErrorCodes.NO_SUCH_DATABASE ||
				errorCode == ErrorCodes.NO_SUCH_RESOURCE ||
				errorCode == ErrorCodes.NO_SUCH_SERVICE || 
				errorCode == ErrorCodes.INVALID_COLLECTION ||
				errorCode == ErrorCodes.INVALID_DATABASE ||
				errorCode == ErrorCodes.INVALID_RESOURCE) {
			// Resource not found or invalid
			return new NotFoundException();
		} else if (errorCode == ErrorCodes.INVALID_URI) {
			return new BadRequestException();
		} else {
			// Unknown error code, so show an error
			return e;
		}
	}
}
