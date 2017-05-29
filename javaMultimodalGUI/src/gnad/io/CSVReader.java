package gnad.io;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import java.util.Scanner;


/**
 *  This class extract an array of value of an attribute from the CSV file 
 * @author Nguyen Trung Ky
 *
 */

public class CSVReader<E> {
	
	/** The CSV file **/
	private File fFile;
	
	@SuppressWarnings("rawtypes")
	private HashMap hm;
	
	/** The length of attributes**/
	private int realLength;  
	
	/**
	Constructor.
	@param aFileName full name of an existing, readable file.
	 */
	@SuppressWarnings("rawtypes")
	public CSVReader(String aFileName){
			realLength = 0;
		    fFile = new File(aFileName);  
		    hm = new HashMap();
	}
	/**
	 * Constructor without any parameters
	 */
	public CSVReader(){	
	}
	
	/**
	 * get real length of data
	 * @return
	 */

	public int getRealLength(){
		return this.realLength;
	}
	/**
	 * get hash map of raw data integrate with time
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public HashMap getHashMap(){
		return this.hm;
	}
	
	/**
	 * this method return an ArrayList value through time is contained in an attribute of skier and walker 
	 * @param outputfilename
	 * @throws IOException
	 * @throws ParseException 
	 */
	@SuppressWarnings("unchecked")
	public E[] processLineByLine(Class<E> c,Date startTime, Date endTime,String info) throws IOException, ParseException {
		System.out.println(startTime);
		System.out.println(endTime);
		final E[] a = (E[]) Array.newInstance(c, 0);
		int length = (int) ((endTime.getTime() - startTime.getTime())/1000);
		ArrayList<E> data = new ArrayList<E>(length);
		
		ArrayList<String> temp_list = new ArrayList<String>();
		String[] set = {"TimeStamp","Heart rate","Breath rate","Temperature"
						,"Posture","Activity","Acceleration","Battery"};
		int index = 0;
		int indexAttribute = 0;
		while(index < set.length){
			if (set[index].matches(info)){
				indexAttribute = index;
				break;
			}
			else index++;
		}
		
		//Note that FileReader is used, not File, since File is not Close
		Scanner scanner = new Scanner(new FileReader(fFile));
		//first use a Scanner to get each line
		int i = 0;
		String target_value;
		if(scanner.nextLine().startsWith("T"))
			scanner.nextLine();
		while ( scanner.hasNextLine() ){
			temp_list = processLine(scanner.nextLine());
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.S");
			Date d = (Date)df.parse(temp_list.get(0));
			long convert_time = d.getTime();
			/*Date f = new Date(convert_time);
			System.out.println("ngay da chuyen: "+ f);*/
//			Date t = translateStringtoDate(temp_list.get(0));
			if ((convert_time >= startTime.getTime()) && (convert_time <=endTime.getTime())){
				target_value = temp_list.get(indexAttribute);
				if(indexAttribute==0){
					//Date dt = df.parse(target_value);
					//System.out.println(dt);
					Long test = new Long(df.parse(target_value).getTime());
					data.add((E) new Long(df.parse(target_value).getTime()));
					hm.put(d, test);
					//Date dt = new Date(test);
					//System.out.println(test + "=>" + dt);
					//data[i] = translateStringtoDate(target_value);
				}
				else{
					E da = (E) new Double(Double.parseDouble(target_value));
					data.add(da);
					hm.put(d,da);
				}
				//System.out.println(d + " : "+ data[i]);
				i++;
				realLength++;
			}
		}
		scanner.close();
		return data.toArray(a);		
	}
	@SuppressWarnings("deprecation")
	public Date translateStringtoDate(String original_line){
		Scanner scanner =  new Scanner(original_line);
		String line = null;
		int h =0;
		int m = 0;
		int sc = 0;
		int d = 0;
		int month = 0;
		int year = 0;
		Date t = null;
			line= scanner.nextLine();
			Scanner ss = new Scanner(line);
			ss.useDelimiter(" ");
			if (ss.hasNext()){
				 String date = ss.next();
				 Scanner s1 = new Scanner(date);
				 s1.useDelimiter("/");
				 if(s1.hasNext()){
					 String dd = s1.next();
					 d = Integer.parseInt(dd);
					 String mm = s1.next();
					 month = Integer.parseInt(mm);
					 String y = s1.next();
					 year = Integer.parseInt(y);
				 }
				String time = ss.next();
				Scanner s2 = new Scanner(time);
				s2.useDelimiter(":");
				if (s2.hasNext()){
					 String hour = s2.next();
					 h = Integer.parseInt(hour);
					 String minutes = s2.next();
					 m = Integer.parseInt(minutes);
					 String seconds = s2.next();
					 sc = Integer.parseInt(seconds.substring(0, 2));	 
				 } 
			}
			t = new Date(year,month,d,h,m,sc);
			return t;
	}
	/**
	 * this method return ArrayList contains attributes on a line of the reading file
	 * @param aLine
	 * @param list
	 * @return
	 */
	protected ArrayList<String> processLine(String aLine){
	    //use a second Scanner to parse the content of each line 
	    Scanner scanner = new Scanner(aLine);
	    ArrayList<String> list = new ArrayList<String>();
	    String date_time;
	    String br = null;
	    String hr = null;
	    String temperature = null;
	    String posture = null;
	    String activity = null;
	    String acceleration = null;
	    String battery = null;
	    scanner.useDelimiter(",");
	    if ( scanner.hasNext() ){
	      date_time = scanner.next();
	      list.add(0, date_time);
	      hr = scanner.next();
	      list.add(1, hr);
	      br = scanner.next();
	      list.add(2, br);
	      temperature = scanner.next();
	      list.add(3, temperature);
	      posture = scanner.next();
	      list.add(4,posture);
	      activity = scanner.next();
	      list.add(5,activity);
	      acceleration = scanner.next();
	      list.add(6,acceleration);
	      battery = scanner.next();
	      list.add(7,battery);
//	      String brAmplitude = scanner.next();
//	      String ecgAmplitude = scanner.next();
//	      String ecgnoise = scanner.next();
//	      String xMin = scanner.next();
//	      String xPeak = scanner.next();
//	      String yMin = scanner.next();
//	      String yPeak = scanner.next();
//	      String zMin = scanner.next();
//	      String zPeak = scanner.next();
	      //log("Date: " + quote(date.trim()) + " time is" + quote(time.trim()) + 
	    	//	  ", and heart rate: " + quote(hr.trim()));
	    }
	    else {
	      log("Empty or invalid line. Unable to process.");
	    }
	    return list;
	    //no need to call scanner.close(), since the source is a String
	  }	  
		  
	private static void log(Object aObject){
		    System.out.println(String.valueOf(aObject));
	}
		  
	
}
