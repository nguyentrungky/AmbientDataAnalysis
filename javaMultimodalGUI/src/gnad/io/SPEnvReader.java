package gnad.io;

import gnad.data.Triplet;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

public class SPEnvReader<E> {
	
	/** The raw data **/
	private E[] data;
	
	/** The length of attributes**/
	private int realLength;
	
	@SuppressWarnings("rawtypes")
	private HashMap hm;
	
	/** The environment smart phone file **/
	private FileInputStream fFile;
	
	@SuppressWarnings("rawtypes")
	public SPEnvReader(String filename){
		realLength = 0;
		try {
			fFile = new FileInputStream(filename);
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
	public E[] readEnvSens(Date startTime, Date endTime, String info){
		try {
			int length = (int)(endTime.getTime() - startTime.getTime())/100;
			data = (E[])new Object[length];
			DataInputStream inputStream= new DataInputStream(fFile);
			int i = 0;
			String[] set = {"Accelerometer","Magnetometer","Gyroscope","Light","Pressure","Proximity"};
			int index = 0;
			int indexAttribute = 0;
			while(index <= set.length){
				if (set[index].matches(info)){
					indexAttribute = index;
					break;
				}
				else index++;
			}
			//System.out.println("The index of attribute concerned:"+ index);
			while ( inputStream.available() != 0){
				short type = inputStream.readShort();
				long  time = inputStream.readLong();
				float x = inputStream.readFloat();
				float y = inputStream.readFloat();
				float z = inputStream.readFloat();
				Date d = new Date(time);
				if((d.getTime() >= startTime.getTime()) && (d.getTime() <= endTime.getTime())){
					//System.out.println("date:" + d);
					switch(indexAttribute){
					case 0:
						if(type == 1){
							Triplet triplet = new Triplet(x,y,z);
							data[i] = (E) triplet;
							hm.put(d, triplet);
//							System.out.println("Acc first value:" + triplet.getFirstValue()
//												+ " second value: "+ triplet.getSecondValue()
//												+" third value: "+ triplet.getThirdValue());
						}
						break;
					case 1:
						if(type == 2){
							Triplet triplet = new Triplet(x,y,z);
							data[i] = (E) triplet;
							hm.put(d, triplet);
//							System.out.println("Magneto first value:" + triplet.getFirstValue()
//									+ " second value: "+ triplet.getSecondValue()
//									+" third value: "+ triplet.getThirdValue());
						}
						break;
					case 2:
						if(type == 4){
							Triplet triplet = new Triplet(x,y,z);
							data[i] = (E) triplet;
							hm.put(d, triplet);
//							System.out.println("Gyroscope first value:" + triplet.getFirstValue()
//									+ " second value: "+ triplet.getSecondValue()
//									+" third value: "+ triplet.getThirdValue());
						}
						break;
					case 3:
						if(type == 5){
							Triplet triplet = new Triplet(x,y,z);
							data[i] = (E) triplet;
//							System.out.println("Light first value:" + triplet.getFirstValue());
						}
						break;
					case 4:
						if(type == 6){
							Triplet triplet = new Triplet(x,y,z);
							data[i] = (E) triplet;
							hm.put(d, triplet);
//							System.out.println("Pressure first value:" + triplet.getFirstValue());
							
						}
						break;
					case 5:
						if(type == 8){
							Triplet triplet = new Triplet(x,y,z);
							data[i] = (E) triplet;
							hm.put(d, triplet);
//							System.out.println("proximity:" + triplet.getFirstValue());
						}
						break;
					}
					i++;
					realLength++;
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
