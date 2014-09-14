package ruleml.api.util;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlRootElement;

import com.sun.xml.bind.marshaller.CharacterEscapeHandler;

@Provider
@Produces("application/xml")
public class RuleMarshaller implements MessageBodyWriter {

	@Override
	public long getSize(Object arg0, Class arg1, Type arg2, Annotation[] arg3,
			MediaType arg4) {
		return -1;
	}

	@Override
	public boolean isWriteable(Class type, Type arg1, Annotation[] arg2,
			MediaType arg3) {
		return type.isAnnotationPresent(XmlRootElement.class);
	}

	@Override
	public void writeTo(Object target, Class type, Type arg2, Annotation[] arg3,
			MediaType arg4, MultivaluedMap arg5, OutputStream outputStream)
					throws IOException, WebApplicationException {
		try {
			JAXBContext ctx = JAXBContext.newInstance(type);
			Marshaller marshaller = ctx.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			CharacterEscapeHandler escapeHandler = NoEscapeHandler.getInstance();
			marshaller.setProperty("com.sun.xml.bind.characterEscapeHandler", escapeHandler);
			marshaller.marshal(target, outputStream);
		} catch (JAXBException ex) {
			throw new RuntimeException(ex);
		}
	}

}
