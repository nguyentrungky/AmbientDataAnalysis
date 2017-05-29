package gnad.data;

import java.util.ArrayList;
/**
 * 
 * @author Nguyen Trung Ky
 *
 */
public class DataFile {
	private String modality;
	private ArrayList<String> fileListName = new ArrayList<String>();
	
	public DataFile(){
		
	}
	
	public void setModality(String modality){
		this.modality = modality;
	}
	public String getModality(){
		return this.modality;
	}
	
	public ArrayList<String> getListFile(){
		return this.fileListName;
	}
	
	public ArrayList<String> addFileToList(String fileName){
		fileListName.add(fileName);
		return fileListName;
	}
}
