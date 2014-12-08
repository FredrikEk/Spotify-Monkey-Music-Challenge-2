package com.monkeymusicchallenge.starterkit;

import java.util.ArrayList;



import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

import org.json.JSONArray;

public class SimpleMonkeyMap {
	
	private class HomewardsComparator implements Comparator<Path> {
		@Override
		public int compare(Path p1, Path p2) {
			return p1.getUserHeuristic() - p2.getUserHeuristic();
		}
	}
	
	public HomewardsComparator comparator = new HomewardsComparator();
	
	
	ArrayList<ArrayList<String>> map = new ArrayList<ArrayList<String>>();
	//ArrayList<ArrayList<String>> map2 = new ArrayList<ArrayList<String>>();
	ArrayList<MapObject> mapObjects = new ArrayList<MapObject>();
	ArrayList<MapObject> users = new ArrayList<MapObject>();;
	ArrayList<PickableObject> pickableObjects = new ArrayList<PickableObject>();
	ArrayList<Door> doors = new ArrayList<Door>();
	ArrayList<MapObject> levers = new ArrayList<MapObject>();
	ArrayList<Tunnel> tunnels = new ArrayList<Tunnel>();
	MapObject monkey;
	MapObject evilMonkey;
	int maxInventory = 0;
	int currentInventory = 0;
	
	MapObject focusedObject;
	MapObject focusedUser;
	
	public SimpleMonkeyMap(JSONArray jsonarray, JSONArray position, JSONArray currInventory, int inventory) {
		maxInventory = inventory;
		currentInventory = currInventory.length();
		int x = position.getInt(0);
		int y = position.getInt(1);
		for(int i = 0; i < jsonarray.length(); i++) {
			JSONArray row = jsonarray.getJSONArray(i);
			ArrayList<String> rowList = new ArrayList<String>();
			for(int j = 0; j < row.length(); j++) {
				String object = row.getString(j);
				if(object.contains("tunnel")) {
					rowList.add("tunnel");
				} else {
					rowList.add(object);
				}
				if(object.equals("user")) {
					users.add(new MapObject("user", j, i));
				} else if(object.equals("monkey")) {
					if(i == x && j == y) {
						monkey = new MapObject("monkey", j, i);
						System.out.println(monkey.x + " : " + monkey.y);
					} else {
						evilMonkey = new MapObject("monkey", j, i);
						mapObjects.add(new MapObject(object, j, i));
					}
				} else if(object.equals("album") || object.equals("song") || object.equals("playlist") || object.equals("banana")) {
					pickableObjects.add(new PickableObject(object, j ,i));
					mapObjects.add(new PickableObject(object, j, i));
				} else if(object.equals("open-door")) {
					doors.add(new Door(new MapObject(object, j, i), true));
					mapObjects.add(new Door(new MapObject(object, j, i), true));
				} else if(object.equals("closed-door")) {
					doors.add(new Door(new MapObject(object, j, i), false));
					mapObjects.add(new Door(new MapObject(object, j, i), false));
				} else if(object.equals("lever")) {
					levers.add(new MapObject(object, j, i));
					mapObjects.add(new MapObject(object, j, i));	
				} else if(object.contains("tunnel")) {
					int s = Integer.parseInt(object.split("-")[1]);
					tunnels.add(new Tunnel(new MapObject("tunnel", j, i), s));
					mapObjects.add(new Tunnel(new MapObject("tunnel", j, i), s));
				}
				else if(!object.equals("empty") && !object.equals("wall")){
					mapObjects.add(new MapObject(object, j, i));
					//System.out.println(object + " - x: " + j + " - y: " + i);
				}
				
			}
			map.add(rowList);
		}
		
		/*for(ArrayList<String> als : map) {
			ArrayList<String> newList = new ArrayList<String>();
			for(String s : als) {
				newList.add(new String(s));
			}
			this.map2.add(newList);
		}*/
	}
	
	public SimpleMonkeyMap(SimpleMonkeyMap copyMap) {
		this.monkey = new MapObject(copyMap.monkey);
		this.evilMonkey = new MapObject(copyMap.evilMonkey);
		
		this.users = MapObject.cloneList(copyMap.users);
		this.doors = Door.cloneDoorList(copyMap.doors);
		this.pickableObjects = PickableObject.clonePickableList(copyMap.pickableObjects);
		this.levers = MapObject.cloneList(copyMap.levers);
		this.tunnels = Tunnel.cloneTunnelList(copyMap.tunnels);
		
		this.currentInventory = copyMap.currentInventory;
		this.maxInventory = copyMap.maxInventory;
		this.focusedObject = copyMap.focusedObject.clone();
		this.focusedUser = copyMap.focusedUser.clone();
		
		this.map = new ArrayList<ArrayList<String>>();
		for(ArrayList<String> als : copyMap.map) {
			ArrayList<String> newList = new ArrayList<String>();
			for(String s : als) {
				newList.add(new String(s));
			}
			this.map.add(newList);
		}
		
		//this.map2 = new ArrayList<ArrayList<String>>();
		/*for(ArrayList<String> als : copyMap.map) {
			ArrayList<String> newList = new ArrayList<String>();
			for(String s : als) {
				newList.add(new String(s));
			}
			this.map2.add(newList);
		}*/
		
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
		double heuristic = Integer.MAX_VALUE;
		if(maxInventory > currentInventory && pickableObjects.size() != 0) {
			for(int i = 0; i < pickableObjects.size(); i++) {
				PickableObject mo = pickableObjects.get(i);
				int diffX = (mo.x-monkey.x)*(mo.x-monkey.x);
				int diffY = (mo.y-monkey.y)*(mo.y-monkey.y);
				double tempHeuristic = Math.sqrt(diffX + diffY);	
				
				if(heuristic > tempHeuristic) {
					if(tempHeuristic == 1.0) {
						heuristic = 0.0;
					} else {
						heuristic = tempHeuristic;
					}
					focusedObject = mo;
				}
			}
		
		} else {
			heuristic = 0;
			double diffLength = Integer.MAX_VALUE;
			for(MapObject user : users) {
				int monkeyDiffX = (user.x-monkey.x)*(user.x-monkey.x);
				int monkeyDiffY = (user.y-monkey.y)*(user.y-monkey.y);
				double tempLength = Math.sqrt(monkeyDiffX + monkeyDiffY);
				if(diffLength > tempLength) {
					diffLength = tempLength;
					focusedObject = user;
				}
			} 
			
			if(diffLength != 1.0) {
				heuristic = (int) Math.floor(diffLength);
			}
		}
		return (int) Math.floor(heuristic);
	}
	
	public int getBaseHeuristic() {
		int heuristic = 0;
		double diffLength = Integer.MAX_VALUE;
		for(MapObject user : users) {
			int monkeyDiffX = (user.x-monkey.x)*(user.x-monkey.x);
			int monkeyDiffY = (user.y-monkey.y)*(user.y-monkey.y);
			double tempLength = Math.sqrt(monkeyDiffX + monkeyDiffY);
			if(diffLength > tempLength) {
				diffLength = tempLength;
				focusedUser = user;
			}
		} 
		
		if(diffLength != 1.0) {
			heuristic = (int) Math.floor(diffLength);
		}
		return heuristic;
	}
	
	public boolean returnToBase(int remainingTurns) {
		if(currentInventory == 0) {
			return false;
		}
		HashMap<String, Path> hmPath = new HashMap<String, Path>();
		
		Path currentPath = new Path(this, new ArrayList<String>(), 0, this.getHeuristic(), this.getBaseHeuristic());
		PriorityQueue<Path> allPaths = new PriorityQueue<Path>(100, comparator);
		
		while(currentPath != null && currentPath.getUserHeuristic() != 0) {
			SimpleMonkeyMap tempMap = currentPath.getMap();
					
			if(tempMap.canMove("down")) {
				Path p = new Path(currentPath);
				p.applyBaseMove("down");
				if(p.getUserHeuristic() <= remainingTurns) {
					Path p2 = hmPath.get(p.getStringMap());
					if(p2 == null) {
						allPaths.add(p);
						hmPath.put(p.getStringMap(), p);
					}
				}
			}
			
			if(tempMap.canMove("up")) {
				Path p = new Path(currentPath);
				p.applyBaseMove("up");
				if(p.getUserHeuristic() <= remainingTurns) {
					Path p2 = hmPath.get(p.getStringMap());
					if(p2 == null) {
						allPaths.add(p);
						hmPath.put(p.getStringMap(), p);
					}
				}
			}
			
			if(tempMap.canMove("left")) {
				Path p = new Path(currentPath);
				p.applyBaseMove("left");
				if(p.getUserHeuristic() <= remainingTurns) {
					Path p2 = hmPath.get(p.getStringMap());
					if(p2 == null) {
						allPaths.add(p);
						hmPath.put(p.getStringMap(), p);
					}
				}
			}
			
			if(tempMap.canMove("right")) {
				Path p = new Path(currentPath);
				p.applyBaseMove("right");
				if(p.getUserHeuristic() <= remainingTurns) { 
					Path p2 = hmPath.get(p.getStringMap());
					if(p2 == null) {
						allPaths.add(p);
						hmPath.put(p.getStringMap(), p);
					}
				}
			}
			
			currentPath = allPaths.poll();
		}
		if(currentPath != null) {
			return currentPath.nrOfSteps > remainingTurns - 3;
		} else {
			return true;
		}
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
		if(monkey.x + x >= 0 && monkey.x + x <= map.size()-1 && monkey.y + y >= 0 && monkey.y + y <= map.size()-1) {
			String type = getPos(monkey.x + x, monkey.y + y);
			if(type.equals("wall") || type.equals("user") || type.equals("closed-door")) {
				return false;
			} else {
				if(maxInventory == currentInventory) {
					if(type.equals("empty") || type.equals("tunnel") || type.equals("open-door") || type.equals("lever") || type.equals("monkey")) {
						return true;
					} else {
						return false;
					}
				}
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
		if(monkey.x + x >= 0 && monkey.x + x <= map.size()-1 && monkey.y + y >= 0 && monkey.y + y <= map.size()-1) {	
			String type = getPos(monkey.x + x, monkey.y + y);
			if(type.equals("empty") || type.equals("monkey")) {
				map.get(monkey.y).set(monkey.x, AI.initMap.get(monkey.y).get(monkey.x));
				monkey.x = monkey.x + x;
				monkey.y = monkey.y + y;
				map.get(monkey.y).set(monkey.x, "monkey");
			} else if(type.equals("tunnel")){
				Tunnel tempTunnel = null;
				for(Tunnel tunnel : AI.initTunnels) {
					if(tunnel.x == monkey.x + x && tunnel.y == monkey.y + y) {
						tempTunnel = tunnel;
						break;
					}
				}
				for(Tunnel tunnel : AI.initTunnels) {
					if(tunnel != tempTunnel && tunnel.id == tempTunnel.id) {
						tempTunnel = tunnel;
						break;
					}
				}
				map.get(monkey.y).set(monkey.x, AI.initMap.get(monkey.y).get(monkey.x));
				monkey.x = tempTunnel.x;
				monkey.y = tempTunnel.y;
				map.get(monkey.y).set(monkey.x, "monkey");
			} else if(type.equals("lever")) {
				for(int i = 0; i < map.size(); i++) {
					ArrayList<String> arrString = map.get(i);
					for(int j = 0; j < arrString.size(); j++) {
						String s = arrString.get(j);
						if(s.equals("open-door")) {
							map.get(i).set(j, "closed-door");
						}
						else if(s.equals("closed-door")) {
							map.get(i).set(j, "open-door");
						}
					}
					for(Door door : doors) {
						door.isOpen = !door.isOpen;
					}
				}
			} else if(type.equals("wall") || type.equals("user")) {
				
			} else {
				int remObject = 0;
				for(int i = 0; i < mapObjects.size(); i++) {
					MapObject mo = mapObjects.get(i);
					if(mo.x == (monkey.x + x) && mo.y == (monkey.y + y)) {
						remObject = i;
						map.get(monkey.y + y).set(monkey.x + x, "empty");
						AI.initMap.get(monkey.y).set(monkey.x + x, "empty");
						break;
					}
				}
				mapObjects.remove(remObject);
			}
			
		}
	}
	/*
	public boolean isOnTunnel() {
		return (map2.get(monkey.y).get(monkey.x).contains("tunnel"));
		
	}
	*/
	public String getFinalMove() {
		int x = monkey.x - focusedObject.x;
		int y = monkey.y - focusedObject.y;
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
	
	public String getUserFinalMove() {
		int x = monkey.x - focusedUser.x;
		int y = monkey.y - focusedUser.y;
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
	
	public String getEvilMonkeyFinalMove() {
		int x = monkey.x - evilMonkey.x;
		int y = monkey.y - evilMonkey.y;
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
	
	public boolean isOnTunnel() {
		return AI.initMap.get(monkey.y).get(monkey.x).contains("tunnel");
	}
	
	public boolean aggresiveMonkey() {
		double aggroMonkey = Math.random();
		if(aggroMonkey >= 0.33) {
			return false;
		} else {
			int monkeyDiffX = (evilMonkey.x-monkey.x)*(evilMonkey.x-monkey.x);
			int monkeyDiffY = (evilMonkey.y-monkey.y)*(evilMonkey.y-monkey.y);
			double tempLength = Math.sqrt(monkeyDiffX + monkeyDiffY);
			if(tempLength == 1.0) {
				return true;
			} else {
				return false;
			}
		}
	}
	
	public boolean returnEarly() {
		if(currentInventory > 1) {
			double diffLength = Integer.MAX_VALUE;
			MapObject testUser = null;
			for(MapObject user : users) {
				int monkeyDiffX = (user.x-monkey.x)*(user.x-monkey.x);
				int monkeyDiffY = (user.y-monkey.y)*(user.y-monkey.y);
				double tempLength = Math.sqrt(monkeyDiffX + monkeyDiffY);
				if(diffLength > tempLength) {
					diffLength = tempLength;
					testUser = user;
				}
			}
			if(diffLength == 1.0) {
				focusedUser = testUser;
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
}
