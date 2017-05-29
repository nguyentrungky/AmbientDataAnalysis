package gnad.data.storyElement;

import java.util.Date;

public abstract class StoryEvent extends StoryElement{

	
	public Date getStartDate(){
		if (instance!=null)
			return instance.getOwnValueAsDate(StoryElementOntologyMapping.START_DATE_PROPERTY);
		return null;
	}

	
	public Date getEndDate(){
		if (instance!=null)
			return instance.getOwnValueAsDate(StoryElementOntologyMapping.END_DATE_PROPERTY);
		return null;
	}	
	
	public void setEndDate(Date endDate) {
		if (instance!=null)
			instance.setOwnValue(StoryElementOntologyMapping.END_DATE_PROPERTY,endDate);
		
	}

	public void setStartDate(Date startDate) {
		if (instance!=null)
			instance.setOwnValue(StoryElementOntologyMapping.START_DATE_PROPERTY,startDate);
		
	}
	
	
	public void addCharacters(Character c){
		if (instance!=null)
			instance.addOwnValue(StoryElementOntologyMapping.CHARACTER_RELATION, c.getInstance());
	}	
	
	
	public long getDurationInSeconds(){			
		return getEndDate().getTime()-getStartDate().getTime()/1000;
	}
	
	public void addStartElement(NaturalElement e) {
		if (instance!=null){
			instance.addOwnValue(StoryElementOntologyMapping.START_ELEMENT_RELATION, e.instance);
			
		}
		
	}
	public void addEndElement(NaturalElement e) {
		if (instance!=null)
			instance.addOwnValue(StoryElementOntologyMapping.END_ELEMENT_RELATION, e.instance);
		
	}
}
