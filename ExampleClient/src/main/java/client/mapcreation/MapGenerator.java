package client.mapcreation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import client.model.Coordinates;
import client.model.Figure;
import client.model.FigureType;
import client.model.Map;
import client.model.MapElement;
import client.model.MapElementType;

// class that generates half maps that are then sent to the server after they are validated by MapValidator.java
public class MapGenerator {
	
	// Approach: Try to be reasonably intelligent while creating Map, to avoid lots of invalid maps
	// and therefore avoid performance loss
	public static Map generateMap() {
		HashMap<Coordinates, MapElement> elements = new HashMap<>();
		List<Coordinates> grassElementTracker = new ArrayList<>(); // track where grass is to place castle -> avoid another iteration through elements
		final int NO_OF_ELEMENTS_X_AXIS = 8; // number of columns for a half map
		final int NO_OF_ELEMENTS_Y_AXIS = 4; // number of rows for a half map
		
		// required min amount per MapElementType: 3 MOUNTAIN, 15 GRASS, 4 WATER
		// scale that to 32 elements: if randomNumber < 4 : Mountain, if >= 4 && < 7 : Water, else : Grass 
		// (make mountains more likely than water, they are more useful anyways)
		// general restriction: randomNumber < 22
		
		// create basic map consisting of MapElements with a given MapElementType
		Random r = new Random();
		for(int x = 0; x < NO_OF_ELEMENTS_X_AXIS; x++) { 
			for(int y = 0; y < NO_OF_ELEMENTS_Y_AXIS; y++) {
				int elementDecider = r.nextInt(22);
				MapElementType typeToPut;
				if(elementDecider < 4) { typeToPut = MapElementType.MOUNTAIN; } 
				else if(elementDecider >= 4 && elementDecider < 7) { typeToPut = MapElementType.WATER; }
				else { 
					typeToPut = MapElementType.GRASS; 
				}
				// plannedMountainField adjusts map -> make it easy to see all fields
				if(plannedMountainField(x,y)) { typeToPut = MapElementType.MOUNTAIN; } // COMMENT THIS FOR NEW AI DEMONSTRATION
				Coordinates coordToBeInserted = new Coordinates(x,y);
				MapElement elementToBeInserted = new MapElement(typeToPut, new ArrayList<Figure>());
				elements.put(coordToBeInserted, elementToBeInserted);
				if(typeToPut.equals(MapElementType.GRASS)) grassElementTracker.add(new Coordinates(x,y));
			}
		}
		
		// put this clients Avatar and castle on a random spot on the map
		int targetElementForCastle = r.nextInt(grassElementTracker.size());
		Coordinates targetCoordinates = new Coordinates(grassElementTracker.get(targetElementForCastle).getX(),
													grassElementTracker.get(targetElementForCastle).getY());
		List<Figure> startingPositionForMyFigures = new ArrayList<>();
		startingPositionForMyFigures.add(new Figure(FigureType.MY_CASTLE));
		startingPositionForMyFigures.add(new Figure(FigureType.MY_AVATAR));
		elements.put(targetCoordinates, new MapElement(MapElementType.GRASS, startingPositionForMyFigures)); // CHECK for storage consistency
		Map result = new Map(elements);
		return result;
	}
	
	// helper method that checks whether a pair of x and y coordinates is on a list of fields that are planned to be mountains
	private static boolean plannedMountainField(int x, int y) {
		if(x == 1 && y == 1) return true;
		if(x == 1 && y == 2) return true;
		if(x == 3 && y == 1) return true;
		if(x == 3 && y == 2) return true;
		if(x == 6 && y == 1) return true;
		if(x == 6 && y == 2) return true;
		return false; // input coordinates are not on a planned mountain field
	}
}
