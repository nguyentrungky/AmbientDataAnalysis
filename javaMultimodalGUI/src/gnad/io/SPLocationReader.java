package gnad.io;

import gnad.data.Coordinate;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

public class SPLocationReader<E> {
	
	/** The raw data **/
	private E[] data;
	
	@SuppressWarnings("rawtypes")
	private HashMap hm;
	
	/** The length of attributes**/
	private int realLength;
	
	/** The location smart phone file **/
	private FileInputStream fFile;
	
	@SuppressWarnings("rawtypes")
	public SPLocationReader(String filename){
		try {
			fFile = new FileInputStream(filename);
			realLength = 0;
			hm = new HashMap();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public int getRealLength(){
		return this.realLength;
	}
	
	@SuppressWarnings("rawtypes")
	public HashMap getHashMap(){
		return this.hm;
	}
	
	@SuppressWarnings("unchecked")
	public E[] readBinaryLocation(Date startTime, Date endTime, String info,String service_provider){
		try {
			int length = (int)(endTime.getTime() - startTime.getTime())/1000;
			data = (E[])new Object[length];
			DataInputStream inputStream= new DataInputStream(fFile);
			int i = 0;
			String[] set = {"Coordinate","Altitude","Speed","Accuracy"};
			int index = 0;
			int indexAttribute = 0;
			while(index <= set.length){
				if (set[index].matches(info)){
					indexAttribute = index;
					break;
				}
				else index++;
			}
			
			while ( inputStream.available() != 0){
				String provider = inputStream.readUTF();
				long time = inputStream.readLong();
				Date d = new Date(time);
				double latitude = inputStream.readDouble();
				double longitude = inputStream.readDouble();
				Double altitude = inputStream.readDouble();
				Float speed = inputStream.readFloat();
				Float accuracy = inputStream.readFloat();
				
				if((d.getTime() >= startTime.getTime()) && (d.getTime() <= endTime.getTime())){
					//System.out.println(" date:" + startTime);
					if(provider.equals(service_provider)){
						switch(indexAttribute){
						case 0:
							latitude = latitude - 4;
							longitude = longitude -29;
							Coordinate coordinate = new Coordinate(latitude,longitude);
							hm.put(d, coordinate);
							data[i] = (E) coordinate;
							break;
						case 1:
							altitude = altitude - 1988;
							E alt = (E) altitude;
							hm.put(d, altitude);
							data[i] = alt;
							break;
						case 2:
							E spd = (E) speed;
							hm.put(d, speed);
							data[i] = spd;
							break;
						case 3:
							E ms = (E)accuracy;
							hm.put(d, accuracy);
							data[i] = ms;
							break;
						}
						i++;
						realLength++;
					}
				}
			}
//			System.out.println(data);
//			System.out.println(i);
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}
}
