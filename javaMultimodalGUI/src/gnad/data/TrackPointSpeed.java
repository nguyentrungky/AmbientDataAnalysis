package gnad.data;

import com.fmt.gps.track.TrackPoint;

public class TrackPointSpeed extends TrackPoint {

	private double speed = 0.0;
	private double accuracy = 0.0;
	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}	
	


	public double getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(double accuracy) {
		this.accuracy = accuracy;
	}	
	
	public String toString(){
		String obj= super.toString();
		obj+= " "+speed +" " + accuracy+ " accuracy";
		return obj;
	}
	
}
