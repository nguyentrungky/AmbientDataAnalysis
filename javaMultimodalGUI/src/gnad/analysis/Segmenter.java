package gnad.analysis;


import gnad.data.ChannelInfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


/**
 * Segmenter - class that segments a time-series using a bottom-up segmentation
 * 
 * The input time series is supposed to be equisampled. No use is made of any time information (only index)
 * 
 * @author portet
 */
public class Segmenter {

	/**
	 * Internal comparator to sort interval by merging error cost  
	 */
	protected Comparator<NewInterval> errorComparator = new Comparator<NewInterval>() {
		public int compare(NewInterval m1, NewInterval m2) {
			// sort by error
			if (m1.error < m2.error)	return -1;
			else if (m1.error > m2.error) return 1;
			// sort by Date
			else{ 
				if (m1.start<m2.start)	return -1;
				else if (m1.start>m2.start)	return 1;
				else{
					if (m1.end<m2.end)	return -1;
					else if (m1.end>m2.end)	return 1;
					return 0;
				}
			}
		}
	};

	/**
	 * Instantiates a new segmenter.
	 */
	public Segmenter() { 
	}


	
	/**
	 * Segments the input channel.
	 * 
	 * @param data the equisampled input channel
	 * 
	 * @return the list of segments
	 */
	public List<NewInterval> segment(double[] data, double maxError){
		return BottomUpSegmentation(data, SignalToIntervalList(data),maxError);
	}
	
	
	/**
	 * Makes the initial list of segment <br>
	 * <p>
	 * creates intervals delimited by two consecutive points (including NaN) and 
	 * computes the error of a fit between between an interval and its previous interval
	 * </p>   
	 * @param data the equisampled time series 
	 * @return the list of intervals
	 */
	public List<NewInterval> SignalToIntervalList(double[] data){

		List<NewInterval>  segs = new LinkedList<NewInterval>();
		// below variable will be used in various places
		NewInterval current = null;
		NewInterval previous = null;
		// now create initial list, one segment between each two points
		// more efficient to skip over non-normal points, won't worry about this for now
		for (int index = 0; index < data.length-1; index++) {
			current = new NewInterval(index, previous);
			current.error= computeMergeError(current,data);
			segs.add(current);		
			previous = current;
		}

		return segs;
	}


	/**
	 * Perform the Bottom up segmentation.<br>
	 *  <p>
	 *  Merge segments according to a cost function that indicates which pair of segment is better to merge first.
	 *  <p>
	 * @param data the time series points
	 * @param seglist the current list of segments to merge
	 * @param maxError the maximum error tolerated
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public LinkedList<NewInterval> BottomUpSegmentation(double[] data, List<NewInterval> seglist, double maxError){
		// create an ordered set of interval (ordered by merge error)
		TreeSet<NewInterval> segs = new TreeSet<NewInterval>(errorComparator);
		segs.addAll(seglist);
		
		
		NewInterval cheapestSeg = findCheapestSeg(segs);

		// while the error is below the threshold and while there is still something to merge
		while (cheapestSeg.error <= maxError && segs.size()>1) {

			//update info 
			NewInterval prev = cheapestSeg.previous;
			cheapestSeg.start = prev.start;
			cheapestSeg.previous =prev.previous;
			if (cheapestSeg.previous!=null)
				cheapestSeg.previous.next=cheapestSeg;
			
			// get rid of old segment
			segs.remove(prev);
			
			// re-order 
			segs.remove(cheapestSeg);
			cheapestSeg.error = computeMergeError(cheapestSeg,data);  // recompute merge error
			segs.add(cheapestSeg);

			// recompute the merge error of the next segment
			if (cheapestSeg.next!=null){
				segs.remove(cheapestSeg.next);
				cheapestSeg.next.error= computeMergeError(cheapestSeg.next,data);  // recompute merge error
				segs.add(cheapestSeg.next);
			}

			// find new cheapest seg
			cheapestSeg = findCheapestSeg(segs);
		}
		
		// format output
		LinkedList<NewInterval> result = new LinkedList<NewInterval>(segs);
		Collections.sort(result);
		NewInterval i = result.get(0);
		i.error = fitError(i.start,i.end,data);
		return result;
	}

	


	/**
	 * Perform an interpolation over the interval and compute the error
	 * <p>
	 * // compute slope parameters
		// assumes segment is anchored at actual data endpoints
		// (linear interpolation, not linear regression)

	 * </p>
	 * @param current The current interval to fit
	 * @param data The original time series 
	 * @return The fit error
	 */
	protected double fitError(int start, int end, double[] data) {
				

		double A = data[0]; // first normal point
		int startA=0;    // index of first normal point
		double B =data[data.length-1];  // last normal point
		int startB = data.length;  // index of last normal point

		
		// find first normal point 
		for (int i = start; i<=end; i++){
			if (!Double.isNaN(data[i]) && !Double.isInfinite(data[i])) {				
				A = data[i];
				startA=i;
				break;
			}
		}

		// find last normal point 
		for (int i = end; i>=start; i--){
			if (!Double.isNaN(data[i]) && !Double.isInfinite(data[i])) {				
				B = data[i];
				startB=i;
				break;
			}
		}

		
		double increment = (B-A) / (startB-startA); // amount to increment by (slope)
		double result = 0;  // accumulated error
		double error = 0;   // error at point

		// compute the model and compare it to the data 
		for (int i = start; i<=end; i++, A+=increment){

			if (!Double.isNaN(data[i]) && !Double.isInfinite(data[i])) {
				
				error = (data[i] < A)?A-data[i]:data[i]-A; // diff from interpolation

				// root-mean-square
				//result = result + error*error;

				// average of absolute value
				//result = result + Math.abs(error);	

				// max divergence
				//	result = Math.max (result,Math.abs(error));
				//error = (error <0)?-error:error;
				result = (result > error)?result:error;

			}
			
		}

		if (startB>startA){ 					
			//return = (float)Math.sqrt((result) / normalCount);  // RMS
			return (float)result;           // max divergence
		}		//return = (float)result/normalCount;  // average of abs value
		else
		// else pure artefact or empty interval
			return -1;
		
	}


	/**
	 * Find the cheapest segment from the list.
	 * 
	 * @param segs the list of segments
	 * 
	 * @return the cheapest segment from the list
	 */
	private NewInterval findCheapestSeg(TreeSet<NewInterval> segs) {
		// the cheapest is the first one (error based) 
		// note that it could be more complex than that
		return segs.first();
	}


	/**
	 * Computes the error from merging the current segment with the previous segment.
	 * 
	 */
	public double computeMergeError(NewInterval seg, double[] data) {
		double error=0;
		
		// if no previous segment should not try to merge it
		if (seg.previous == null) // 
			error= Double.POSITIVE_INFINITY;
		else
			error =fitError(seg.previous.start,seg.end,data);
		return error; 
	}

	public ArrayList<NewInterval> mergeInterval(List<NewInterval> intervals){
		ArrayList<NewInterval> final_intervals = new ArrayList<NewInterval>();		
		int i = 0;
		
		while( i < intervals.size() - 1){
			NewInterval inter = intervals.get(i);
			System.out.println("type: " + inter.getType());
			int j = i + 1;
			while(j < intervals.size()){
				NewInterval t = intervals.get(j);
				if(t.getType().matches(inter.getType()) && t.getValue().matches(inter.getValue())){
					inter.setEndTime(t.getEndTime());
					//intervals.remove(j);
				}
				else{
					final_intervals.add(inter);
					if(j == intervals.size()-1){
						final_intervals.add(t);
					}
					break;
				}
				j++;
			}
			i = j;
		}
		return final_intervals;
	}
	

	
	
	/**
	 * set the error tolerance for the segmentation according to the variance of the channel: 
	 * 
	 * residu = (s(t)-baseline(t))/dynamic, where baseline = medianFilter(s), and dynamic is the dynamic of the channel
	 * I = {t in {1,..., length(s)}: isnormal(s(t))}
	 * error = log10(var(residu(I)))
	 * if error < - 4 then  
	 * 	tolerance = 0.05*dynamic
	 * else
	 * 	tolerance = 0.1*dynamic
	 * endif 
	 * 
	 * @param channel the channel to process
	 */
	public void initChannelError(double[] data, ChannelInfo info) {
		// half of the window for the baseline (default 60 secondes)
		// use 2 minutes for all channel except for temperature for which 10 minutes is prefer
		int halfWin = 60; 

		Object[] res = (new MedianFilter()).filterPlusResidu(data, halfWin);
		
		// continue to compute the residu
		double dynamic = info.computeMaxValue(data)-info.computeMinValue(data);
		double[] residu = (double[])res[1];
		double accumulation=0;
		int numberOfNormalvalues=0;

		// normalize the residue and compute the mean for the next variance computation
		for (int i=0;i<residu.length;i++){
				residu[i]/=dynamic;
				accumulation += residu[i];	
				numberOfNormalvalues++;
		}

		double mean = accumulation/numberOfNormalvalues;
		double var =0.0;
		// compute the variance
		for (int i=0;i<residu.length;i++){
				var += (residu[i] - mean)*(residu[i] - mean);
		}	
		var/= numberOfNormalvalues;
		// use the logarithm base 10
		var = Math.log10(var);

		double alpha = 0.1;
		if (var < -4){
			alpha=0.05;
		}
		else{
			alpha=0.1;
		}
		info.setSegmentError(dynamic*alpha);
		// System.out.println("channel " + channel.info.name + " tolerance is " + alpha + " threshold is "+ channel.info.segmentError);
		// dereference the objects to facilitate the garbage collection
		residu=null;
		res=null;
	}	
	

	/**
	 * Read a time series from a file assuming that values are represented as string separated by blanks
	 * !!! Univariate and equisampled data only is assumed 
	 * @param filename The path to the file
	 * @return an array of double representing the time series described in the file
	 * @throws FileNotFoundException
	 */
	/*public static double[] readFromFile(String filename) throws FileNotFoundException{
		List<Double> temp = new ArrayList<Double>();
		int i=0;
		Scanner sc = new Scanner(new File(filename));
		while(sc.hasNext())
			temp.add(new Double(sc.next()));
		sc.close();
		double[] data = new double[temp.size()];
		for (Double val :temp) data[i++]=val;			
		return data;
	}

	/**
	 * Write the output of the segmentation to a file
	 * @param filename The path to the file
	 * @param intervals The segments 
	 * @throws IOException
	 */
	/*public static void writeToFile(String filename, List<Interval> intervals) throws IOException{
		FileWriter output = new FileWriter(filename);
		for (Interval inter : intervals) output.write(inter.toString());
		output.close();
	}


	public static void main(String[] args) throws IOException{

		Segmenter seg = new Segmenter();
		double[] data = readFromFile("test_tourism_Travelodge_Southbank_Melbourne.dat");
		List<Interval> intervals = seg.segment(data, 2);
		writeToFile("out.txt", intervals);
	}*/
}
