package gnad.data;

import java.util.Date;

public class UserDate {
	private Date startDate;
	private Date endDate;
	
	public UserDate(){}
	
	public void setStartDate(Date startDate){
		this.startDate = startDate;
	}
	
	public void setEndDate(Date endDate){
		this.endDate = endDate;
	}
	
	public Date getStartDate(){
		return startDate;
	}
	
	public Date getEndDate(){
		return endDate;
	}
}
