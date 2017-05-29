package gnad.analysis;

import java.util.ArrayList;
import java.util.List;



/**
 * The Class MedianFilter. A basic median filter that compute a median over a temporal sliding window
 * @author nguyentk
 */
public class MedianFilter {

	/**
	 * Instantiates a new median filter.
	 */
	public MedianFilter(){}

	/**
	 * median filter used to compute the baseline ignoring pre-detected artifact zones.
	 * 
	 * @param channel the channel data
	 * @param halfWin the window used to compute the baseline
	 * 
	 * @return the filtered input
	 * 
	 * the median filtered data where pre-detected artifact zones have been ignored
	 */
	public float[] filter(double[] data, int halfWin) {
		return (float[])(filterPlusResidu(data,halfWin)[0]);
	}

	
	/**
	 * median filter used to compute the baseline ignoring pre-detected artifact zones. it also returns the difference between input and output. 
	 * 
	 * @param channel the channel data
	 * @param halfWin the window used to compute the baseline
	 * 
	 * @return an object containing at index 0 the filtered input and at index 1 the difference between input and output
	 * 
	 * the median filtered data where pre-detected artifact zones have been ignored
	 */
	public Object[] filterPlusResidu(double[] data, int halfWin) {


		double[] residu = new double[data.length];
		double[] output = new double[data.length];
		
		// if the signal is long enough 
		if (data.length > halfWin){
		
		// first part approximate with the following neighbors mainly
		// and feed the buffer with the next events	
		SortedBuffer buffer = new SortedBuffer(data,0,halfWin);
		
		
		for (int i=0;i<halfWin+1;i++){
			buffer.add(data[halfWin+i]);
			output[i]=buffer.getCenterValue();
			residu[i]=data[i]-output[i];
		}

		for (int i=halfWin+1;i<data.length-halfWin;i++){
			buffer.put(data[halfWin+i]);
			output[i]=buffer.getCenterValue();
			residu[i]=data[i]-output[i];
		}

		/** last part approximate with the previous neighbors mainly*/
		for (int i=data.length-halfWin;i<data.length;i++){
			buffer.removeOldest();
			output[i]=buffer.getCenterValue();
			residu[i]=data[i]-output[i];
		}
		}
		else
			output= data;
		return new Object[]{output,residu};
	}
	
	/**
	 * ascending sorted buffer.
	 * 
	 * @author fportet
	 */
	private class SortedBuffer{

		/** sorted array used for computing the median. */
		private List<Double> m_sortedBuffer;
		/** The m_index. */
		private List<Long> m_index;
		/** The lowest index. */
		private long oldest=0;
		/** The highest index (the youngest). */
		private long youngest=-1;

		/**
		 * Instantiates a new sorted buffer.
		 * 
		 * @param objects the input values 
		 * @param i the index of the sample that is currently beeing filtered
		 * @param halfWin the half of the temporal window 
		 */
		public SortedBuffer(double[] data, int i, int halfWin) {
			init(data, i, halfWin);
		}

		/**
		 * Inits the buffer by filling the internal buffer with the input values.
		 * 
		 * @param values the input values
		 * @param i the index of the sample that is currently beeing filtered
		 * @param halfWin the half of the temporal window 
		 */
		public void init(double[] values, int i, int halfWin) {
			m_sortedBuffer = new ArrayList<Double>();
			m_index = new ArrayList<Long>();
			for (int j = 0; j<halfWin-i;j++){
				add(values[i+j]);
			}
			// update the tags
			oldest=0;
			youngest=halfWin-i-1;
		}

		/**
		 * Gets the center of the buffer, i.e.: the median value. If even compute the mean of the two center points
		 * 
		 * @return the median value
		 */
		public Double getCenterValue() {

			float value=Float.NaN;
			// if there is something in the buffer 
			if (m_sortedBuffer.size()>0){
				// if odd
				if (m_sortedBuffer.size()%2 >0){
					return  m_sortedBuffer.get(m_sortedBuffer.size()/2);
				}
				// else it is even
				value  = (float) (m_sortedBuffer.get(m_sortedBuffer.size()/2) +m_sortedBuffer.get((m_sortedBuffer.size()/2)-1));
				// then return the mean of the two values around the center
			}
			return (double) (value/2);
		}


		/**
		 * Puts a new element and remove the oldest
		 * 
		 * @param data the value
		 */
		public void put(double data) {
			removeOldest();
			add(data);			
		}


		/**
		 * Adds a value to the buffer (i.e. expands it).
		 * 
		 * @param object the value to add
		 */
		public void add(double value) {

			for (int j = 0; j<m_sortedBuffer.size();j++){
				if (value < m_sortedBuffer.get(j)){
					m_sortedBuffer.add(j,value);
					youngest++;
					m_index.add(j,youngest);
					return;
				}
			}
			// if this is reached then value is the greatest value and must be added at the end	
			youngest++;
			m_sortedBuffer.add(value);
			m_index.add(youngest);
		}

		/**
		 * Removes the oldest (first) element that has been entered, i.e. trim the buffer.
		 */
		public void removeOldest() {			
			for (int j = 0; j<m_index.size();j++){
				if (m_index.get(j)==oldest){
					m_sortedBuffer.remove(j);
					m_index.remove(j);
					// next oldest is the next index
					oldest++;
					return;
				}
			}
			// this shouldn't de reached
			System.err.println("Error, the first element has not been found (no index ==" + oldest+")");			
		}
	}
}
