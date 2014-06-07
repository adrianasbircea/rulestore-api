package rule.ml.api.rule;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a RuleML rule.
 * 
 * @author Adriana
 */
public class Rule {
	/**
	 * The ID of the rule. It also represents the rule's URI.
	 */
	private URI id;

	/**
	 * Rule's name.
	 */
	private String name;

	/**
	 * The description of the rule.
	 */
	private String description;
	/**
	 * The metadata of the rule. A rule can have at most one metadata.
	 */
	private String metadata;
	/**
	 * Rule's conclusion. A rule can have at most one conclusion.
	 */
	private String conclusion; 
	/**
	 * An event.
	 */
	private String event;
	/**
	 * The actions associated with the rule. A rule can have 0 or more actions.
	 */
	private List<String> actions = new ArrayList<String>();
	/**
	 * The conditions associated with the rule. A rule can have 0 or more conditions.
	 */
	private List<String> conditions = new ArrayList<String>();

	/**
	 * Constructor.
	 * 
	 * @param id 			Represents the ID or the URI of the rule.
	 * @param name 			Represent the name of the rule.
	 * @param description 	The rule's description.
	 */
	public Rule(URI id, String name, String description) {
		this.id = id;
		this.name = name;
		this.description = description;
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
	 * Obtain the rule conclusion.
	 * 
	 * @return The conclusion of the rule.
	 */
	public String getConclusion() {
		return conclusion;
	}

	/**
	 * Set the new rule's conclusion.
	 * 
	 * @param conclusion The new conclusion to set.
	 */
	public void setConclusion(String conclusion) {
		this.conclusion = conclusion;
	}

	/**
	 * Get the rule's event.
	 * 
	 * @return The event associated with the rule.
	 */
	public String getEvent() {
		return event;
	}

	/**
	 * @param event the event to set
	 */
	public void setEvent(String event) {
		this.event = event;
	}

	/**
	 *  Obtain the list of actions. It can be an empty list if the rule has no actions.
	 *  
	 *  @return A list with rule's actions.
	 */
	public List<String> getActions() {
		return actions;
	}

	/**
	 * Set the list of actions. It can be empty if the rule has no actions.
	 * 
	 * @param actions A list with new actions. 
	 */
	public void setActions(List<String> actions) {
		if (actions != null) {
			this.actions = actions;
		}
	}

	/**
	 * Add a new action to the actions list.
	 * 
	 * @param newAction The action to add.
	 */
	public void addAction(String newAction) {
		if (newAction != null) {
			actions.add(newAction);
		}
	}
	
	/**
	 * Obtain the list of conditions. It can be an empty list if the rule has no conditions.
	 * 
	 * @return A list with conditions.
	 */
	public List<String> getConditions() {
		return conditions;
	}

	/**
	 * Set the conditions of the rule.
	 * 
	 * @param conditions The list of new conditions.
	 */
	public void setConditions(List<String> conditions) {
		if (conditions != null) {
			this.conditions = conditions;
		}
	}

	/**
	 * Add a new condition to the conditions list.
	 * 
	 * @param newCondition The conditions to add.
	 */
	public void addCondition(String newCondition) {
		if (newCondition != null) {
			conditions.add(newCondition);
		}
	}
	
	/**
	 * Get the URI of the rule.
	 * 
	 * @return The rule's URI or ID.
	 */
	public URI getId() {
		return id;
	}

	/**
	 * Obtain the rule name.
	 * 
	 * @return The name of the rule.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the rule's description.
	 * 
	 * @return The rule's description.
	 */
	public String getDescription() {
		return description;
	}
}