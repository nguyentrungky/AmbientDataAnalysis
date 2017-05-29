package gnad.data;
/**
 * 
 * @author Nguyen Trung Ky
 *
 */
public class Coordinate {

	/** The x coordinate **/
	private double x;
	/** The y coordinate **/
	private double y;
	
	/**
	 * Constructor
	 * @param x
	 * @param y
	 */
	public Coordinate(double x, double y){
		this.x = x;
		this.y = y;
	}
	
	/**
	 * set and get X coordinate
	 * @param x
	 */
	public void setXCoordinate(double x){
		this.x = x;
	}
	public double getXCoordinate(){
		return this.x;
	}
	/**
	 * set and get Y coordinate
	 * @param y
	 */
	public void setYCoordiante(double y){
		this.y = y;
	}
	public double getYCoordinate(){
		return this.y;
	}
}
