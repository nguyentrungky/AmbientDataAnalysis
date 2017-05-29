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
public class EquiSampledNumericalChannel extends NumericalChannel{

	/** Sampling frequency always = 1Hz. */
	private float samplingFrequency =1;

	/**
	 * Instantiates a new channel data.
	 * 
	 * @param startTime the start time
	 * @param endTime the end time
	 * @param values the values
	 */		
	public EquiSampledNumericalChannel(Date startTime, Date endTime, float[] values) {
		super(startTime,endTime,values);
	}



	/**
	 * Gets the index in the channel at this specific date.
	 * 
	 * @param time the date of the sample
	 * 
	 * @return the index of the sample in the array of data
	 */
	@Override
	public int getIndex(Date time) {
		// get array index corresponding to a Java Date
		// return -1 if time is not in range of data set
		if (time.getTime()/1000 >= startTime.getTime()/1000 && time.getTime()/1000 <= endTime.getTime()/1000)
			return (int) (time.getTime()/1000) - (int)(startTime.getTime()/ 1000);
		else
			return -1;		
	}

	/**
	 * Gets the date of the sample at the index.
	 * 
	 * @param index the index of the sample
	 * 
	 * @return the date of the sample
	 */
	@Override
	public Date getDate(int index) {
		// get Java Date corresponding to array index
		return new Date(((startTime.getTime()/1000) + ((long)index))*1000);
	}


	/**
	 * Returns TRUE if data point at index is artifact (NaN of infinite).
	 * 
	 * @param index - the index of the sample
	 * 
	 * @return true, if sample is artifact
	 */
	@Override
	public boolean isArtifact(int index) {
		return Float.isNaN(data[index])|| Float.isInfinite(data[index]);
	}

	/**
	 * Gets the sampling frequency.
	 * 
	 * @return the sampling frequency
	 */
	public float getSamplingFrequency() {
		return samplingFrequency;
	}
}