package com.monkeymusicchallenge.starterkit;

import java.awt.List;
import java.util.ArrayList;

public class MapObject {

	String type;
	int x;
	int y;
	public MapObject(String type, int x, int y) {
		this.type = type;
		this.x = x;
		this.y = y;
	}
	
	public MapObject(MapObject mo) {
		this.type = mo.type;
		this.x = mo.x;
		this.y = mo.y;
	}
	
	public MapObject clone() {
		return new MapObject(this);
	}
	
	public static ArrayList<MapObject> cloneList(ArrayList<MapObject> list) {
	    ArrayList<MapObject> clone = new ArrayList<MapObject>(list.size());
	    for(MapObject item: list) {
	    	if(item instanceof Door) {
	    		clone.add(((Door) item).clone());
	    	} else if(item instanceof PickableObject) {
	    		clone.add(((PickableObject) item).clone());
	    	} else {
	    		clone.add(item.clone());
	    	}
	    }
	    return clone;
	}
}
