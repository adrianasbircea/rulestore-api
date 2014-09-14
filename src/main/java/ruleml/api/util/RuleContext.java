package ruleml.api.util;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.sun.xml.bind.marshaller.CharacterEscapeHandler;

import ruleml.api.repository.Repository;

public class RuleContext
implements ContextResolver<JAXBContext> {
	private JAXBContext ctx;
	public RuleContext() {
		String packageName = "ruleml.api.repository";
		try {
			ctx = JAXBContext.newInstance(packageName);
			Marshaller marshaller = ctx.createMarshaller();
			
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			CharacterEscapeHandler escapeHandler = NoEscapeHandler.getInstance();
			marshaller.setProperty("com.sun.xml.bind.characterEscapeHandler", escapeHandler);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}
	public JAXBContext getContext(Class<?> type) {
		if (type.equals(Repository.class)) {
			return ctx;
		} else {
			return null;
		}
	}
}
