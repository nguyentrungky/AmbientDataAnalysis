package gnad.data;
/**
 * 
 * @author Nguyen Trung Ky
 *
 */
public class Triplet {
	
	/** The x coordinate **/
	private float x;
	/** The y coordinate **/
	private float y;
	/** The z coordinate **/
	private float z;
	/**
	 * Constructor
	 * @param x
	 * @param y
	 */
	public Triplet(float x, float y, float z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	/**
	 * set and get X coordinate
	 * @param x
	 */
	public void setFirstValue(float x){
		this.x = x;
	}
	public float getFirstValue(){
		return this.x;
	}
	/**
	 * set and get Y coordinate
	 * @param y
	 */
	public void setSecondValue(float y){
		this.y = y;
	}
	public float getSecondValue(){
		return this.y;
	}
	/**
	 * set and get Z coordinate
	 */
	public void setThirdValue(float z){
		this.z = z;
	}
	public float getThirdValue(){
		return this.z;
	}
}
