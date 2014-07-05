package ruleml.api.repository;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class representing the name of a resource.
 * 
 * @author Adriana
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Name {
	/**
	 * The name value.
	 */ 
	private String name;
	/**
	 * The language.
	 */
	private String language;
	
	/**
	 * Constructor.
	 */
	public Name() {}
	/**
	 * Obtain the resource's name.
	 * 
	 * @return The name of the resource.
	 */
	@XmlElement(name="value")
	public String getName() {
		return name;
	}
	
	/**
	 * Set the resource's name.
	 * 
	 * @param name The new name of the resource.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Obtain the language.
	 * 
	 * @return the language.
	 */
	@XmlElement(name="language")
	public String getLanguage() {
		return language;
	}
	/**
	 * Set the language.
	 * 
	 * @param language the language to set.
	 */
	public void setLanguage(String language) {
		this.language = language;
	}
}
