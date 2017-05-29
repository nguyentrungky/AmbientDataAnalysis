package gnad.data;

import gnad.io.CSVReader;
import gnad.io.SPAudioZCRReader;
import gnad.io.SPEnvReader;
import gnad.io.SPLocationReader;
import gnad.io.GPSReader;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
/**
 * 
 * @author Nguyen Trung Ky
 *
 * @param <E>
 */

public class Channel<E>{
	
	/** The raw data integrate with time **/
	@SuppressWarnings("rawtypes")
	 HashMap hm;
	/** The raw data mapping in TreeMap **/
	@SuppressWarnings("rawtypes")
	 TreeMap treeMap;
	/** The raw data (one value per second)**/
	 E[] data;
	/** The length of data **/
	 int realLength;
	/** The started time **/
	 Date startTime;
	/** The end time **/
	 Date endTime;
	/** The period **/
	private int period;
	/** The id of person **/
	private String id;
	/** The channel info **/
	String info;
	/** The CSV file */
	private CSVReader csv;
	/** The GPX file **/
	@SuppressWarnings("rawtypes")
	private GPSReader gpx;
	/** The smart phone location file **/
	@SuppressWarnings("rawtypes")
	private SPLocationReader smphoneLoc;
	/** The smart phone environment file **/
	@SuppressWarnings("rawtypes")
	private SPEnvReader smphoneEnv;
	/** The smart phone audioZCR file**/
	@SuppressWarnings("rawtypes")
	private SPAudioZCRReader smphoneAudioZCR;
	
	
	
	/** get method of hash map **/
	@SuppressWarnings("rawtypes")
	public HashMap getHashMap(){
		return this.hm;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public TreeMap getSortedMap(){
		treeMap = new TreeMap(hm);
		return treeMap;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public HashMap concatHashMap(HashMap map){
		Set set = map.entrySet();
		Iterator iter = set.iterator();
		while (iter.hasNext()){
			Map.Entry me = (Map.Entry) iter.next();
            hm.put(me.getKey(),me.getValue());
        }
		return hm;
	}
	
	/** set and get Element **/
	public void setData(E data, int index){
		this.data[index] = data;
	}
	public E getData(int index){
		return this.data[index];
	}
	public int getLength(){
		return data.length;
	}
	public int getRealLength(){
		return realLength;
	}
	public E[] getArrayData(){
		return data;
	}
	
	public double[] getArrayDataAsDouble(){
		double d[] = new double[data.length];
		for (int i =0; i<data.length;i++){
			d[i]=(Double) data[i];
		}
		return d;
	}
	/** set and get start time **/
	public void setStartTime(Date startTime){
		this.startTime = startTime;
	}
	public Date getStartTime(){
		return this.startTime;
	}
	/** set and get end time **/
	public void setEndTime(Date endTime){
		this.endTime = endTime;
	}
	public Date getEndTime(){
		return this.endTime;
	}
	/** set and get the period **/
	public void setPeriod(int period){
		this.period = period;
	}
	public int getPeriod(){
		return this.period;
	}
	/** set and get the id **/
	public void setId(String id){
		this.id = id;
	}
	public String getId(){
		return this.id;
	}
	/** set and get channel info **/
	public void setInfo(String info){
		this.info = info;
	}
	public String getInfo(){
		return this.info;
	}
	
	
	public String toString(){
		String chaine = info + " " + this.startTime+ " " + this.endTime + " with " + this.realLength + " samples" ;
		return chaine;
	}
	
	
	public  String toStringAll(){
		String chaine = this.toString();
		for ( E v : this.data){
			chaine += v + " ";
		}
		return chaine;
	}
	
	
	Channel(){
		
	}
	 Channel(Date startTime, Date endTime) {
		this.startTime = startTime;
		this.endTime = endTime;
	}
	/**
	 * The Channel constructor read the information from GPX file
	 */
/*	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Channel(String gpxFileName, Date startTime, Date endTime, String info){
		gpx = new GPSReader(gpxFileName);
		this.startTime = startTime;
		this.endTime = endTime;
		data = (E[]) gpx.processXMLFile(startTime, endTime, info);
		hm = gpx.getHashMap();
		realLength = gpx.getRealLength();
	}*/
	
	/**
	 * The Channel constructor read the information from CSV file 
	 */
	/*	@SuppressWarnings("unchecked")
	public Channel(String csvFileName, String id, Date startTime, Date endTime, String info) throws ParseException{
		csv = new CSVReader(csvFileName);
		this.id = id;
		this.info = info;
		this.startTime = startTime;
		this.endTime = endTime;
		try {
			data = (E[]) csv.processLineByLine(startTime, endTime, info);
			hm = csv.getHashMap();
			realLength = csv.getRealLength();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}*/
	
	/**
	 * The Channel constructor read the information from audioZCR file
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Channel(String audioZCR,String info){
		smphoneAudioZCR = new SPAudioZCRReader(audioZCR);
		data = (E[]) smphoneAudioZCR.readAudioZCR();
		realLength = smphoneAudioZCR.getRealLength();
	}
	
	/**
	 * The Channel constructor read the information from smart phone location recorded
	 * @param sphoneLocationName
	 * @param startTime
	 * @param endTime
	 * @param info
	 * @throws IOException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Channel(String sphoneLocationName, Date startTime, Date endTime, String info, String service) throws IOException{
		smphoneLoc = new SPLocationReader(sphoneLocationName);
		this.info = info;
		this.startTime = startTime;
		this.endTime = endTime;
		data = (E[]) smphoneLoc.readBinaryLocation(startTime, endTime, info,service);
		hm = smphoneLoc.getHashMap();
		realLength = smphoneLoc.getRealLength();
	}
	/**
	 * The Channel constructor read the information from the smart phone environment recorded 
	 * @param startTime
	 * @param endTime
	 * @param info
	 * @param sphoneEnvName
	 * @throws IOException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Channel(Date startTime, Date endTime, String info, String sphoneEnvName) throws IOException{
		smphoneEnv = new SPEnvReader(sphoneEnvName);
		this.info = info;
		this.startTime = startTime;
		this.endTime = endTime;
		data = (E[]) smphoneEnv.readEnvSens(startTime, endTime, info);
		hm = smphoneEnv.getHashMap();
		realLength = smphoneEnv.getRealLength();
	}

	
}
