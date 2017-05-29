package fr.lig.misc;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * This class defines all the constant used in the BT-Core system
 * 
 * @author fportet
 *
 */
public class GeneralConfig {
	
	public static String BASE_DIRECTORY="./"; 
	
	public static int SECOND_PER_DAY = 24*60*60; 
	
	/** the default time zone of BT-CORE is GMT*/
	public final static TimeZone DEFAULT_TIME_ZONE = TimeZone.getTimeZone("GMT");
	static{
		TimeZone.setDefault(DEFAULT_TIME_ZONE);
	}
	
	/** The Constant NO_IMPORTANCE: do not mention events/groups with importance <= NO_IMPORTANCE */
	public final static float NO_IMPORTANCE = 1; 

	/** The Constant LOW_IMPORTANCE: only mention events/groups with importance <= LOW_IMPORTANCE according to the context. */
	public final static int LOW_IMPORTANCE = 5;  

	/** The Constant HIGH_IMPORTANCE: always mention events/groups with importance >= HIGH_IMPORTANCE*/
	public final static int HIGH_IMPORTANCE = 50; 
	
	/** The date used to shift all dates (used only to shift or unshift dates when dealing with the ontology) */
	public static final Date REFERENCE_DATE = (new GregorianCalendar(1996, 1, 1, 0, 0)).getTime();
	
	/** reduction coefficient for signal importance*/
	public static final float SIGNAL_ANALYSIS_COEFFICIENT = (float)0.8;
	
	/** reduction coefficient for data directly read from database*/
	public static final float DATA_READING_COEFFICIENT = (float)0.9;
}
