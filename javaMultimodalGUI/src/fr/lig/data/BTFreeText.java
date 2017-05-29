/* ==========================================
 * btcore - BTFreeText.java
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

import java.util.Date;

import kb.ProtegeInstance;
import kb.ProtegeKB;

/**
 * BTFreeText - Holds text a specific Free text information (reports, notes ...).
 * It contains patientID, startTime and EndTime.
 * <P>
 * It also contains a String that actually contains the free text and a ProtegeInstance
 * that corresponds to the class of this piece of information (admission report, shift summary... ).
 * 
 * @author fportet
 */
public class BTFreeText {

	/** The free text. */
	private String freeText;    

	/** The start time of data. */
	private Date startTime;  

	/** The end time of data. */
	private Date endTime;    	

	/** The patient's ID. */
	private int patientID; 

	/** Instance in the ontology that represents this channel. */
	private ProtegeInstance instance;

	/**
	 * Instantiates a new channel data.
	 * 
	 * @param patientID the patient id
	 * @param startTime the start time
	 * @param endTime the end time
	 * @param freeText the free text
	 */		

	public BTFreeText(int patientID, Date startTime, Date endTime, String freeText) {
		this.patientID = patientID;
		this.startTime = startTime;
		this.endTime = endTime;
		this.freeText=freeText;
		instance=null;
	}


	/**
	 * Instantiates a new bT free text.
	 * 
	 * @param kb the kb
	 * @param patientID the patient id
	 * @param startTime the start time
	 * @param endTime the end time
	 * @param ProtegeName the protege name
	 * @param freeText the free text
	 * @throws Exception 
	 */
	public BTFreeText(ProtegeKB kb, int patientID, Date startTime, Date endTime, String ProtegeName, String freeText) throws Exception {
		this(patientID, startTime, endTime, freeText);
		this.instance = kb.makeInstance(ProtegeName);
	}

	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		String result="";
		result =  patientID + "  free text " + instance.getClassName() + " starts with: " + freeText.substring(0,Math.min(freeText.length(), 40));
		return result;
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
	 * @param endTime the new end time
	 */
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}


	/**
	 * Gets the free text.
	 * 
	 * @return the free text
	 */
	public String getFreeText() {
		return freeText;
	}


	/**
	 * Sets the free text.
	 * 
	 * @param freeText the new free text
	 */
	public void setFreeText(String freeText) {
		this.freeText = freeText;
	}


	/**
	 * Gets the Protege Instance.
	 * 
	 * @return the Protege Instance
	 */
	public ProtegeInstance getInstance() {
		return instance;
	}


	/**
	 * Sets the the Protege Instance.
	 * 
	 * @param instance the new the Protege Instance
	 */
	public void setInstance(ProtegeInstance instance) {
		this.instance = instance;
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
	 * @param patientID the new patient id
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
	 * @param startTime the new start time
	 */
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
}