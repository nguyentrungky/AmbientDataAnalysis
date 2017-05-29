package gnad.data;

import gnad.analysis.MedianFilter;

/**
 * 
 * @author Nguyen Trung Ky
 *
 */
public class ChannelInfo {
	// general info
	/** The name. */
	public String name;       // short name, eg BM
		
	// low/high data range
	/** The lowest value. */
	public double lowestValue;  // lowest possible value (if not artefact)
		
	/** The highest value. */
	public double highestValue;  // highest possible value (if not artefact)
	
	// segment error value
	/** The segment error. */
	public double segmentError;  // controls segmentation
	
	public ChannelInfo(){
		
	}
	
	public void setSegmentError(double segmentError){
		this.segmentError = segmentError;
	}
	
	public double getSegmentError(){
		return this.segmentError;
	}
	public double computeMaxValue(double[]data){
		double max = data[0];
		for(int i = 1; i < data.length; i++){
			if (max < data[i]){
				max = data[i];
			}
		}
		highestValue = max;
		return highestValue;
	}
	
	public double computeMinValue(double[] data){
		double min = data[0];
		for(int i = 1; i < data.length; i++){
			if (min > data[i]){
				min = data[i];
			}
		}
		lowestValue = min;
		return lowestValue;
	}
	
	public double[] initChannelError(double[] data) {
		// half of the window for the baseline (default 60 secondes)
		// use 2 minutes for all channel except for temperature for which 10 minutes is prefer
		int halfWin = 60; 

		Object[] res = (new MedianFilter()).filterPlusResidu(data, halfWin);
		
		// continue to compute the residu
		double dynamic = computeMaxValue(data)- computeMinValue(data);
		double[] residu = (double[])res[1];
		double[] output = (double[])res[0];
		double accumulation=0;
		int numberOfNormalvalues=0;

		// normalize the residue and compute the mean for the next variance computation
		for (int i=0;i<residu.length;i++){
				residu[i]/=dynamic;
				accumulation += residu[i];	
				numberOfNormalvalues++;
		}

		double mean = accumulation/numberOfNormalvalues;
		double var =0.0;
		// compute the variance
		for (int i=0;i<residu.length;i++){
				var += (residu[i] - mean)*(residu[i] - mean);
		}	
		var/= numberOfNormalvalues;
		// use the logarithm base 10
		var = Math.log10(var);

		double alpha = 0.1;
		if (var < -4){
			alpha=0.05;
		}
		else{
			alpha=0.1;
		}
		setSegmentError(dynamic*alpha);
		// System.out.println("channel " + channel.info.name + " tolerance is " + alpha + " threshold is "+ channel.info.segmentError);
		// dereference the objects to facilitate the garbage collection
		residu=null;
		res=null;
		return output;
	}
	
}
