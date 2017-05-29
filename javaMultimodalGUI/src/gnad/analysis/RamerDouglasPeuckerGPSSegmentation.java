package gnad.analysis;

import gnad.data.Channel;
import gnad.data.DataSet;
import gnad.data.DataType;

import java.util.ArrayList;
import java.util.List;

//import test.gnad.DataReading.TreatGPSTrace;

import com.fmt.gps.track.TrackPoint;
import com.fmt.gps.track.TrackSegment;
import com.fmt.gps.track.TrackSegment.caminarType;


/**
 * 
 * @author portet
 *
 * This class implements the Ramer-Douglas-Peucker Algorithm for segmenting a time series. 
 * The algorithm is very basic and not robust at all to outliers but it is fast and simple.
 * The algorithm is tunable via the epsilon parameter which is roughly the distance below which 
 * the split of a segment is not worth it. Once again very crude way... 
 *      
 */
public class RamerDouglasPeuckerGPSSegmentation {




	public static List<TrackSegment> DouglasPeucker(List<TrackPoint> list, double epsilon){
		List<TrackSegment> resultList = new ArrayList<TrackSegment>();
		// Trouve le point le plus éloigné du segment
		double dmax = 0;
		int index = 0;
		for (int i = 1;i<list.size() - 2;i++){
			double d = DistanceAltitudePointSegment(list.get(i), list);
			if (d > dmax){
				index = i;
				dmax = d;
			}
		}

		// Si la distance dmax est supérieure au seuil, on simplifie
		if (dmax >= epsilon){
			// Appel récursif de la fonction
			List<TrackSegment> resultList1= DouglasPeucker(list.subList(0, index+1), epsilon);
			List<TrackSegment> resultList2 = DouglasPeucker(list.subList(index,list.size()), epsilon);

			// Construit la liste des résultats à partir des résultats partiels
			resultList.addAll(resultList1);
			resultList.addAll(resultList2);
		}  
		else{
			// Tous les points sont proches → renvoie un segment avec les extrémités
			TrackSegment track = new TrackSegment(list, caminarType.walk);
			//formatSegment(track);
			resultList.add(track);		  
		}

		// Renvoie le nouveau résultat
		return resultList;
	}


	private static double DistanceAltitudePointSegment(TrackPoint trackPoint, List<TrackPoint> list) {

		TrackPoint first = list.get(0);
		TrackPoint last = list.get(list.size()-1);
		double y_average = (first.getElevation() + last.getElevation())/2;
		double x_average = (first.getTime().getTime() + last.getTime().getTime())/2;

		double coef_dir = (first.getTime().getTime() - x_average)* (first.getElevation()-y_average) + (last.getTime().getTime() - x_average)* (last.getElevation()-y_average);
		coef_dir =	coef_dir / ((first.getTime().getTime() - x_average)*(first.getTime().getTime() - x_average) + (last.getTime().getTime() - x_average)*(last.getTime().getTime() - x_average));

		double origine =  y_average - coef_dir*x_average;

		double elevation_estimate = origine + coef_dir*trackPoint.getTime().getTime();

		return Math.abs(elevation_estimate-trackPoint.getElevation());
	}



	/**
	 * for debug: outputs list descriptions.
	 * @param pts list of points
	 * @param name name of list
	 **/
	public static void seeList(List<TrackPoint> pts, String name) {


		System.out.print(name+ " "+ pts.size()+ ": ");
		for(TrackPoint tp: pts) {
			System.out.print(tp+", ");
		}
		System.out.print("\n");
	}
	/**
	 * for debug: outputs list descriptions.
	 * @param pts list of points
	 **/
	public static void seeList(List<TrackSegment> pts) {

		for(TrackSegment tp : pts) {
			System.out.print("segment" + tp.getFirstPoint().getTime()+"-"+tp.getLastPoint().getTime() + " from " + tp.getFirstPoint().getElevation() + " to "+ tp.getLastPoint().getElevation() + "\n"); 
		}
		System.out.print("\n");
	}

	/**
	 * @param args
	 * @throws Exception 
	 */

	/*public static void main(String[] args) throws Exception{

		TreatGPSTrace test = new TreatGPSTrace();

		if (args.length !=1){
			System.err.println("Error the program should have at least one argument");
			System.out.println("Usage : executable path_to/description.xml");
			System.exit(1);
		}


		DataSet d = test.readGPSData(args[0], TreatGPSTrace.KB_URL);
		Channel c = d.getPersons().get(0).getChannelsOfType(DataType.GPS_TRACKPOINTS).get(0);
		List<TrackPoint> points = new ArrayList<TrackPoint>();
		for (Object e : c.getArrayData()){
			points.add((TrackPoint) e );
		}

		List<TrackSegment> list = RamerDouglasPeuckerGPSSegmentation.DouglasPeucker(points,40.0);
		seeList(list);
	}*/
}