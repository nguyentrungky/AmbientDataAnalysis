package fr.lig.displayableObject;

import java.awt.Color;
import java.util.Date;

import fr.lig.misc.ProtegeTools;

import fr.lig.GUIConfig;
import fr.lig.misc.ONTOConfig;

import kb.ProtegeInstance;

/**
 * Class used to represent Interval in signal processing module. 
 * It is not linked to any ontology class. 
 * @author fportet
 *
 */
public class GUIInterval extends GUIDisplayableTemporalObject implements Comparable<GUIInterval>{
	

	/** the message to display when clicked*/
	private String message;

	/**
	 * creates a new GUIInterval object from basic information 
	 * @param ID - the unique identifier of the channel (NOT CHECKED)
	 * @param start - the start date of the channel 
	 * @param end - the end date of the channel (inclusive)
	 */
	public GUIInterval(String ID, Date start, Date end, String message) {
		super(ID,start,end);
		this.message = message;
	}

	/**
	 * creates a new GUIInterval object from basic information 
	 * @param ID - the unique identifier of the channel (NOT CHECKED)
	 * @param start - the start date of the channel 
	 * @param end - the end date of the channel (inclusive)
	 * @param color - the colour to use to draw the object
	 */
	public GUIInterval(String ID, Date start, Date end, String message, Color color) {
		this(ID,start,end,message);
		super.setColor(color);
	}

/**
 * creates a new GUIInterval object from a protege instance
 * @param instance - the ProtegeInstance
 */
	public GUIInterval(ProtegeInstance instance) {
		this(instance.getClassName(),ProtegeTools.unShiftDate((Integer)instance.getValue(ONTOConfig.LATE_START_TIME_FEATURE))
				,ProtegeTools.unShiftDate((Integer)instance.getValue(ONTOConfig.EARLY_END_TIME_FEATURE)),
				ProtegeTools.toStringProtegeInstance(instance),
				GUIConfig.DEFAULT_INTERVAL_COLOR);
	}


	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(GUIInterval interval2) {
		// start first
		int difference = this.start.compareTo(interval2.start);

		// then try end time
		if (difference == 0){
			difference = this.end.compareTo(interval2.end);
		}

		// otherwise if even the message is the same, it is the same object
		if (difference == 0){
			difference = message.compareTo(interval2.message);
		}
		return difference;		
	}
	
	/**
	 * Checks if a GUI interval overlaps another
	 * @param interval2 - the interval to compare with
	 * @return true if overlap, false otherwise (including meets and finishes) 
	 */
	public boolean overlap(GUIInterval interval2){
		return overlap(interval2.start, interval2.end);
	}
	
	/**
	 * Checks if a GUI interval overlaps an interval defined by two dates
	 * @param startTime - beginning of the interval
	 * @param endTime - end of the interval
	 * @return true if overlap, false otherwise (including meets and finishes) 
	 */
	public boolean overlap(Date startTime, Date endTime){
		return (start.before(endTime) && startTime.before(end));
	}
	
	@Override
	public String toString(){
		return message + " " +start + " " + end;
	}

	//... Getters and Setters
	
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}