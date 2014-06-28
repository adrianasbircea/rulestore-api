package rule.ml.api.repository;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="store")
public class Store {

	List<Repository> repos = null;
	
	/**
	 * Constructor.
	 */
	public Store() {}
	
	/**
	 * Obtain the repositories.
	 * 
	 * @return
	 */
	@XmlElements(value = { 
			@XmlElement(name="repository", 
			type=Repository.class) })
	public List<Repository> getRepos() {
		return repos;
	}
	
	/**
	 * Set the new repositories.
	 * 
	 * @param repos The list containing the new repositories.
	 */
	public void setRepos(List<Repository> repos) {
		this.repos = repos;
	}
}
