package gnad.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;


/**
 * 
 * @author Nguyen Trung Ky
 *
 * @param <E>
 */
public class PersonRecord {
	
	static String[] firstname ={"Chris", "Alex","Jess","Nat","Pat","Sam","Terry","Vic"};
		
	private Date startTime;
	private Date endTime;
	private String name;
	private int age;
	private String gender;
	private String personId;
	
	private Channel<Coordinate> coordinateGPX, coordinateSP;
	private Channel<Double> altitudeGPX, altitudeSP;
	private Channel<Long> timeStamp;
	private Channel<Double> heartRate, breathRate, temperature;
	private Channel<Float> speed, accuracy, audioZCR;
	private Channel<Triplet> accelerometer, magnetometer, gyroscope, light, pressure, proximity;
	
	
	private ArrayList<Channel> allChannels = new ArrayList<Channel>();
	//private ArrayList<DeviceRecord> deviceRecords = new ArrayList<DeviceRecord>();
	private DeviceRecord deviceRecords; 
	/** Constructor t**/
	PersonRecord(){
	}
	
	public String toString(){
		String chaine = personId + " " + ((name!=null)? (name +" "): "") + ((age!=0)? (age +" ans "): "") + ((gender!=null)? (gender +" "): "")+  "\n";		
		
		for (Channel c : allChannels){
			chaine += "\t" + c+"\n";
		}
		return chaine;
	}
	
	/** set and get method of attributes of person record
	 * @return **/
	public void setDeviceRecord(DeviceRecord deviceRecords){
		this.deviceRecords = deviceRecords;
	}
	public DeviceRecord getDeviceRecord(){
		return this.deviceRecords;
	}
	
	
	public void setStartTime(Date startTime){
		this.startTime = startTime;
	}
	public Date getStartTime(){
		return this.startTime;
	}
	
	public void setEndTime(Date startTime){
		this.endTime = startTime;
	}
	public Date getEndTime(){
		return this.endTime;
	}
	
	public void setAge(String age){
		this.age = new Integer(age);
	}
	public Integer getAge(){
		return this.age;
	}
	
	public void setGender(String gender){
		this.gender = gender;
	}
	public String getGender(){
		return this.gender;
	}

	
	/*public void setDirectory(String directory){
		this.directory = directory;
	}
	public void concatDirectory(String directory){
		this.directory += System.getProperty("file.separator") + directory; 
	}
	public String getDirectory(){
		return this.directory;
	}*/
	public void setPersonId(String personId){
		this.personId = personId;
	}
	public String getPersonId(){
		return this.personId;
	}
		
	public List<Channel> getChannelsOfType(String type){
		List<Channel> channels = new ArrayList<Channel>();
		for (Channel c : allChannels){
			if (c.info.matches(type)) channels.add(c);
		}		
		if (channels.isEmpty()){
			System.out.println("There is no channel of type "  + type+ " for person: " + personId);
		}
		
		return channels;		
	}
	
	public ArrayList<Channel> getAllChannels(){
		return this.allChannels;
	}
	
	
	/** =======================get the raw data of heart rate from CSV records====================== **/
	public Channel<Double> getChannelHR(){
		if(heartRate == null) System.out.println("There is no heart rate info from the person: " + personId);
		return this.heartRate;
	}
	
	
	@SuppressWarnings("rawtypes")
	public TreeMap getSortedMapHR(){
		return heartRate.getSortedMap();
	}
	/** ========================get the raw data of breath rate from CSV records==================== **/
	public Channel<Double> getChannelBR(){
		if(breathRate == null) System.out.println("There is no breath rate informationn from the person: "+ personId);
		return this.breathRate;
	}
	@SuppressWarnings("rawtypes")
	public TreeMap getSortedMapBR(){
		return breathRate.getSortedMap();
	}
	/** ========================get the raw data of temperature from CSV records==================== **/
	public Channel<Double> getChannelTemperature(){
		if(temperature == null) System.out.println("There is no temperature info from the person: " + personId);
		return this.temperature;
	}
	@SuppressWarnings("rawtypes")
	public TreeMap getSortedMapTemperature(){
		return heartRate.getSortedMap();
	}
	/** =========================get the raw data of time from CSV records========================= **/
	public Channel<Long> getChannelTimeStamp() {
		if(timeStamp == null) System.out.println(personId+" does not have bioharness record");
		return timeStamp;
	}
	
	
	
	
	/** ========================get the raw data of coordinate from GPS records===================== **/
	public Channel<Coordinate> getChannelCoordinateGPX(){
		if(coordinateGPX == null) System.out.println(personId+" does not have coordinate record from GPS");
		return this.coordinateGPX;
	}
	
	/** get the raw data of altitude from GPS records **/
	public Channel<Double> getChannelAltitudeGPX(){
		if(altitudeGPX == null) System.out.println(personId+" does not have altitude record from GPS");
		return this.altitudeGPX;
	}
	
	@SuppressWarnings("rawtypes")
	public HashMap getHashMapAltitudeGPX(){
		return altitudeGPX.getHashMap();
	}
	@SuppressWarnings("rawtypes")
	public TreeMap getSortedMapAltitudeGPX(){
		return altitudeGPX.getSortedMap();
	}
	
	/** ========================get the raw data of accelerometer from smart phone records============= **/
	public Channel<Triplet> getChannelAccSP(){
		if(accelerometer == null) System.out.println(personId+ " does not have accelerometer record from smartphone");
		return this.accelerometer;
	}
	
	@SuppressWarnings("rawtypes")
	public HashMap getHashMapAccSP(){
		return accelerometer.getHashMap();
	}
	
	@SuppressWarnings("rawtypes")
	public TreeMap getSortedMapAccSP(){
		return accelerometer.getSortedMap();
	}
	/** ========================get the raw data of altitude from SmartPhone records================== **/
	public Channel<Double> getChannelAltSP(){
		if(altitudeSP == null) System.out.println(personId+ " does not have altitude record from smartphone");
		return this.altitudeSP;
	}
	
	@SuppressWarnings("rawtypes")
	public HashMap getHashMapAltSP(){
		return altitudeSP.getHashMap();
	}
	
	@SuppressWarnings("rawtypes")
	public TreeMap getSortedMapAltSP(){
		return altitudeSP.getSortedMap();
	}
	
	/** ======================get the raw data of coordinate from SmartPhone records=============== **/
	public Channel<Coordinate> getChannelCoordinateSP(){
		if(coordinateSP == null) System.out.println(personId+ " does not have coordinate record from smartphone");
		return this.coordinateSP;
	}
	
	@SuppressWarnings("rawtypes")
	public HashMap getHashMapCoordinateSP(){
		return coordinateSP.getHashMap();
	}
	
	@SuppressWarnings("rawtypes")
	public TreeMap getSortedMapCoordinateSP(){
		return coordinateSP.getSortedMap();
	}
	
	/** ======================get the raw data from speed of SmartPhone records ================= **/
	public Channel<Float> getChannelSpeed(){
		if(speed == null) System.out.println(personId+" does not have speed record from smartphone");
		return this.speed;
	}
	@SuppressWarnings("rawtypes")
	public HashMap getHashMapSpeedSP(){
		return speed.getHashMap();
	}
	@SuppressWarnings("rawtypes")
	public TreeMap getSortedMapSpeedSP(){
		return speed.getSortedMap();
	}
	
	/** ========================get the raw data of accuracy from SmartPhone records=============**/
	public Channel<Float> getChannelAccuracy(){
		if(accuracy == null) System.out.println(personId+" does not have accuracy record from smartphone");
		return this.accuracy;
	}
	@SuppressWarnings("rawtypes")
	public HashMap getHashMapAccuracySP(){
		return accuracy.getHashMap();
	}
	@SuppressWarnings("rawtypes")
	public TreeMap getSortedMapAccuracySP(){
		return accuracy.getSortedMap();
	}
	
	
	
	
	/** ==============================set and get deviceRecord================================ **/
/*	public void setDeviceRecords(ArrayList<DeviceRecord> deviceRecords){
		this.deviceRecords = deviceRecords;
	}
	public ArrayList<DeviceRecord> getDeviceRecords(){
		return this.deviceRecords;
	}
	public void addDeviceRecord(DeviceRecord deviceRecord){
		deviceRecords.add(deviceRecord);
	}
	*/
	
	/*
	@SuppressWarnings("unchecked")
	public void classifyDeviceRecords() throws ParseException, IOException{
		
		for (int i =0; i< deviceRecords.size(); i++){
			DeviceRecord deviceRecord = deviceRecords.get(i);
			
			if (deviceRecord.getDeviceType().matches("GPS") ){
				for(int k = 0; k < deviceRecord.getDataFileList().size(); k++){
					DataFile files = deviceRecord.getDataFileList().get(k);
					if(files.getModality().matches("location-gps")){
						for(int j = 0; j< files.getListFile().size(); j++){
							String fileName = files.getListFile().get(j);
							coordinateGPX = new Channel<Coordinate>(fileName,startTime, endTime, "GPXCoordinates");
							altitudeGPX = new Channel<Double>(fileName, startTime, endTime, "GPXAltitude");
							
							allChannels.add( coordinateGPX);
							allChannels.add( altitudeGPX);
						}
					}
				}
			}
			else if(deviceRecord.getDeviceType().matches("Bioharness")){
				for(int k = 0; k < deviceRecord.getDataFileList().size(); k++){
					DataFile files = deviceRecord.getDataFileList().get(k);
					if(files.getModality().matches("bioharness")){
						for(int j = 0; j< files.getListFile().size(); j++){
							String fileName = files.getListFile().get(j);
							timeStamp = new Channel<Long>(fileName,personId,startTime,endTime,"TimeStamp");
							heartRate = new Channel<Double>(fileName,personId,startTime,endTime,"Heart rate"); 
							breathRate = new Channel<Double>(fileName,personId,startTime,endTime,"Breath rate");
							temperature =new  Channel<Double>(fileName,personId,startTime,endTime,"Temperature");
							
							allChannels.add( timeStamp);
							allChannels.add( heartRate);
							allChannels.add( breathRate);
							allChannels.add( temperature);
						}
					}	
				}
			}
			else if(deviceRecord.getDeviceType().matches("SmartPhone")){
					for(int k = 0; k < deviceRecord.getDataFileList().size(); k++){
						DataFile files = deviceRecord.getDataFileList().get(k);
						if(files.getModality().matches("audio-zcr")){
							for(int j = 0; j< files.getListFile().size(); j++){
								String fileName = files.getListFile().get(j);
								audioZCR = new Channel<Float>(fileName,"Audio Cross Zero");
								allChannels.add( audioZCR);
							}
						}
						else if(files.getModality().matches("location-gps")){
							String firstFileName = files.getListFile().get(0);
							coordinateSP = new Channel<Coordinate>(firstFileName,startTime,endTime,"Coordinate","gps");
							altitudeSP = new Channel<Double>(firstFileName,startTime,endTime,"Altitude","gps");
							speed = new Channel<Float>(firstFileName,startTime,endTime,"Speed","gps");
							accuracy = new Channel<Float>(firstFileName,startTime,endTime,"Accuracy","gps");
							
							for(int j = 1; j< files.getListFile().size(); j++){
								String fileName = files.getListFile().get(j);
								Channel<Double> temp_altitude = new Channel<Double>(fileName,startTime,endTime,"Altitude","gps");
								Channel<Coordinate> temp_coordinate = new Channel<Coordinate>(fileName,startTime,endTime,"Coordinate","gps");
								altitudeSP.concatHashMap(temp_altitude.getHashMap());
								coordinateSP.concatHashMap(temp_coordinate.getHashMap());
							}
							
							allChannels.add( coordinateSP);
							allChannels.add( altitudeSP);
							allChannels.add( speed);
							allChannels.add( accuracy);
						}
						else if(files.getModality().matches("envsen-gps")){
							for(int j = 0; j< files.getListFile().size(); j++){
								String fileName = files.getListFile().get(j);
								accelerometer = new Channel<Triplet>(startTime,endTime,"Accelerometer",fileName);
								magnetometer = new Channel<Triplet>(startTime,endTime,"Magnetometer",fileName);
								gyroscope = new Channel<Triplet>(startTime,endTime,"Gyroscope",fileName);
								light = new Channel<Triplet>(startTime,endTime,"Light",fileName);
								pressure = new Channel<Triplet>(startTime,endTime,"Pressure",fileName);
								proximity = new Channel<Triplet>(startTime,endTime,"Proximity",fileName);
								
								allChannels.add( accelerometer);
								allChannels.add( magnetometer);
								allChannels.add( gyroscope);
								allChannels.add( light);
								allChannels.add( pressure);
								allChannels.add( proximity);
							}
						}
					}
			}		
		}
	} */
}
