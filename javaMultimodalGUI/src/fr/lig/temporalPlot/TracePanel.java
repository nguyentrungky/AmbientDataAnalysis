package fr.lig.temporalPlot;

import fr.lig.displayableObject.GUIChannel;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import fr.lig.GUIConfig;
import fr.lig.data.UnEquiSampledNumericalChannel;

/**
 * TracePanel manages the display of one channel data
 * 
 * @author fportet
 *
 */
public class TracePanel extends GUIPlot implements MouseListener{
	/** The serial version UID */
	private static final long serialVersionUID = 1L;

	/** The channel to display*/
	private List<GUIChannel> channels;

	/** The number of samples into one time unit */
	private int samplePerSecond;

	/** the number of samples per pixel */
	private double samplePerPixel;

	/** The length of the visible trace from the trace reference (in second, i.e. in samples)*/
	private int lengthOfVisibleTrace;

	/** the index first visible sample */
	private int indexOfFirstVisibleSample;

	/** numerical value formatter */
	private NumberFormat nf ;

	/** size of the marker for sample of un-equisampled channel*/
	private int halfMarkerSize;
	
	/**  minimum value of the y axis */
	private float minimum;
	/**  maximum value of the y axis */
	private float maximum;
	
	/** color of the channel**/
	private static int colorChannel=-1;
	
	/**
	 * create a new TracePanel from its parent and the channel to display
	 * @param ID - the unique ID of the panel (NOT CHECKED)
	 * @param parent - the parent panel
	 * @param channel - the channel to display
	 */
	public TracePanel(String ID,TemporalPanel parent, GUIChannel channel) {
		super(ID,parent);

		// update fields
		channels = new ArrayList<GUIChannel>();
		addChannel(channel);
		nf =  new DecimalFormat("#0.0");;
		addMouseListener(this);

		samplePerSecond = (int)GUIConfig.SAMPLING_FREQUENCY;
		halfMarkerSize = GUIConfig.DEFAULT_MARKER_SIZE/2;

		//... Set panel characteristics
		setBackground(GUIConfig.TRACE_BACKGROUND);	
		repaint();
	}


	@Override
	public void updateCanvas(Graphics g) {

		// paints the grid
		paintGrid(g);

		for (GUIChannel channel:channels){

			// set variable
			setVariables( channel, g);
			// paint the curves
			if (channel.isEquiSampled()){
				try {paintEquiSampledCurves(channel,g);}catch (Exception e) {}
			}else{
				try {paintUnEquiSampledCurves(channel,g);}catch (Exception e) {}
			}
		}		
	}


	private void setVariables(GUIChannel channel,Graphics g){
		double width = g.getClipBounds().width;
		// recompute the resolution
		samplePerPixel = samplePerSecond / parent.getPixelsPerSecond(width);

		lengthOfVisibleTrace = (int) Math.min(channel.getDataChannel().getData().length, parent.getTraceLength()*samplePerSecond);

		// the first visible sample
		indexOfFirstVisibleSample = (int) (channel.getDataChannel().getIndex(parent.getFirstVisibleDate()));

	}


	@Override
	protected void updateLabel(Graphics g) {
		Rectangle box = g.getClipBounds();

		g.setColor(GUIConfig.DEFAULT_GRID_COLOR);
		// draw horizontal lines
		g.drawLine(0, getHeight(), getWidth(),getHeight());
		g.drawLine(0, 0, getWidth(), 0);
		// draw the traces name
		g.setColor(GUIConfig.DEFAULT_TRACE_COLOR[1]);
		g.drawString(getTitle(),  0, (int)(box.height*0.6));

		// draw min and max
		String tag = nf.format(minimum);
		g.drawString(tag, box.width - tag.length()* 7-1 , (int)(box.height*0.9));
		tag = nf.format(maximum);
		g.drawString(tag, box.width - tag.length()* 7-1 , (int)(box.height*0.2));
	}


	@Override
	public void mouseClicked(MouseEvent e) {
		String message = getMessage(e.getX(),e.getY());

		parent.getPopup().setVisible(false);
		if (message != null && message.length()>0){		
			//popup.removeAll();
			parent.getMessageArea().setText(message);
			parent.getPopup().setSize(parent.getMessageArea().getSize());
			parent.getPopup().setVisible(true);	
			parent.getPopup().pack();
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {}


	@Override
	public void mouseExited(MouseEvent e) {}


	@Override
	public void mousePressed(MouseEvent e) {}


	@Override
	public void mouseReleased(MouseEvent e) {}

	/**
	 * find the message to display 
	 * @param x - the x-axis value of the mouse click
	 * @param y - the y-axis value of the mouse click
	 * @return the message corresponding to the data at these coordinates
	 */
	private String getMessage(int x, int y){

		String message = "";
		// compute the shift corresponding to the label area
		int shift = (int) (getWidth()*GUIConfig.DEFAULT_LABEL_TRACE_OCCUPANCY);
		double width = getWidth() - shift;
		x = x-shift;

		if (x>0){
			for (GUIChannel channel:channels){
				// if close enough to an interval
				Date mouseDate = new Date ((long) (parent.getFirstVisibleDate().getTime() + 1000*((double)x/parent.getPixelsPerSecond(width))));			
				int index = channel.getDataChannel().getIndex(mouseDate);
				if (index>=0 && index <channel.getDataChannel().getData().length)
					message += channel.getID() +": " + channel.getDataChannel().getData()[index] +"\n";
			}
		}
		return message;
	}

	/**
	 * Paint the curve at the current resolution
	 * @param channel 
	 * 
	 * @param g - the graphic context
	 */
	private void paintEquiSampledCurves(GUIChannel channel, Graphics g) {
		int x1 ;
		double y1;
		double coefficientY ;
		float[] signal = new float[lengthOfVisibleTrace];

		// initialise the parameter to draw the curve
		x1 = 0;
		y1 = 0;
		coefficientY = (getHeight()-2) / (maximum - minimum+1) ;
		signal =  Arrays.copyOfRange(channel.getDataChannel().getData(), 
				indexOfFirstVisibleSample,
				indexOfFirstVisibleSample
				+ lengthOfVisibleTrace-1);
		g.setColor(channel.getColor());

		// draw all the pieces of the curve
		for (int i = 0; i < lengthOfVisibleTrace; i++) {

			// conversion from the real value to the position in pixel
			int x2 = (int) (i / samplePerPixel);

			// get of the value
			double value = signal[i];
			double y2 =  ((getHeight()-2) - ((value -minimum) * coefficientY))+1;

			// Drawing of the line if acceptable values
			if ((!((x1==0) && (y1==0))) && (!Double.isNaN(y1)) && (!Double.isNaN(y2))&& (!Double.isInfinite(y1)) && (!Double.isInfinite(y2))) {
				g.drawLine(x1, (int)y1, x2, (int)y2);
			}

			// exchange of the values
			x1 = x2;
			y1 = y2;
		}
	}


	/**
	 * Paint the curve at the current resolution
	 * @param channel 
	 * 
	 * @param g - the graphic context
	 */
	private void paintUnEquiSampledCurves(GUIChannel channel, Graphics g) {
		int x1 ;
		double y1;
		double coefficientY ;


		// translates this into a higher level graphic manager
		Graphics2D g2d = ((Graphics2D) g);

		// sets the Graphics2D object's Stroke attribute 
		// this is used for drawing dotted lines and marker line
		BasicStroke lineStroke = new BasicStroke(1f,BasicStroke.CAP_ROUND,   BasicStroke.JOIN_ROUND, 1f, new float[] {2f}, 0f);
		BasicStroke markerStroke = new BasicStroke(1.5f);
		// this is used to draw a line even if the starting point is out of the window		
		g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );

		// initialise the parameter to draw the curve
		x1 = 0;
		y1 = 0;
		coefficientY = (getHeight()-2) / (maximum - minimum+1) ;
		g.setColor(channel.getColor());
		Date startTime = parent.getFirstVisibleDate();
		Date endTime = new Date(startTime.getTime() + (parent.getTraceLength()*samplePerSecond)*1000);

		// get the data 
		UnEquiSampledNumericalChannel chan =  (UnEquiSampledNumericalChannel)channel.getDataChannel();

		// draw all the pieces of the curve
		for (int i = Math.max(0,indexOfFirstVisibleSample-1); i <=Math.min(chan.getIndex(endTime)+1,chan.getSize()); i++) {

			// conversion from the real value to the position in pixel
			int x2 = (int) ((chan.getDate(i).getTime()-startTime.getTime()) / (1000*samplePerPixel));

			// get of the value
			double value = chan.getValue(i);
			double y2 =  ((getHeight()-2) - ((value -minimum) * coefficientY))+1;

			// Drawing of the line if acceptable values
			if ((!((x1==0) && (y1==0))) && (!Double.isNaN(y1)) && (!Double.isNaN(y2))&& (!Double.isInfinite(y1)) && (!Double.isInfinite(y2))) {
				g2d.setStroke(lineStroke);
				g2d.drawLine(x1, (int)y1, x2, (int)y2);
				g2d.setStroke(markerStroke);
				g2d.drawOval(x2-halfMarkerSize, (int)y2-halfMarkerSize, 2*halfMarkerSize, 2*halfMarkerSize);
			}

			// exchange of the values
			x1 = x2;
			y1 = y2;
		}
	}


	/**
	 * paint the grid behind the trace
	 * @param g - the graphic context
	 */
	private void paintGrid(Graphics g) {
		Rectangle box = g.getClipBounds();

		// intervalLength : number of pixels between 2 Ticks lines
		double intervalLength = parent.getPixelsPerSecond(box.width)* parent.getTickLength();

		// get the first tick Date 
		long firstVisibleTick = parent.getFirstVisibleDateTick().getTime();


		// compute the time from the beginning of the window
		long firstVisibleWindowRef = (firstVisibleTick-parent.getFirstVisibleDate().getTime())/1000 ;

		// computation of the pixel index of the first tick
		double shiftFromRefWindow = parent.roundCeiling(parent.getPixelsPerSecond(box.width)*(float)firstVisibleWindowRef);

		g.setColor(GUIConfig.DEFAULT_GRID_COLOR);

		// draw vertical lines 
		for (double i = shiftFromRefWindow; i <= box.width; i+=intervalLength) {
			int x = (int) (i);			
			g.drawLine(x, 0, x, getHeight());
		}

		// draw horizontal lines
		g.drawLine(0, box.height, box.width,box.height);
		g.drawLine(0, 0,  box.width, 0);
	}


	@Override
	protected String getTitle() {
		return channels.get(0).getID();
	}

/**
 * add a new channel in the trace and reset minimum and maximum y axis values
 * @param channel the channel to add
 */
	public void addChannel(GUIChannel channel) {
		//channel.setColor(GUIConfig.DEFAULT_TRACE_COLOR[channels.size()%GUIConfig.DEFAULT_TRACE_COLOR.length]);
		colorChannel++;
		//System.out.println(colorChannel);
		channel.setColor(GUIConfig.DEFAULT_TRACE_COLOR[colorChannel]);
		channels.add(channel);	
		
		minimum = getMinimum();
		maximum = getMaximum();
		
	}
	
	/**
	 * the maximum over all the channels of the trace
	 * @return the maximum
	 */
	private float getMaximum(){
		float max=Float.NEGATIVE_INFINITY;
		for (GUIChannel channel:channels){
			float maxi = channel.getMaximum();
			if (!Float.isNaN(maxi) && !Float.isInfinite(maxi)) 
				max = Math.max(max,maxi);
		}		
		return max;
	}

	/**
	 * the minimum over all the channels of the trace
	 * @return the minimum
	 */

	private float getMinimum(){
		float min=Float.POSITIVE_INFINITY;
		for (GUIChannel channel:channels){
			float mini = channel.getMinimum();
			if (!Float.isNaN(mini) && !Float.isInfinite(mini)) 
				min = Math.min(min,mini);
		}		
		return min;
	}
}
