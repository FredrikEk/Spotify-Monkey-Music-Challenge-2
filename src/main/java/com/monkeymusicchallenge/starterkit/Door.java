package com.monkeymusicchallenge.starterkit;

import java.util.ArrayList;

public class Door extends MapObject {

	public boolean isOpen;
	
	public Door(Door d){
		super(d);
		this.isOpen=d.isOpen;
	}
	
	public Door(MapObject mo, boolean isOpen) {
		super(mo);
		this.isOpen = isOpen;
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public Door clone(){
		return new Door(this);	
	}
	
	public static ArrayList<Door> cloneDoorList(ArrayList<Door> list) {
	    ArrayList<Door> clone = new ArrayList<Door>(list.size());
	    for(Door door: list) clone.add(door.clone());
	    return clone;
	}

}
