package gnad.io;

import gnad.data.OSMNode;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * Class to communicate with OSM server or OSM document. 
 * TODO: Really needs some polishing 
 * @author portet
 *
 */
public class OSMOverpassQuery {

	private static final String OVERPASS_API = "http://overpass-api.de/api/interpreter";
	//private static final String OPENSTREETMAP_API_06 = "http://www.openstreetmap.org/api/0.6/";
	//essayer : http://overpass-api.de/api/interpreter?data=node[natural~"."](45.125,5.902,45.126,5.903);out;
	/*Utiliser openStreetMap : 
	 * http://overpass-api.de/query_form.html
	 * (node(45.125,5.90,45.126,5.91);<;);out; donne le code XML autour de la croix de chamrousse
	 * on recupere les tags de type :
	 * <tag k="name" v="Croix de Chamrousse"/><tag k="natural" v="peak"/>
	 * <tag k="name" v="Col des 3 Fontaines"/><tag k="natural" v="saddle"/>
	 * <tag k="aerialway" v="station"/>
	 * <tag k="aerialway" v="pylon"/>
	 */
	

	public OSMNode getNode(String nodeId) throws IOException, ParserConfigurationException, SAXException {
		String string = "http://www.openstreetmap.org/api/0.6/node/" + nodeId;
		URL osm = new URL(string);
		HttpURLConnection connection = (HttpURLConnection) osm.openConnection();

		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
		Document document = docBuilder.parse(connection.getInputStream());
		List<OSMNode> nodes = this.getNodes(document);
		if (!nodes.isEmpty()) {
			return nodes.iterator().next();
		}
		return null;
	}

	/**
	 * 
	 * @param lon the longitude
	 * @param lat the latitude
	 * @param vicinityRange bounding box in this range
	 * @return the xml document containing the queries nodes
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	/*	@SuppressWarnings("nls")
	private static Document getXML(double lon, double lat, double vicinityRange) throws IOException, SAXException,
			ParserConfigurationException {

		DecimalFormat format = new DecimalFormat("##0.0000000", DecimalFormatSymbols.getInstance(Locale.ENGLISH)); //$NON-NLS-1$
		String left = format.format(lat - vicinityRange);
		String bottom = format.format(lon - vicinityRange);
		String right = format.format(lat + vicinityRange);
		String top = format.format(lon + vicinityRange);

		String string = OPENSTREETMAP_API_06 + "map?bbox=" + left + "," + bottom + "," + right + ","
				+ top;
		URL osm = new URL(string);
		HttpURLConnection connection = (HttpURLConnection) osm.openConnection();

		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
		return docBuilder.parse(connection.getInputStream());
	}
*/
	public static Document getXMLFile(String location) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
		return docBuilder.parse(location);
	}

	/**
	 * 
	 * @param xmlDocument 
	 * @return a list of openseamap nodes extracted from xml
	 */
	@SuppressWarnings("nls")
	public static  List<OSMNode> getNodes(Document xmlDocument) {
		List<OSMNode> osmNodes = new ArrayList<OSMNode>();

		// Document xml = getXML(8.32, 49.001);
		Node osmRoot = xmlDocument.getFirstChild();
		NodeList osmXMLNodes = osmRoot.getChildNodes();
		for (int i = 1; i < osmXMLNodes.getLength(); i++) {
			Node item = osmXMLNodes.item(i);
			if (item.getNodeName().equals("node")) {
				NamedNodeMap attributes = item.getAttributes();
				NodeList tagXMLNodes = item.getChildNodes();
				Map<String, String> tags = new HashMap<String, String>();
				for (int j = 1; j < tagXMLNodes.getLength(); j++) {
					Node tagItem = tagXMLNodes.item(j);
					NamedNodeMap tagAttributes = tagItem.getAttributes();
					if (tagAttributes != null) {
						tags.put(tagAttributes.getNamedItem("k").getNodeValue(), tagAttributes.getNamedItem("v")
								.getNodeValue());
					}
				}
				Node namedItemID = attributes.getNamedItem("id");
				Node namedItemLat = attributes.getNamedItem("lat");
				Node namedItemLon = attributes.getNamedItem("lon");
				Node namedItemVersion = attributes.getNamedItem("version");

				String id = namedItemID.getNodeValue();
				String latitude = namedItemLat.getNodeValue();
				String longitude = namedItemLon.getNodeValue();
				String version = "0";
				if (namedItemVersion != null) {
					version = namedItemVersion.getNodeValue();
				}

				osmNodes.add(new OSMNode(id, latitude, longitude, version, tags));
			}

		}
		return osmNodes;
	}
	
	
	
	/**
	 * 
	 * @param xmlDocument 
	 * @return a list of openseamap nodes extracted from xml
	 */
	@SuppressWarnings("nls")
	public static  List<OSMNode>getNaturalNodesDoc(Document xmlDocument,double lon, double lat, double around){
		List<OSMNode> osmNodes = new ArrayList<OSMNode>();

		double east = 	lon - around;
		double west = 	lon + around;
		double south = 	lat - around;
		double north = 	lat + around;
		
		
		
		// Document xml = getXML(8.32, 49.001);
		Node osmRoot = xmlDocument.getFirstChild();
		NodeList osmXMLNodes = osmRoot.getChildNodes();
		for (int i = 1; i < osmXMLNodes.getLength(); i++) {
			Node item = osmXMLNodes.item(i);
			if (item.getNodeName().equals("node")) {
				NamedNodeMap attributes = item.getAttributes();
				Node namedItemID = attributes.getNamedItem("id");
				String id = namedItemID.getNodeValue();

				Node namedItemLat = attributes.getNamedItem("lat");
				String latitude = namedItemLat.getNodeValue();
				Double lati = new Double(latitude);
				
				if (lati < east || lati > west) continue;
				
				
				Node namedItemLon = attributes.getNamedItem("lon");
				String longitude = namedItemLon.getNodeValue();
				Double longi = new Double(longitude);
				
				if (longi < south || longi > north) continue;
				
				
				NodeList tagXMLNodes = item.getChildNodes();
				Map<String, String> tags = new HashMap<String, String>();
				for (int j = 1; j < tagXMLNodes.getLength(); j++) {
					Node tagItem = tagXMLNodes.item(j);
					NamedNodeMap tagAttributes = tagItem.getAttributes();
					if (tagAttributes != null) {
						tags.put(tagAttributes.getNamedItem("k").getNodeValue(), tagAttributes.getNamedItem("v")
								.getNodeValue());
					}
				}
				
				if (tags.get("natural")==null) continue;
				
				
				
				Node namedItemVersion = attributes.getNamedItem("version");

				String version = "0";
				if (namedItemVersion != null) {
					version = namedItemVersion.getNodeValue();
				}

				osmNodes.add(new OSMNode(id, latitude, longitude, version, tags));
			}

		}
		return osmNodes;
	}

	/*public static List<OSMNode> getOSMNodesInVicinity(double lat, double lon, double vicinityRange) throws IOException,
			SAXException, ParserConfigurationException {
		return OSMWrapperAPI.getNodes(getXML(lon, lat, vicinityRange));
	}*/

	
	public Document getNaturalNodesViaOverpass(double lon, double lat, double around) throws IOException, ParserConfigurationException, SAXException {
		String query = "node[natural~\".\"]("+ (lon-around) +"," + (lat-around) + ", "+ (lon+around) +"," + (lat+around) +");out;";
		return getNodesViaOverpass(query);
	
	}
	
	
	/**
	 * 
	 * @param query the overpass query
	 * @return the nodes in the formulated query
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	public Document getNodesViaOverpass(String query) throws IOException, ParserConfigurationException, SAXException {
		String hostname = OVERPASS_API;
		String queryString = query; //readFileAsString(query);

		URL osm = new URL(hostname);
		HttpURLConnection connection = (HttpURLConnection) osm.openConnection();
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

		DataOutputStream printout = new DataOutputStream(connection.getOutputStream());
		printout.writeBytes("data=" + URLEncoder.encode(queryString, "utf-8"));
		System.out.println(URLEncoder.encode(queryString, "utf-8"));
		printout.flush();
		printout.close();

		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
		return docBuilder.parse(connection.getInputStream());
	}

	/**
	 * 
	 * @param filePath
	 * @return
	 * @throws java.io.IOException
	 */
	private static String readFileAsString(String filePath) throws java.io.IOException {
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
		reader.close();
		return fileData.toString();
	}

	
	
	
	/**
	 * main method that simply reads some nodes
	 * 
	 * @param args
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {
		
		List<OSMNode> osmNodesInVicinity =null;
		/*Authenticator.setDefault(new BasicAuthenticator("youruser", "yourpassword"));
		List<OSMNode> osmNodesInVicinity = getOSMNodesInVicinity(49, 8.3, 0.005);
		 */
		OSMOverpassQuery api = new OSMOverpassQuery();
		//List<OSMNode> osmNodesInVicinity =api.getNodes(getNodesViaOverpass("node[\"name\"=\"Gielgen\"];out;"));
		//45.125848,5.902472,45.125848,5.902472
		//List<OSMNode> osmNodesInVicinity =api.getNodes(getNodesViaOverpass("node[\"natural\"=\"peak\"](45.12,5.90,45.13,5.99);out;"));
		//List<OSMNode> osmNodesInVicinity =api.getNodes(api.getNodesViaOverpass("node[natural~\".\"](45.125,5.902,45.126,5.903);out;"));
		//List<OSMNode> osmNodesInVicinity =api.getNodes(getNodesViaOverpass("node(45.125848,5.902472,45.125848,5.902472);node(around:1000)[natural~\".\"];out;"));
		
		// 0.0005 corresponds roughly to 68 meters
		//osmNodesInVicinity =api.getNodes(api.getNaturalNodesViaOverpass(45.125848,5.902472,0.0005));
	
		Document doc = getXMLFile("/data/corpus/ski_tour/20130317081251/OSM.xml");
		//osmNodesInVicinity =getNodes(doc);
		osmNodesInVicinity =getNaturalNodesDoc(doc,45.125848, 5.902472,0.0005);
		
		for (OSMNode osmNode : osmNodesInVicinity) {
			System.out.println(osmNode);
		}
	}

	

}



