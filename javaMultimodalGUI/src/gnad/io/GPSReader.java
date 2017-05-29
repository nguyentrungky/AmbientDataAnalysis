package gnad.io;

import gnad.data.Coordinate;
import gnad.data.DataFile;
import gnad.data.TrackPointSpeed;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import com.fmt.gps.data.GpxFileDataAccess;
import com.fmt.gps.track.TrackPoint;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class GPSReader<E> {


	/** The length of data **/
	private int realLength;

	/** The hash map **/
	@SuppressWarnings("rawtypes")
	private HashMap hm;

	/** The GPX file **/
	private File xmlFile;

	@SuppressWarnings("rawtypes")
	public GPSReader(String fileName){
		this.xmlFile = new File(fileName);
		this.realLength = 0;
		hm = new HashMap();
	}

	public int getRealLength(){
		return this.realLength;
	}

	@SuppressWarnings("rawtypes")
	public HashMap getHashMap(){
		return this.hm;
	}

	@SuppressWarnings("unchecked")
	public E[] processXMLFile(Class<E> c,Date startTime, Date endTime,String info){
		List<TrackPoint> points= null;
		try {
			points= GpxFileDataAccess.getPointsFromGpx(new FileInputStream(xmlFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		final E[] data = (E[]) Array.newInstance(c, points.size());

		int i=0;
		for (TrackPoint p : points){
			data[i++] = (E) p;
			/*
			E altitude = (E) (new Double(p.getElevation()));
			Date d = p.getTime();

			if( altitude != null && d != null)
				if(d.getTime() >= startTime.getTime() && d.getTime()<= endTime.getTime()){
					data[realLength] = altitude;				
					hm.put(d, data[realLength]);
					realLength++;
				}*/
		}

		return data;
	}




	@SuppressWarnings("unchecked")
	public E[] processXMLFileOld(Class<E> c,Date startTime, Date endTime,String info){
		String[] set = {"GPXCoordinate","GPXAltitude"};
		int index = 0;
		int indexAttribute = 0;
		while(index < set.length){
			if (set[index].matches(info)){
				indexAttribute = index;
				break;
			}
			else index++;
		}
		System.out.println(indexAttribute);

		int length = (int)(endTime.getTime() - startTime.getTime())/1000;
		final E[] data = (E[]) Array.newInstance(c, length);

		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(xmlFile);
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getDocumentElement().getChildNodes();
			searchNode(nList,data,indexAttribute,startTime,endTime);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}

	@SuppressWarnings({ "unchecked"})
	private  void searchNode(NodeList nodeList,E[] data,int indexAttribute, Date startTime, Date endTime) throws DOMException, ParseException {

		for (int count = 0; count < nodeList.getLength(); count++) {
			Node tempNode = nodeList.item(count);
			// make sure it's element node.
			if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
				// get node name and value
				if(tempNode.getNodeName().matches("trkpt")){
					if(indexAttribute==1){
						for(int j = 0; j < tempNode.getChildNodes().getLength(); j++){
							Node childNode = tempNode.getChildNodes().item(j);
							Double alt = null;
							Date d = null;
							if(childNode.getNodeName().matches("ele")){
								alt = Double.parseDouble(childNode.getTextContent());
								E altitude = (E) alt;
								data[realLength] = altitude;
							}
							if(childNode.getNodeName().matches("time")){
								DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
								d = (Date)df.parse(childNode.getTextContent());
							}
							if( data[realLength] != null && d != null)
								if(d.getTime() >= startTime.getTime() && d.getTime()<= endTime.getTime()){
									hm.put(d, data[realLength]);
									realLength++;
								}
						}

					}
					else if(indexAttribute==0){

						if (tempNode.hasAttributes()) {
							double x = 0;
							double y = 0;
							NamedNodeMap nodeMap = tempNode.getAttributes();
							for (int i = 0; i < nodeMap.getLength(); i++) {
								Node node = nodeMap.item(i);

								if(node.getNodeName().equals("lat")){
									x = Double.parseDouble(node.getNodeValue());
								}
								else if (node.getNodeName().equals("lon")){
									y = Double.parseDouble(node.getNodeValue());
								}
							}
							if(x!=0 && y!= 0){
								Coordinate coordinate = new Coordinate(x,y);
								data[realLength] = (E) coordinate;
							}
						}
						for(int j = 0; j < tempNode.getChildNodes().getLength(); j++){
							Node childNode = tempNode.getChildNodes().item(j);
							Date d = null;
							if(childNode.getNodeName().matches("time")){
								DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
								d = (Date)df.parse(childNode.getTextContent());
							}

							if( data[realLength] != null && d != null)
								if(d.getTime() >= startTime.getTime() && d.getTime()<= endTime.getTime()){
									hm.put(d, data[realLength]);
									realLength++;
								}
						}
					}
				}
				if (tempNode.hasChildNodes()) {
					// loop again if has child nodes
					searchNode(tempNode.getChildNodes(),data,indexAttribute,startTime,endTime);
				}
			}
		}
	}

	public E[] processSPFiles(Class<TrackPointSpeed> c,DataFile files, Date startTime, Date endTime,String service_provider) throws IOException {
		String[] set = {"Coordinate","Altitude","Speed","Accuracy"};
		int length = (int)(endTime.getTime() - startTime.getTime())/1000;
		List<TrackPointSpeed> points= new ArrayList<TrackPointSpeed>(length);

		realLength=0;
		for( String filename : files.getListFile()){
			DataInputStream inputStream= new DataInputStream(new FileInputStream(filename));																	

			while ( inputStream.available() != 0){
				String provider = inputStream.readUTF();
				Date d = new Date(inputStream.readLong());
				double latitude = inputStream.readDouble();
				double longitude = inputStream.readDouble();
				double altitude = inputStream.readDouble();
				float speed = inputStream.readFloat();
				float accuracy = inputStream.readFloat();

				if((d.getTime() >= startTime.getTime()) && (d.getTime() <= endTime.getTime())){
					//System.out.println(" date:" + startTime);
					if(provider.equals(service_provider)){
						TrackPointSpeed p = new TrackPointSpeed();
						p.setElevation((int) altitude);
						p.setLat(latitude);
						p.setLon(longitude);
						p.setAccuracy(accuracy);
						p.setSpeed(speed);
						p.setTime(d);
						p.setPosition(realLength);
						points.add(p);
						realLength++;
					}
				}
			}
			inputStream.close();
		}


		final E[] data = (E[]) Array.newInstance(c, points.size());

		int i=0;
		for (TrackPointSpeed p : points){
			data[i++] = (E) p;

		}

		return data;

	}
}
