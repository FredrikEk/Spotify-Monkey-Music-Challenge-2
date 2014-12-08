package com.monkeymusicchallenge.starterkit;

import java.util.ArrayList;

public class Tunnel extends MapObject {

	int id;
	
	public Tunnel(MapObject mo, int id) {
		super(mo);
		this.id = id;
	}

	public Tunnel(Tunnel tunnel) {
		super(tunnel);
		this.id = tunnel.id;
	}
	
	public Tunnel clone() {
		return new Tunnel(this);
	}
	
	public static ArrayList<Tunnel> cloneTunnelList(ArrayList<Tunnel> list) {
	   ArrayList<Tunnel> clone = new ArrayList<Tunnel>(list.size());
	    for(Tunnel item: list) clone.add(item.clone());
	    return clone;
	}
}
