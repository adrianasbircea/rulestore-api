package ruleml.api.repository;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class representing a RuleML repository.
 * 
 * @author Adriana
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Repository {
	/**
	 * The ID of the repository. It also represents the repository's URI.
	 */
	private String id;
	/**
	 * Repository's name.
	 */
	private Name name;
	/**
	 * The description of the repository.
	 */
	private String description;
	/**
	 * The rulesets from the repository. A repository can have 1 or more rulesets.
	 */
	private List<Ruleset> rulesets;
	/**
	 * The rules from the repository. A repository can have 0 or more rules.
	 */
	private List<Rule> rules = new ArrayList<Rule>();
	
	/**
	 * Constructor.
	 */
	public Repository() {}
	/**
	 * Constructor.
	 * 
	 * @param id 			Represents the ID or the URI of the repository.
	 * @param name 			Represent the name of the repository.
	 * @param description 	The repository's description.
	 * @param rulesets		The list with rulesets. It must contains at least on ruleset.
	 */
	public Repository(String id, Name name, String description, List<Ruleset> rulesets) {
		this.id = id;
		this.name = name;
		this.description = description;
		if (rulesets != null) {
			this.rulesets = rulesets;
		}
	}
	
	/**
	 * Set an ID for the current repository.
	 * 
	 * @param id The ID of the repository.
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Get the URI of the repository.
	 * 
	 * @return The repository's URI or ID.
	 */
	@XmlElement(name="ID")
	public String getId() {
		return id;
	}

	/**
	 * Obtain the repository name.
	 * 
	 * @return The name of the repository.
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
	 * Get the repository description.
	 * 
	 * @return The repository description.
	 */
	@XmlElement(name="description")
	public String getDescription() {
		return description;
	}

	/**
	 * Set the repository description.
	 *  
	 * @param description The new description of the repository.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Obtain the list of rulesets. It cannot be an empty list because the 
	 * repository must have at least one ruleset.
	 * 
	 * @return A copy of the list with rulesets.
	 */
	@XmlElements(value = { 
			@XmlElement(name="ruleset", 
			type=Ruleset.class) })
	public List<Ruleset> getRulesets() {
		return rulesets;
	}

	/**
	 * Set the rulesets of the repository.
	 * 
	 * @param rulesets The list of new {@link Ruleset}s.
	 */
	public void setRulesets(List<Ruleset> rulesets) {
		if (rulesets != null && !rulesets.isEmpty()) {
			this.rulesets = rulesets;
		}
	}

	/**
	 * Obtain the list of rules. It can be an empty list if the repository has no rule.
	 * 
	 * @return A list with rules.
	 */
//	@XmlElement(name="rules")
	public List<Rule> getRules() {
		return rules;
	}

	/**
	 * Set the rules of the repository.
	 * 
	 * @param rules The list of new rules.
	 */
	public void setRules(List<Rule> rules) {
		if (rules != null && !rules.isEmpty()) {
			this.rules = rules;
		}
	}
	
	/**
	 * Add a new ruleset to the rulesets list.
	 * 
	 * @param newRuleset The ruleset to add.
	 */
	public void addRuleset(Ruleset newRuleset) {
		if (newRuleset != null) {
			rulesets.add(newRuleset);
		}
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "ID: " + id + " rulesets: " + rulesets;
	}
}
