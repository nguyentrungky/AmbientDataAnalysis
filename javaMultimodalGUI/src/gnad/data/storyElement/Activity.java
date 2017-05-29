package gnad.data.storyElement;

import java.util.Date;

import uk.ac.abdn.carados.OntologyClassHandler;
import uk.ac.abdn.carados.OntologyHandler;
import uk.ac.abdn.carados.OntologyInstanceFilter;
import uk.ac.abdn.carados.OntologyInstanceHandler;


public class Activity extends StoryEvent{

	public Activity(OntologyHandler kb,String activity, Date startDate, Date endDate) {
		super();
		OntologyClassHandler clas = kb.getOntologyClass(StoryElementOntologyMapping.ACTIVITY_EVENT_CLASS);
		this.instance = clas.createInstance();
		
		OntologyInstanceFilter filter = new OntologyInstanceFilter(){
			@Override
			public boolean canInclude(OntologyInstanceHandler instance) {
				return (instance!=null) 
						&&
						instance.isA(StoryElementOntologyMapping.MOVEMENT_CLASS);				
			}};
		
		
		for (OntologyInstanceHandler instance : kb.getAllInstances(filter)){
			//System.out.println(instance.getOntologyClass().getName()+ "kykykky");
			if (instance.getOntologyClass().getName().compareToIgnoreCase(activity.trim())==0){ 
				//System.out.println(instance.getOntologyClass().getName().compareToIgnoreCase(activity.trim()) + "kykykky");
				this.instance.setOwnValue(StoryElementOntologyMapping.HAS_ACTIVITY_RELATION, instance);
				break;
			}
		}		
		
		setStartDate(startDate);
		setEndDate(endDate);
		
	}

	
}
