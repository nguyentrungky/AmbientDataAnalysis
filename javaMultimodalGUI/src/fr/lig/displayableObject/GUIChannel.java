package fr.lig.displayableObject;

import java.util.Date;

import fr.lig.data.BTChannel;
import fr.lig.data.EquiSampledNumericalChannel;
import fr.lig.data.NumericalChannel;

/**
 * class to represent a channel in the GUI side of the system. This is mainly a wrapper
 * @author fportet
 *
 */
public class GUIChannel extends GUIDisplayableTemporalObject {

	/** the data object that contain the channel values	 */
	NumericalChannel dataChannel;

	/** minimum value of the channel (for axis setting)  */
	private float minimum;

	/** maximum value of the channel (for axis setting) */
	private float maximum; 

	/**
	 * creates a new Gui object 
	 * @param ID - the unique identifier of the channel (NOT CHECKED)
	 * @param start - the start date of the channel 
	 * @param end - the end date of the channel (inclusive)
	 */
	public GUIChannel(String ID, Date start, Date end) {
		super(ID, start, end);
	}

	/**
	 * creates a new GUIChannel object from a BTChannel 
	 * @param channel - the BT channel
	 */
	public GUIChannel(BTChannel channel){
		super(channel.getChannelName(), channel.getStartTime(),channel.getEndTime());
		// make sure that ranges are initialized
		channel.initRanges();
		dataChannel =  channel.getChannel();

		float[] legalRange = channel.getLegalRange();
		float[] minAndMax = channel.getMinAndMax();
		//System.out.println(channel.getMinAndMax()[0]);
		//System.out.println(channel.getMinAndMax()[1]);
		if (legalRange !=null && minAndMax[0]!=Float.NaN){
			minimum = Math.min(legalRange[0],minAndMax[0]);
			System.out.println(minimum);
			maximum = Math.max(legalRange[1],minAndMax[1]);
			System.out.println(maximum);
		}
		else{
			minimum = channel.getMinAndMax()[0];
			maximum = channel.getMinAndMax()[1];
		}					
	}

//	... Getters and Setters
	public float getMinimum() {
		return minimum;
	}

	public void setMinimum(float minimum) {
		this.minimum = minimum;
	}

	public float getMaximum() {
		return maximum;
	}

	public void setMaximum(float maximum) {
		this.maximum = maximum;
	}
	public NumericalChannel getDataChannel() {
		return dataChannel;
	}

	public void setDataChannel(EquiSampledNumericalChannel dataChannel) {
		this.dataChannel = dataChannel;
	}

	public boolean isEquiSampled() {
		return (dataChannel instanceof EquiSampledNumericalChannel);
	}
}