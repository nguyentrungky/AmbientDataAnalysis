package gnad.analysis;

/**
 * 
 * @author nguyentk
 *
 */

public class Chi {
	
	/** the name of chi **/
	private String name;
	/** the arg1 of the chi **/
	private double arg1;
	/** the arg2 of the chi **/
	private double arg2;
	
	public Chi(){}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return this.name;
	}
	
	public void setArg1(double arg1){
		this.arg1 = arg1;
	}
	
	public void setArg2(double arg2){
		this.arg2 = arg2;
	}
	
	public double getArg1(){
		return this.arg1;
	}
	
	public double getArg2(){
		return this.arg2;
	}
	
}
