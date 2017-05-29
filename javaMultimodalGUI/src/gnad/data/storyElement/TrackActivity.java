package gnad.data.storyElement;

import java.util.Date;

import uk.ac.abdn.carados.OntologyHandler;

/**
 * Activities of displacement kind. Is dedicated to descent, mount, stop, go on etc. 
 * @author portet
 *
 */
public class TrackActivity extends Activity {



	public float getDenivelee() {
		if (instance!=null)
			return instance.getOwnValueAsFloat(StoryElementOntologyMapping.DENIVELEE_PROPERTY).intValue();
		return 0;
	}

	public void setDenivelee(float denivelee) {
		if (instance!=null)
			instance.setOwnValue(StoryElementOntologyMapping.DENIVELEE_PROPERTY, new Float(denivelee));
	}

	public float getAlt_end() {
		if (instance!=null)
			return instance.getOwnValueAsFloat(StoryElementOntologyMapping.END_ALTITUDE_PROPERTY);
		return 0;
	}

	public void setAlt_end(float alt_end) {
		if (instance!=null)
			instance.setOwnValue(StoryElementOntologyMapping.END_ALTITUDE_PROPERTY, new Float(alt_end));
	}

	public float getAlt_start() {
		if (instance!=null)
			return instance.getOwnValueAsFloat(StoryElementOntologyMapping.START_ALTITUDE_PROPERTY);
		return 0;
	}

	public void setAlt_start(float alt_start) {
		if (instance!=null)
			instance.setOwnValue(StoryElementOntologyMapping.START_ALTITUDE_PROPERTY, new Float(alt_start));
	}

	public float getAvg_speed() {
		if (instance!=null)
			return instance.getOwnValueAsFloat(StoryElementOntologyMapping.AVERAGE_SPEED_PROPERTY);
		return 0;
	}

	public void setAvg_speed(float avg_speed) {
		if (instance!=null)
			instance.setOwnValue(StoryElementOntologyMapping.AVERAGE_SPEED_PROPERTY, new Float(avg_speed));
	}



	public TrackActivity(OntologyHandler kb,String activity, Date startDate, Date endDate, float denivelee, float alt_end, float alt_start, float avg_speed) {
		super(kb, activity,  startDate,  endDate);		

		setDenivelee(denivelee);
		setAlt_start(alt_start);
		setAlt_end(alt_end);
		setAvg_speed(avg_speed);



	}

	public void setPreviousActivity(Activity previous) {
		if (instance!=null && previous!=null && previous.instance!=null)
			instance.setOwnValue(StoryElementOntologyMapping.PREVIOUS_RELATION, previous.instance);// TODO Auto-generated method stub

	}






	/**
	 * 
	 * @param trkseg
	 * @param i index of the segment
	 * @param ent
	 */
	/*
	private String choseVerbFromActivity(String activity, int i) {

		switch(activity){
		case "walk":
		case "ski":
			if (denivelee[i]>10)
				return "start the ascent";
				//return "mount";
			else if (denivelee[i]<-10)
				return "descend";
			else 
				return "go on";				
		case "stop":
		default:
				return "have a break";
		}
	}

	public void lexicalise(NLGFactory nlgFactory){

		SPhraseSpec p = nlgFactory.createClause();

		if (agent!=null){
			agent.lexicalise(nlgFactory);
			p.setSubject(agent.el);
		}
	    p.setVerb(this.verb);
	    if (this.goal!=null){
//	    	goal.lexicalise(nlgFactory);
	    	p.setObject(goal);
	    }

	    this.el=p;

	    if (this.startDate!=null){
	    	DocumentElement p1 = nlgFactory.createSentence("at "+ timeFormatter.format(this.startDate));
	    	p.addFrontModifier(p1);
	    	//this.el = nlgFactory.createCoordinatedPhrase(p1, p);
	    }




	}

	protected String getActivity(TrackSegment trkseg) {

		TrackPoint first = trkseg.getFirstPoint();
		TrackPoint last = trkseg.getLastPoint();
		double second = Distance.getSecondsDiff(first, last);
		double hours = second /(60*60);

		double meters = distance (last.getLat(),last.getLon(),first.getLat(),first.getLon());
		double km = meters/1000;

		double speed = (hours!=0)?km/hours:0;
		System.out.println(speed +" = " + km + " km in " + hours + " hours");
		if (speed>6) return "ski";
		else if (speed > 0.9) return "walk";
		else if (speed >= 0.0) return "stop";
		else return "stop";
	}


	public String toString(){
		String chaine = "Activity ";
		chaine+= (verb != null)?("\t verb " + verb.toString() + " "):"";
		chaine+= (goal!=null)?("\t goal " + goal.toString() + " "):"";
		chaine+= (time != null)?("\t at " + time.toString() + " "):"";
		chaine+= (startDate!=null)?("\t from " + startDate.toString() + " "):"";
		chaine+= (endDate!=null)?("\t to " + endDate.toString() + " "):"";

		chaine+=  (agent!=null)?("\t agent " +agent.toString() + " "):"";
		chaine+= (patient!=null)?("\t patient " + patient.toString() + " "):"";
		chaine+= (theme!=null)?("\t theme " + theme.toString() + " "):"";

		chaine+= (theme!=null)?("\t theme " + theme.toString() + " "):"";

					return chaine ;
	}

	 */
}


