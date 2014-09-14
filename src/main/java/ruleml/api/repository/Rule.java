package ruleml.api.repository;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class representing a RuleML rule.
 * 
 * @author Adriana
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Rule {
	/**
	 * The ID of the rule. It also represents the rule's URI.
	 */
	private String id;

	/**
	 * Rule's name.
	 */
	private Name name;

	/**
	 * The description of the rule.
	 */
	private String description;
	
	/**
	 * The content of the rule
	 */
	private String xmlContent;

	public Rule() {
		
	}
	/**
	 * Constructor.
	 * 
	 * @param id 			Represents the ID or the URI of the rule.
	 * @param name 			Represent the name of the rule.
	 * @param description 	The rule's description.
	 */
	public Rule(String id, Name name, String description) {
		this.id = id;
		this.name = name;
		this.description = description;
	}

	/**
	 * Get the URI of the rule.
	 * 
	 * @return The rule's URI or ID.
	 */
	public String getId() {
		return id;
	}

	@XmlElement(name="ID")
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * Obtain the rule name.
	 * 
	 * @return The name of the rule.
	 */
	@XmlElement(name="name")
	public Name getName() {
		return name;
	}

	/**
	 * Get the rule's description.
	 * 
	 * @return The rule's description.
	 */
	@XmlElement(name="description")
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public void setName(Name name) {
		this.name = name;
	}
	
	
	/**
	 * The real rule.
	 * 
	 * @return The content.
	 */
	@XmlElement()
	public String getXmlContent() {
		System.out.println("xmlContent " + xmlContent);
		return xmlContent;
	}
	
	/**
	 * Set the real rule.
	 * 
	 * @param xmlContent The rule content.
	 */
	public void setXmlContent(String xmlContent) {
		this.xmlContent = xmlContent;
	}
}