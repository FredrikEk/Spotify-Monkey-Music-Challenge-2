package com.monkeymusicchallenge.starterkit;

import java.util.ArrayList;


import org.json.JSONArray;

public class MonkeyMap {
	
	ArrayList<ArrayList<String>> map = new ArrayList<ArrayList<String>>();
	ArrayList<MapObject> mapObjects = new ArrayList<MapObject>();
	MapObject user;
	MapObject monkey;
	
	public MonkeyMap(JSONArray jsonarray, JSONArray position) {
		int x = position.getInt(0);
		int y = position.getInt(1);
		for(int i = 0; i < jsonarray.length(); i++) {
			JSONArray row = jsonarray.getJSONArray(i);
			ArrayList<String> rowList = new ArrayList<String>();
			for(int j = 0; j < row.length(); j++) {
				String object = row.getString(j);
				rowList.add(object);
				if(object.equals("user")) {
					user = new MapObject("user", j, i);
				} else if(object.equals("monkey")) {
					if(i == x && j == y) {
						monkey = new MapObject("monkey", j, i);
						System.out.println(monkey.x + " : " + monkey.y);
					} else {
						mapObjects.add(new MapObject(object, j, i));
					}
				}
				else if(!object.equals("empty") && !object.equals("wall")){
					mapObjects.add(new MapObject(object, j, i));
					//System.out.println(object + " - x: " + j + " - y: " + i);
				}
				
			}
			map.add(rowList);
		}
	}
	
	public MonkeyMap(MonkeyMap copyMap) {
		this.user = new MapObject(copyMap.user);
		this.monkey = new MapObject(copyMap.monkey);
		this.map = new ArrayList<ArrayList<String>>();
		for(ArrayList<String> als : copyMap.map) {
			ArrayList<String> newList = new ArrayList<String>();
			for(String s : als) {
				newList.add(new String(s));
			}
			this.map.add(newList);
		}
		this.mapObjects = MapObject.cloneList(copyMap.mapObjects);
	}
	
	public String getPos(int x, int y) {
		//System.out.println(map.get(y).get(x) + " : " + x + ":" + y);
		return map.get(y).get(x);
	}
	
	@Override
	public String toString() {
		return map.toString();
	}
	
	public int getHeuristic() {
		int heuristic = 0;
		if(mapObjects.size() > 1) {
			for(int i = 0; i < mapObjects.size(); i++) {
				MapObject mo = mapObjects.get(i);
				int tempHeuristic = Integer.MAX_VALUE;
				for(int j = 0; j < mapObjects.size(); j++) {
					if(j != i) {
						MapObject mo2 = mapObjects.get(j);
						int diffX = (mo2.x-mo.x)*(mo2.x-mo.x);
						int diffY = (mo2.y-mo.y)*(mo2.y-mo.y);
						int diffLength = (int) Math.floor(Math.sqrt(diffX + diffY));
						if(diffLength < tempHeuristic) {
							tempHeuristic = diffLength;
						}
					}
				}
				heuristic += tempHeuristic;
			}
		} else if(mapObjects.size() == 1) {
			MapObject lastObject = mapObjects.get(0);
			int diffX = (lastObject.x-monkey.x)*(lastObject.x-monkey.x);
			int diffY = (lastObject.y-monkey.y)*(lastObject.y-monkey.y);
			heuristic = (int) Math.floor(Math.sqrt(diffX + diffY));	
		}
		int monkeyDiffX = (user.x-monkey.x)*(user.x-monkey.x);
		int monkeyDiffY = (user.y-monkey.y)*(user.y-monkey.y);
		double diffLength = Math.sqrt(monkeyDiffX + monkeyDiffY);
		
		if(diffLength != 1.0) {
			heuristic += (int) Math.floor(diffLength);
		}
		return heuristic;
	}
	
	public boolean canMove(String move) {
		int x = 0;
		int y = 0;
		switch (move) {
		case "down":
			y = 1;
			break;
		case "up":
			y = -1;
			break;
		case "left":
			x = -1;
			break;
		case "right":
			x = 1;
			break;
		}
		if(monkey.x + x >= 0 && monkey.x + x <= map.size() && monkey.y + y >= 0 && monkey.y + y <= map.size()) {
			String type = getPos(monkey.x + x, monkey.y + y);
			if(type.equals("wall") || type.equals("user")) {
				return false;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}
	
	public void applyMove(String move) {
		int x = 0;
		int y = 0;
		switch (move) {
			case "down":
				y = 1;
				break;
			case "up":
				y = -1;
				break;
			case "left":
				x = -1;
				break;
			case "right":
				x = 1;
				break;
		}
		if(monkey.x + x >= 0 && monkey.x + x <= 5 && monkey.y + y >= 0 && monkey.y + y <= 5) {	
			String type = getPos(monkey.x + x, monkey.y + y);
			if(type.equals("empty")) {
				map.get(monkey.y).set(monkey.x, "empty");
				monkey.x = monkey.x + x;
				monkey.y = monkey.y + y;
				map.get(monkey.y).set(monkey.x, "monkey");
			} else if(type.equals("wall") || type.equals("user")) {
				
			} else {
				int remObject = 0;
				for(int i = 0; i < mapObjects.size(); i++) {
					MapObject mo = mapObjects.get(i);
					if(mo.x == (monkey.x + x) && mo.y == (monkey.y + y)) {
						remObject = i;
						map.get(monkey.y + y).set(monkey.x + x, "empty");
						break;
					}
				}
				mapObjects.remove(remObject);
			}
			
		}
	}
	
	public String getFinalMove() {
		int x = monkey.x - user.x;
		int y = monkey.y - user.y;
		if(x == 0) {
			if(y == 1) {
				return "up";
			} else {
				return "down";
			}
		} else {
			if(x == 1) {
				return "left";
			} else {
				return "right";
			}
		}
	}
	
	
}
