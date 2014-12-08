package com.monkeymusicchallenge.starterkit;

import java.util.ArrayList;

public class PickableObject extends MapObject {

	int score;
	boolean isBanana = false;
	
	public PickableObject(String type, int x, int y) {
		super(type, x, y);
		if(type.equals("playlist")) {
			score = 4;
		} else if(type.equals("album")) {
			score = 2;
		} else if(type.equals("song")) {
			score = 1;
		} else if(type.equals("banana")) {
			score = 0;
			isBanana = true;
		}
	}
	
	public PickableObject(MapObject mo) {
		super(mo);
		if(mo.type.equals("playlist")) {
			score = 4;
		} else if(mo.type.equals("album")) {
			score = 2;
		} else if(mo.type.equals("song")) {
			score = 1;
		} else if(mo.type.equals("banana")) {
			score = 0;
			isBanana = true;
		}
	}
	
	public PickableObject(PickableObject po) {
		super(po);
		this.score = po.score;
	}
	
	@Override
	public PickableObject clone() {
		return new PickableObject(this);
	}
	
	public static ArrayList<PickableObject> clonePickableList(ArrayList<PickableObject> list) {
	    ArrayList<PickableObject> clone = new ArrayList<PickableObject>(list.size());
	    for(PickableObject item: list) clone.add(item.clone());
	    return clone;
	}

	
}
