package fr.lig.temporalPlot;

import fr.lig.displayableObject.GUIChannel;
import fr.lig.displayableObject.GUIInterval;

import fr.lig.GUIConfig;
import fr.lig.misc.GeneralConfig;
import fr.lig.misc.ONTOConfig;
import fr.lig.data.BTChannel;

import fr.lig.data.BTData;
import kb.ProtegeInstance;


import java.awt.Adjustable;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JTextArea;




/**
 * This class manages all the temporal sub-components (i.e. charts, traces, time bar)
 * 
 * @author fportet
 *
 */
public class TemporalPanel extends JPanel implements AdjustmentListener {

	/** The serial version UID */
	private static final long serialVersionUID = 1L;

	/** the date at which the traces start*/
	private Date firstTimeValue;

	/** Date of the first visible sample in the screen*/ 
	private Date firstVisibleSampleDate;

	/** The data to display */
	private BTData data;

	/** Panel with the traces */
	private List<TracePanel> traces;

	/** Panel with the labels */
	private List<IntervalPanel> intervalTraces;

	/** ScrollBar for the canvas */
	private JScrollBar scroll;

	/** the current length of a trace in second*/
	private int traceLength;

	/** the current length of a tick in second*/
	private int tickLength;

	/** number of pixels for a second */
	private double pixelsPerSecond;

	/** the unique timeBar of this temporal Panel*/
	private TimeBar timeBar;

	/** The frame used to display information about the data designed by the mouse click */
	private JFrame popup;

	/** text Area used to display additional information*/
	private JTextArea messageArea;

	/**
	 * Creates a new temporal Panel from the data given and the dimension 
	 * @param data - the BTData
	 * @param dimension - setting of the dimension
	 */
	public TemporalPanel(BTData data, Dimension dimension) {

		super();

		// update fields
		this.data = data;
		firstTimeValue = data.getStartTime();
		firstVisibleSampleDate = firstTimeValue;
		intervalTraces = new ArrayList<IntervalPanel>();
		traces = new ArrayList<TracePanel>();
		traceLength =  (int) Math.min(GUIConfig.DEFAULT_TRACE_LENGTH, (data.getEndTime().getTime()-data.getStartTime().getTime())/1000.0);
		updateTick();
		popup = new JFrame();
		messageArea = new JTextArea();
		popup.add(messageArea);

		//... Set panel characteristics
		setPreferredSize(dimension);
		setSize(dimension);
		setBackground(Color.WHITE);
		GridBagLayout gridbag = new GridBagLayout();
		setLayout(gridbag);
		setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		GridBagConstraints c = new GridBagConstraints();


		// ... Now creates the components and add them

		// creates an empty row to set the GridBagLayout dimension
		// don't know how to do it otherwise...
		addEmptyRow(gridbag,c);
		int row=1;
		// add traces
		c.gridheight=2;
		for (BTChannel channel:data.getAllChannels()){
			traces.add(addTrace(channel,gridbag,c));
			intervalTraces.add(addIntervalTrace(data,channel,gridbag,c));
			row++;
		}		

		c.gridheight=1;
		if (data.getKB()!=null)
			addIntervalTrace("" + row,data.getKB().getIndirectInstancesOfClass(ONTOConfig.INTERVAL_CLASS),gridbag,c);
		else
			addIntervalTrace("" + row,new HashSet<ProtegeInstance>(),gridbag,c);
		row++;

		// add Scroll Bar
		c.gridheight=1;
		addScrollBar("" + row,gridbag,c);	
		row++;

		// add Time bar
		c.gridheight=1;
		addTimeBar("" + row,gridbag,c);
		row++;


		addEmptyRow(gridbag,c);
	}

	/**
	 * Search in the current trace the one that corresponds to the TraceID and
	 * add the channel to this trace.
	 * 
	 * @param channel
	 *            the channel to add
	 * 
	 * @param TraceID
	 *            the Id of the trace in which the channel must be added.
	 */
	public void addChannelInTrace(GUIChannel channel, String TraceID){
		// search along all the current traces
		for (TracePanel trace:traces){
			// the one that corresponds 
			if (TraceID.startsWith(trace.getID())){
				//add and exit 
				trace.addChannel(channel);
				return;
			}
		}
		System.err.println("channel "+ channel.getID() + " can not be added to any trace");
	}

	/**
	 * this method is just used to set correctly the dimension of the gridbagLayout
	 * @param gridbag - the Layout 
	 * @param c - the constraints
	 */
	private void addEmptyRow(GridBagLayout gridbag, GridBagConstraints c){

		// set constraints
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.gridwidth = 1; //end row
		c.ipady = 1;

		// add empty Panel
		JPanel nothing; 
		for (int i=0;i<6;i++){
			nothing = new JPanel();
			gridbag.setConstraints(nothing, c);
			add(nothing);
		}
		// this one is for closing the row (constraints the next component to start a new row)
		c.gridwidth = GridBagConstraints.REMAINDER;
		nothing = new JPanel();
		gridbag.setConstraints(nothing, c);
		add(nothing);
	}

	/**
	 * add a tracePanel to the windows setting also the label (i.e. y axis label)
	 * @param channel - the channel to display
	 * @param gridbag - the Layout 
	 * @param c - the constraints
	 * @return 
	 */
	private TracePanel addTrace(BTChannel channel, GridBagLayout gridbag, GridBagConstraints c) {

		//... Create components

		//Create the TracePanel corresponding to this channel
		GUIChannel guiChannel = new GUIChannel(channel);
		TracePanel trace = new TracePanel(guiChannel.getID(),this,guiChannel );	

		c.ipady = 60;
		c.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(trace, c);
		add(trace);

		// reset constraints
		c.ipady = 0;

		return trace;
	}

	/**
	 * 
	 * add a time bar at the bottom of the temporalPannel
	 * @param ID - the unique identifier of the time bar (NOT CHECKED)
	 * @param gridbag - the Layout 
	 * @param c - the constraints
	 */
	private void addTimeBar(String ID, GridBagLayout gridbag, GridBagConstraints c) {

		//... Create components

		//create first a label Area 

		c.ipady = 20;
		timeBar = new TimeBar(ID,this);
		c.gridwidth = GridBagConstraints.REMAINDER;		
		gridbag.setConstraints(timeBar, c);
		add(timeBar);	
	}

	/**
	 * create interval panel and set the information contained in each 


	 * @param ID - the unique identifier of the time bar (NOT CHECKED)
	 *  * @param collection - the collection of protegeInstances that represent intervals
	 * @param gridbag - the Layout 
	 * @param c - the constraints
	 * @return the interval panel
	 */
	private IntervalPanel addIntervalTrace(String ID,Collection<ProtegeInstance> collection, GridBagLayout gridbag, GridBagConstraints c) {

		c.ipady = 15;
		IntervalPanel intervalTrace = new IntervalPanel(ID,this,new HashSet<GUIInterval>());
		for (ProtegeInstance instance:collection){
			if (instance.hasAncestor(ONTOConfig.EVENT_CLASS) && 
					!(instance.hasAncestor(ONTOConfig.NUMERICAL_DESCRIPTION_CLASS))&&
					!(instance.hasAncestor(ONTOConfig.PATTERN_CLASS))&&
					!(instance.getClassName().equals(ONTOConfig.SEQUENCE_CLASS))&&
					!(instance.hasAncestor(ONTOConfig.COMMUNICATION_CLASS))
			){
				GUIInterval guiInterval = new GUIInterval(instance);
				Object obj = instance.getValue(ONTOConfig.IMPORTANCE_FEATURE);
				Float importance = (float)0;
				if (obj!=null){
					importance = (Float) obj;
				}

				if (importance>=GeneralConfig.HIGH_IMPORTANCE){
					guiInterval.setColor(GUIConfig.DEFAULT_IMPORTANT_INTERVAL_COLOR);
				}
				intervalTrace.addInterval(guiInterval);
			}

		}
		c.gridwidth = GridBagConstraints.REMAINDER;		
		gridbag.setConstraints(intervalTrace, c);
		add(intervalTrace);	

		return intervalTrace;
	}

	/**
	 * create interval panel linked to a trace panel and set the information contained 
	 * 
	 * @param data the BT data from which interval must be extracted
	 * @param channel - the channel to which the interval are related
	 * @param gridbag - the Layout 
	 * @param c - the constraints
	 * @return the interval panel
	 */
	private IntervalPanel addIntervalTrace(BTData data, BTChannel channel,
			GridBagLayout gridbag, GridBagConstraints c) {

		c.ipady = 15;
		// creates the interval Panel
		IntervalPanel intervalTrace = new IntervalPanel(channel.getName(),this,new HashSet<GUIInterval>());

		// for all protege instances that have been inferred from signals
		if (data.getKB()!=null){
			for (ProtegeInstance instance:data.getKB().getIndirectInstancesOfClass(ONTOConfig.PATTERN_CLASS)){
				// if there are not trends
				if (!instance.getClassName().equals(ONTOConfig.TREND_CLASS)){

					// find the channel they are related to
					ProtegeInstance channelInstance = (ProtegeInstance)instance.getValue(ONTOConfig.CHANNEL_FEATURE);
					String channelName = null; 
					if (channelInstance!=null){
						Object obj = channelInstance.getDefaultValue(ONTOConfig.SHORT_NAME_FEATURE);
						if (obj!=null)
							channelName = ((String)obj).trim();
					}

					// add the interval and set the property
					if (channelName!=null && channel.getName().equals(channelName)){
						GUIInterval guiInterval = new GUIInterval(instance);
						Object obj = instance.getValue(ONTOConfig.IMPORTANCE_FEATURE);
						Float importance = (float)0;
						if (obj!=null){
							importance = (Float) obj;
						}
						if (importance>=GeneralConfig.HIGH_IMPORTANCE){
							guiInterval.setColor(GUIConfig.DEFAULT_IMPORTANT_INTERVAL_COLOR);
						}
						intervalTrace.addInterval(guiInterval);
					}
				}
			}
		}

		c.gridwidth = GridBagConstraints.REMAINDER;		
		gridbag.setConstraints(intervalTrace, c);
		add(intervalTrace);	

		return intervalTrace;
	}


	/**
	 * create a label Panel
	 * @param gridbag
	 * @param c
	 * @return a JPanel
	 */
	private JPanel createLabelBox(GridBagLayout gridbag, GridBagConstraints c){
		// set constraints
		c.weightx = 1.0;
		c.gridwidth = 1;

		// create an empty Panel
		JPanel labelArea = new JPanel();

		return labelArea;
	}

	/**
	 * 
	 * add a scroll bar at the bottom of the temporalPannel 
	 * the length of the scroll is the length of the time period (in second to be displayed)
	 * @param ID - the unique identifier (NOT CHECKED)
	 * @param gridbag - the Layout 
	 * @param c - the constraints
	 */
	private void addScrollBar(String ID,GridBagLayout gridbag, GridBagConstraints c) {

		//... Create components
		JPanel labelArea = createLabelBox(gridbag,c);

		// Initialization of the scrollbar
		scroll = new JScrollBar(Adjustable.HORIZONTAL, 0, 0, 0, 0);
		scroll.addAdjustmentListener(this);
		scroll.setUnitIncrement(10);
		scroll.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				updateScroll();
			}

			public void mousePressed(MouseEvent e) {}

			public void mouseReleased(MouseEvent e) {
				//int sw = (int) scroll.getBounds().getSize().getWidth();
			}

			public void mouseEntered(MouseEvent e) {}

			public void mouseExited(MouseEvent e) {}
		});


		//... Add components to the window
		c.fill = GridBagConstraints.BOTH;
		c.ipady = 0;
		gridbag.setConstraints(labelArea, c);
		add(labelArea,c);

		c.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(scroll, c);
		add(scroll);
	}


	/**
	 * Method to update the scroll bar (length, etc.)
	 */
	private void updateScroll() {

		// total length of the scrollBar in second
		int scrollLength = 1+(int)(this.data.getEndTime().getTime() - this.data.getStartTime().getTime())/1000;

		scroll.setMaximum(Math.max(0, scrollLength-getTraceLength()));
		scroll.setMinimum(0);
		scroll.setBlockIncrement(1);
	}

	/**
	 * convert a number to the highest closest integer (e.g. 3.2 -> 4; 3.0 -> 3) 
	 * @param number - the number to convert
	 * @return - the converted number
	 */
	int roundCeiling(double number){
		if (number == (int)number){
			return (int)number;
		}
		else{
			return (int) (number+1);
		}
	}

	/**
	 * @see AdjustmentListener#adjustmentValueChanged(java.awt.event.AdjustmentEvent)
	 */
	public void adjustmentValueChanged(AdjustmentEvent evt) {

		double newvalue = evt.getValue();
		double coeff = 1.0 / scroll.getMaximum()-scroll.getMinimum(); 
		if (!Double.isNaN(coeff) && !Double.isInfinite(coeff)){ 
			firstVisibleSampleDate = new Date((long) (
					((double)this.firstTimeValue.getTime()+1000* newvalue)
			));
		}
		repaint();	
	}

	/**
	 * add intervals to the first interval trace
	 * @param intervals
	 */
	public void addIntervals(Collection<GUIInterval> intervals) {
		intervalTraces.get(0).setIntervals(intervals);		
	}

	/**
	 * Method to make an horizontal zoom in
	 */
	public void zoomIn() {

		if (this.traceLength/GUIConfig.ZOOM_DIVIDER > GUIConfig.MIN_TRACE_LENGTH){
			this.traceLength/=GUIConfig.ZOOM_DIVIDER;
		}
		updateTick();
		updateScroll();
		repaint();
	}

	/**
	 * Method to make an horizontal zoom out
	 */
	public void zoomOut() {
		if (this.traceLength*GUIConfig.ZOOM_DIVIDER < getTotalDataDuration()){
			this.traceLength*=GUIConfig.ZOOM_DIVIDER;
		}
		else
			this.traceLength = (int) getTotalDataDuration();
		updateScroll();
		updateTick();
		repaint();
	}

	/**
	 * Update the tick size information after a zoom 
	 */
	private void updateTick() {

		// there are approximately 10 tick in a trace
		int approximation = this.traceLength/5;
		if (approximation >= GUIConfig.TICK_LENGTH_LIST[GUIConfig.TICK_LENGTH_LIST.length-1])
			this.tickLength=GUIConfig.TICK_LENGTH_LIST[GUIConfig.TICK_LENGTH_LIST.length-1];
		else if (approximation <= GUIConfig.TICK_LENGTH_LIST[0])
			this.tickLength=(int) GUIConfig.MIN_TICK_LENGTH;
		else{
			tickLength=GUIConfig.TICK_LENGTH_LIST[GUIConfig.TICK_LENGTH_LIST.length-1];
			for (int i=1; i< GUIConfig.TICK_LENGTH_LIST.length;i++){
				if (approximation > GUIConfig.TICK_LENGTH_LIST[i-1] && 
						approximation <= GUIConfig.TICK_LENGTH_LIST[i]){
					if (approximation-GUIConfig.TICK_LENGTH_LIST[i-1] < GUIConfig.TICK_LENGTH_LIST[i]-approximation)
						tickLength = GUIConfig.TICK_LENGTH_LIST[i-1];
					else 
						tickLength = GUIConfig.TICK_LENGTH_LIST[i];
				}
			}
		}		
	}

	/**
	 *  compute the number of pixels per time unit (i.e. second). 
	 *  This is simply done by dividing the graphical area in pixel 
	 *  by the current trace length in second  
	 *  @param the width (pxl) of the graphical object on which the drawing must be done
	 * 	@return a number in pxl/s
	 */
	public double getPixelsPerSecond(double width) {
		// this is the width of the graphic area divided by the trace length (in second)
		pixelsPerSecond = (width  / (double)traceLength);
		return pixelsPerSecond;
	}

	/**
	 * @return the index of the first visible sample in the window
	 */
	public Date getFirstVisibleDate() {
		return firstVisibleSampleDate;
	}

	/**
	 * Compute the date of the first tick that should be visible on the window
	 * @return the date of the first visible tick
	 */
	public Date getFirstVisibleDateTick() {

		// compute the shift in second
		long shiftFromFirstDate = (this.firstTimeValue.getTime()/1000)%(long)tickLength;

		// if not zero then this is not the remaining portion that it is interesting but 
		// the portion to add to go to the first tick index 
		if (shiftFromFirstDate!=0.0){
			shiftFromFirstDate = (long)tickLength-shiftFromFirstDate;
		}

		long firstVisibleDateTick = this.firstTimeValue.getTime()/1000 + shiftFromFirstDate;

		while(firstVisibleDateTick<getFirstVisibleDate().getTime()/1000){
			firstVisibleDateTick+=getTickLength();
		}

		return new Date(firstVisibleDateTick*1000);
	}

	/**
	 * @return the duration in second of the data to display
	 */
	public float getTotalDataDuration(){
		return (this.data.getEndTime().getTime() - this.data.getStartTime().getTime())/1000;
	}


	//... Getters and Setters

	public Date getFirstTimeValue() {
		return firstTimeValue;
	}


	public int getTraceLength() {
		return traceLength;
	}


	public int getTickLength() {
		return tickLength;
	}

	public JFrame getPopup() {
		return popup;
	}

	public JTextArea getMessageArea() {
		return messageArea;
	}
}