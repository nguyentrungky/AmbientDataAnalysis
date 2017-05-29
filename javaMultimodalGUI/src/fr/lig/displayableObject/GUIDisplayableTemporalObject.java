package fr.lig.displayableObject;

import java.awt.Color;
import java.util.Date;

import fr.lig.GUIConfig;

/**
 * super class for displayable object
 * @author fportet
 *
 */
public abstract class GUIDisplayableTemporalObject {

	/** start of the temporal object*/
	protected Date start;

	/** end of the temporal object*/
	protected Date end;

	/** the colour used to display the object*/
	protected Color color; 

	/** The unique identifier of the object (NOT CHECKED) */
	protected String ID;  

	/** The unique identifier of the plot in which this object is plotted*/
	protected String plotID;

	/**
	 * creates a new displayable object from basic information 
	 * @param ID - the unique identifier of the channel (NOT CHECKED)
	 * @param start - the start date of the channel 
	 * @param end - the end date of the channel (inclusive)
	 */
	public GUIDisplayableTemporalObject(String ID, Date start, Date end) {
		this.ID = ID;
		this.start=start;
		this.end=end;
		this.color = GUIConfig.DEFAULT_TRACE_COLOR[0];
	}

	/**
	 * creates a new displayable object from basic information 
	 * @param ID - the unique identifier of the channel (NOT CHECKED)
	 * @param start - the start date of the channel 
	 * @param end - the end date of the channel (inclusive)
	 * @param color - the colour to use to draw the object
	 */
	public GUIDisplayableTemporalObject(String ID, Date start, Date end, Color color) {
		this(ID,start,end);
		this.color=color;
	}

	// ...Getters and Setters

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public String getID() {
		return ID;
	}

	public void setID(String id) {
		ID = id;
	}

	public String getPlotID() {
		return plotID;
	}

	public void setPlotID(String plotID) {
		this.plotID = plotID;
	}  
}