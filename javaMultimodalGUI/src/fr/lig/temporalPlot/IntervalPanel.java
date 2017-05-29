package fr.lig.temporalPlot;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.util.Collection;
import java.util.Date;
import java.util.TreeSet;

import fr.lig.GUIConfig;
import fr.lig.displayableObject.*;

/**
 * IntervalPanel manages the display of intervals
 * 
 * @author fportet
 *
 */
public class IntervalPanel extends GUIPlot implements MouseListener{
	/** The serial version UID */
	private static final long serialVersionUID = 1L;

	/** The time ordered sets of intervals*/
	TreeSet<GUIInterval> intervals;
	

	/**
	 * create a new IntervalPanel from its parent and the channel to display
	 * @param parent - the parent panel
	 * @param intervals - the intervals to display
	 */
	public IntervalPanel(String ID,TemporalPanel parent, Collection<GUIInterval> intervals) {
		super(ID,parent);

		// update fields
		this.intervals = new TreeSet<GUIInterval>();
		this.intervals.addAll(intervals);
		addMouseListener(this);

		setBackground(GUIConfig.TRACE_BACKGROUND);	
		repaint();
	}

	@Override
	public void updateCanvas(Graphics g) {
		// paint the curves
		try {
			// paint the curves
			paintIntervals(g);
		}
		catch (NullPointerException e) {}
		catch (ArrayIndexOutOfBoundsException e) {}
	}

	@Override
	protected void updateLabel(Graphics g) {
		Rectangle box = g.getClipBounds();
		g.setColor(GUIConfig.DEFAULT_GRID_COLOR);
		// draw horizontal lines
		g.drawLine(0, getHeight(), getWidth(),getHeight());
		g.drawLine(0, 0, getWidth(), 0);
		// draw the date
		g.setColor(GUIConfig.DEFAULT_TRACE_COLOR[0]);
		g.drawString(getTitle(),  0, (int)(box.height*0.8));		
	}
	
	
	
	//... Getters and Setters

	/**
	 * Sets the new intervals removing the oldest
	 * @param intervals - the intervals to display (no need to be ordered)
	 */
	public void setIntervals(Collection<GUIInterval> intervals) {
		this.intervals.clear();
		this.intervals.addAll(intervals);
	}

	public TreeSet<GUIInterval> getIntervals() {
		return intervals;
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

	public void addInterval(GUIInterval interval) {
		intervals.add(interval);
		
	}
	
	/**
	 * Paint the curve at the current resolution
	 * 
	 * @param g - the graphic context
	 */

	private void paintIntervals(Graphics g) {
		double width = g.getClipBounds().width;
		int x1, recWidth;

		// last date of the visible window
		Date endDate = new Date(parent.getFirstVisibleDate().getTime() + parent.getTraceLength()*1000);

		// draw all the pieces of the curve
		for (GUIInterval interval: intervals) {
			if (interval.overlap(parent.getFirstVisibleDate(),endDate)){
				// paint the rectangle 
				g.setColor(interval.getColor());

				// start of the interval (in pixel)
				x1 = (int) ((interval.getStart().getTime() 
						- parent.getFirstVisibleDate().getTime())
						*parent.getPixelsPerSecond(width)/1000);
				// width of the interval (in pixel)
				recWidth =  1+(int)((interval.getEnd().getTime() 
						- interval.getStart().getTime())
						*parent.getPixelsPerSecond(width)/1000);
				// draw a rectangle occupying the whole height
				g.fillRect(x1,0, recWidth, getHeight());
			}
		}
	}

	private String getMessage(int x, int y){

		String message = "";
		// compute the shift corresponding to the label area
		int shift = (int) (getWidth()*GUIConfig.DEFAULT_LABEL_TRACE_OCCUPANCY);
		double width = getWidth() - shift;
		
		x = x-shift;
		
		if (x>0){
			// if close enough to an interval
			Date mouseDateStart = new Date ((long) (parent.getFirstVisibleDate().getTime() + 1000*((double)x/parent.getPixelsPerSecond(width)-20)));			
			Date mouseDateEnd = new Date ((long) (parent.getFirstVisibleDate().getTime() + 1000*(20+(double)x/parent.getPixelsPerSecond(width))));
			for (GUIInterval interval: intervals) {
				if (interval.overlap(mouseDateStart,mouseDateEnd)){
					message+= interval.getMessage() + "\n";
				}
			}
		}
		return message;
	}

	@Override
	protected String getTitle() {
		return getID();
	}
}
