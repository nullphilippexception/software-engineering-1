package client.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import client.model.Coordinates;
import client.model.FigureType;
import client.model.Map;
import client.model.MapElementType;
import client.model.MovementType;

public class PathAnalyzerFactory {
	public static PathAnalyzer getPathAnalyzer(Map map) {
		// initialize all the datastructures and fields
        List<Coordinates> unvisitedElements = new ArrayList<>();
		List<Coordinates> opponentCastleArea = new ArrayList<>();
		HashMap<Coordinates, Double> searchMap = new HashMap<Coordinates, Double>();
		List<Coordinates> visitedCoordinates = new ArrayList<>();;
		MovementType mySiteOfMap;
		List<Coordinates> mountainViewForTreasure = new ArrayList<Coordinates>();
		
		// assign values to searchMap and visitedPenalty
		for(int x = 0; x < map.getNumberOfColumns(); x++) {
			for(int y = 0; y < map.getNumberOfRows(); y++) {
				MapElementType currentMapElementType = map.getElementByCoordinates(new Coordinates(x,y)).getMapElementType();
				if(currentMapElementType.equals(MapElementType.MOUNTAIN)) { searchMap.put(new Coordinates(x,y), 2.0); }
				if(currentMapElementType.equals(MapElementType.GRASS)) { searchMap.put(new Coordinates(x,y), 1.0); }
				if(currentMapElementType.equals(MapElementType.WATER)) { searchMap.put(new Coordinates(x,y), -500.0); } // CHANGE
			}
		}
		 
		// assign value to mySiteOfMap
		if(map.getNumberOfColumns() > 8) { // it is a 4x16 map
			if(map.findThisFigure(FigureType.MY_CASTLE).getX() > 7) { // my avatar is on east site of field
				mySiteOfMap = MovementType.EAST;
			}
			else { // my avatar is on west site of field
				mySiteOfMap = MovementType.WEST;
			}
		}
		else { // it is a 8x8 map
			if(map.findThisFigure(FigureType.MY_CASTLE).getY() > 3) { // my avatar is on south site of field
				mySiteOfMap = MovementType.SOUTH;
			}
			else { // my avatar is on north site of field
				mySiteOfMap = MovementType.NORTH;
			}
		}
		
		// assign values to unvisitedCoordinates
		for(int x = 0; x < map.getNumberOfColumns(); x++) {
			for(int y = 0; y < map.getNumberOfRows(); y++) {
				if(MapAnalyzer.isOnMySite(mySiteOfMap, new Coordinates(x,y)) 
				   && !map.getElementByCoordinates(new Coordinates(x,y)).getMapElementType().equals(MapElementType.WATER)) {
					unvisitedElements.add(new Coordinates(x,y));
				}
			}
		}
		
		// assign locations of mountains for treasure view
		int offsetX = 0;
		int offsetY = 0;
		if(mySiteOfMap.equals(MovementType.NORTH)) ; // no offset needed
		if(mySiteOfMap.equals(MovementType.SOUTH)) {
			offsetY = 4;
		}
		if(mySiteOfMap.equals(MovementType.WEST)) ; // no offset needed
		if(mySiteOfMap.equals(MovementType.EAST)) {
			offsetX = 8;
		}
		
		// COMMENT THIS FOR NEW AI DEMONSTRATION
		mountainViewForTreasure.add(new Coordinates(1+offsetX,1+offsetY));
		mountainViewForTreasure.add(new Coordinates(1+offsetX,2+offsetY));
		mountainViewForTreasure.add(new Coordinates(3+offsetX,1+offsetY));
		mountainViewForTreasure.add(new Coordinates(3+offsetX,2+offsetY));
		mountainViewForTreasure.add(new Coordinates(6+offsetX,1+offsetY));
		mountainViewForTreasure.add(new Coordinates(6+offsetX,2+offsetY));
		
		// return the new PathAnalyzer
		return new PathAnalyzer(map, searchMap, mySiteOfMap, opponentCastleArea, mountainViewForTreasure, 
				visitedCoordinates, unvisitedElements);
	}
}
