/* ==========================================
 * btcore - BTData.java
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
package fr.lig.data;

import java.util.*;


import fr.lig.misc.ProtegeTools;

import fr.lig.misc.ONTOConfig;

import fr.lig.data.BTChannel;
import fr.lig.data.BTFreeText;
import kb.ProtegeInstance;
import kb.ProtegeKB;

/**
 * BTData - Holds data for a baby for a time period Contains collection of
 * channels and free text plus the ontology knowledge base.
 * 
 * @author fportet
 */

public class BTData {

	/** The start of the data period. */
	private Date startTime;

	/** The end of the data period. */
	private Date endTime;

	/** The baby id. */
	private int patientID;

	/** List of all the channels. */
	private List<BTChannel> allChannels;

	/** List of all the channels. */
	private Set<BTFreeText> allFreeTexts;

	/** List of all the channels. */
	private ProtegeKB kb;

	private String text;

	/**
	 * Instantiates a new BT data.
	 * 
	 * @param patientID
	 *            the patient id
	 * @param startTime
	 *            the start time
	 * @param endTime
	 *            the end time
	 * @param kb
	 *            the kb
	 */

	public BTData(int patientID, Date startTime, Date endTime, ProtegeKB kb) {
		this.startTime = startTime;
		this.endTime = endTime;
		this.patientID = patientID;

		allChannels = new ArrayList<BTChannel>();
		allFreeTexts = new HashSet<BTFreeText>();

		this.kb = kb;
	}

	/**
	 * Add a new channel in the channel list. If a channel of same type and from
	 * same patient is already there, they will be merged.
	 * 
	 * @param channel -
	 *            the channel to add in the data
	 */
	public void addChannel(BTChannel channel) {

		// check if a same channel is already present there (checked only once)
		boolean FOUND = false;
		//for (BTChannel c : allChannels) {
			/*if (c.sameType(channel)) {
				FOUND = true;
				c.mergeWith(channel);
			}*/
		//}
		// if not already present then add it
		if (!FOUND)
			allChannels.add(channel);

	}

	/**
	 * Add a new free text in the free text list
	 * 
	 * @param freetext -
	 *            the free text to add
	 */
	public void addFreeText(BTFreeText freetext) {
		allFreeTexts.add(freetext);
	}

	/**
	 * Represents the channels into a string.
	 * 
	 */
	public void printChannels() {
		System.out.println("Channels: ");
		for (BTChannel channel : allChannels)
			System.out.print(channel.toString() + "\n");

	}

	/**
	 * Represents the free texts into a string.
	 * 
	 */
	public void printFreeTexts() {
		System.out.println("Free texts: ");
		for (BTFreeText text : allFreeTexts)
			System.out.print(text.toString() + "\n");
	}

	/**
	 * Gets the events as a string (for testing purposes)
	 * @return the events
	 */
	public String eventsToString() {
		// return String listing all events
		System.out.println("Events: ");
		List<ProtegeInstance> intervals =new ArrayList<ProtegeInstance>(kb.getIndirectInstancesOfClass(ONTOConfig.INTERVAL_CLASS));
		Collections.sort(intervals, ProtegeTools.getIntervalComparator());
		String s = "";
		for (ProtegeInstance instance : intervals){
			if  (instance.getValue(ONTOConfig.CHANNEL_FEATURE) != null)
			   s+= "["+ ((ProtegeInstance) instance.getValue(ONTOConfig.CHANNEL_FEATURE)).getDefaultValue("abbreviated_name") + "] "+ ProtegeTools.toStringProtegeInstance(instance);
			else
				   s+= ""+ProtegeTools.toStringProtegeInstance(instance);

		}
		return s;
	}

	/**
	 * Print out the events (just print the string)
	 * 
	 */
	public void printEvents(){
		System.out.println(eventsToString());
	}
	/**
	 * Print out the entities.
	 * 
	 */
	public void printEntities() {
		// return String listing all entities
		System.out.println("Entities: ");
		for (ProtegeInstance instance : this.kb
				.getIndirectInstancesOfClass(ONTOConfig.ENTITY_CLASS)) {
			System.out.print(ProtegeTools.toStringProtegeInstance(instance));

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		// return string of ID and interval
		return String.format("BTData (baby %d) from %tc to %tT", patientID,
				startTime, endTime);
	}

	
	/**
	 * Generates the unequisampled channels for representing the trend 
	 * @return - the list of channels
	 * @throws Exception 
	 */
	public List<BTChannel> generateTrendChannel() throws Exception{
		List<BTChannel> trendChannels = new ArrayList<BTChannel>();
		
		for (BTChannel physiological_channel : allChannels){
			BTChannel trend = new BTChannel(this.getPatientID(),
					physiological_channel.getStartTime(), 
					physiological_channel.getEndTime(),
					new float[0], new Date[0]);
			trend.setInstance(physiological_channel.getInstance());
			trendChannels.add(trend);
		}
		
		for(ProtegeInstance trend : kb.getDirectInstancesOfClass(ONTOConfig.TREND_CLASS)){
			ProtegeInstance chan = (ProtegeInstance) trend.getValue(ONTOConfig.CHANNEL_FEATURE);
			for (BTChannel trendChannel: trendChannels){
				if (trendChannel.getInstance().equals(chan)){
					Date startTime = ProtegeTools.unShiftDate((Integer) trend.getValue(ONTOConfig.LATE_START_TIME_FEATURE));
					Date endTime = ProtegeTools.unShiftDate((Integer) trend.getValue(ONTOConfig.EARLY_END_TIME_FEATURE));
					float startValue = (Float)trend.getValue(ONTOConfig.START_VALUE_FEATURE);
					float endValue = (Float)trend.getValue(ONTOConfig.END_VALUE_FEATURE);
					
					if (((String)trend.getValue(ONTOConfig.PATTERN_DIRECTION_FEATURE)).startsWith(ONTOConfig.STEADY_VALUE_FEATURE)||
							((String)trend.getValue(ONTOConfig.PATTERN_DIRECTION_FEATURE)).startsWith(ONTOConfig.WANDERING_VALUE_FEATURE)
							){
						startValue = (Float) trend.getValue(ONTOConfig.MEAN_FEATURE);
						endValue = startValue;
					}
					((UnEquiSampledNumericalChannel)trendChannel.getChannel()).add(startValue, startTime);
					((UnEquiSampledNumericalChannel)trendChannel.getChannel()).add(endValue, endTime);
				}
			}
		}
		
		return trendChannels;
	}
	
	/**
	 * Gets all the channels.
	 * 
	 * @return the all channels
	 */
	public List<BTChannel> getAllChannels() {
		return allChannels;
	}

	/**
	 * Gets the all free texts.
	 * 
	 * @return the all free texts
	 */
	public Set<BTFreeText> getAllFreeTexts() {
		return allFreeTexts;
	}

	/**
	 * Gets the end time.
	 * 
	 * @return the end time
	 */
	public Date getEndTime() {
		return endTime;
	}

	/**
	 * Sets the end time.
	 * 
	 * @param endTime
	 *            the new end time
	 */
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	/**
	 * Gets the kB.
	 * 
	 * @return the kB
	 */
	public ProtegeKB getKB() {
		return kb;
	}

	/**
	 * Sets the kB.
	 * 
	 * @param kb
	 *            the new kB
	 */
	public void setKB(ProtegeKB kb) {
		this.kb = kb;
	}

	/**
	 * Gets the patient id.
	 * 
	 * @return the patient id
	 */
	public int getPatientID() {
		return patientID;
	}

	/**
	 * Sets the patient id.
	 * 
	 * @param patientID
	 *            the new patient id
	 */
	public void setPatientID(int patientID) {
		this.patientID = patientID;
	}

	/**
	 * Gets the start time.
	 * 
	 * @return the start time
	 */
	public Date getStartTime() {
		return startTime;
	}

	/**
	 * Sets the start time.
	 * 
	 * @param startTime
	 *            the new start time
	 */
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public void setText(String text) {
		this.text=text;
		
	}

	public String getText() {
		return text;
	}

}