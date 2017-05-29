package gnad.analysis;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


import gnad.analysis.RamerDouglasPeuckerGPSSegmentation;

import gnad.data.Channel;
import gnad.data.ChannelInfo;
import gnad.data.DataFactory;
import gnad.data.DataSet;
import gnad.data.DataType;
import gnad.data.PersonRecord;
import gnad.data.UserDate;
import gnad.data.storyElement.Character;
import gnad.data.storyElement.NaturalElement;
import gnad.data.storyElement.PhysiologyState;
import gnad.data.storyElement.PhysiologyStateFactory;
import gnad.data.storyElement.StoryElementFactory;
import gnad.data.storyElement.Activity;
import gnad.data.storyElement.StoryElementOntologyMapping;
import gnad.data.storyElement.TrackActivity;
import gnad.data.storyElement.TrackPhysioState;
import uk.ac.abdn.carados.owl2.Owl2CaradosOntology;

import com.fmt.gps.track.Distance;
import com.fmt.gps.track.TrackPoint;
import com.fmt.gps.track.TrackSegment;
import com.fmt.gps.track.TrackSegment.caminarType;





/**
 * Class implementing the data processing of GPS data. 
 * Each GPS file is segmented using the altitude value. Each segment is then classified in 
 * activity (going up, down, straight, stop) and displacement mode (walk, ski, stop). 
 * consecutive segments of similar activity are merged.
 * Then, geographic points of interest (POIs) are extracted from OpenStreetMap to enrich the
 * description of the activity. 
 * TODO: extract points on the trajectory not only end and start of the segments.      
 * @author portet
 *
 */
public class TreatGPSBioharnessTrace {


	public static String KB_URL = "file:///home/ky/workspace/AmbientDataAnalysis/files/Skitouring.owl";
	private static Double halfBoudingBox = 0.003; // roughly equivalent to 68 meter around the GPS coord
	double epsilon=40;



	//"10:57,4:15,22:58,1:17,31:44,6:09,47:34,22:32"
/*	float duration[] = {10*60+57,4*60+15,22*60+58,1*60+17,31*60+44,6*60+9,47*60+34,22*60+32};
	float denivelee[] = {35    ,-1.3F  ,114.6F ,-0.6F ,163.4F,-7.3F ,236.8F,-528.2F};
	float speed[]=      {2.9F   , 3.8F   ,3.2F   ,3.9F  ,2.7F  ,3.2F  , 2.4F  ,16.7F};
	float alt_end[] =   {1760  ,1758.8F,1873  ,1873 ,2036 ,2028 ,2265 ,1725}; 
	float alt_start[] = {1725  ,1760  ,1758  ,1872 ,1872 ,2036 ,2028 ,2265};
	String main_goal="Croix de Chamrousse";
	String Start="Bachat Bouloud";
	int goal_index=6;
	*/
	



	public static List<TrackSegment> makeAchardSegments(List<TrackPoint> points){
		int croix_chamrousse_lac_Achard[] = {0,32,33, 61,62, 183,184, 193,194, 326,	327, 354,	355, 486,	487, 1006};
		List<TrackSegment> allSegs= new ArrayList<TrackSegment>();

		for (int i=0;i<croix_chamrousse_lac_Achard.length;i+=2){
			List<TrackPoint> trackseg = points.subList(croix_chamrousse_lac_Achard[i],croix_chamrousse_lac_Achard[i+1]+1);
			caminarType currentType = TrackSegment.setCaminarType(0, trackseg);
			allSegs.add(new TrackSegment(trackseg, currentType));
		}

		return allSegs;
	}


	public DataSet readGPSData(String directory, String ontology) throws Exception{
		DataFactory fac = new DataFactory(directory);
		DataSet s = fac.createDataSet(ontology);		
		return s;
	}
	
	public List<PhysiologyState> processBioharnessTrace(DataSet s, String modelPath) throws Exception{
		StoryElementFactory factory = new StoryElementFactory();
		Owl2CaradosOntology kb = s.getKB();
		
		List<PhysiologyState> states = new ArrayList<PhysiologyState>();
		List<PhysiologyState> states1 = new ArrayList<PhysiologyState>();
		Channel hr = null; Channel br = null; Channel gps = null;
		Channel time_stamp = null; Channel accelX = null;
		
		for(PersonRecord  p : s.getPersons()){
			Iterator it = p.getAllChannels().iterator();
			while(it.hasNext()){
				Channel c = (Channel) it.next();
				if(c.getInfo().matches(DataType.HEART_RATE)){
					hr = c;
				}
				else if(c.getInfo().matches(DataType.BREATH_RATE)) {
					br = c;
				}
				else if(c.getInfo().matches(DataType.GPS_TRACKPOINTS)){
					gps = c;
				}
				else if(c.getInfo().matches(DataType.PHYSIOLOGICAL_SERIES_TIME_STAMP)){
					time_stamp = c;
					/*for (int j = 0;  j < time_stamp.getRealLength(); j++)
					{
						Long t = (Long) time_stamp.getData(j);
						Date d = new Date(t);
						//System.out.println("Correct date time value: " + d);
						
					}*/
				}
				/*else if (c.getInfo().matches(DataType.ACCELEROMETERX)){
                	accelX = c;
                }*/
			}
			ChannelInfo info = new ChannelInfo();
			//need to be considered here the heart rate and breath rate of returned confident value 
			double[] hr_filter = info.initChannelError(hr.getArrayDataAsDouble());
			double[] br_filter = info.initChannelError(br.getArrayDataAsDouble());
			List<TrackPoint> points = new ArrayList<TrackPoint>();
			
			PhysiologyStateFactory phyFactory = new PhysiologyStateFactory(hr_filter, br_filter);
			phyFactory.readStateModel(modelPath);
			phyFactory.mergeState();
			
			phyFactory.printState();
			states = phyFactory.getFinalStates();
			
			List<Date> list_date_start = new ArrayList<Date>();
			List<Date> list_date_end = new ArrayList<Date>();
			
			/*Ngay mai 31/01 xem xet lai cho nay tai sao khong lay duoc gia tri ngay gio dau, cuoi*/
			for (int j = 0;  j < time_stamp.getRealLength(); j++)
			{
				for (int k = 0; k < states.size(); k++){
					
					if ( j == states.get(k).getStart()){
						Long t = (Long) time_stamp.getData(j);
						Date ds = new Date(t);
						list_date_start.add(ds);
						//System.out.println("start: "+ d);
						//states.get(k).setStartDate(d);
						
					}
					else if (j == states.get(k).getEnd()){
						Long t = (Long) time_stamp.getData(j);
						Date de = new Date(t);
						list_date_end.add(de);
						//System.out.println("end: "+ d);
						//states.get(k).setEndDate(d);
						
					}
				}
				//System.out.println("Correct date time value: " + d);
			}
			
			/*for (int k = 0; k < list_date_start.size()-1; k++){
				System.out.println("start: " + k + " :"+ list_date_start.get(k));
			}
			for (int k = 0; k < list_date_end.size(); k++){
				System.out.println("end: " + k + " :"+  list_date_end.get(k));
			}*/
			
			
			for (int k = 0; k < list_date_start.size() - 1; k++){
				//System.out.println(list_date_start.get(k));
				states.get(k).setStartDate(list_date_start.get(k));
				states.get(k).setEndDate(list_date_end.get(k));
				//System.out.println("start: " + k + " : "+ states.get(k).getStartDate()+ " end: "
					//				+ states.get(k).getEndDate()+ "name:" + states.get(k).getName() +" type: " + states.get(k).getType());
			}
			/*for (Object e : gps.getArrayData()){
				points.add((TrackPoint) e );
			}
		
			List<TrackSegment> trksegs = RamerDouglasPeuckerGPSSegmentation.DouglasPeucker(points, epsilon);
			trksegs = mergeTrack(trksegs);
			for (TrackSegment trkseg : trksegs){
				RamerDouglasPeuckerGPSSegmentation.seeList(trkseg.getPoints(), p.getPersonId());
			}*/
			
			PhysiologyState previous = null;
			
			//for (TrackSegment trkseg : trksegs){
				for(PhysiologyState physiostate : states){
					TrackPhysioState phs = factory.createPhysiologyStateFromBioharness(kb, physiostate, previous);
					
					phs.getInstance().setOwnValue(StoryElementOntologyMapping.PHYSIOLOGY_MODE_PROPERTY, physiostate.getType());
					states1.add(phs);
					
					Character car= factory.createCharacterFromPersonRecord( kb, p);
					phs.addCharacters(car);
					
					/*if (s.getOsmDoc()!=null){
						for (NaturalElement e : factory.createNaturalElementFromOSM(kb, s.getOsmDoc(), trkseg.getFirstPoint().getLat(),trkseg.getFirstPoint().getLon(), halfBoudingBox)){
	
							phs.addStartElement(e);
						}
						for (NaturalElement e : factory.createNaturalElementFromOSM(kb, s.getOsmDoc(), trkseg.getLastPoint().getLat(),trkseg.getLastPoint().getLon(), halfBoudingBox)){
							phs.addEndElement(e);
						} 
					}*/
					
					previous=phs;
				
				}
			//}
		}
		return states1;
	}

	public List<Activity> processGPSTrace(DataSet s) throws Exception{
		StoryElementFactory factory = new StoryElementFactory();

		Owl2CaradosOntology kb = s.getKB();
		List<Activity> activities= new ArrayList<Activity>();

		for ( PersonRecord  p : s.getPersons()){

			List<Channel> channels = p.getChannelsOfType(DataType.GPS_TRACKPOINTS);

			if (channels.isEmpty()) continue;
			Channel c = channels.get(0);
			
			List<TrackPoint> points = new ArrayList<TrackPoint>();
			for (Object e : c.getArrayData()){
				points.add((TrackPoint) e );
			}
			
				
			List<TrackSegment> trksegs = RamerDouglasPeuckerGPSSegmentation.DouglasPeucker(points, epsilon);
			trksegs = mergeTrack(trksegs);
			//for (TrackSegment trkseg : trksegs){
				//System.out.println("new track " +TrackSegment.setCaminarType(0, trkseg.getPoints()) +" \n");
				//System.out.println("\t " +trkseg.getFirstPoint().getPosition() +" - " +trkseg.getLastPoint().getPosition()+" \n");
				//RamerDouglasPeuckerGPSSegmentation.seeList(trkseg.getPoints(), p.getPersonId());
				//System.out.println(" ");
			//}

		
			Activity previous=null;
			for (TrackSegment trkseg : trksegs){


				TrackActivity a = factory.createActivityFromGPSSegment(kb, trkseg, getDenivelee(trkseg), getSpeed(trkseg), p,previous);
				String activity = getActivity(trkseg);

				a.getInstance().setOwnValue(StoryElementOntologyMapping.TRAVEL_MODE_PROPERTY, activity);

				activities.add(a);
				Character car= factory.createCharacterFromPersonRecord( kb, p);
				a.addCharacters(car);


				if (s.getOsmDoc()!=null){
					for (NaturalElement e : factory.createNaturalElementFromOSM(kb, s.getOsmDoc(), trkseg.getFirstPoint().getLat(),trkseg.getFirstPoint().getLon(), halfBoudingBox)){

						a.addStartElement(e);
					}
					for (NaturalElement e : factory.createNaturalElementFromOSM(kb, s.getOsmDoc(), trkseg.getLastPoint().getLat(),trkseg.getLastPoint().getLon(), halfBoudingBox)){
						a.addEndElement(e);
					}
				}
				previous=a;
				
			}	
		}
		return activities;



	}


	private List<TrackSegment> mergeTrack(List<TrackSegment> allSegs) {		
		List<TrackSegment> lessSegs= new ArrayList<TrackSegment>();
		TrackSegment prev = allSegs.get(0);

		for(int i= 1; i < allSegs.size(); i++) {
			TrackSegment curr= allSegs.get(i);
			String activity_previous = StoryElementFactory.classifyActivity(getDenivelee(prev));
			String activity_current = StoryElementFactory.classifyActivity(getDenivelee(curr));

			if(activity_current.equals(activity_previous)) {
				prev.getPoints().addAll(curr.getPoints());
			}
			else{
				lessSegs.add(prev);
				prev=curr;
			}
		}

		lessSegs.add(prev);

		return lessSegs;
	}		


	private float getSpeed(TrackSegment trkseg) {
		TrackPoint first = trkseg.getFirstPoint();
		TrackPoint last = trkseg.getLastPoint();
		float d = (float) distance(first.getLat(),first.getLon(),last.getLat(), last.getLon());
		return d /(1000*(last.getTime().getTime()-first.getTime().getTime()));
	}


	private float getDenivelee(TrackSegment trkseg){
		return trkseg.getLastPoint().getElevation()-trkseg.getFirstPoint().getElevation();
	}

	private String getActivity(TrackSegment trkseg) {

		TrackPoint first = trkseg.getFirstPoint();
		TrackPoint last = trkseg.getLastPoint();
		double second = Distance.getSecondsDiff(first, last);
		double hours = second /(60*60);

		double meters = distance (last.getLat(),last.getLon(),first.getLat(),first.getLon());
		double km = meters/1000;

		double speed = (hours!=0)?km/hours:0;
		System.out.println(speed +" = " + km + " km in " + hours + " hours");
		if (speed>6) return "ski";
		else if (speed > 0.9) return "walk";
		else if (speed >= 0.0) return "stop";
		else return "stop";
	}



	double distance (double lat1, double lon1,double lat2, double lon2  ){
		double R = 6371000; // metres earth radius
		double phi2 = lat2 * Math.PI / 180;
		double phi1 = lat1 * Math.PI / 180;		
		double l2 = lon2 * Math.PI / 180;
		double l1 = lon1 * Math.PI / 180;

		double x = Math.abs(l2-l1) * Math.cos((phi1+phi2)/2);
		double y = Math.abs(phi2-phi1);
		double d = Math.sqrt(x*x + y*y) * R;

		return d;
	}



	/**
	 * @param args
	 * @throws Exception 
	 */

	public static void main(String[] args) throws Exception{


		///data/corpus/ski_tour/Catherine/2013_03_10_Merlet_Lavoire


		TreatGPSBioharnessTrace test = new TreatGPSBioharnessTrace();
		String fileName = args[2];

		/*if (args.length !=1){
			System.err.println("Error the program should have at least one argument");
			System.out.println("Usage : executable path_to/description.xml");
			System.exit(1);
		}*/


		DataSet d = test.readGPSData(args[0], args[1]);

		List<Activity> a = test.processGPSTrace(d);
		List<PhysiologyState> state= test.processBioharnessTrace(d,args[3]);

		System.out.println(URI.create(fileName));
		d.getKB().saveOntology(fileName);

		GPSNLG nlg = new GPSNLG();
		String text = nlg.realise(fileName);
		//String text1 = nlg.realises(fileName);

		System.out.println(text);
		//System.out.println(text1);
	}

}