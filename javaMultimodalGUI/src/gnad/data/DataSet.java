package gnad.data;



import java.util.ArrayList;
import java.util.Date;

import org.w3c.dom.Document;

import uk.ac.abdn.carados.owl2.Owl2CaradosOntology;


import gnad.data.PersonRecord;


/**
 * @author François Portet
 */
public class DataSet{
	

	
	/** start time of data set **/
	Date startTime;
	
	/** end time of data set **/
	Date endTime;
	
	protected Owl2CaradosOntology kb;
	
	
	ArrayList<PersonRecord> persons;
	
	private Document osmDoc;

	public Document getOsmDoc() {
		return osmDoc;
	}

	public void setOsmDoc(Document osmDoc) {
		this.osmDoc = osmDoc;
	}

	
	
	DataSet() throws Exception{
		persons = new ArrayList<PersonRecord>();
		
	}	
	
	public Date getStartTime(){
		return startTime;
	}
	
	public Date getEndTime(){
		return endTime;
	}
	
	
	
	public ArrayList<PersonRecord> getPersons() {
		return persons;
	}


	public PersonRecord getPersonFromID(String personId){
		for(PersonRecord p : persons)
			if(p.getPersonId().matches(personId)){
				return p;
			}		
		return null;
	}

	
	public String toString(){
		String chaine =  "record : [" + startTime+ "," + endTime +"] containing records of \n";
		for(PersonRecord p : persons)
			chaine += "\t"+ p + "\n";
		return chaine;
		
		
	}

	public void setKB(Owl2CaradosOntology kb) {
		this.kb=kb;		
	}

	public Owl2CaradosOntology getKB() {
		return this.kb;
	}		
	
}
