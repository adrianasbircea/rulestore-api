package ruleml.api.repository;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class representing a RuleML ruleset.
 * 
 * @author Adriana
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Ruleset {
	/**
	 * The ID of the ruleset. It also represents the ruleset's URI.
	 */
	private String id;

	/**
	 * Repository's name.
	 */
	private Name name;
	/**
	 * The description of the ruleset.
	 */
	private String description;
	/**
	 * The rules from the ruleset. A ruleset can have 0 or more rules.
	 */
	private List<Rule> rules = new ArrayList<Rule>();
	
	/**
	 * Constructor.
	 */
	public Ruleset() {
	}
	
	/**
	 * Constructor.
	 * 
	 * @param id 			Represents the ID or the URI of the ruleset.
	 * @param name 			Represent the name of the ruleset.
	 * @param description 	The ruleset's description.
	 */
	public Ruleset(String id, Name name, String description) {
		this.id = id;
		this.name = name;
		this.description = description;
	}
	
	/**
	 * Get the URI of the rule.
	 * 
	 * @return The rule's URI or ID.
	 */
	@XmlElement(name="ID")
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Obtain the ruleset name.
	 * 
	 * @return The name of the ruleset.
	 */
	@XmlElement(name="name")
	public Name getName() {
		return name;
	}
	
	/**
	 * Set the repository's name.
	 * 
	 * @param name The new name of the repository.
	 */
	public void setName(Name name) {
		this.name = name;
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
	
	/**
	 * Obtain the list of rules. It can be an empty list if the ruleset has no rule.
	 * 
	 * @return A list with rules.
	 */
	public List<Rule> getRules() {
		return rules;
	}

	/**
	 * Add a new rule to the rules list.
	 * 
	 * @param newRule The rule to add.
	 */
	public void addRule(Rule newRule) {
		if (newRule != null) {
			rules.add(newRule);
		}
	}
	
	/**
	 * Set the rules of the ruleset.
	 * 
	 * @param rules The list of new rules.
	 */
	public void setRules(List<Rule> rules) {
		if (rules != null) {
			this.rules = rules;
		}
	}
	
	@Override
	public String toString() {
		return "ID: " + id;
	}
}