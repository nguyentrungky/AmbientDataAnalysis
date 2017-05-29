package gnad.io;

import gnad.data.Coordinate;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import java.io.File;
import java.util.Date;
 
public class XMLReader<E> {
	E[] data;
	int length;
	private File xmlFile;
	
	public XMLReader(String fileName){
		this.xmlFile = new File(fileName);
		this.length = 0;
	}
	
	public void setLength(int length){
		this.length = length;
	}
	@SuppressWarnings("unchecked")
	public E[] processXMLFile(Date startTime, Date endTime,String info){
		String[] set = {"GPXCoordinate","GPXAltitude"};
		int index = 0;
		int indexAttribute = 0;
		while(index <= set.length){
			if (set[index].matches(info)){
				indexAttribute = index;
				break;
			}
			else index++;
		}
		System.out.println(indexAttribute);
		
		int length = (int)(endTime.getTime() - startTime.getTime());
		data = (E[])new Object[length];
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(xmlFile);
			doc.getDocumentElement().normalize();
			//sSystem.out.println("Root element :" + doc.getDocumentElement().getNodeName());
			NodeList nList = doc.getDocumentElement().getChildNodes();
			searchNode(nList,data,indexAttribute);
	    	}catch (Exception e) {
	    		e.printStackTrace();
	    	}
		return data;
	}
	@SuppressWarnings({ "unchecked"})
	private  void searchNode(NodeList nodeList,E[] data,int indexAttribute) {
		
	    for (int count = 0; count < nodeList.getLength(); count++) {
	    	Node tempNode = nodeList.item(count);
	    	// make sure it's element node.
			if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
				// get node name and value
//				System.out.println("\nNode Name =" + tempNode.getNodeName() + " [OPEN]");
//				System.out.println("Node Value =" + tempNode.getTextContent());
//				System.out.println(indexAttribute);
				if(indexAttribute==1){
					if(tempNode.getNodeName().matches("ele")){
						Double alt = Double.parseDouble(tempNode.getTextContent());
						E altitude = (E) alt;
						data[length] = altitude; 
						length++;
					}
				}
				else if(indexAttribute==0){
					if (tempNode.hasAttributes()) {
						// get attributes names and values
						
						double x = 0;
						double y = 0;
						NamedNodeMap nodeMap = tempNode.getAttributes();
						for (int i = 0; i < nodeMap.getLength(); i++) {
							Node node = nodeMap.item(i);
							
							
							if(node.getNodeName().equals("lat")){
								x = Double.parseDouble(node.getNodeValue());
							}
							else if (node.getNodeName().equals("lon")){
								y = Double.parseDouble(node.getNodeValue());
							}
							
	//						System.out.println("attr name : " + node.getNodeName());
	//						System.out.println("attr value : " + node.getNodeValue());
						}
						if(x!=0 && y!= 0){
							
							Coordinate coordinate = new Coordinate(x,y);
							//System.out.println("X :" + coordinate.getXCoordinate() + " Y: " + coordinate.getYCoordinate());
							//System.out.println(length);
							data[length] = (E) coordinate;
							length++;
						}
						//
					}
				}
				if (tempNode.hasChildNodes()) {
					// loop again if has child nodes
					searchNode(tempNode.getChildNodes(),data,indexAttribute);
				}
				//System.out.println("Node Name =" + tempNode.getNodeName() + " [CLOSE]");
			}
	    }
	 
	  }
//	public static void main(String argv[]) {
//	    try {
//			File fXmlFile = new File("2013-03-10-01.GPX");
//			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
//			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
//			Document doc = dBuilder.parse(fXmlFile);
//			doc.getDocumentElement().normalize();
//			System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
//			NodeList nList = doc.getDocumentElement().getChildNodes();
//			printNode(nList,data);
//	    	}catch (Exception e) {
//	    		e.printStackTrace();
//	    	}
//	}
		
}
