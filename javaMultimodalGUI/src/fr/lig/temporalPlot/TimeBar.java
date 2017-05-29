package fr.lig.temporalPlot;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import java.text.SimpleDateFormat;
import java.util.Date;

import fr.lig.GUIConfig;


/**
 * this class is used to display the time scale at the bottom of a TemporalPanel
 * 
 * @author fportet
 *
 */
public class TimeBar extends GUIPlot  {
	/** The serial version UID */
	private static final long serialVersionUID = 1L;

	/** the formatter to display the time stamp*/
	private SimpleDateFormat df;
	
	/** the formatter to display the date*/
	private SimpleDateFormat dfdate;

	/**
	 * Creates a newTimeBar panel.
	 * 
	 * @param parent -  The parent panel
	 */
	public TimeBar(String ID,TemporalPanel parent) {
		super(ID, parent);
		
		df = new SimpleDateFormat("HH:mm");
		dfdate = new SimpleDateFormat("dd MMM yyyy");
		setBackground(GUIConfig.TRACE_BACKGROUND);
		repaint();
	}

	@Override
	public void updateCanvas(Graphics g) {	
		paintTimeScale(g);
	}

	@Override
	protected void updateLabel(Graphics g) {
		Rectangle box = g.getClipBounds();
		
		g.setColor(GUIConfig.DEFAULT_GRID_COLOR);
		
		// draw horizontal lines
		g.drawLine(0, box.height, box.width, box.height);
		g.drawLine(0, 0, box.width, 0);
		
		// draw the date
		g.setColor(GUIConfig.DEFAULT_TRACE_COLOR[0]);
		
		g.drawString(getTitle(),  0, (int)(box.height*0.8));		
	}
	
	/**
	 * paint the time scale 
	 * 
	 * @param g - the graphic context
	 */
	private void paintTimeScale(Graphics g) {

		Rectangle box = g.getClipBounds();
		
		// intervalLength : number of pixels between 2 Ticks lines
		double intervalLength = parent.getPixelsPerSecond(box.width)* parent.getTickLength();
		//double intervalLength = ((double) width  / (double)parent.getTraceLength())* parent.getTickLength();
		
		// get the first tick Date 
		long firstVisibleTick = parent.getFirstVisibleDateTick().getTime();
		
		// compute the time from the beginning of the window
		long firstVisibleWindowRef = (firstVisibleTick-parent.getFirstVisibleDate().getTime())/1000 ;
		
		// computation of the pixel index of the first tick
		double shiftFromRefWindow = parent.roundCeiling(parent.getPixelsPerSecond(box.width)*(double)firstVisibleWindowRef);

		// paint the red line 
		g.setColor(Color.RED);
		g.fillRect(0,0, box.width, box.height/2);

		g.setColor(GUIConfig.DEFAULT_TRACE_COLOR[0]);

		// draw the scale
		for (double i = shiftFromRefWindow; i <= box.width; i+=intervalLength) {
			int x = (int) i;

			g.drawLine(x, 0, x, box.height);

			// Calculation of the corresponding time
			String n = "" +  df.format(new Date(firstVisibleTick));
			g.drawString(n, x - ((n.length() * 7) / 2), box.height);
			// index of next tick 
			firstVisibleTick+=(long)parent.getTickLength()*1000;			
		}
	}

	@Override
	protected String getTitle() {
		return dfdate.format(parent.getFirstVisibleDate());
	}
}