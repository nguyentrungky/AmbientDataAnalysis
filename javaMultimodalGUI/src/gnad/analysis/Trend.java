package gnad.analysis;


public class Trend extends NewInterval{
	
	private String type;
	
	private String value;
	
	/** Value used to compute the limit for the slope. */
	public final static double coefSlopeLimit=0.1;     

	/** Threshold in minutes to decide if it is a slow or reasonable change. */
	public final static double slowChange=15;  
	
	public Trend(int start, int end) {
		super(start, end);
	}
	
	public Trend(int start, NewInterval previous){
		super(start,previous);
	}
	
	
	
	public double computeMaxValue(int start, int end, double[]data){
		double max = data[start];
		for(int i = start; i <= end; i++){
			if (max < data[i]){
				max = data[i];
			}
		}
		return max;
	}
	public double computeMinValue(int start, int end, double[] data){
		double min = data[start];
		for(int i = start; i <= end; i++){
			if (min > data[i]){
				min = data[i];
			}
		}
		return min;
	}
	
	public void setType(String type){
		this.type = type;
	}
	public String getType(String type){
		return this.type;
	}
	public void setValue(String value){
		this.value = value;
	}
	public String getValue(String value){
		return this.value;
	}
	
	/**
	 * Classify the segment into a trend {steady, increasing, decreasing} and compute its parameters {quickly, slowly}.
	 */
	public void classify(double[] data) {

		// classify all trends, even if short
		double startValue = data[start];
		double endValue = 	data[end];
		double deltaY = endValue - startValue;
		
		//find the maxValue and minValue in the segment
		double highestAcceptableValue = computeMaxValue(start, end, data);
		double lowestAcceptableValue = computeMinValue(start, end, data);
		double amplitudeRange= highestAcceptableValue-lowestAcceptableValue;
		// period in minutes
		double period = (end-start)/60.0;
		double angle = Math.abs(deltaY)/period;			
		double flatTrendThreshold = error/2;
		double rapidChangeThreshold = error;
		if (Math.abs(deltaY) <= flatTrendThreshold){
			setType("FLATTREND_CLASS");
			setValue("STEADY_VALUE_FEATURE");
			float mean = 0;
			int nbNormal=0;
			for (int i=start; i<=end;i++){
				mean+=data[i];
				nbNormal++;
			}
			mean/=nbNormal;
			float std =0;
			for (int i=start; i<=end;i++){
				std+=Math.pow((data[i] -mean),2);
			}			
			std = (float)Math.sqrt(std/nbNormal);
			startValue=mean;
			endValue=mean;
			
			if (4*std > rapidChangeThreshold){
				startValue = mean-2*std;
				endValue = mean+2*std;				
				setValue("WANDERING_VALUE_FEATURE");			
			}
			
		}
		else if (deltaY > 0){
			setType("UPTREND_CLASS");
			setValue("UPWARD_VALUE_FEATURE");

			if (angle > amplitudeRange*coefSlopeLimit){				
				setValue("quick");
			}
			else if (period>slowChange) {
				setValue("slow");
			}		
		}
		else{
			setType("DOWNTREND_CLASS");	
			setValue("DOWNWARD_VALUE_FEATURE");
			if (angle > amplitudeRange*coefSlopeLimit){
				setValue("quick");
			}			
		}
	}
}
