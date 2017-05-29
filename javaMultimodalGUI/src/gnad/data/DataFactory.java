package gnad.data;

import gnad.io.CSVReader;
import gnad.io.GPSReader;
import gnad.io.OSMOverpassQuery;
import gnad.io.SPAccelReader;
import gnad.io.SPAudioZCRReader;
import gnad.io.SPEnvReader;
import gnad.io.SPLocationReader;
import gnad.io.WeatherReader;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import uk.ac.abdn.carados.owl2.Owl2CaradosOntology;
import com.fmt.gps.track.TrackPoint;

public class DataFactory {


	private static String start="",end="";

	/** root directory **/
	private String rootdirectory;

	
	public DataFactory (String description_file) throws Exception{
		File file = new File(description_file);

		if (!file.canRead()) throw new Exception("The file " + description_file + " is not readable");

		rootdirectory = file.getParent();
		
		
	}

	public DataSet createDataSet() throws Exception{
		DataSet d = new DataSet();

		readDescriptionFile(d,rootdirectory);
		return d;
	}
	
	public DataSet createDataSet(String ontologyPath) throws Exception{
		Owl2CaradosOntology kb = new Owl2CaradosOntology();
		kb.loadOntology(URI.create(ontologyPath));
		
		DataSet d = this.createDataSet();
		d.setKB(kb);
		return d;
	}
	/**
	 * reads the XML description of the dataset 
	 * @param rootdatasetpath the absolute path to the dataset where the description.xml is stored
	 */
	public void readDescriptionFile(DataSet d,String rootdatasetpath){
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(rootdatasetpath+System.getProperty("file.separator") + "description.xml");
			doc.getDocumentElement().normalize();
			System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
			rootdirectory = rootdatasetpath;
			NodeList nList = doc.getDocumentElement().getChildNodes();
			searchNode(d,nList);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}




	/**
	 * walk the description file and retrieve all the different element of the dataset 
	 * @param nList
	 * @throws ParseException
	 * @throws IOException
	 */

	public void searchNode(DataSet d,NodeList nList) throws ParseException, IOException{
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		for (int count = 0; count < nList.getLength(); count++) {
			Node tempNode = nList.item(count);
			// make sure it's element node.
			if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
				// get node name and value
				/*	if(tempNode.getNodeName().matches("rootdirectory")){
						rootdirectory = tempNode.getTextContent();
					}*/
				if(tempNode.getNodeName().matches("startdate")){
					start = tempNode.getTextContent();	
					d.startTime = (Date)df.parse(start);
				}
				
				if(tempNode.getNodeName().matches("osmmap")){
					String osmfilename = tempNode.getTextContent();					
					try {
						Document osmDoc = OSMOverpassQuery.getXMLFile(this.rootdirectory+File.separator+osmfilename);
						
						if (osmDoc!=null) d.setOsmDoc(osmDoc);
						
					} catch (ParserConfigurationException | SAXException e) {
						e.printStackTrace();
					}				
				}
				
				if(tempNode.getNodeName().matches("enddate")){
					end = tempNode.getTextContent();
					d.endTime = (Date)df.parse(end);
				}
				if(tempNode.getNodeName().matches("person")){
					d.persons.add(createPersonFromXml(tempNode,d));
				}

				if (tempNode.hasChildNodes()) {
					// loop again if has child nodes
					searchNode(d,tempNode.getChildNodes());
				}
			}
		}
	}


	private PersonRecord createPersonFromXml(Node tempNode, DataSet d){
		PersonRecord person = new PersonRecord();														
		person.setStartTime(d.startTime);
		person.setEndTime(d.endTime);

		//set person attributes
		if (tempNode.hasAttributes()) {
			// get attributes names and values
			NamedNodeMap nodeMap = tempNode.getAttributes();
			for (int i = 0; i < nodeMap.getLength(); i++) {
				Node node = nodeMap.item(i);
				if(node.getNodeName().equals("age")){
					person.setAge(node.getNodeValue());
				}
				else if (node.getNodeName().equals("gender")){
					person.setGender(node.getNodeValue());
				}
			}
		}


		for(int j = 0; j < tempNode.getChildNodes().getLength();j++){
			Node childNodePerson = tempNode.getChildNodes().item(j);
			if(childNodePerson.getNodeName().matches("directory")){
				//person.setDirectory(rootdirectory+ System.getProperty("file.separator")+ childNodePerson.getTextContent());
				person.setPersonId(childNodePerson.getTextContent());
			}				

			// get all the records linked to a device
			if(childNodePerson.getNodeName().matches("deviceRecords")){
				for(int k = 0; k < childNodePerson.getChildNodes().getLength(); k++){
					Node childNodeDeviceRecords = childNodePerson.getChildNodes().item(k);
					if (childNodeDeviceRecords.getNodeName().matches("deviceRecord")){
						//person.addDeviceRecord(createDeviceRecord(childNodeDeviceRecords,person));
						createDeviceRecord(childNodeDeviceRecords,person);
					}
				}
			}
		}
		return person;
	}

	private DeviceRecord createDeviceRecord(Node childNodeDeviceRecords, PersonRecord p) {

		DeviceRecord deviceRecord = new DeviceRecord();
		for (int l = 0; l < childNodeDeviceRecords.getChildNodes().getLength(); l++){

			// attributes of the device
			Node childNodeDeviceRecord = childNodeDeviceRecords.getChildNodes().item(l);
			if(childNodeDeviceRecord.getNodeName().matches("deviceInfo")){
				deviceRecord.setDeviceInfo(childNodeDeviceRecord.getTextContent());
			}
			if(childNodeDeviceRecord.getNodeName().matches("deviceType")){
				deviceRecord.setDeviceType(childNodeDeviceRecord.getTextContent());
			}

			if(childNodeDeviceRecord.getNodeName().matches("datafiles")){
				for(int m = 0; m < childNodeDeviceRecord.getChildNodes().getLength(); m++){
					Node childNodeDataFiles = childNodeDeviceRecord.getChildNodes().item(m);
					if(childNodeDataFiles.getNodeName().matches("datafile")){
						DataFile datafile = new DataFile();
						if(childNodeDataFiles.hasAttributes())
						{
							NamedNodeMap nodeMap = childNodeDataFiles.getAttributes();
							for (int i = 0; i < nodeMap.getLength(); i++) {
								Node node = nodeMap.item(i);
								if(node.getNodeName().equals("modality")){
									datafile.setModality(node.getNodeValue());
								}
							}
						}
						for(int n = 0; n< childNodeDataFiles.getChildNodes().getLength();n++){
							Node childNodeDataFile = childNodeDataFiles.getChildNodes().item(n);
							if(childNodeDataFile.getNodeName().matches("file")){
								String file = childNodeDataFile.getTextContent();
								datafile.addFileToList(this.rootdirectory+System.getProperty("file.separator")+p.getPersonId()+System.getProperty("file.separator")+file.trim());
								//System.out.println(this.rootdirectory+System.getProperty("file.separator")+p.getPersonId()+System.getProperty("file.separator")+file.trim());
							}
						}

						deviceRecord.addDataFileToList(datafile);
						try {
							extractDataFromFiles(deviceRecord, p);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}


			/*if(childNodeDeviceRecord.getNodeName().matches("datafiles")){
				for(int m = 0; m < childNodeDeviceRecord.getChildNodes().getLength(); m++){
					Node childNodeDataFiles = childNodeDeviceRecord.getChildNodes().item(m);
					if(childNodeDataFiles.getNodeName().matches("datafile")){
						DataFile datafile = new DataFile();
						if(childNodeDataFiles.hasAttributes())
						{
							NamedNodeMap nodeMap = childNodeDataFiles.getAttributes();
							for (int i = 0; i < nodeMap.getLength(); i++) {
								Node node = nodeMap.item(i);
								if(node.getNodeName().equals("modality")){
									datafile.setModality(node.getNodeValue());
								}
							}
						}
						for(int n = 0; n< childNodeDataFiles.getChildNodes().getLength();n++){
							Node childNodeDataFile = childNodeDataFiles.getChildNodes().item(n);
							if(childNodeDataFile.getNodeName().matches("file")){
								String file = childNodeDataFile.getTextContent();
								datafile.addFileToList(this.rootdirectory+System.getProperty("file.separator")+file.trim());
							}
						}
						deviceRecord.addDataFileToList(datafile);
					}
				}
			}*/
		}

		return deviceRecord;
	}


	private void extractDataFromFiles(DeviceRecord deviceRecord, PersonRecord p) throws Exception {
		switch(deviceRecord.getDeviceType()){
		case "GPS": extractDataFromGPXFile(deviceRecord, p);
		break;
		case "Bioharness":  extractDataFromBioharnessFiles(deviceRecord, p);
		break ;
		case "SmartPhone": extractDataFromSPFiles(deviceRecord, p);
		break;
		case "Website": extractDataFromWebsite(deviceRecord, p);
		break;
		default :
			throw new Exception(" device " + deviceRecord.getDeviceType() + "unknown");
		}
	}
	
	private void extractDataFromWebsite(DeviceRecord deviceRecord, PersonRecord p) throws ParseException{
		for(int k = 0; k < deviceRecord.getDataFileList().size(); k++){
			DataFile files = deviceRecord.getDataFileList().get(k);
			if(files.getModality().matches("weather")){
				for(int j = 0; j< files.getListFile().size(); j++){
					String fileName = files.getListFile().get(j);
					
					Channel<Long> timeStamp = new Channel<Long>(p.getStartTime(),p.getEndTime());
					timeStamp.info=DataType.WEATHER_SERIES_TIME_STAMP;
					
					try {
						WeatherReader<Long> csv = new WeatherReader<Long>(fileName);
						timeStamp.data =  csv.processLineByLine(Long.class,p.getStartTime(),p.getEndTime(),timeStamp.info);
						timeStamp.hm = csv.getHashMap();
						timeStamp.realLength = csv.getRealLength();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					p.getAllChannels().add( timeStamp);
					
					WeatherReader<Double> csv = new WeatherReader<Double>(fileName);
					for (String s : new String[]{DataType.WEATHER_TEMPERATURE, DataType.WEATHER_HUMIDITY, DataType.WEATHER_ATMOSPHERIC_PRESSURE}){
						Channel<Double> ch = new Channel<Double>(p.getStartTime(),p.getEndTime());
						ch.info=s;
						try {
							ch.data = csv.processLineByLine(Double.class,p.getStartTime(),p.getEndTime(),ch.info);
							ch.hm = csv.getHashMap();
							ch.realLength = csv.getRealLength();
						} catch (IOException e) {
							e.printStackTrace();
						}	
						p.getAllChannels().add(ch);
					}
				}
			}
		}
	}


	private void extractDataFromGPXFile(DeviceRecord deviceRecord, PersonRecord p){
		
		for(int k = 0; k < deviceRecord.getDataFileList().size(); k++){
			DataFile files = deviceRecord.getDataFileList().get(k);
			if(files.getModality().matches("location-gps")){
				for(int j = 0; j< files.getListFile().size(); j++){
					String fileName = files.getListFile().get(j);

					/*GPSReader<TrackPoint> gpx = new GPSReader<TrackPoint>(fileName);

					Channel<TrackPoint> coordinateGPX = new Channel<TrackPoint>(p.getStartTime(),p.getEndTime());
					coordinateGPX.info=DataType.GPS_TRACKPOINTS;
					coordinateGPX.data = gpx.processXMLFile(TrackPoint.class,p.getStartTime(),p.getEndTime(), coordinateGPX.info);
					coordinateGPX.hm = gpx.getHashMap();
					coordinateGPX.realLength = gpx.getRealLength();*/

					GPSReader<Double> gpx_alt = new GPSReader<Double>(fileName);
					
					Channel<Double> altitudeGPX = new Channel<Double>(p.getStartTime(),p.getEndTime());
					altitudeGPX.info=DataType.GPX_ALTITUDE;
					altitudeGPX.data = gpx_alt.processXMLFileOld(Double.class,p.getStartTime(),p.getEndTime(), altitudeGPX.info);
					altitudeGPX.hm = gpx_alt.getHashMap();
					altitudeGPX.realLength = gpx_alt.getRealLength();
					
					GPSReader<Coordinate> gpx_coor = new GPSReader<Coordinate>(fileName);
					
					Channel<Coordinate> coorGPX = new Channel<Coordinate>(p.getStartTime(),p.getEndTime());
					coorGPX.info=DataType.GPX_COORDINATES;
					coorGPX.data = gpx_coor.processXMLFileOld(Coordinate.class,p.getStartTime(),p.getEndTime(), coorGPX.info);
					coorGPX.hm = gpx_coor.getHashMap();
					coorGPX.realLength = gpx_coor.getRealLength();
					
					p.getAllChannels().add(coorGPX);
					p.getAllChannels().add(altitudeGPX);
					//p.allChannels.add(coordinateGPX);

				}
			}
		}		
	}

	private void extractDataFromBioharnessFiles(DeviceRecord deviceRecord, PersonRecord p) throws ParseException{
		for(int k = 0; k < deviceRecord.getDataFileList().size(); k++){
			DataFile files = deviceRecord.getDataFileList().get(k);
			if(files.getModality().matches("bioharness")){
				for(int j = 0; j< files.getListFile().size(); j++){
					String fileName = files.getListFile().get(j);


					Channel<Long> timeStamp = new Channel<Long>(p.getStartTime(),p.getEndTime());
					timeStamp.info=DataType.PHYSIOLOGICAL_SERIES_TIME_STAMP;
					try {
						CSVReader<Long> csv = new CSVReader<Long>(fileName);
						timeStamp.data =  csv.processLineByLine(Long.class,p.getStartTime(),p.getEndTime(),timeStamp.info);
						timeStamp.hm = csv.getHashMap();
						timeStamp.realLength = csv.getRealLength();
					} catch (IOException e) {
						e.printStackTrace();
					}	

					p.getAllChannels().add( timeStamp);

					CSVReader<Double> csv = new CSVReader<Double>(fileName);
					for (String s : new String[]{DataType.HEART_RATE,DataType.BREATH_RATE,DataType.BODY_TEMPERATURE}){
						Channel<Double> ch = new Channel<Double>(p.getStartTime(),p.getEndTime());
						ch.info=s;
						try {
							ch.data = csv.processLineByLine(Double.class,p.getStartTime(),p.getEndTime(),ch.info);
							ch.hm = csv.getHashMap();
							ch.realLength = csv.getRealLength();
						} catch (IOException e) {
							e.printStackTrace();
						}	
						p.getAllChannels().add(ch);
					}
				}
			}	
		}
	}


	private void extractDataFromSPFiles(DeviceRecord deviceRecord, PersonRecord p) throws IOException{
		for(int k = 0; k < deviceRecord.getDataFileList().size(); k++){
			DataFile files = deviceRecord.getDataFileList().get(k);
			/*	if(files.getModality().matches("audio-zcr")){
				for(int j = 0; j< files.getListFile().size(); j++){
					String fileName = files.getListFile().get(j);
					Channel<Float> audioZCR = new Channel<Float>(fileName,"Audio Cross Zero");
					p.allChannels.add( audioZCR);
				}
			}*/
			/*else*/
			if(files.getModality().matches("location-gps")){

				GPSReader<TrackPointSpeed> gpx = new GPSReader<TrackPointSpeed>(files.getListFile().get(0));

				Channel<TrackPointSpeed> coordinateGPS = new Channel<TrackPointSpeed>(p.getStartTime(),p.getEndTime());
				coordinateGPS.info=DataType.GPS_TRACKPOINTS;
				coordinateGPS.data = gpx.processSPFiles(TrackPointSpeed.class,files,p.getStartTime(),p.getEndTime(), "gps");
				coordinateGPS.hm = gpx.getHashMap();
				coordinateGPS.realLength = gpx.getRealLength();
				p.getAllChannels().add(coordinateGPS);


				/*String firstFileName = files.getListFile().get(0);
				Channel<Coordinate> coordinateSP = new Channel<Coordinate>(firstFileName,p.getStartTime(), p.getEndTime(),"Coordinate","gps");
				Channel<Float> altitudeSP = new Channel<Float>(firstFileName,p.getStartTime(), p.getEndTime(),"Altitude","gps");
				Channel<Float> speed = new Channel<Float>(firstFileName,p.getStartTime(), p.getEndTime(),"Speed","gps");
				Channel<Float> accuracy = new Channel<Float>(firstFileName,p.getStartTime(), p.getEndTime(),"Accuracy","gps");

				for(int j = 1; j< files.getListFile().size(); j++){
					String fileName = files.getListFile().get(j);
					Channel<Float> temp_altitude = new Channel<Float>(fileName,p.getStartTime(), p.getEndTime(),"Altitude","gps");
					Channel<Coordinate> temp_coordinate = new Channel<Coordinate>(fileName,p.getStartTime(), p.getEndTime(),"Coordinate","gps");
					altitudeSP.concatHashMap(temp_altitude.getHashMap());
					coordinateSP.concatHashMap(temp_coordinate.getHashMap());
				}

				p.allChannels.add( coordinateSP);
				p.allChannels.add( altitudeSP);
				p.allChannels.add( speed);
				p.allChannels.add( accuracy);*/
			}
			/*else if(files.getModality().matches("acceleration")){
				for(int j = 0; j< files.getListFile().size(); j++){
					String fileName = files.getListFile().get(j);
					SPAccelReader<Float> acc = new SPAccelReader<Float>(fileName);
					for (String s : new String[]{DataType.ACCELEROMETERX,DataType.ACCELEROMETERY,DataType.ACCELEROMETERZ}){
						Channel<Float> ch = new Channel<Float>(p.getStartTime(),p.getEndTime());
						ch.info=s;
						ch.data = acc.readAccelSens(Float.class,p.getStartTime(),p.getEndTime(),ch.info);
						ch.hm = acc.getHashMap();
						ch.realLength = acc.getRealLength();	
						p.getAllChannels().add(ch);
					}
				}
			}*/
			/*	else if(files.getModality().matches("envsen-gps")){
				for(int j = 0; j< files.getListFile().size(); j++){
					String fileName = files.getListFile().get(j);
					Channel<Triplet> accelerometer = new Channel<Triplet>(fileName,p.getStartTime(), p.getEndTime(),"Accelerometer");
					Channel<Triplet> magnetometer = new Channel<Triplet>(fileName,p.getStartTime(), p.getEndTime(),"Magnetometer");
					Channel<Triplet> gyroscope = new Channel<Triplet>(fileName,p.getStartTime(), p.getEndTime(),"Gyroscope");
					Channel<Triplet> light = new Channel<Triplet>(fileName,p.getStartTime(), p.getEndTime(),"Light");
					Channel<Triplet> pressure = new Channel<Triplet>(fileName,p.getStartTime(), p.getEndTime(),"Pressure");
					Channel<Triplet> proximity = new Channel<Triplet>(fileName,p.getStartTime(), p.getEndTime(),"Proximity");

					p.allChannels.add( accelerometer);
					p.allChannels.add( magnetometer);
					p.allChannels.add( gyroscope);
					p.allChannels.add( light);
					p.allChannels.add( pressure);
					p.allChannels.add( proximity);
				}
			}*/
		}
	}		




}
