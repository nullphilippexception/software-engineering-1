package client.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import client.model.Coordinates;
import client.model.FigureType;
import client.model.Map;
import client.model.MapElementType;
import client.model.MovementType;
import client.pathalgorithm.*;

// main AI method that calls helper methods to analyze map and game circumstances
// gets a target coordinate from MapAnalyzer depending on phase of game and then moves towards this target
public class PathAnalyzer {
	private Map map; // the current map of the game
	private EGamePhase currentPhase; // current phase of the game
	private boolean treasureFound;  // indicator if treasure has been found, used to switch between states
	private MovementType mySiteOfMap; // the site of the map the client is located on
	private List<Coordinates> opponentCastleArea; // will be set by a method call by the MainClient after six moves, used to find castle quick
	private List<Coordinates> mountainViewForTreasure; // a list of mountains on the map to go to,to quickly find treasure
	private List<Coordinates> visitedCoordinates; // a list of all the visited coordinates and penalties for x visits to them (expansion feature)
	private List<Coordinates> unvisitedElements; // list of elements on clients side of map that are legal to vist but havent been visited yet
	private Logger logger; // logger for this object
	
	// initialized with an adapted factory pattern -> PathAnalyzerFactory
	public PathAnalyzer(Map map, HashMap<Coordinates, Double> searchMap, MovementType mySiteOfMap, List<Coordinates> opponentCastleArea,
			List<Coordinates> mountainViewForTreasure, List<Coordinates> visitedCoordinates, List<Coordinates> unvisitedElements) {
		this.map = map;
		this.currentPhase = EGamePhase.FIND_TREASURE; 
		this.treasureFound = false;
		this.mySiteOfMap = mySiteOfMap;
		this.opponentCastleArea = opponentCastleArea;
		this.mountainViewForTreasure = mountainViewForTreasure;
		this.visitedCoordinates = visitedCoordinates;
		this.unvisitedElements = unvisitedElements;
		this.logger = LoggerFactory.getLogger(PathAnalyzer.class);
	} 
	
	// main method of this class, get target, ask for path to it and act accordingly
	public MovementType move() {
		Coordinates target = new Coordinates(-1,-1); // default value for undefined coordinates
		// if client is already on a mountain for treasure search: remove this mountain from list of visited mountains
		int visitedMountain = -1; // default to make sure it is a not possible value (negative index)
		for(int i = 0; i < mountainViewForTreasure.size(); i++) {
				if(map.findThisFigure(FigureType.MY_AVATAR).equals(mountainViewForTreasure.get(i))) { visitedMountain = i; }
		}
		if(visitedMountain > -1) { mountainViewForTreasure.remove(visitedMountain);}
		
		// remove visited Coordinates from possible area of opponents castle
		for(Coordinates coord : visitedCoordinates) {
			// edge case: second part of && needed if opponent castle is discovered by mountain before own treasure is discovered
			if(opponentCastleArea.contains(coord) && !(map.findThisFigure(FigureType.OPPONENT_CASTLE).equals(coord))) {
				boolean removalSuccessful = opponentCastleArea.remove(coord);
				if(removalSuccessful) logger.info("Removal of " + coord + " from opponentCastleArea was successful");
			}
		}
		
		// determin next target and also log the current task
		logger.info("Current phase of game: " + currentPhase);
		target = determineTarget();
		if(!target.equals(new Coordinates(-1,-1))) return moveTowardsTarget(target);
		else {
			logger.warn("No reasonable target found");
			return MovementType.EAST; // default return
		}
	}
	
	// helper method to mark already visited coordinates in visitedCoordinates and unvisitedElements
	public void markVisitedCoordinates(Coordinates coord) {
		if(!visitedCoordinates.contains(coord)) visitedCoordinates.add(coord);
		unvisitedElements.remove(coord);
	}
	
	// give a new game map to the class on whose basis future computations will be made
	public void setMap(Map map) {
		this.map = map;
	}
	
	// set current game phase to force class to act accordingly
	public void setGamePhase(EGamePhase currentPhase) {
		this.currentPhase = currentPhase;
	}
	
	// helper method to let pathAnalyzer know that treasure was found
	public void treasureFound() {
		this.treasureFound = true;
		logger.info("TREASURE FOUND!");
	}
	
	// helper method to set area of opponents castle (once six moves are made by this client)
	public void determineOpponentCastleArea() {
		this.opponentCastleArea = OpponentAreaAnalyzer.computeOpponentCastleArea(map, mySiteOfMap);
	}
	
	// private helper method to strictly move towards an important target
	private MovementType moveTowardsTarget(Coordinates target) {
		// determine current client pos and combine it with target paramter to hand it to ShortestPathAlgorithm
		Coordinates myCurrentPos = map.findThisFigure(FigureType.MY_AVATAR);
		ShortestPathAlgorithm algo = new ShortestPathAlgorithm(map, myCurrentPos, target);
		Coordinates nextStep = algo.computeShortestPath().get(0); // first step on path
		if(nextStep.getX() > myCurrentPos.getX()) return MovementType.EAST;
		if(nextStep.getX() < myCurrentPos.getX()) return MovementType.WEST;
		if(nextStep.getY() > myCurrentPos.getY()) return MovementType.SOUTH;
		if(nextStep.getY() < myCurrentPos.getY()) return MovementType.NORTH;
		
		// following code segment will never be reached
		logger.error("Illegal target coordinates");
		return MovementType.EAST; // default return
	}
	
	// helper method that determines the next target coordinate dependent on the phase of the game
	// calls other classes if computation is non-trivial
	private Coordinates determineTarget() {
		if(currentPhase.equals(EGamePhase.GET_CASTLE)) return map.findThisFigure(FigureType.OPPONENT_CASTLE);
		else if(currentPhase.equals(EGamePhase.GET_TREASURE)) return map.findThisFigure(FigureType.MY_TREASURE);
		else if(currentPhase.equals(EGamePhase.GET_OPP_SITE)) return MapAnalyzer.closestOpponentCastleAreaElement(map, 
																							opponentCastleArea, mySiteOfMap);
		else if(currentPhase.equals(EGamePhase.FIND_TREASURE)) return TreasureScanner.scanForTreasure(map, 
																		mySiteOfMap, mountainViewForTreasure, unvisitedElements);
		logger.warn("Non-identifiable game phase: " + currentPhase);
		return new Coordinates(-1,-1); // default return for bad request on coordinates
	}
	
	

}
