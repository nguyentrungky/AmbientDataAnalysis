/* ==========================================
 * btcore - NumericalChannel.java
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


/**
 * NumericalChannel - Holds data for a specific channel, specific baby, 
 * specific time period stored in an array, assuming 1 HZ sampling frequency.
 * <P>
 * This class is expected to the replaced by one of the TSNET classes.
 * 
 * @author fportet
 */
public abstract class NumericalChannel {

	/** The raw data. */
	protected float data[];  
	
	/** the time associated with the data (not used in equi-sampled channel)*/
	protected int time[];

	/** The start time of data. */
	protected Date startTime;  

	/** The end time of data. */
	protected Date endTime;

	
	/**
	 * Instantiates a new channel data.
	 * 
	 * @param startTime the start time
	 * @param endTime the end time
	 * @param values the values
	 */		
	public NumericalChannel(Date startTime, Date endTime) {
		this.startTime = startTime;
		this.endTime = endTime;
		this.data=new float[0];
		this.time=new int[0];
	}
	
	
	/**
	 * Instantiates a new channel data.
	 * 
	 * @param startTime the start time
	 * @param endTime the end time
	 * @param values the values
	 */		
	public NumericalChannel(Date startTime, Date endTime, float[] values) {
		this(startTime,endTime);
		this.data=values;
	}

	/**
	 * Gets the index in the channel at this specific date.
	 * 
	 * @param time the date of the sample
	 * 
	 * @return the index of the sample in the array of data
	 */
	public abstract int getIndex(Date time);

	/**
	 * Gets the date of the sample at the index.
	 * 
	 * @param index the index of the sample
	 * 
	 * @return the date of the sample
	 */
	public abstract Date getDate(int index);
	
	/**
	 * Gets the value of the sample at the index.
	 * 
	 * @param index the index of the sample
	 * 
	 * @return the value of the sample
	 */
	public float getValue(int index) {
		return this.data[index];
	}


	/**
	 * Returns TRUE if data point at index is artifact (NaN of infinite).
	 * 
	 * @param index - the index of the sample
	 * 
	 * @return true, if sample is artifact
	 */
	public boolean isArtifact(int index) {
		return Float.isNaN(data[index])|| Float.isInfinite(data[index]);
	}

	/**
	 * Gets the data.
	 * 
	 * @return the numerical data
	 */
	public float[] getData() {
		return data;
	}
	
	/**
	 * Gets the time.
	 * 
	 * @return the time associated which each data sample
	 */
	public int[] getTime() {
		return time;
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
	 * Gets the start time.
	 * 
	 * @return the start time
	 */
	public Date getStartTime() {
		return startTime;
	}
	
	/**
	 * 
	 * @return the number of samples 
	 */
	public int getSize(){
		return this.data.length;
	}
}