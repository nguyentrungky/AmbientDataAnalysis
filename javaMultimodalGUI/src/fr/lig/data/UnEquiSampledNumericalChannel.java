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
 * UnEquiSampledNumericalChannel
 * <P>
 * This class is expected to the replaced by one of the TSNET classes.
 * 
 * @author fportet
 */
public class UnEquiSampledNumericalChannel extends NumericalChannel{

	/**
	 * Instantiates a new channel data.
	 * 
	 * @param startTime - the start time
	 * @param endTime - the end time
	 * @param values - the values
	 * @param times - the date of each values
	 * @throws Exception 
	 */
	public UnEquiSampledNumericalChannel(Date startTime, Date endTime, float[] values, Date[] times) throws Exception {
		super(startTime,endTime);
		if (values.length!=times.length) throw new Exception("the length of values ("+ values.length+")and times ("+ times.length+") should be the same!");  
		for (int i=0;i<values.length;i++){
			add(values[i],times[i]);
		}
	}


	/**
	 *  add a value to the channel in ascending order
	 * @param v
	 * @param date
	 */
	public void add(float v, Date date) {
		int index = 0;		
		if (date.before(super.startTime)){
			this.startTime=date;
			index=0;
		}
		else if (date.after(super.endTime)){
			this.endTime=date;
			index=data.length;
		}else{
			for (index=0;index<data.length;index++){
				if (date.getTime()<this.startTime.getTime()+time[index]){
					break;
				}
			}
		}
		// create new values
		float[] values = new float[data.length+1];
		int[] times = new int[time.length+1];

		// copy before index
		System.arraycopy(data, 0, values,0, index);
		System.arraycopy(time, 0, times,0, index);

		//add value
		values[index]=v;
		times[index]=(int) (date.getTime()-startTime.getTime());

		// copy rest after value if needed
		if(index <data.length){
			System.arraycopy(data, index, values,index+1, data.length-index);
			System.arraycopy(time, index, times,index+1, time.length-index);
		}
		data=values;
		time=times;
		times=null;
		values=null;
	}

	/**
	 * Gets the index in the channel which is the closest to this specific date.
	 * 
	 * @param time the date of the sample
	 * 
	 * @return the index of the sample which is the closest in the array of data
	 */
	@Override
	public int getIndex(Date date) {
		int index=0;
		int searchedTime= (int) (date.getTime()-this.startTime.getTime());
		
		int diff = searchedTime;
		// the difference will decrease until the value just after the closest 
		//search until the difference become higher
		for (index=0;index<time.length;index++){
			if (Math.abs(searchedTime-time[index])>diff){
				break;
			}
			diff = Math.abs(searchedTime-time[index]);
		}
		return Math.max(--index,0);
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
		return new Date(startTime.getTime()+time[index]);
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
}