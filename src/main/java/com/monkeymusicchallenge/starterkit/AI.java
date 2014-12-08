package com.monkeymusicchallenge.starterkit;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import org.json.JSONArray;
import org.json.JSONObject;

public class AI {
	
	private class AStarComparator implements Comparator<Path> {
		@Override
		public int compare(Path p1, Path p2) {
			return p1.getHeuristic() - p2.getHeuristic();
		}
	}
	
	private class HomewardsComparator implements Comparator<Path> {
		@Override
		public int compare(Path p1, Path p2) {
			return p1.getUserHeuristic() - p2.getUserHeuristic();
		}
	}
	
	SimpleMonkeyMap map;
	ArrayList<String> moves = new ArrayList<String>();
	Comparator<Path> comparator = new AStarComparator();
	Comparator<Path> takeMeHomeComparator = new HomewardsComparator();
	boolean initialize = false;
	int numberOfTurns = 0;
	int inventorySize = 0;
	
	public static ArrayList<ArrayList<String>> initMap;
	public static ArrayList<Tunnel> initTunnels;
	SimpleMonkeyMap initSimpleMap;
	
	
	public Map<String, Object> move(final JSONObject gameState) {
		final JSONArray inventory = gameState.getJSONArray("inventory");
		for (int i = 0; i < inventory.length(); i++) {
			if(inventory.getString(i).equals("banana")) {
				final Map<String, Object> nextCommand = new HashMap<String, Object>();
			    nextCommand.put("command", "use");
			    nextCommand.put("item", "banana");
			    return nextCommand;
			}
			if(inventory.getString(i).equals("trap")) {
				final Map<String, Object> nextCommand = new HashMap<String, Object>();
			    nextCommand.put("command", "use");
			    nextCommand.put("item", "trap");
			    return nextCommand;
			}
		}
		
		JSONObject buff = gameState.getJSONObject("buffs");
		boolean superspeed = false;
		if(buff.keySet().contains("speedy")) {
			superspeed = true;
		}
		
		HashMap<String, Path> hmPath = new HashMap<String, Path>();
		
		final int remainingNumberOfTurns = gameState.getInt("remainingTurns");
		
		final JSONArray currentLevelLayout = gameState.getJSONArray("layout");
		final JSONArray position = gameState.getJSONArray("position");
		
		if(!initialize) {
			numberOfTurns = gameState.getInt("remainingTurns");
			inventorySize = gameState.getInt("inventorySize");
			initSimpleMap = new SimpleMonkeyMap(currentLevelLayout, position, inventory, inventorySize);
			initMap = initSimpleMap.map;
			initTunnels = initSimpleMap.tunnels;
			initialize = true;
		}
		// Every game has a limited number of turns. Use every turn wisely!
		//System.out.println(currentLevelLayout.toString());
		//if(moves.size() == 0) {
			map = new SimpleMonkeyMap(currentLevelLayout, position, inventory, inventorySize);
			
			if(remainingNumberOfTurns > 30 || !map.returnToBase(remainingNumberOfTurns)) {
				if(map.returnEarly()) {
					final Map<String, Object> nextCommand = new HashMap<String, Object>();
				    nextCommand.put("command", "move");
				    nextCommand.put("direction", map.getUserFinalMove());
				    return nextCommand;
				}
				
				if(map.aggresiveMonkey()) {
					final Map<String, Object> nextCommand = new HashMap<String, Object>();
				    nextCommand.put("command", "move");
				    nextCommand.put("direction", map.getEvilMonkeyFinalMove());
				    return nextCommand;
				}
				Path currentPath = new Path(map, new ArrayList<String>(), 0, map.getHeuristic(), map.getBaseHeuristic());
				PriorityQueue<Path> allPaths = new PriorityQueue<Path>(100, comparator);
				while(currentPath != null && currentPath.getRemainingHeuristic() != 0) {
					SimpleMonkeyMap tempMap = currentPath.getMap();
							
					if(tempMap.canMove("down")) {
						Path p = new Path(currentPath);
						p.applyMove("down");
						if(p.getHeuristic() <= remainingNumberOfTurns) {
							Path p2 = hmPath.get(p.getStringMap());
							if(p2 == null) {
								if(!p.isOnTunnel()) {
									hmPath.put(p.getStringMap(), p);
								}
								allPaths.add(p);
							}
						}
					}
					
					if(tempMap.canMove("up")) {
						Path p = new Path(currentPath);
						p.applyMove("up");
						if(p.getHeuristic() <= remainingNumberOfTurns) {
							Path p2 = hmPath.get(p.getStringMap());
							if(p2 == null) {
								if(!p.isOnTunnel()) {
									hmPath.put(p.getStringMap(), p);
								}
								allPaths.add(p);
							}
						}
					}
					
					if(tempMap.canMove("left")) {
						Path p = new Path(currentPath);
						p.applyMove("left");
						if(p.getHeuristic() <= remainingNumberOfTurns) {
							Path p2 = hmPath.get(p.getStringMap());
							if(p2 == null) {
								if(!p.isOnTunnel()) {
									hmPath.put(p.getStringMap(), p);
								}
								allPaths.add(p);
							}
						}
					}
					
					if(tempMap.canMove("right")) {
						Path p = new Path(currentPath);
						p.applyMove("right");
						if(p.getHeuristic() <= remainingNumberOfTurns) { 
							Path p2 = hmPath.get(p.getStringMap());
							if(p2 == null) {
								if(!p.isOnTunnel()) {
									hmPath.put(p.getStringMap(), p);
								}
								allPaths.add(p);
							}
						}
					}
					
					currentPath = allPaths.poll();
				}
			if(currentPath != null) {
				moves = currentPath.moves;
				moves.add(currentPath.map.getFinalMove());
				//System.out.println(moves.toString());
			} else {
				final Map<String, Object> nextCommand = new HashMap<String, Object>();
				nextCommand.put("command", "move");
				nextCommand.put("direction", randomDirection());
				return nextCommand;
			}
		}
		else {
			moves = baseReturn(remainingNumberOfTurns);
		}
		//}
		//System.out.println(moves);
		final Map<String, Object> nextCommand = new HashMap<String, Object>();
	    nextCommand.put("command", "move");
	    if(superspeed && moves.size() > 1) {
	    	JSONArray jsArr = new JSONArray();
	    	jsArr.put(moves.remove(0));
	    	jsArr.put(moves.remove(0));
	    	nextCommand.put("directions", jsArr);	
	    } else {
	    	nextCommand.put("direction", moves.remove(0));
	    }
	    return nextCommand;
	
	}
	
	
	public ArrayList<String> baseReturn(int remainingNumberOfTurns) {
		HashMap<String, Path> hmPath = new HashMap<String, Path>();
		ArrayList<String> moves2 = new ArrayList<String>();
		Path currentPath = new Path(map, new ArrayList<String>(), 0, map.getHeuristic(), map.getBaseHeuristic());
		PriorityQueue<Path> allPaths = new PriorityQueue<Path>(100, takeMeHomeComparator);
		while(currentPath != null && currentPath.getUserHeuristic() != 0) {
			SimpleMonkeyMap tempMap = currentPath.getMap();
					
			if(tempMap.canMove("down")) {
				Path p = new Path(currentPath);
				if(p.getUserHeuristic() <= remainingNumberOfTurns) {
					Path p2 = hmPath.get(p.getStringMap());
					if(p2 == null) {
						allPaths.add(p);
						if(!p.isOnTunnel()) {
							hmPath.put(p.getStringMap(), p);
						}
					}
				}
			}
			
			if(tempMap.canMove("up")) {
				Path p = new Path(currentPath);
				p.applyBaseMove("up");
				if(p.getUserHeuristic() <= remainingNumberOfTurns) {
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
				if(p.getUserHeuristic() <= remainingNumberOfTurns) {
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
				if(p.getUserHeuristic() <= remainingNumberOfTurns) { 
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
			moves2 = currentPath.moves;
			moves2.add(map.getUserFinalMove());
		} else {
			moves2.add(randomDirection());
		}
		return moves2; 
	}
	
	private String randomDirection() {
		  return new String[] {"up", "down", "left", "right"}[(int) Math.round(Math.random()*3)];
	}
	/*  
	int[] testInt = new int[] {0,0,3,3,1,1};
	  
	private String moveSmarter(int i) {
		String[] test = new String[] {"up", "down", "left", "right"};
		if(i > 5) {
			return "right";
		} else {
			return test[testInt[i]];
		}
	}
	*/
}
