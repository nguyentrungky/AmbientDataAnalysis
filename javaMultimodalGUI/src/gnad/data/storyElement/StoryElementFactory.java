package gnad.data.storyElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;

import gnad.data.OSMNode;
import gnad.data.PersonRecord;
import uk.ac.abdn.carados.OntologyClassHandler;
import uk.ac.abdn.carados.OntologyHandler;
import uk.ac.abdn.carados.OntologyInstanceFilter;
import uk.ac.abdn.carados.OntologyInstanceHandler;
import gnad.data.storyElement.Character; 
import gnad.io.OSMOverpassQuery;

import com.fmt.gps.track.TrackSegment;


/**
 * 
 * @author portet
 *
 * The class in charge of creating StoryElement. Contains a lot of hand-crafted method
 * This should be completely revised
 *
 */
public class StoryElementFactory {

	
	public Character createCharacterFromPersonRecord(OntologyHandler kb, PersonRecord ent){
		Character car =new Character();
		OntologyInstanceHandler c = getCharacterFromPersonRecord( kb,  ent);
		if (c==null){
			OntologyClassHandler clas = kb.getOntologyClass(StoryElementOntologyMapping.HUMAN_CLASS);
			c = clas.createInstance();
			c.setOwnValue(StoryElementOntologyMapping.ID_PROPERTY,ent.getPersonId());
			
			c.setOwnValue(StoryElementOntologyMapping.AGE_PROPERTY,ent.getAge());
			c.setOwnValue(StoryElementOntologyMapping.GENDER_PROPERTY,ent.getGender());
		}		
		
		car.setInstance(c);
		return car;
	}
	
	/*private String instanceToString(OntologyInstanceHandler instance, OntologyHandler kb){
		
		String chaine=instance.toString() + "["; 
		Owl2CaradosInstance inst= ((Owl2CaradosInstance) instance);
		
		inst.
		
		for (String s : instance.getPropertyNames())
			chaine+= s + ","; 
		
		return chaine+"]";
	}*/
	

	public TrackActivity createActivityFromGPSSegment(OntologyHandler kb, TrackSegment trkseg, float denivelee, float avg_speed, PersonRecord ent){
		TrackActivity activity=null;
		
		String activity_class = this.classifyActivity(denivelee);		
		
		activity = new TrackActivity(kb,activity_class, trkseg.getFirstPoint().getTime(), trkseg.getLastPoint().getTime(),denivelee, trkseg.getFirstPoint().getElevation(), 
				trkseg.getLastPoint().getElevation(),
				 avg_speed);
		
		return activity;
	}
	
	public TrackActivity createActivityFromGPSSegment(OntologyHandler kb, TrackSegment trkseg, float denivelee, float avg_speed, PersonRecord ent, Activity previous){
		TrackActivity activity=createActivityFromGPSSegment( kb,  trkseg,  denivelee,  avg_speed,  ent);
		if (previous!=null)
			activity.setPreviousActivity(previous);
		
		return activity;
	}
	
	public TrackPhysioState createPhysiologyStateFromBioharness(OntologyHandler kb, PhysiologyState state, PhysiologyState previous_state){
		TrackPhysioState physioState = null;
		String physioState_class = this.classifyPhysioState(state);
		
		physioState = new TrackPhysioState(kb, physioState_class, state.getStartDate(), state.getEndDate()
											, state.getStart(), state.getEnd());
		System.out.println("physioState class: " + physioState_class + " start time: " + state.getStartDate() + " end time: " + state.getEndDate() );
		if (previous_state!=null)
			physioState.setPreviousPhysiologyState(previous_state);
		
		return physioState;
	}
	
	
	public List<NaturalElement> createNaturalElementFromOSM(OntologyHandler kb, Document osmDoc, double lat,double lon , double halfboudingbox) {
		List<NaturalElement> els = new ArrayList<NaturalElement>();
		List<OSMNode> osmNodesInVicinity;
	
			osmNodesInVicinity = OSMOverpassQuery.getNaturalNodesDoc(osmDoc,lat,lon,halfboudingbox);
			for ( OSMNode o : osmNodesInVicinity){
				System.out.println(o);
				els.add(createNaturalElementFromOSMNode(kb,  o));				
			}
			
		return els;
	}
	
	
	public NaturalElement createNaturalElementFromOSMNode(OntologyHandler kb, OSMNode o){
		NaturalElement el =new NaturalElement();
		Map<String, String> tags = o.getTags();
		OntologyInstanceHandler c=null;
		//OntologyInstanceHandler c = getNaturalElementFromName( kb, o. );
		if (c==null){
			String classe = tags.get("natural");
			if (classe ==null) return null;
			classe=classe.trim();
			classe = (classe.length() == 0) ? classe : classe.substring(0, 1).toUpperCase() + classe.substring(1);
			OntologyClassHandler clas = kb.getOntologyClass(classe);
			
			for(OntologyInstanceHandler i : clas.getInstances(false)){
				if (o.getId().compareToIgnoreCase(i.getOwnValueAsString(StoryElementOntologyMapping.ID_PROPERTY))==0){
					c=i;
					break;
				}
			}
			
			if (clas!=null && c==null){
				c = clas.createInstance();
				c.setOwnValue(StoryElementOntologyMapping.ID_PROPERTY,o.getId());
				String val= tags.get("name");
				if (val!=null)c.setOwnValue(StoryElementOntologyMapping.NAME_PROPERTY,val);
				val= tags.get("ele");
				if (val!=null)c.setOwnValue(StoryElementOntologyMapping.ALTITUDE_PROPERTY,val);
			}
		}		
		el.setInstance(c);
		return el;
	}
	
	private OntologyInstanceHandler getCharacterFromPersonRecord(OntologyHandler kb, PersonRecord ent){
		OntologyInstanceFilter filter = new OntologyInstanceFilter(){
			@Override
			public boolean canInclude(OntologyInstanceHandler instance) {
				return (instance!=null) 
						&&
						instance.isA(StoryElementOntologyMapping.HUMAN_CLASS);				
			}};
		
		
		for (OntologyInstanceHandler instance : kb.getAllInstances(filter)){
			for (String s : instance.getPropertyNames()){ System.out.println(s);}
			System.out.println(instance.getOwnValue("Name") + " "+ instance.toString());
			Object val = instance.getOwnValue(StoryElementOntologyMapping.ID_PROPERTY); 
			String value =instance.getOwnValueAsString(StoryElementOntologyMapping.ID_PROPERTY); 
			if (value !=null && value.equals(ent.getPersonId())) 
				return instance;
		}
		return null;
	}

	
	public static String classifyActivity(double denivelee) {

			if (denivelee>10)
				return  StoryElementOntologyMapping.MOUNT_ACTIVITY_CLASS;
				//return "mount";
			else if (denivelee < -10)
				return StoryElementOntologyMapping.DESCENT_ACTIVITY_CLASS;
			else 
				return StoryElementOntologyMapping.GO_ON_ACTIVITY_CLASS;
	}
	
	public static String classifyPhysioState(PhysiologyState state){
		if (state.getType().matches("basal")) 
			return StoryElementOntologyMapping.BASAL_CLASS;
		else if (state.getType().matches("digestion"))
			return StoryElementOntologyMapping.DIGESTION_CLASS;
		else if(state.getType().matches("exercise"))
			return StoryElementOntologyMapping.EXERCISE_CLASS;
		else if (state.getType().matches("drowsiness"))
			return StoryElementOntologyMapping.DROWSINESS_CLASS;
		else if (state.getType().matches("exertion"))
			return StoryElementOntologyMapping.EXERTION_CLASS;
		else
			return StoryElementOntologyMapping.RECOVERY_CLASS;
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
