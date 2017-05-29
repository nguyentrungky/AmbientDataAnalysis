package gnad.data.storyElement;

import uk.ac.abdn.carados.OntologyInstanceHandler;

/**
 * 
 * @author portet
 *
 * root class of all the components of a story from a data analysis perspective
 * the current class hierarchy is clearly inspired by the one of the ontology
 * Each StoryElement instance contains an ontology instance which is the main "data repository"  
 */
public abstract class StoryElement {

	OntologyInstanceHandler instance; 
	
	
	public String getName(){
		if (instance!=null)
			return instance.getOwnValueAsString(StoryElementOntologyMapping.NAME_PROPERTY);
		return null;
	}
	
	public void setName(String name){
		if (instance!=null)
			instance.setOwnValue(StoryElementOntologyMapping.NAME_PROPERTY,name);		
	}
	
	public OntologyInstanceHandler getInstance(){
		return this.instance;
	}
	
	public void setInstance(OntologyInstanceHandler c) {
		this.instance=c;		
	}
}
