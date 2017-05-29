package gnad.data.storyElement;

import gnad.analysis.Component;
import gnad.data.storyElement.*;

/**
 * 
 * @author nguyentk
 *
 */
import java.util.ArrayList;
import java.util.Date;

import uk.ac.abdn.carados.OntologyClassHandler;
import uk.ac.abdn.carados.OntologyHandler;
import uk.ac.abdn.carados.OntologyInstanceFilter;
import uk.ac.abdn.carados.OntologyInstanceHandler;

public class PhysiologyState extends StoryEvent{
	
	/** name of state **/
	private String name;
	/** type of state **/
	private String type;
	/** start time **/
	private int start;
	/** end time **/
	private int end;
	
	private Date startDate;
	private Date endDate;
	
	/** confidence value **/
	private double cValue;
	
	private ArrayList<Component> components = new ArrayList<Component>();
	
	public PhysiologyState(OntologyHandler kb,String physiologystate, Date startDate, Date endDate){
		super();
		OntologyClassHandler clas = kb.getOntologyClass(StoryElementOntologyMapping.PHYSIOLOGY_EVENT_CLASS);
		this.instance = clas.createInstance();
		
		OntologyInstanceFilter filter = new OntologyInstanceFilter(){
			@Override
			public boolean canInclude(OntologyInstanceHandler instance) {
				return (instance!=null) 
						&&
						instance.isA(StoryElementOntologyMapping.PHYSIOLOGY_STATE_CLASS);				
			}};
		
		
		for (OntologyInstanceHandler instance : kb.getAllInstances(filter)){
			//System.out.println(instance.getOntologyClass().getName()+ "kykykky");
			if (instance.getOntologyClass().getName().compareToIgnoreCase(physiologystate.trim())==0){ 
				//System.out.println(instance.getOntologyClass().getName());
				this.instance.setOwnValue(StoryElementOntologyMapping.HAS_STATE_RELATION, instance);
				break;
			}
		}		
		
		super.setStartDate(startDate);
		super.setEndDate(endDate);
	}
	public PhysiologyState(){
		
	}
	
	public void setStartDate(Date startDate){
		this.startDate = startDate;
	}
	
	public void setEndDate(Date endDate){
		this.endDate = endDate;
	}
	
	public Date getStartDate(){
		return startDate;
	}
	
	public Date getEndDate(){
		return endDate;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return this.name;
	}
	
	public void setType(String type){
		this.type = type;
	}
	
	public String getType(){
		return this.type;
	}
	
	public void setStart(int start){
		this.start = start;
	}
	
	public int getStart(){
		return this.start;
	}
	
	public void setEnd(int end){
		this.end = end;
	}
	
	public int getEnd(){
		return this.end;
	}
	
	
	
	public double getCValue(){
		return cValue;
	}
	public ArrayList<Component> getComponents(){
		return this.components;
	}
	
	public void addToComponents(Component c){
		components.add(c);
	}
	
	public double computeCValue(){

		for(int i = 0; i < components.size(); i++){
			Component c = components.get(i);
			cValue = cValue + c.computeCValue();
		}
		return cValue;
	}
	
	public String toString() { 
		return start + " " + end + " state : " + type  + "\n";
	}
}
