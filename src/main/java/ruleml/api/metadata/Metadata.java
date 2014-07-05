package ruleml.api.metadata;

import javax.xml.bind.annotation.XmlElement;


/**
 * Class representing a metadata.
 * 
 * @author Adriana
 */
public class Metadata {
	/**
	 * The metadata text.
	 */
	private String text = "metadata";
	public Metadata() {
		
	}
	@XmlElement(name="text")
	public String getText() {
		return text;
	}
}
