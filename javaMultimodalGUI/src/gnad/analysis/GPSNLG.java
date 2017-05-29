package gnad.analysis;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import gnad.data.storyElement.PhysiologyState;
import gnad.data.storyElement.StoryElementOntologyMapping;
import simplenlg.framework.NLGElement;
import simplenlg.framework.NLGFactory;
import simplenlg.lexicon.Lexicon;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.realiser.english.Realiser;
import uk.ac.abdn.carados.OntologyClassHandler;
import uk.ac.abdn.carados.OntologyException;
import uk.ac.abdn.carados.OntologyInstanceHandler;
import uk.ac.abdn.carados.owl2.Owl2CaradosOntology;



/**
 * A simple class that implements a very crude NLG system. 
 * The classe pulls out any activity Event from the ontology, lexicalise them and realise the text.
 * 
 * @author portet
 *
 */
public class GPSNLG {
	static Map<String,Entity> entities = new HashMap<String,Entity>();
	static List<Activity> events = new ArrayList<Activity>();
	static List<PhysiologyState> physio_events = new ArrayList<PhysiologyState>();
	
	
	SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");
	public static Date endDate=null;
	
	public GPSNLG(){
	}
	
	private abstract class DiscourseElement{
		NLGElement el;
		public abstract void lexicalise(NLGFactory nlgFactory);
	}

	private class Entity extends DiscourseElement implements Comparable{

		String name;
		String gender;
		int age;
		Boolean living;
		Boolean introduced;
		
		
		public Entity(OntologyInstanceHandler instance) {
			super();
			living = instance.isA(StoryElementOntologyMapping.LIVING_CLASS);
			introduced= false;
			
			if (living){
				name = instance.getOwnValueAsString(StoryElementOntologyMapping.ID_PROPERTY);
				gender = instance.getOwnValueAsString(StoryElementOntologyMapping.GENDER_PROPERTY);
//			age = instance.getOwnValueAsFloat(StoryElementOntologyMapping.AGE_PROPERTY).intValue();
			}
			else{
				name = instance.getOwnValueAsString(StoryElementOntologyMapping.NAME_PROPERTY);
			}
		}


		public String toString(){
			String chaine = "Entity ";
			chaine+= (name!=null)?name.toString() + " ":"";
			if (living)
				chaine+= (gender!=null)?gender.toString()+ " ":"";
				chaine+= (age!=0)?age+ " ":"";					
			return chaine ;
		}

		@Override
		public void lexicalise(NLGFactory nlgFactory) {
			NPPhraseSpec p = nlgFactory.createNounPhrase();
			if (!introduced){
				p.setNoun(this.name);
				introduced=true;
			}
			else if (gender.startsWith("f"))
				p.setNoun("she");
			else if (gender.startsWith("m"))
				p.setNoun("he");
			else
				p.setNoun("it");
		    this.el=p;
			
		}
		 public int compareTo(Object o){
			 if (o instanceof Entity) 
				 return (((Entity) o).name.compareTo(this.name) );
			return -1;
			 
		 }
		 
		 public boolean equals(Object e){
			 if (e instanceof Entity) 
				 return ((Entity) e).name.equals(this.name);
			 return false;
		 }
	}

	private class PhysiologyState extends DiscourseElement implements Comparable{
		Entity agent;
		Entity patient;
		Entity theme;
		Entity start_location;
		Entity end_location;
		String time;
		Date startDate;
		Date endDate;
		String verb;
		String goal;
		
		public PhysiologyState(OntologyInstanceHandler instance){
			
			Entity ent=null;
			verb = choseVerbFromPhysiologyState(instance);
			
			if (instance.hasProperty(StoryElementOntologyMapping.CHARACTER_RELATION)){
				Collection<Object> val = instance.getOwnValueAsCollection(StoryElementOntologyMapping.CHARACTER_RELATION);
				if (val!=null && val.size()>0){
					ent = new Entity((OntologyInstanceHandler)(val.toArray()[0]));
					if (GPSNLG.entities.containsValue(ent)){
						agent =GPSNLG.entities.get(ent.name);
					}
					else{
					GPSNLG.entities.put(ent.name,ent);
					agent = ent;
					}
				}
			}
			
			/*if (instance.hasProperty(StoryElementOntologyMapping.START_ELEMENT_RELATION) && (instance.isA(StoryElementOntologyMapping.BASAL_CLASS)
					|| instance.isA(StoryElementOntologyMapping.EXERCISE_CLASS)
					|| instance.isA(StoryElementOntologyMapping.EXERTION_CLASS)
					|| instance.isA(StoryElementOntologyMapping.DIGESTION_CLASS)
					|| instance.isA(StoryElementOntologyMapping.DROWSINESS_CLASS)
					|| instance.isA(StoryElementOntologyMapping.RECOVERY_CLASS)
					)){
				Collection<Object> val = instance.getOwnValueAsCollection(StoryElementOntologyMapping.START_ELEMENT_RELATION);
				if (val!=null && val.size()>0){
					for ( Object o : val){
						OntologyInstanceHandler i = (OntologyInstanceHandler)(o);
						if (i.hasProperty(StoryElementOntologyMapping.NAME_PROPERTY) && i.getOwnValueAsString(StoryElementOntologyMapping.NAME_PROPERTY)!=null){
						ent = new Entity(i);
						if (GPSNLG.entities.containsValue(ent)){
							start_location =GPSNLG.entities.get(ent.name);
						}else{
						GPSNLG.entities.put(ent.name,ent);
						start_location = ent;
						}
						break;
						}
					}
			    }
			}
			
			if (instance.hasProperty(StoryElementOntologyMapping.END_ELEMENT_RELATION)){
				Collection<Object> val = instance.getOwnValueAsCollection(StoryElementOntologyMapping.END_ELEMENT_RELATION);
				if (val!=null && val.size()>0){
					for ( Object o : val){
						OntologyInstanceHandler i = (OntologyInstanceHandler)(o);
						if (i.hasProperty(StoryElementOntologyMapping.NAME_PROPERTY) && i.getOwnValueAsString(StoryElementOntologyMapping.NAME_PROPERTY)!=null){
						ent = new Entity(i);
						if (GPSNLG.entities.containsValue(ent)){
							end_location =GPSNLG.entities.get(ent.name);
						}
						else{
						GPSNLG.entities.put(ent.name,ent);
						end_location = ent;
						}
						break;
						}
					}
			    }
			}*/
			
			startDate = instance.getOwnValueAsDate(StoryElementOntologyMapping.START_DATE_PROPERTY);
			endDate = instance.getOwnValueAsDate(StoryElementOntologyMapping.END_DATE_PROPERTY);
			//System.out.println(startDate + " start test");
			//System.out.println(endDate  + " end test");
		}
		
		private String choseVerbFromPhysiologyState(OntologyInstanceHandler instance) {
			String physiologyMode= instance.getOwnValueAsString(StoryElementOntologyMapping.PHYSIOLOGY_MODE_PROPERTY);
			if(physiologyMode != null)
				System.out.println(physiologyMode + " state detection");
			else System.out.println("State detection is null");
			OntologyInstanceHandler physioState = (OntologyInstanceHandler)instance.getOwnValue(StoryElementOntologyMapping.HAS_STATE_RELATION);
			String state = null;
			switch(physiologyMode){
			case "exercise": 
				if (physioState.isA(StoryElementOntologyMapping.EXERCISE_CLASS))
					state = "exercise";
				break;
			case "exertion": 
				if (physioState.isA(StoryElementOntologyMapping.EXERTION_CLASS))
					state = "exertion";
				break;
			case "basal": 
				if (physioState.isA(StoryElementOntologyMapping.BASAL_CLASS))					
					state = "basal";
				break;
			case "digestion": 
				if (physioState.isA(StoryElementOntologyMapping.DIGESTION_CLASS))
					state = "digestion";
				break;
			case "recovery": 
				if (physioState.isA(StoryElementOntologyMapping.RECOVERY_CLASS))
					state = "recovery";
				break;
			case "drowsiness": 
				if (physioState.isA(StoryElementOntologyMapping.DROWSINESS_CLASS))
					state = "drowsiness";
				break;
			default:
				state = "nothing";
				break;
			}
			return state;
		}
		
		public void lexicalise(NLGFactory nlgFactory){
			
			SPhraseSpec p = nlgFactory.createClause();
			
			if (agent!=null){
				agent.lexicalise(nlgFactory);
				p.setSubject(agent.el);
			}
		    p.setVerb(this.verb);
		    if (this.goal!=null){
//		    	goal.lexicalise(nlgFactory);
		    	p.setObject(goal);
		    }
		    
		    this.el=p;
		    
		    
		    if (this.start_location!=null){
		    	NLGElement p1 = nlgFactory.createStringElement("from "+ this.start_location.name);
		    	p.addFrontModifier(p1);
		    	
		    }
		    else if (this.startDate!=null){
		    	NLGElement p1 = nlgFactory.createStringElement("at "+ timeFormatter.format(this.startDate));
		    	p.addFrontModifier(p1);
		    	//this.el = nlgFactory.createCoordinatedPhrase(p1, p);
		    }
		    
		    if (this.end_location!=null){
		    	NLGElement p1 = nlgFactory.createStringElement("to "+ this.end_location.name);
		    	p.addPostModifier(p1);
		    	
		    }
		    
		    if (this.startDate!=null && this.endDate!=null ){
		    	
		    	NLGElement p1 = nlgFactory.createStringElement("during "+ GPSNLG.formatDifferenceBetweenDate(this.endDate,startDate));
		    	p.addPostModifier(p1);
		    }
		    
		    if (this.endDate!=null && (GPSNLG.endDate==null || GPSNLG.endDate.before(this.endDate)))
		    	GPSNLG.endDate=this.endDate;
		    
		    
		    
		}
		
		/*public String startSentence(String where){
			return " Departing from " + where + ".";
		}
		
		public  String endSentence(String where, Date d){
		
			return " Arrived back to " + where + " at " +   timeFormatter.format(d) + ".";
		}*/
		
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
		
		
		@Override
		public int compareTo(Object o) {
			if (o instanceof PhysiologyState){
				if (((PhysiologyState) o).startDate!= null && startDate != null)
				return (int) (startDate.getTime() -((PhysiologyState) o).startDate.getTime());
			}
				
			return 0;
		}
		
		/*private String realiseAll(List<? extends DiscourseElement> discourse) {
			Lexicon lexicon = Lexicon.getDefaultLexicon();
			NLGFactory nlgFactory = new NLGFactory(lexicon);
			Realiser realiser = new Realiser(lexicon);
			Date d=null;
			String s ="";
			s+= this.startSentence("Chamrousse");
			for(DiscourseElement e : discourse){
				e.lexicalise(nlgFactory);
				s+=" "+ realiser.realiseSentence(e.el);			
			}		
			s+= this.endSentence("Chamrousse",this.endDate);
			return s;

		}
		
		/*public String realises(String fileName)  {
			String text =null;
			Owl2CaradosOntology kb = new Owl2CaradosOntology();
			try {
				kb.loadOntology(URI.create(fileName));
				
				
				OntologyClassHandler classePhy = kb.getOntologyClass(StoryElementOntologyMapping.PHYSIOLOGY_EVENT_CLASS);
				
				
				PhysiologyState[] st = new PhysiologyState[classePhy.getInstances(true).size()];
				
				int l = 0;
				

				for (OntologyInstanceHandler i : classePhy.getInstances(true)){
					
					st[l++]= new PhysiologyState(i);
					//GPSNLG.events.add(new Activity(i));
				}
				
				
				Arrays.sort(st);
				GPSNLG.physio_events = Arrays.asList(st); 
				text = this.realiseAll(GPSNLG.physio_events);
				
			} catch (OntologyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return text;
		}*/
	}
	private class Activity extends DiscourseElement implements Comparable{

		Entity agent;
		Entity patient;
		Entity theme;
		Entity start_location;
		Entity end_location;
		String time;
		Date startDate;
		Date endDate;
		String verb;
		String goal;
		
		
		public Activity(OntologyInstanceHandler instance){
			
			Entity ent=null;
			verb = choseVerbFromActivity(instance);
			
			if (instance.hasProperty(StoryElementOntologyMapping.CHARACTER_RELATION)){
				Collection<Object> val = instance.getOwnValueAsCollection(StoryElementOntologyMapping.CHARACTER_RELATION);
				if (val!=null && val.size()>0){
					ent = new Entity((OntologyInstanceHandler)(val.toArray()[0]));
					if (GPSNLG.entities.containsValue(ent)){
						agent =GPSNLG.entities.get(ent.name);
					}
					else{
					GPSNLG.entities.put(ent.name,ent);
					agent = ent;
					}
				}
			}
			
			if (instance.hasProperty(StoryElementOntologyMapping.START_ELEMENT_RELATION) && (instance.isA(StoryElementOntologyMapping.MOUNT_ACTIVITY_CLASS)
					|| instance.isA(StoryElementOntologyMapping.DESCENT_ACTIVITY_CLASS)
					|| instance.isA(StoryElementOntologyMapping.GO_ON_ACTIVITY_CLASS)
					)){
				Collection<Object> val = instance.getOwnValueAsCollection(StoryElementOntologyMapping.START_ELEMENT_RELATION);
				if (val!=null && val.size()>0){
					for ( Object o : val){
						OntologyInstanceHandler i = (OntologyInstanceHandler)(o);
						if (i.hasProperty(StoryElementOntologyMapping.NAME_PROPERTY) && i.getOwnValueAsString(StoryElementOntologyMapping.NAME_PROPERTY)!=null){
						ent = new Entity(i);
						if (GPSNLG.entities.containsValue(ent)){
							start_location =GPSNLG.entities.get(ent.name);
						}else{
						GPSNLG.entities.put(ent.name,ent);
						start_location = ent;
						}
						break;
						}
					}
			    }
			}
			
			if (instance.hasProperty(StoryElementOntologyMapping.END_ELEMENT_RELATION)){
				Collection<Object> val = instance.getOwnValueAsCollection(StoryElementOntologyMapping.END_ELEMENT_RELATION);
				if (val!=null && val.size()>0){
					for ( Object o : val){
						OntologyInstanceHandler i = (OntologyInstanceHandler)(o);
						if (i.hasProperty(StoryElementOntologyMapping.NAME_PROPERTY) && i.getOwnValueAsString(StoryElementOntologyMapping.NAME_PROPERTY)!=null){
						ent = new Entity(i);
						if (GPSNLG.entities.containsValue(ent)){
							end_location =GPSNLG.entities.get(ent.name);
						}
						else{
						GPSNLG.entities.put(ent.name,ent);
						end_location = ent;
						}
						break;
						}
					}
			    }
			}
			
			startDate = instance.getOwnValueAsDate(StoryElementOntologyMapping.START_DATE_PROPERTY);
			endDate = instance.getOwnValueAsDate(StoryElementOntologyMapping.END_DATE_PROPERTY);
			
		}



		private String choseVerbFromActivity(OntologyInstanceHandler instance) {
			String travelMode= instance.getOwnValueAsString(StoryElementOntologyMapping.TRAVEL_MODE_PROPERTY);
			 OntologyInstanceHandler activity = (OntologyInstanceHandler)instance.getOwnValue(StoryElementOntologyMapping.HAS_ACTIVITY_RELATION);
			switch(travelMode){
			case "walk":
			case "ski":
				if (activity.isA(StoryElementOntologyMapping.MOUNT_ACTIVITY_CLASS))					
					return "mount";
				else if (activity.isA(StoryElementOntologyMapping.DESCENT_ACTIVITY_CLASS))
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
//		    	goal.lexicalise(nlgFactory);
		    	p.setObject(goal);
		    }
		    
		    this.el=p;
		    
		    
		    if (this.start_location!=null){
		    	NLGElement p1 = nlgFactory.createStringElement("from "+ this.start_location.name);
		    	p.addFrontModifier(p1);
		    	
		    }
		    else if (this.startDate!=null){
		    	NLGElement p1 = nlgFactory.createStringElement("at "+ timeFormatter.format(this.startDate));
		    	p.addFrontModifier(p1);
		    	//this.el = nlgFactory.createCoordinatedPhrase(p1, p);
		    }
		    
		    if (this.end_location!=null){
		    	NLGElement p1 = nlgFactory.createStringElement("to "+ this.end_location.name);
		    	p.addPostModifier(p1);
		    	
		    }
		    
		    if (this.startDate!=null && this.endDate!=null ){
		    	
		    	NLGElement p1 = nlgFactory.createStringElement("during "+ GPSNLG.formatDifferenceBetweenDate(this.endDate,startDate));
		    	p.addPostModifier(p1);
		    }
		    
		    if (this.endDate!=null && (GPSNLG.endDate==null || GPSNLG.endDate.before(this.endDate)))
		    	GPSNLG.endDate=this.endDate;
		    
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



		@Override
		public int compareTo(Object o) {
			if (o instanceof Activity){
				return (int) (startDate.getTime() -((Activity) o).startDate.getTime());
			}
				
			return 0;
		}


	}

	
	public static String formatDifferenceBetweenDate(Date d1,Date d2){
		String s= "";
		double diff;
		if (d1!=null || d2!=null){
			 diff = Math.abs(d2.getTime() - d1.getTime());
			 long d = ((long) diff)/1000;
			 long hours =d/(60*60);
			 long minutes = (d-60*60*hours)/60;
			 if((hours == 0) && (minutes!=0))
				 s = minutes + " minutes";
			 else if ((hours != 0) && (minutes!=0))
				 s = hours + " hour " + minutes + " minutes";
		}
		
		return s;
	}

	public String startSentence(String where){
		return " Departing from " + where + ".";
	}
	
	public  String endSentence(String where, Date d){
		
		return " Arrived back to " + where + " at " +   timeFormatter.format(d) + ".";
	}


	private String realiseAll(List<? extends DiscourseElement> discourse) {
		Lexicon lexicon = Lexicon.getDefaultLexicon();
		NLGFactory nlgFactory = new NLGFactory(lexicon);
		Realiser realiser = new Realiser(lexicon);
		Date d=null;
		String s ="";
		s+= this.startSentence("");
		for(DiscourseElement e : discourse){
			e.lexicalise(nlgFactory);
			s+=" "+ realiser.realiseSentence(e.el);			
		}		
		s+= this.endSentence("",this.endDate);
		return s;

	}
	
	public String realise(String fileName)  {
		String text =null;
		Owl2CaradosOntology kb = new Owl2CaradosOntology();
		try {
			kb.loadOntology(URI.create(fileName));
			
			OntologyClassHandler classe = kb.getOntologyClass(StoryElementOntologyMapping.ACTIVITY_EVENT_CLASS);
			
			Activity[] a = new Activity[classe.getInstances(true).size()];
			
			int k= 0;
			
			for (OntologyInstanceHandler i : classe.getInstances(true)){
				a[k++]= new Activity(i);
				
				//GPSNLG.events.add(new Activity(i));
			}
			Arrays.sort(a);
			
			/*for(int i = 0; i < a.length; i++)
			System.out.println(a[i]);*/
			
			GPSNLG.events = Arrays.asList(a); 
			text = this.realiseAll(GPSNLG.events);
			
			
			OntologyClassHandler classePhy = kb.getOntologyClass(StoryElementOntologyMapping.PHYSIOLOGY_EVENT_CLASS);		
			PhysiologyState[] st = new PhysiologyState[classePhy.getInstances(true).size()];
			
			int l = 0;
			for (OntologyInstanceHandler i : classePhy.getInstances(true)){
				
				st[l++]= new PhysiologyState(i);
				//GPSNLG.events.add(new Activity(i));
			}
			
			
			Arrays.sort(st);
			/*for(int i = 0; i < st.length; i++)
				System.out.println(st[i]);*/
			
			GPSNLG.physio_events = Arrays.asList(st); 
			text = text + " \n" +  this.realiseAll(GPSNLG.physio_events);
			
		} catch (OntologyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return text;
	}

}
