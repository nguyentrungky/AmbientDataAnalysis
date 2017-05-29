package gnad.data;

import java.util.Map;
import java.util.Map.Entry;


public class OSMNode {

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLon() {
		return lon;
	}

	public void setLon(String lon) {
		this.lon = lon;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Map<String, String> getTags() {
		return tags;
	}

	private String id;

	private String lat;

	private String lon;

	private final Map<String, String> tags;

	private String version;
	
	public OSMNode(String id2, String latitude, String longitude, String version2, Map<String, String> tags2){
		id = id2; lat =latitude;lon=  longitude; version=version2;tags=tags2;
	}
	
	public String toString(){
		String chaine = getId() + ":" + getLat() + ":" + getLon();
		
		chaine += "[";
			for (Entry<String, String> t : tags.entrySet()){
				chaine += t.getKey() + ":" + t.getValue() + "; ";
			}
			chaine += "]";
		return chaine;
	}

}