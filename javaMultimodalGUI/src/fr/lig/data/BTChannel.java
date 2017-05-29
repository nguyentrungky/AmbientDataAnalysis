/* ==========================================
 * btcore - BTChannel.java
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

import java.util.Arrays;
import java.util.Date;

import fr.lig.misc.ONTOConfig;

import kb.ProtegeInstance;
import kb.ProtegeKB;

/**
 * <p>ChannelData - Holds data for a specific continuous channel</p>
 * <P>It contains patientID and also contains a NumericalChannel that actually contains 
 * all the numerical data and a ProtegeInstance that corresponds to the class of the channel in the ontology.   
 * <P>
 * Ex: the Heart Rate for the baby 12 between X and Y is read. Then an Heart Rate Instance is created in the ontology 
 * and the actual data of baby 12 from X to Y are stored in the Numerical channel.
 * <P>
 * This class is a wrapper for NumericalChannel which is expected to the replaced by one of the TSNET channel classes. 
 * 
 * @author fportet
 */
public class BTChannel {
	
	/**the name of channel**/
	private String name;

	/** the patient's ID. */
	private int patientID;

	/** Numerical channel in which data are stored. */ 
	private NumericalChannel channel;

	/** internal data structures type of a particular data point. */
	private enum PointType {
		/** The NORMAL value. */
		NORMAL, 
		/** The ARTEFACT point belonging to an artifact segment. */
		ARTEFACT, 
		/** The PATTERN normal value but marked as specific pattern. */
		PATTERN,
		/** The REPAIRED value reconstructed from a model. */
		REPAIRED
	};

	/** The data type: for each sample, the sample is tagged with its point type. */
	private PointType dataType[];  

	//... define the ranges values
	/** typical value of the channel*/
	private float typicalValue;

	/** normal value range of the channel*/
	private float[] normalRange;

	/** physiologically acceptable value range of the channel*/
	private float[] physiologicalRange;

	/** extreme dynamic of the channel*/
	private float[] legalRange;

	/** Instance in the ontology that represents this channel. */
	private ProtegeInstance instance;

	/**
	 * Instantiates a new channel data given the values, the period and the Protege knowledge base.
	 * 
	 * @param patientID - the patient id
	 * @param startTime - the beginning of the channel 
	 * @param endTime - the end of the channel 
	 * @param ProtegeName - the protege class name
	 * @param values - the numerical values
	 * @param kb - the protege knowledge base 
	 * @throws Exception 
	 */		

	public BTChannel(ProtegeKB kb, int patientID, Date startTime, Date endTime, String ProtegeName, float[] values) throws Exception {
		this(patientID, startTime, endTime,values);		
		// if channel instance already created takes it 
		instance = kb.getFirstDirectInstanceOfClass(ProtegeName);
		if (instance ==null)
			instance = kb.makeInstance(ProtegeName);

		initRanges();
	}

	/**
	 * Instantiates a new channel data given the values but without link to the ontology.
	 * 
	 * @param patientID - the patient id
	 * @param startTime - the beginning of the channel 
	 * @param endTime - the end of the channel 
	 * @param values - the numerical values
	 * 
	 */
	public BTChannel(int patientID, Date startTime, Date endTime,  float[] values) {
		this.patientID = patientID;
		channel = new EquiSampledNumericalChannel(startTime,endTime,values);
		dataType = new PointType[channel.getData().length];
		Arrays.fill(dataType, PointType.NORMAL);
		instance = null;
	}
	
	/**
	 * Instantiates a new channel data given the values but without link to the ontology.
	 * 
	 * @param patientID - the patient id
	 * @param startTime - the beginning of the channel 
	 * @param endTime - the end of the channel 
	 * @param values - the numerical values
	 * @param name - the name of the channel
	 */
	public BTChannel(int patientID, Date startTime, Date endTime,  float[] values, String name) {
		this.patientID = patientID;
		channel = new EquiSampledNumericalChannel(startTime,endTime,values);
		dataType = new PointType[channel.getData().length];
		Arrays.fill(dataType, PointType.NORMAL);
		instance = null;
		this.name = name;
	}

	/**
	 * Instantiates a new channel data given the values but without link to the ontology.
	 * 
	 * @param patientID - the patient id
	 * @param startTime - the beginning of the channel 
	 * @param endTime - the end of the channel 
	 * @param values - the numerical values
	 * @throws Exception 
	 * 
	 */
	public BTChannel(int patientID, Date startTime, Date endTime,  float[] values, Date[] dates) throws Exception {
		this.patientID = patientID;
		channel = new UnEquiSampledNumericalChannel(startTime,endTime,values,dates);
		dataType = new PointType[channel.getData().length];
		Arrays.fill(dataType, PointType.NORMAL);
		instance = null;
	}

	/**
	 * Checks if two channels have the same patient ID and the same channel
	 * class assuming the same sampling frequency
	 * 
	 * @param channel 
	 *            the channel to compare to
	 * @return true if they are the sameType
	 */
	public boolean sameType(BTChannel channel){

		boolean result = this.channel.getClass().equals(
				channel.getChannel().getClass())
				&& instance.getValue(ONTOConfig.PATIENT_ID_FEATURE) == channel
				.getInstance().getValue(ONTOConfig.PATIENT_ID_FEATURE)
				&& instance.getClassName() == channel.getInstance()
				.getClassName();
		return result;
	}

	/**
	 * merge two channels of same type and same patient. If the channel are of the same type. 
	 * 
	 * @param channel
	 */
	public void mergeWith(BTChannel channel){
		if (this.sameType(channel)){
			// update start and end time 
			Date startTime = new Date(Math.min(this.getStartTime().getTime(),channel.getStartTime().getTime()));
			Date endTime = new Date(Math.max(this.getEndTime().getTime(),channel.getEndTime().getTime()));


			// number of elements of the new data 
			int numElements = (int)((endTime.getTime()-startTime.getTime())/1000);
			// create the new signal
			float[] values = new float[numElements];
			Arrays.fill(values,Float.NaN);

			// create the new numerical channel 
			EquiSampledNumericalChannel cha = new EquiSampledNumericalChannel(startTime,endTime,values);
			PointType[] newDataType = new PointType[cha.getData().length];

			// find from where the current channel must be copied  
			int index = cha.getIndex(this.getStartTime());
			System.arraycopy(this.channel.getData(), 0, cha.getData(), index, this.channel.getData().length);
			System.arraycopy(dataType, 0, newDataType, index, dataType.length);
			// find from where the merged channel must be copied 
			// overwriting is the two channels overlap			
			index = cha.getIndex(channel.getStartTime());
			System.arraycopy(channel.getChannel().getData(), 0, cha.getData(), index, channel.getChannel().getData().length);
			System.arraycopy(channel.getDataType(), 0, newDataType, index, channel.getDataType().length);

			// set new numerical channel 
			this.channel=cha;
			this.dataType=newDataType;
			// dereference old values
			values=null;
		}
	}

	/**
	 * Gets the index in the channel at this specific date.
	 * 
	 * @param time the date of the value
	 * 
	 * @return the index of the value in the array of data
	 */
	public int getIndex(Date time) {
		return channel.getIndex(time);
	}

	/**
	 * Gets the date of the value at the index.
	 * 
	 * @param index the index
	 * 
	 * @return the date
	 */
	public Date getDate(int index) {
		return channel.getDate(index);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		String result="";
		result =  patientID + "  channel " + instance.getClassName();
		return result;
	}

	/**
	 * Returns TRUE if data point at index is artifact.
	 * 
	 * @param index the index
	 * 
	 * @return true, if is artifact
	 */
	public boolean isArtifact(int index) {
		return (dataType[index]==PointType.ARTEFACT || channel.isArtifact(index));
	}

	/**
	 * Returns index of first normal data point. Returns -1 if no normal data points
	 * 
	 * @return index of first normal data point
	 */
	public int firstNormal() {
		for (int index = 0; index < channel.getData().length; index++)
			if (!isArtifact(index))
				return index;

		return -1;
	}

	/**
	 * Returns index of last normal data point. Returns -1 if no normal data points
	 * 
	 * @return index of last normal data point
	 */
	public int lastNormal() {
		for (int index = channel.getData().length-1; index >= 0 ; index--)
			if (!isArtifact(index))
				return index;
		return -1;
	}

	/**
	 * Sets value at index as artifact.
	 * 
	 * @param index of the sample
	 */
	public void setArtifact(int index) {
		//channel.setArtefact(index);
		dataType[index]=PointType.ARTEFACT;	
	}

	/**
	 * Gets the end time.
	 * 
	 * @return the end time
	 */
	public Date getEndTime() {
		return channel.getEndTime();
	}

	/**
	 * Gets the start time.
	 * 
	 * @return the start time
	 */
	public Date getStartTime() {
		return channel.getStartTime();
	}

	/**
	 * Gets the channel.
	 * 
	 * @return the numerical channel
	 */
	public NumericalChannel getChannel() {
		return this.channel;
	}

	/**
	 * Sets the channel.
	 * 
	 * @param channel the new channel
	 */
	public void setChannel(EquiSampledNumericalChannel channel) {
		this.channel=channel;
	}

	/**
	 * Gets the instance of the channel
	 *  
	 * @return - the channel instance
	 */
	public ProtegeInstance getInstance() {
		return instance;
	}

	/**
	 * Sets the instance of the channel
	 *  
	 * @param instance - the channel instance
	 */
	public void setInstance(ProtegeInstance instance){
		this.instance=instance;
	}

	/**
	 * Gets the typical value of this channel 
	 * @return the typical value
	 */
	public float getTypicalValue() {
		return typicalValue;
	}

	/**
	 * Sets the typical value of this channel 
	 * @param typicalValue - the typical value
	 */
	public void setTypicalValue(float typicalValue) {
		this.typicalValue = typicalValue;
	}

	/**
	 * Gets the values range in which the channel value are normal 
	 * 
	 * @return - the range (minimum first)
	 */
	public float[] getNormalRange() {
		return normalRange;
	}

	/**
	 * Sets the values range in which the channel value are normal 
	 * 
	 * @param normalRange - the range (minimum first)
	 */
	public void setNormalRange(float[] normalRange) {
		this.normalRange = normalRange;
	}

	/**
	 * Gets the values range in which the channel value are physiologically explainable 
	 * 
	 * @return - the range (minimum first)
	 */
	public float[] getPhysiologicalRange() {
		return physiologicalRange;
	}

	/**
	 * Sets the values range in which the channel value are physiologically explainable  
	 * 
	 * @param physiologicalRange - the range (minimum first)
	 */
	public void setPhysiologicalRange(float[] physiologicalRange) {
		this.physiologicalRange = physiologicalRange;
	}

	/**
	 * Gets the values range in which the channel value are possible 
	 * 
	 * @return - the range (minimum first)
	 */
	public float[] getLegalRange() {
		return legalRange;
	}

	/**
	 * Sets the values range in which the channel value are possible 
	 * 
	 * @param legalRange - the range (minimum first)
	 */
	public void setLegalRange(float[] legalRange) {
		this.legalRange = legalRange;
	}

	/** initialise the channel range using the protege instance*/
	public void initRanges() {
		if (instance!=null){
			legalRange=setRange(instance.getDefaultValue(ONTOConfig.LOW_LEGAL_RANGE_FEATURE),instance.getDefaultValue(ONTOConfig.HIGH_LEGAL_RANGE_FEATURE));
			physiologicalRange=setRange(instance.getDefaultValue(ONTOConfig.LOW_PHYSIOLOGICAL_RANGE_FEATURE),instance.getDefaultValue(ONTOConfig.HIGH_PHYSIOLOGICAL_RANGE_FEATURE));
			normalRange=physiologicalRange;
			typicalValue=(normalRange[1]+normalRange[0])/2;
		}
	}

	/** initialise the legal range using the value defined in the protege instance*/
	private float[] setRange(Object lowestDefaultValue, Object highestDefaultValue) {
		float[] range=null;
		if (lowestDefaultValue!=null && highestDefaultValue!=null)
			range= new float[]{((Number)lowestDefaultValue).floatValue(),((Number)highestDefaultValue).floatValue()};
		return range;
	}

	/**
	 * Compute the minimum and maximum of the channel values ignoring NaN and infinite
	 * @return - minimum and maximum (minimum first)
	 */
	public float[] getMinAndMax(){
		float min = Float.POSITIVE_INFINITY;
		float max = Float.NEGATIVE_INFINITY;
		float[] data =channel.getData();

		for (int i=0;i<data.length;i++){
			if (!Float.isInfinite(data[i])){
				if (min>data[i]){
					min=data[i];
				}
				if (max<data[i]){
					max=data[i];
				}
			}
		}

		// if the minimum is still superior to the maximum
		// then it is because there are only NaN, infinite values or no value at all.
		if (min>max){
			min = Float.NaN;
			max = Float.NaN;			
		}
		return new float[]{min,max};
	}

	/**
	 * compute the mean of the channel but taking only normal values into
	 * account (i.e. not artifact or patterns)
	 * 
	 * @return the mean of the channel
	 */
	public double computeMeanExcludingTranscient() {
		return computeMeanExcludingTranscient(0,channel.getSize());
	}

	/**
	 * compute the mean of a period of the channel but taking only normal values into
	 * account (i.e. not artifact or patterns)
	 * 
	 * @param start the index from which the mean must be computed 
	 * @param end the index until which (excluding) the mean must be computed
	 * @return the mean of the channel
	 */
	public double computeMeanExcludingTranscient(int start, int end) {
		int numberOfSamples=0;
		double mean=0;
		end = Math.min(channel.getSize(), end);
		for (int i=start;i<end;i++){
			if (isNormal(i)){
				mean+=channel.getValue(i);
				numberOfSamples++;
			}
		}
		return mean/numberOfSamples;
	}

	/**
	 * compute the standard deviation of the channel but taking only normal
	 * values into account (i.e. not artifact or patterns)
	 * 
	 * @param mean
	 *            the mean obtained from
	 * @see data.BTChannel#computeMeanExcludingTranscient()
	 * @return the standard deviation of the channel
	 */

	public double computeStdExcludingTranscient(double mean) {
		return computeStdExcludingTranscient(mean,0,channel.getSize());	
	}

	/**
	 * compute the standard deviation of a period of the channel but taking only normal
	 * values into account (i.e. not artifact or patterns) 
	 * @param mean the mean obtained from
	 * @param start the index from which the std must be computed 
	 * @param end the index until which (excluding) the std must be computed
	 * 
	 * @see data.BTChannel#computeMeanExcludingTranscient()
	 * @return the standard deviation of the channel
	 */
	public double computeStdExcludingTranscient(double mean,int start, int end) {
		int numberOfSamples=0;
		double std=0;
		end = Math.min(channel.getSize(), end);
		for (int i=start;i<end;i++){
			if (isNormal(i)){
				double value = channel.getValue(i) - mean;
				std+= value*value;
				numberOfSamples++;
			}
		}
		return Math.sqrt(std/numberOfSamples);
	}


	/**
	 * Gets the abbreviated channel name
	 * @return - the name
	 */
	public String getChannelName(){
		String name="";
		if (instance!=null){
			name = (String)instance.getDefaultValue(ONTOConfig.SHORT_NAME_FEATURE);
		}		
		return name;
	}
	
	public String getName(){
		return this.name;
	}


	//... getter and Setter for the channel dataType
	public PointType[] getDataType(){
		return dataType;
	}

	public void setPattern(int i) {
		dataType[i]=PointType.PATTERN;
	}

	public void setRepaired(int i) {
		dataType[i]=PointType.REPAIRED;		
	}

	public void setNormal(int i) {
		dataType[i]=PointType.NORMAL;			
	}

	public boolean isRepaired(int i) {
		return dataType[i]==PointType.REPAIRED;		
	}

	public boolean isNormal(int i) {
		return (!isArtifact(i) && dataType[i]==PointType.NORMAL);
	}

	public boolean isPattern(int i) {
		return dataType[i]==PointType.PATTERN;
	}
}