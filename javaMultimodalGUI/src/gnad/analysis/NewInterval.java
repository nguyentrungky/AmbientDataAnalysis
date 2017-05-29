package gnad.analysis;

import gnad.data.ChannelInfo;

/**
 * Interval - segment Description with extra information for segmentation and merging extra info is forward 
 * and backward links, and mergeError (error from merging this segment with previous segment)
 * 
 * @author nguyentk
 */
public class NewInterval implements Comparable<NewInterval>{

	private String name;
	
	private String type;
	
	private String value;
	
	private double speed;
	
	/** Value use to measure speed quickly **/
	private double quickValue = 400/3600.0;
	
	/** Value use to measure speed slowly **/
	private double slowValue = 300/3600.0;

	/** The start of the interval. */
	public int start;            

	/** The end of the interval. */
	public int end;        

	/** The error from merging the current segment with the previous segment. */
	double error; 

	/** The previous segment. */
	protected NewInterval previous;

	/** The next segment. */
	protected NewInterval next;   


	/**
	 * Instantiates a new interval.
	 * 
	 * @param start The index of the start of the interval
	 * @param end The index of the end of the interval
	 */
	public NewInterval(int start, int end) {
		// constructor
		this.start = start; 
		this.end = end;
		next = null;
		previous =null;
	}


	/**
	 * Instantiates a new interval.
	 * 
	 * @param index The index of first value of the segment to create 
	 * @param previous The previous segment
	 */
	public NewInterval(int index, NewInterval previous) {		
		this(index,index+1);
		// link this segment to previous segment
		this.previous = previous;
		if (previous != null)
			previous.next = this;

		// to be updated when next segment created
		next = null;  
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return this.name;
	}
	
	public void setSpeed(double speed){
		this.speed = speed;
	}
	
	public double getSpeed(){
		return this.speed;
	}
	
	public void setType(String type){
		this.type = type;
	}
	
	public String getType(){
		return this.type;
	}
	
	public void setValue(String value){
		this.value = value;
	}
	
	public String getValue(){
		return this.value;
	}
	
	public int getStartTime(){
		return start;
	}
	
	public void setEndTime(int endTime){
		this.end = endTime;
	}
	public int getEndTime(){
		return end;
	}
	
	public String toString() { 
		return start + " " + end + "name: " + name + "type: " + type + " error :" + error + "\n";
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

	@Override
	public int compareTo(NewInterval i2) {
		if (this.start<i2.start)	return -1;
		else if (this.start>i2.start)	return 1;
		else{
			if (this.end<i2.end)	return -1;
			else if (this.end>i2.end)	return 1;
			return 0;
		}
	}
	
	public void classify(double[] data, ChannelInfo info) {

		// classify all trends, even if short
		double startValue = data[start];
		double endValue = 	data[end];
		double deltaY = endValue - startValue;
		
		// period in minutes
		//double period = (end-start)/60;
		double angle = Math.abs(deltaY)/(end-start);
		double flatTrendThreshold = info.getSegmentError()/2;
		setName("Interval");
		setSpeed(angle);
				
		if (Math.abs(deltaY) <= flatTrendThreshold){
			setType("go_flat");
			if (angle > quickValue){				
				setValue("Quickly");
			}
			else if (angle > slowValue && angle < quickValue) {
				setValue("Normal");
			}
			else if (angle < slowValue){
				setValue("Slowly");
			}
		}
		else if (deltaY > 0){
			setType("go_up");
			if (angle > quickValue){				
				setValue("Quickly");
			}
			else if (angle > slowValue && angle < quickValue) {
				setValue("Normal");
			}
			else if (angle < slowValue){
				setValue("Slowly");
			}
		}
		else{
			setType("go_down");	
			if (angle > quickValue){
				setValue("Quickly");
			}			
			else if (angle > slowValue && angle < quickValue) {
				setValue("Normal");
			}
			else if (angle < slowValue){
				setValue("Slowly");
			}
		}
	}
}

