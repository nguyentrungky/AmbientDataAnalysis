package fr.lig.test;

import fr.lig.BTCOREDisplay;
import fr.lig.displayableObject.GUIInterval;
import gnad.analysis.GPSNLG;
import gnad.analysis.TreatGPSBioharnessTrace;
import gnad.data.Channel;
import gnad.data.ChannelInfo;
import gnad.data.DataSet;
import gnad.data.DataType;
import gnad.data.PersonRecord;
import gnad.data.storyElement.Activity;
import gnad.data.storyElement.PhysiologyState;

import java.awt.Color;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import fr.lig.data.BTChannel;
import fr.lig.data.BTData;
/**
 * Class for testing the GUI. Simply creates some data to display
 * 
 * 
 * @author fportet
 *
 */
public class GUITest {
	
	public static void main(String[] args) throws Exception {
		SimpleDateFormat df = new SimpleDateFormat("yyMMddHHmmss");
		Set<GUIInterval> intervals = new HashSet<GUIInterval>(); 
		BTData data = null;
		TreatGPSBioharnessTrace test = new TreatGPSBioharnessTrace();
		/*if (args.length !=1){
			System.err.println("Error the program should have at least one argument");
			System.out.println("Usage : executable path_to/description.xml");
			System.exit(1);
		}*/
		DataSet d = test.readGPSData(args[0], args[1]);
		//DataSet d = test.readGPSData("", test.KB_URL);
		String fileName = args[2];
		List<Activity> a = test.processGPSTrace(d);
		List<PhysiologyState> state= test.processBioharnessTrace(d,args[3]);
		System.out.println(URI.create(fileName));
		d.getKB().saveOntology(fileName);
		GPSNLG nlg = new GPSNLG();
		String text = nlg.realise(fileName);
		
		Channel hr_channel = null; Channel br_channel = null; Channel altitude_channel = null;
		Channel weather_temperature = null; Channel time_stamp = null; Channel weather_timestamp = null;
		float[] hr1 = null;float[] br1 = null;
		float[] hr2 = null;float[] br2 = null;
		float[] alt1 = null; float[] alt2= null;
		float[] weather = null; Date[] weather_times = null;
		int number_person = d.getPersons().size();
        int count_person = 0;
		for(int i = 0; i < number_person; i++){
        	PersonRecord  p = d.getPersons().get(i);
        	if(p!=null){
	        	count_person = count_person + 1;
	            Iterator it = p.getAllChannels().iterator();
	            while(it.hasNext()){
	                Channel c = (Channel) it.next();
	                if(c.getInfo().matches(DataType.HEART_RATE)){
	                    hr_channel = c;
	                }
	                else if(c.getInfo().matches(DataType.BREATH_RATE)) {
	                    br_channel = c;
	                }
	                else if(c.getInfo().matches(DataType.GPX_ALTITUDE)){
	                    altitude_channel = c;
	                }
	                else if(c.getInfo().matches(DataType.WEATHER_TEMPERATURE)){
	                	weather_temperature = c;
	                }
	                else if(c.getInfo().matches(DataType.PHYSIOLOGICAL_SERIES_TIME_STAMP)){
	                    time_stamp = c;
	                   
	                }
	                else if(c.getInfo().matches(DataType.WEATHER_SERIES_TIME_STAMP)){
	                	weather_timestamp  = c;
	                }
	            }
        	}
		


			ChannelInfo info = new ChannelInfo();
			double[] hr_filter = info.initChannelError(hr_channel.getArrayDataAsDouble());
	        double[] br_filter = info.initChannelError(br_channel.getArrayDataAsDouble());
	        
	        Map treeMap1 = new TreeMap(altitude_channel.getHashMap());
	        System.out.println(treeMap1.size());
	        double[] alt = new double[treeMap1.size()];
            Set set1 = treeMap1.entrySet();                        
            Iterator i1 = set1.iterator();           
            Map.Entry me1 = null;           
           
            boolean m1 = true;           
            int k = 0;
           
            while(i1.hasNext()) {
                if(i1.hasNext()!=false)
                    me1 = (Map.Entry)i1.next();
                 if(m1){
                     System.out.println("start altitude records: " + me1.getKey());
                     m1 =false;         
                 }
                 if(i1.hasNext()!=false){
                     alt[k] = (Double) me1.getValue();
                     k++;
                 }             
            }
            System.out.println("end altitude records: " + me1.getKey());  
            int signalSize = treeMap1.size();
	        //int signalSize = altitude_channel.getRealLength();
			Date startTime = altitude_channel.getStartTime();
			Date endTime = altitude_channel.getEndTime();
			
			System.out.println(signalSize);
			/*int signalSize = 30*60*10000;
			Date startTime = df.parse("000101100000");
			Date endTime = new Date(startTime.getTime()+signalSize*1000);*/
			if(count_person == 1){
				data = new BTData(0, startTime, endTime, null);
			}
			
			hr1 = new float[signalSize];
			br1 = new float[signalSize];
			hr2 = new float[signalSize];
			br2 = new float[signalSize];
			alt1 = new float[signalSize];
			alt2 = new float[signalSize];
			weather = new float[signalSize+900];
			weather_times = new Date[signalSize];
			for (int j = 0 ; j < signalSize; j++)
			{
				if(count_person == 1){
					hr1[j] = (float) hr_filter[j];
					br1[j] = (float) br_filter[j];
					alt1[j] = (float) alt[j];
				}
				if(count_person == 2){
					hr2[j] = (float) hr_filter[j];
					br2[j] = (float) br_filter[j];
					alt2[j] = (float) alt[j];
				}
			}
			
			double previous_value_temperateur = (Double) weather_temperature.getData(0);
			weather[0] = (float) previous_value_temperateur;
			int index = 1;
			for (int j = 0;  j < hr_channel.getRealLength(); j++){
			boolean diff = true;
			Long t1 = (Long) time_stamp.getData(j);
			int dem = index;
				for(int l = 1; l< weather_timestamp.getRealLength(); l++){
				Long t2 = (Long) weather_timestamp.getData(l);
					if( t1.compareTo(t2) ==0  ){
					double current_value_temperateur = (Double) weather_temperature.getData(l);
					while(dem < j){
						weather[index] = linear_interpolation(previous_value_temperateur, current_value_temperateur, 0.5 );
						index++;
						dem = index;
					}
					System.out.println(dem);
					previous_value_temperateur = current_value_temperateur;
					diff = false;
					break;
					//weather[l] = (float)current_value_temperateur;
					//weather_times[l] = new Date(t2);
					/*double value_humidity = (Double) weather_humidity.getData(k);
					double value_apressure = (Double) atmospheric_pressure.getData(k);*/
					//System.out.println("thoi tiet: "+ value);
					}
				}
			}
			
			/*float[] signal = new float[signalSize];
			for (int i=0;i<signalSize;i++){
				signal[i] = (float) (50.0+ 50.0*Math.sin(2*Math.PI*i*0.001));
			}
			for (int i=0;i<200;i++)
				signal[i+signalSize/2]=i;	
			signal[signalSize/2+200]=(float) 211.6666666;
	
			intervals.add(new GUIInterval("bouh", new Date (startTime.getTime() + 1000*signalSize/2),
					new Date (startTime.getTime() + 1000*(200+signalSize/2)),
					"something",
					Color.BLUE));
	
			for (int i=100;i<120;i++)
				signal[i+signalSize/2]=Float.NaN;
	
			intervals.add(new GUIInterval("ba",new Date (startTime.getTime() + 1000*(500 + signalSize/2)),
					new Date (startTime.getTime() + 1000*(signalSize-1)),
					"nothing",
					Color.GREEN));*/
			
			
			if(count_person == 1){
				data.addChannel(new BTChannel(0,startTime , endTime, hr1, "HR P1"));
				data.addChannel(new BTChannel(0,startTime , endTime, br1, "BR P1"));
				data.addChannel(new BTChannel(0,startTime , endTime, alt1, "ALT P1"));
				data.addChannel(new BTChannel(0,startTime , endTime, weather, "WEATHER"));
				//data.addChannel(new BTChannel(0,startTime , endTime, signal));
			}
			if(count_person == 2){
				data.addChannel(new BTChannel(0,startTime , endTime, hr2, "HRP2"));
				data.addChannel(new BTChannel(0,startTime , endTime, br2, "BR P2"));
				data.addChannel(new BTChannel(0,startTime , endTime, alt2, "ALT P2"));
				//data.addChannel(new BTChannel(0,startTime , endTime, signal));
			}
			//data.addChannel(generateDummyUnequisampledChannel(startTime , endTime));
		}
		BTCOREDisplay window = new BTCOREDisplay(data);
		window.addIntervals(intervals);
		window.setVisible(true);

		window.setText(text);
	}

	private static BTChannel generateDummyUnequisampledChannel(Date startTime, Date endTime) throws Exception{

		float[] values = new float[]{20,20,40,30,60};
		long shift = startTime.getTime();
		Date [] times =  new Date[]{ 
				new Date(shift+120*1000), new Date(shift+10*1000),new Date(shift+1250*1000),new Date(shift+120*1000), new Date(shift+560*1000)};
		return new BTChannel(0,startTime , endTime, values, times);
	}
	
	private static float linear_interpolation(double x0, double x1, double mu){
		return (float) ((float) (1-mu) * x0 + x1 * mu) ;
	}
}
