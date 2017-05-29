package gnad.data.storyElement;
/**
 * 
 * @author nguyentk
 *
 */

import gnad.analysis.Carac;
import gnad.analysis.Chi;
import gnad.analysis.Component;

import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class PhysiologyStateFactory {
	
	private double[] hr;
	private double[] br;
	private int window;
	
	private ArrayList<PhysiologyState> semifinal_states = new ArrayList<PhysiologyState>();
	private ArrayList<PhysiologyState> final_states = new ArrayList<PhysiologyState>();
	
	public PhysiologyStateFactory(double[] hr, double[] br){
		this.hr = hr;
		this.br = br;
		this.window = 0;
	}
	
	public ArrayList<PhysiologyState> getFinalStates(){
		return this.final_states;
	}
	
	public void readStateModel(String filePath){
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(filePath + System.getProperty("file.separator")+ "data_models.xml");
			doc.getDocumentElement().normalize();
			System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
			NodeList nList = doc.getDocumentElement().getChildNodes();
			searchNode(nList);
	    	}catch (Exception e) {
	    		e.printStackTrace();
	    	}
	}
	
	public void searchNode(NodeList nList){
		
		while(window < hr.length){
			int previousWin = window;
			int currentWin = window;
			ArrayList<PhysiologyState> states = new ArrayList<PhysiologyState>();
			for (int count = 0; count < nList.getLength(); count++) {
		    	Node tempNode = nList.item(count);
		    	// make sure it's element node.
				if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
					if(tempNode.getNodeName().matches("state")){
						PhysiologyState s =  new PhysiologyState();
						s.setStart(previousWin);
						if (tempNode.hasAttributes()) {
							NamedNodeMap nodeMap = tempNode.getAttributes();
							for (int i = 0; i < nodeMap.getLength(); i++) {
								Node node = nodeMap.item(i);
								if(node.getNodeName().equals("name")){
									s.setName("State");
									s.setType(node.getNodeValue());
								}
							}
						}
						for(int j = 0; j < tempNode.getChildNodes().getLength();j++){
							Node childNodeState = tempNode.getChildNodes().item(j);
							if(childNodeState.getNodeName().matches("components")){
								for(int h = 0; h < childNodeState.getChildNodes().getLength(); h++){
									Node childNodeComponent = childNodeState.getChildNodes().item(h);
									if(childNodeComponent.getNodeName().matches("compo")){
										Component c =  new Component(hr, br);
										if (childNodeComponent.hasAttributes()) {
											NamedNodeMap nodeMap = childNodeComponent.getAttributes();
											for (int k = 0; k < nodeMap.getLength(); k++) {
												Node node = nodeMap.item(k);
												if(node.getNodeName().equals("name")){
													c.setName(node.getNodeValue());
												}
											}
										}
										for(int l = 0; l < childNodeComponent.getChildNodes().getLength();l++){
											Node childNode = childNodeComponent.getChildNodes().item(l);
											if(childNode.getNodeName().matches("carac")){
												Carac ca = new Carac();
												if (childNode.hasAttributes()) {
													NamedNodeMap nodeMap = childNode.getAttributes();
													for (int k = 0; k < nodeMap.getLength(); k++) {
														Node node = nodeMap.item(k);
														if(node.getNodeName().equals("name")){
															ca.setName(node.getNodeValue());
														}
														if(node.getNodeName().equals("win")){
															currentWin = previousWin + Integer.parseInt(node.getNodeValue());
															ca.setWindow(previousWin, currentWin);
														}
													}
												}
												c.setCarac(ca);
											}
											if(childNode.getNodeName().matches("chi")){
												Chi ch = new Chi();
												if(childNode.hasAttributes()){
													NamedNodeMap nodeMap = childNode.getAttributes();
													for(int k = 0; k < nodeMap.getLength(); k++){
														Node node = nodeMap.item(k);
														if(node.getNodeName().equals("name")){
															ch.setName(node.getNodeValue());
														}
														if(node.getNodeName().equals("arg1")){
															ch.setArg1(Double.parseDouble(node.getNodeValue()));
														}
														if(node.getNodeName().equals("arg2")){
															ch.setArg2(Double.parseDouble(node.getNodeValue()));
														}
													}
												}
												c.setChi(ch);
											}
										}
										s.addToComponents(c);
									}
								}
							}
							s.setEnd(currentWin);
						}
						states.add(s);
					}
					
				}
			}
			previousWin = currentWin;
			PhysiologyState t = null;
			double maxCValue = 0;
			for(int i = 0; i < states.size(); i++){
				PhysiologyState s = states.get(i);
				if(s.computeCValue() > maxCValue){
					maxCValue = s.computeCValue();
					t = s;
				}
			}
			semifinal_states.add(t);
			window = previousWin;
			//System.out.println("currentWin: " + window);
		}
	}
	
	public void printState(){
		for(int i = 0; i < final_states.size(); i++){
			PhysiologyState s = final_states.get(i);
			System.out.println("start: " + s.getStart()+ " end: "
								+ s.getEnd()+ "name:" + s.getName() +" type: " + s.getType());
		}
	}
	
	public void mergeState(){
		int i = 0;
		while( i < semifinal_states.size() - 1){
			PhysiologyState s = semifinal_states.get(i);
			int j = i + 1;
			while(j < semifinal_states.size()){
				PhysiologyState t = semifinal_states.get(j);
				if(t.getType().matches(s.getType())){
					s.setEnd(t.getEnd());
					//semifinal_states.remove(j);
				}
				else{
					final_states.add(s);
					if(j == semifinal_states.size()-1){
						final_states.add(t);
					}
					break;
				}
				j++;
			}
			i = j--;
		}
	}
	
}
