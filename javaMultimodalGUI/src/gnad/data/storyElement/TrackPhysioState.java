package gnad.data.storyElement;

import java.util.Date;

import uk.ac.abdn.carados.OntologyHandler;


public class TrackPhysioState extends PhysiologyState{
	
	public void setState_start(float state_start) {
		if (instance!=null)
			instance.setOwnValue(StoryElementOntologyMapping.START_STATE_PROPERTY, new Float(state_start));
	}
	
	public void setState_end(float state_end) {
		if (instance!=null)
			instance.setOwnValue(StoryElementOntologyMapping.END_STATE_PROPERTY, new Float(state_end));
	}

	public float getState_start() {
		if (instance!=null)
			return instance.getOwnValueAsFloat(StoryElementOntologyMapping.START_STATE_PROPERTY);
		return 0;
	}
	public float getState_end() {
		if (instance!=null)
			return instance.getOwnValueAsFloat(StoryElementOntologyMapping.END_STATE_PROPERTY);
		return 0;
	}
	
	
	public TrackPhysioState(OntologyHandler kb, String physiologystate,
			Date startDate, Date endDate, float state_start, float state_end) {
		super(kb, physiologystate, startDate, endDate);
		setState_start(state_start);
		setState_end(state_end);
	}
	
	public void setPreviousPhysiologyState(PhysiologyState previous) {
		if (instance!=null && previous!=null && previous.instance!=null)
			instance.setOwnValue(StoryElementOntologyMapping.PREVIOUS_RELATION, previous.instance);// TODO Auto-generated method stub

	}
		
}
