package com.monkeymusicchallenge.starterkit;

import java.util.ArrayList;

public class Path implements Comparable<Path> {
	
	SimpleMonkeyMap map;
	ArrayList<String> moves;
	int nrOfSteps;
	int actualHeuristic;
	int userHeuristic;
	boolean oldPath = false;
	
	public Path(SimpleMonkeyMap map, ArrayList<String> moves, int nrOfSteps, int actualHeuristic, int userHeuristic) {
		this.map = new SimpleMonkeyMap(map);
		this.moves = new ArrayList<String>(moves);
		this.nrOfSteps = nrOfSteps;
		this.actualHeuristic = actualHeuristic;
		this.userHeuristic = userHeuristic;
	}
	
	public Path(Path p) {
		this.map = new SimpleMonkeyMap(p.map);
		this.moves = new ArrayList<String>(p.moves);
		this.nrOfSteps = p.nrOfSteps;
		this.actualHeuristic = p.actualHeuristic;
		this.userHeuristic = p.userHeuristic;
		//System.out.println(moves);
	}
	
	public int getNrOfSteps() {
		return this.nrOfSteps;
	}
	
	public ArrayList<String> getMoves() {
		return this.moves;
	}
	
	public SimpleMonkeyMap getMap() {
		return this.map;
	}
	
	public int getRemainingHeuristic() {
		return this.actualHeuristic;
	}
	
	public int getHeuristic() {
		return this.actualHeuristic + this.nrOfSteps;
	}
	
	public int getUserHeuristic() {
		return userHeuristic;
	}
	
	public void setHeuristic(int actualHeuristic) {
		this.actualHeuristic = actualHeuristic;
	}
	
	public void setUserHeuristic(int userHeuristic) {
		this.userHeuristic = userHeuristic;
	}
	
	public void applyMove(String move) {
		map.applyMove(move);
		this.nrOfSteps++;
		this.actualHeuristic = map.getHeuristic();
		this.userHeuristic = map.getBaseHeuristic();
		this.moves.add(move);
		
	}
	
	public void applyBaseMove(String move) {
		map.applyMove(move);
		this.nrOfSteps++;
		this.userHeuristic = map.getBaseHeuristic();
		this.moves.add(move);
		
	}
	
	
	public String getStringMap() {
		return map.toString();
	}
	
	public void setOldPath(boolean oldPath){
		this.oldPath = oldPath;
	}
	
	public boolean getOldPath() {
		return this.oldPath;
	}
	
	public boolean isOnTunnel() {
		return map.isOnTunnel();
	}
	
	@Override
	public int compareTo(Path p) {
		return -Integer.compare(p.getHeuristic(), this.getHeuristic());
	}
	
}
