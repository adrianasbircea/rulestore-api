package rule.ml.api.repository;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import rule.ml.api.rule.Rule;

/**
 * Class representing a RuleML ruleset.
 * 
 * @author Adriana
 */
@XmlRootElement(name="ruleset")
public class Ruleset {
	/**
	 * The ID of the ruleset. It also represents the ruleset's URI.
	 */
	private URI id;

	/**
	 * Ruleset's name.
	 */
	private String name;

	/**
	 * The description of the ruleset.
	 */
	private String description;
	/**
	 * The metadata of the ruleset. A ruleset can have at most one metadata.
	 */
	private String metadata;
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
	public Ruleset(URI id, String name, String description) {
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
	public URI getId() {
		return id;
	}

	/**
	 * Obtain the rule name.
	 * 
	 * @return The name of the rule.
	 */
	@XmlElement(name="name")
	public String getName() {
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
	
	/**
	 * Obtain the rule metadata.
	 * 
	 * @return The rule's metadata.
	 */
	public String getMetadata() {
		return metadata;
	}

	/**
	 * Set the new metadata of the rule.
	 * 
	 * @param metadata The new metadata object.
	 */
	public void setMetadata(String metadata) {
		this.metadata = metadata;
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
}