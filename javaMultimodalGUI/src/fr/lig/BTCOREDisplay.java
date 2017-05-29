package fr.lig;

/*import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;*/

import fr.lig.displayableObject.GUIChannel;
import fr.lig.displayableObject.GUIInterval;
import fr.lig.temporalPlot.TemporalPanel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import fr.lig.GUIConfig;
import fr.lig.data.BTChannel;
import fr.lig.data.BTData;

/**
 * This class display all the information contained in a BTData object. 
 * 
 * @author fportet
 *
 */
public class BTCOREDisplay extends JFrame implements ActionListener {


	private static final long serialVersionUID = 1L;

	/** Panel in which the BT-System output is displayed */
	private JTextArea textArea;

	/** Panel in which all temporal data are displayed */
	private TemporalPanel temporalPanel;

	/** Menu elements for zooming in and out*/
	JMenuItem zoomIn, zoomOut;

	/**
	 * Unique constructor which take as input the BTData
	 * @param data - the data to display
	 */
	public BTCOREDisplay(BTData data) {

		//... Set window characteristics
		setLayout(new BorderLayout());
		Dimension ScreenDimension = Toolkit.getDefaultToolkit().getScreenSize();
		setPreferredSize(new Dimension((int) (ScreenDimension.width*.7), (int) (ScreenDimension.height*0.7)));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Ambient Data Analysis");
		setLocation(0,0);

		// ... create components and set characteristics
		Dimension panelDimension = getPreferredSize();
		float occupancyCoeff=(float) GUIConfig.BTCOREDISPLAY_OCCUPANCY_COEFFICIENT;

		// creates the text area in which the computer generated text is displayed
		textArea = new JTextArea("No Text");
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);		
		//textArea.setPreferredSize(new Dimension((int)(panelDimension.width*occupancyCoeff),panelDimension.height));
		textArea.setPreferredSize(new Dimension((int)(panelDimension.width*occupancyCoeff),(int)(panelDimension.height)));
		// create the temporal panel that manage all the temporal data (signals and intervals)
		temporalPanel = new TemporalPanel(data,new Dimension((int)(panelDimension.width*(1-occupancyCoeff)+.2),panelDimension.height));

		//create google map
		//final Browser browser = new Browser();
	    //BrowserView browserView = new BrowserView(browser);
	    //browserView.setPreferredSize(new Dimension((int)(panelDimension.width)/4,(int)(panelDimension.height)/4));
	    //browserView.setPreferredSize(new Dimension((int)(panelDimension.width*occupancyCoeff),panelDimension.height));
	    //JPanel panel_google = new JPanel();
	    //panel_google.add(browserView);
	    
	    
		// add all components to the window
		add(textArea,BorderLayout.EAST);
		add(temporalPanel,BorderLayout.CENTER);
		add(generateMenu(),BorderLayout.NORTH);
		//add(browserView,BorderLayout.EAST);
		//String url = "file:///home/ky/workspace/AmbientDataAnalysis/map_simple.html";
		//browser.loadURL(url);
		pack();
	}

	
	/**
	 * Sets the text in the text area
	 * @param text - the summary
	 */
	public void setText(String text){
		textArea.setText(text);
	}

	/**
	 * add intervals to the temporal panel 
	 * @param intervals - the intervals to add
	 */
	public void addIntervals(Collection<GUIInterval> intervals) {
		temporalPanel.addIntervals(intervals);
	}



	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("Zoom In")){
			this.temporalPanel.zoomIn();
		}
		if (e.getActionCommand().equals("Zoom Out")){
			this.temporalPanel.zoomOut();
		}		
	}

	/**
	 * Adds new channels to the existing traces. The trace to which each channel
	 * is added is found by comparing the ID of the trace and the channel which
	 * is by default the abbreviated name of the channel instance in the
	 * ontology. If no trace has the same ID as a channel, then the channel is
	 * not added. If the channel has no data, then it is ignored.
	 * 
	 * @param channels 
	 *            the channels to add to the traces
	 */
	public void addChannelsInTraces(List<BTChannel> channels) {
		for (BTChannel channel: channels){
			// if there is data then try to add it in the temporal panel
			if (channel.getChannel().getSize()>1)
				temporalPanel.addChannelInTrace(new GUIChannel(channel),channel.getName());
			
		}
	}
	
	/**
	 * generate the menu bar of the main window
	 * @return a menubar
	 */
	private JMenuBar generateMenu(){
		// create the menu
		JMenuBar menuBar;
		JMenu zoom;

		//Create the menu bar.
		menuBar = new JMenuBar();

		//Build the first menu.
		zoom = new JMenu("Zoom");
		menuBar.add(zoom);

		//a group of JMenuItems
		zoomIn = new JMenuItem("Zoom In");
		zoomIn.addActionListener(this);
		zoom.add(zoomIn);
		zoomOut = new JMenuItem("Zoom Out");
		zoomOut.addActionListener(this);
		zoom.add(zoomOut);
		
		return menuBar;
	}	
}
