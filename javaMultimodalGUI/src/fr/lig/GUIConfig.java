package fr.lig;

import java.awt.Color;


/**
 * Class for storing any constant used in the GUI package. 
 * All the elements related to time are defined in second unit.
 * 
 * @author fportet
 */
public class GUIConfig {

	
	//... Display settings (Zoom, duration, etc)
	/** The default zoom value */
	public static final float DEFAULT_ZOOM = 1;
	
	/** The default time Unit in second */
	public static final float DEFAULT_TIME_UNIT=1;
	
	/** The default trace length in second */
	public static final float DEFAULT_TRACE_LENGTH= 12*60*60;
	
	public static final int[] TICK_LENGTH_LIST=new int[]{10, 30, 60, 2*60, 5*60, 10*60, 20*60, 30*60, 60*60, 2*60*60, 5*60*60, 10*60*60};
	
	/** The max interval length (for the grid) in second */
	public static final float MAX_TICK_LENGTH= 60*60;

	/** The min interval length (for the grid) in second */
	public static final float MIN_TICK_LENGTH= 10;

	/** The minimum length of a trace (in second) */
	public static final float MIN_TRACE_LENGTH = 60;
	
	/** The divider used for zooming */ 
	public static final float ZOOM_DIVIDER = 5;
	
	/** The marker size*/
	public static final int DEFAULT_MARKER_SIZE = 8;

	//... define the space occupancy of the different panels to display
	
	/** occupancy of the label vs trace in TracePanels */
	public static final float DEFAULT_LABEL_TRACE_OCCUPANCY = (float) 0.145;

	/** occupancy of the text in the main window*/
	public static final float BTCOREDISPLAY_OCCUPANCY_COEFFICIENT = (float) 0.3;

		
	//... Defines the colour of the elements to display
	
	/** Colour of basic interval*/
	public static final Color DEFAULT_INTERVAL_COLOR = Color.BLUE;
	
	/** Colour of interval that are important*/
	public static final Color DEFAULT_IMPORTANT_INTERVAL_COLOR = Color.RED;
	
	/** The default colour of the trace */
	public static Color[] DEFAULT_TRACE_COLOR= {Color.BLACK,Color.BLUE,Color.GREEN,Color.YELLOW,Color.RED,Color.CYAN,Color.PINK,Color.MAGENTA};

	/** The default colour of the trace background */
	public static Color TRACE_BACKGROUND=Color.WHITE;
	
	/** The default colour of the label background */
	public static Color DEFAULT_LABEL_BACKGROUND= Color.lightGray;
	
	/** The default grid colour */
	public static Color DEFAULT_GRID_COLOR= Color.LIGHT_GRAY;
	
	/** Custom grid color**/
	public static Color[] CUSTOM_GRID_COLOR = {Color.BLUE,Color.GREEN,Color.YELLOW};
	
	
	//... Defines the channel features
	
	public static float SAMPLING_FREQUENCY= 1;
}