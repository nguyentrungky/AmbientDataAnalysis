package gnad.io;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import gnad.data.Triplet;

public class SPAccelReader<E> {
	/** The raw data **/
	private E[] data;
	
	/** The length of attributes**/
	private int realLength;
	
	@SuppressWarnings("rawtypes")
	private HashMap hm;
	
	/** The environment smart phone file **/
	private FileInputStream fFile;
	
	@SuppressWarnings("rawtypes")
	public SPAccelReader(String filename){
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
	public E[] readAccelSens(Class<E> c, Date startTime, Date endTime, String info){
		try {
			final E[] a = (E[]) Array.newInstance(c, 0);
			int length = (int)((endTime.getTime() - startTime.getTime())/1000);
			ArrayList<E> data = new ArrayList<E>(length);
			DataInputStream inputStream= new DataInputStream(fFile);
			int i = 0;
			String[] set = {"TimeStamp","AccelerometerX","AccelerometerY","AccelerometerZ"};
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
				//short type = inputStream.readShort();
				long  time = inputStream.readLong();
				float x = inputStream.readFloat();
				float y = inputStream.readFloat();
				float z = inputStream.readFloat();
				Date d = new Date(time);
				if((d.getTime() >= startTime.getTime()) && (d.getTime() <= endTime.getTime())){
					//System.out.println("date:" + d);
					switch(indexAttribute){
					case 0:
						break;
					case 1:
						E dx = (E) new Float(x);
						data.add(dx);
						hm.put(d,dx);
						break;
					case 2:
						E dy = (E) new Float(y);
						data.add(dy);
						hm.put(d,dy);
						break;
					case 3:
						E dz = (E) new Float(z);
						data.add(dz);
						hm.put(d,dz);
						break;
					}
					i++;
					realLength++;
				}
			}	
			//inputStream.close();
			return data.toArray(a);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}

}
