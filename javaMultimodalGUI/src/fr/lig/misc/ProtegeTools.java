/* ==========================================
 * btcore - ProtegeTools.java
 * ==========================================
 *
 * Copyright (c) 2007-2007, the University of Aberdeen
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, 
 * are permitted FOR RESEARCH PURPOSES ONLY, provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, 
 * 		this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation and/or 
 *    other materials provided with the distribution.
 * 3. Neither the name of the University of Aberdeen nor the names of its contributors 
 * 	  may be used to endorse or promote products derived from this software without 
 *    specific prior written permission.
 *    
 *    THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 *    AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
 *    THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 *    ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE 
 *    FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 *    (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *     LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND 
 *     ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 *     (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 *     EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *     
 *  Redistribution and use for purposes other than research requires special permission by the
 *  copyright holders and contributors. Please contact Ehud Reiter (e.reiter@abdn.ac.uk) for
 *  more information.
 */
package fr.lig.misc;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.TimeZone;

import kb.ProtegeInstance;


/**
 * Class designed to provide methods to do deal with the protege format.
 * 
 * @author fportet
 */
public final class ProtegeTools {

	/** The owl date format. */
	public static final String OWL_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

	/** The date formatter used for proper data translation. */
	private static DateFormat df;

	static{
		df = new SimpleDateFormat(OWL_DATE_FORMAT);
	}

	static NumberFormat importanceFormatter =  new DecimalFormat("#0");
	
	/** the formatter to display the time stamp*/
	private static SimpleDateFormat shortTime = new SimpleDateFormat("HH:mm:ss");;
	
	/**
	 * Convert date to string.
	 * 
	 * @param date - the date
	 * 
	 * @return the string representation of the date
	 */
	public static String convertDateToOWLString(Date date){ 
		String result;
		if (date != null)
			result = df.format(date);
		else
			result = "";
		if (result.endsWith("+0000"))
			result = result.replace("+0000", "");           
		return result;
	}

	/**
	 * Convert a OWL string to a date.
	 * 
	 * @param string - the string representation of the date
	 * 
	 * @return the date
	 * 
	 * @throws ParseException the parse exception
	 */
	public static Date convertOWLStringToDate(String string) throws ParseException {
		String result = string;
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		return df.parse(result);
	}

	/**
	 * Convert a string to make it respect the OWL className convention. which is all letter in Upper case and special characteres replaced by a _:
	 * conCept-of some/kind -> CONCEPT_OF_SOME_KIND 
	 * 
	 * @param input the input
	 * 
	 * @return the string
	 */
	public static String toOWLClassNameConvention(String input){
		char[] output = input.trim().toCharArray();
		for(int i=0;i<output.length;i++){
			if (!Character.isLetterOrDigit(output[i])){
				output[i]='_';
			}
		}
		return new String(output).toUpperCase();
	}

	/*Other possible convention (more common): 
	 * The convention is the following: 
	 * conCept-of some/kind -> Concept_Of_Some_Kind
	 * 
	 * 
	 * public static String toOWLClassNameConvention(String input){
		char[] output = input.trim().toLowerCase().toCharArray();
		if (output.length>0) output[0]=Character.toUpperCase(output[0]);
		for(int i=0;i<output.length;i++){
			if (!Character.isLetterOrDigit(output[i])){
				output[i]='_';
				if (i+1<output.length && Character.isLetterOrDigit(output[i+1])){
					output[i+1]=Character.toUpperCase(output[i+1]);
				}
			}
		}
		return new String(output);
	}*/

	/**
	 * convert an absolute date into a int shifted representation of the date in
	 * second.
	 * 
	 * @param date
	 *            the date
	 * 
	 * @return the int
	 */
	public static int shiftDate(Date date) {
		return (int) ((date.getTime() - GeneralConfig.REFERENCE_DATE.getTime()) / 1000);
	}

	/**
	 * convert a int shifted representation of a date in second into an absolute
	 * date.
	 * 
	 * @param date
	 *            the date
	 * 
	 * @return the date
	 */
	public static Date unShiftDate(int date) {
		return new Date(GeneralConfig.REFERENCE_DATE.getTime() + ((long) date) * 1000);
	}

	public static Comparator<ProtegeInstance> getIntervalComparator() {
		Comparator<ProtegeInstance> comp =null; 
		comp = new Comparator<ProtegeInstance>() {
			public int compare(ProtegeInstance m1, ProtegeInstance m2) {

				// sort by start Date
				if ((Integer)m1.getValue(ONTOConfig.LATE_START_TIME_FEATURE) < (Integer)m2.getValue(ONTOConfig.LATE_START_TIME_FEATURE))
					return -1;
				else if ((Integer)m1.getValue(ONTOConfig.LATE_START_TIME_FEATURE) > (Integer)m2.getValue(ONTOConfig.LATE_START_TIME_FEATURE))
					return 1;
				// sort by end Date
				else{ 
					if ((Integer)m1.getValue(ONTOConfig.EARLY_END_TIME_FEATURE) < (Integer)m2.getValue(ONTOConfig.EARLY_END_TIME_FEATURE))
						return -1;
					else if ((Integer)m1.getValue(ONTOConfig.EARLY_END_TIME_FEATURE) > (Integer)m2.getValue(ONTOConfig.EARLY_END_TIME_FEATURE))
						return 1;
					else{
						if (!m1.getClassName().equals(m2.getClassName()))
						   return ((String)m1.getClassName()).compareTo((String)m2.getClassName());
						else  {  // compare channel names if appropriate
						   ProtegeInstance m1ChanFeature =  ((ProtegeInstance) m1.getValue(ONTOConfig.CHANNEL_FEATURE));
						   ProtegeInstance m2ChanFeature =  ((ProtegeInstance) m2.getValue(ONTOConfig.CHANNEL_FEATURE));
						   if ((m1ChanFeature != null) && (m2ChanFeature != null))
							   return ((String)(m1ChanFeature.getDefaultValue("abbreviated_name"))).compareTo(((String)(m2ChanFeature.getDefaultValue("abbreviated_name"))));
						   else
							   return 0;
						}
					}
				}
			}};
			return comp;
	}

	public static String toStringProtegeInstance(ProtegeInstance instance){
		String result=""; 
		
		if (instance.hasAncestor(ONTOConfig.INTERVAL_CLASS)){
		float importance = (instance.getValue(ONTOConfig.IMPORTANCE_FEATURE) != null) ? (Float) instance
				.getValue(ONTOConfig.IMPORTANCE_FEATURE): 0;

		result = result + instance.getClassName() + " ("+importanceFormatter.format(importance)+ ") "+ ": from "
				+ shortTime.format(ProtegeTools.unShiftDate((Integer)instance.getValue(ONTOConfig.LATE_START_TIME_FEATURE)))
				+ " to "
				+ shortTime.format(ProtegeTools.unShiftDate((Integer)instance.getValue(ONTOConfig.EARLY_END_TIME_FEATURE)))
				+ "\n";
		}
		
		else if (instance.hasAncestor("LINK")){
			float importance = (instance.getValue(ONTOConfig.IMPORTANCE_FEATURE) != null) ? (Float) instance
					.getValue(ONTOConfig.IMPORTANCE_FEATURE): 0;

					
			result = result + instance.getClassName() +" "+ instance.getValue(ONTOConfig.LINK_TYPE_FEATURE) + " ("+importance+ ") \n";
			for (ProtegeInstance inst: (Collection<ProtegeInstance>) instance.getValue(ONTOConfig.LINK_EVENT_LIST_FEATURE)){
				result += "\t " + toStringProtegeInstance(inst);
			}
		}
			
		else{
			float importance = (instance.getValue(ONTOConfig.IMPORTANCE_FEATURE) != null) ? (Float) instance
					.getValue(ONTOConfig.IMPORTANCE_FEATURE): 0;

			String num = (instance.getValue(ONTOConfig.NUMERICAL_VALUE_FEATURE) != null) ? instance
					.getValue(ONTOConfig.NUMERICAL_VALUE_FEATURE).toString()
					: "";
			String str = (instance.getValue(ONTOConfig.STRING_VALUE_FEATURE) != null) ? instance
					.getValue(ONTOConfig.STRING_VALUE_FEATURE).toString()
					: "";
			
			result = result + instance.getClassName()  + " ("+importanceFormatter.format(importance)+ ") "+  num + " " + str.substring(0,Math.min(20, str.length()))
					+ "\n";
			}
		return result;
	}
}
