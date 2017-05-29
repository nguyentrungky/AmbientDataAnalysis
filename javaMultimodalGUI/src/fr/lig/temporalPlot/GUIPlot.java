package fr.lig.temporalPlot;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.JPanel;

import fr.lig.GUIConfig;

/**
 * 
 * @author fportet
 *
 */
public abstract class GUIPlot extends JPanel{
	/** The parent panel */
	protected TemporalPanel parent;
	
	/** The unique identifier of the object (NOT CHECKED) */
	protected String ID;  
	
	/**
	 * create a new GUIplot object 
	 * @param ID - the unique identifier (NOT CHECKED)
	 * @param parent - the parent panel
	 */
	public GUIPlot (String ID,TemporalPanel parent){
		this.ID=ID;
		this.parent=parent;
		setLayout(new BorderLayout());		
	}
	
	/**
	 * @see JPanel#paintComponents(Graphics)
	 */
	public void paintComponent(Graphics g) {
		Graphics offgc;
		Image offscreen = null;
		
		// create the rectangle areas for label (i.e. Y axis title) and chart (curve) 
		Rectangle chartBox = g.getClipBounds();
		Rectangle labelBox = (Rectangle) chartBox.clone();
		labelBox.width = (int) (chartBox.width*GUIConfig.DEFAULT_LABEL_TRACE_OCCUPANCY);
		
		chartBox.width=chartBox.width-labelBox.width;
		chartBox.x = labelBox.width+1;
	
		//... update label
		// create the offscreen buffer and associated Graphics
		offscreen = createImage(labelBox.width, labelBox.height);
		offgc = offscreen.getGraphics();
		// CLIP should absolutely be set to communicate real dimensions to subclass methods
		offgc.setClip(0,0,labelBox.width, labelBox.height);
		
		// clear the exposed area
		offgc.setColor(parent.getBackground());
		offgc.fillRect(0, 0, labelBox.width, labelBox.height);
		offgc.setColor(getForeground());
		
		updateLabel(offgc);
		offgc.translate(-labelBox.x, -labelBox.y);
		g.drawImage(offscreen, labelBox.x, labelBox.y, this);
		

		//... update chart
		
		// create the offscreen buffer and associated Graphics
		offscreen = createImage(chartBox.width, chartBox.height);
		offgc = offscreen.getGraphics();
		offgc.setClip(0,0,chartBox.width, chartBox.height);
		
		// clear the exposed area
		offgc.setColor(getBackground());
		offgc.fillRect(0, 0, chartBox.width, chartBox.height);
		offgc.setColor(getForeground());
		
		// draw chart
		updateCanvas(offgc);
		offgc.translate(-chartBox.x, -chartBox.y);
		g.drawImage(offscreen, chartBox.x, chartBox.y, this);
	}

	/**
	 * display information about the plotted area  
	 * @param offgc - the graphics in with information are drawn
	 */
	protected abstract void updateLabel(Graphics offgc);

	/**
	 * Updates this GraphCanvas when required.
	 * 
	 * @param  -  the graphic context
	 */
	protected abstract void updateCanvas(Graphics offgc);
	
	/**
	 * @return the y-axis label of the trace
	 */
	protected abstract String getTitle();


	//... Getters and Setters
	public TemporalPanel getParent() {
		return parent;
	}

	public void setParent(TemporalPanel parent) {
		this.parent = parent;
	}

	public String getID() {
		return ID;
	}

	public void setID(String id) {
		ID = id;
	}
}