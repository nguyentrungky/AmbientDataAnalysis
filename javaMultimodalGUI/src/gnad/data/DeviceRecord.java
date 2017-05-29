package gnad.data;

import java.util.ArrayList;
/**
 * 
 * @author Nguyen Trung Ky
 *
 */
public class DeviceRecord {
	private String deviceInfo;
	private String deviceType;
	private ArrayList<DataFile> datafiles = new ArrayList<DataFile>();
	public DeviceRecord(){
		
	}
	/** set and get method **/
	public void setDeviceInfo(String deviceInfo){
		this.deviceInfo = deviceInfo;
	}
	public String getDeviceInfo(){
		return this.deviceInfo;
	}
	
	public void setDeviceType(String deviceType){
		this.deviceType = deviceType;
	}
	public String getDeviceType(){
		return this.deviceType;
	}
	
	public void addDataFileToList(DataFile datafile){
		datafiles.add(datafile);
	}
	public ArrayList<DataFile> getDataFileList(){
		return this.datafiles;
	}
}
