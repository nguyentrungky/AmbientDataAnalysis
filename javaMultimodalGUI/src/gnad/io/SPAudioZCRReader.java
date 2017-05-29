package gnad.io;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
/**
 * 
 * @author Nguyen Trung Ky
 *
 * @param <E>
 */
public class SPAudioZCRReader<E> {
	private E[] data;
	private int realLength;
	private FileInputStream fFile;
	
	public SPAudioZCRReader(String filename){
		realLength = 0;
		try {
			fFile = new FileInputStream(filename);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public int getRealLength(){
		return this.realLength;
	}
	
	@SuppressWarnings("unchecked")
	public E[] readAudioZCR(){
		data = (E[])new Object[5000000];
		DataInputStream inputStream= new DataInputStream(fFile);
		int i = 0;
		try {
			while ( inputStream.available() != 0){
				Float zcr = inputStream.readFloat();
				E value = (E) zcr;
				data[i] = value;
				i++;
				realLength++;
			}
//			System.out.println(i);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}
}
