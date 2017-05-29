package gnad.analysis;
/**
 * 
 * @author nguyentk
 *
 */

public class Carac {
	
	/** name of carac **/
	private String name;
	/** previous window of carac **/
	private int previousWin;
	/** current window of carac **/
	private int currentWin;
	
	public Carac(){}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return this.name;
	}
	
	public void setWindow(int previousWin, int currentWin){
		this.previousWin = previousWin;
		this.currentWin = currentWin;
	}
	
	public Object[] getWindow(){
		Object[] win = {previousWin, currentWin};
		return win;
	}
	
	public int getPreviousWin(){
		return previousWin;
	}
	
	public int getCurrentWin(){
		return currentWin;
	}
}
