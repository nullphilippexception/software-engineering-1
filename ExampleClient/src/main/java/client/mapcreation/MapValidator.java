package client.mapcreation;

import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import client.model.Coordinates;
import client.model.Figure;
import client.model.FigureType;
import client.model.Map;
import client.model.MapElement;
import client.model.MapElementType;

// this class gets a generated map as input and then validates if it fits all criteria for a legal map
public class MapValidator {
	private static final int X_ELEMENTS = 8; // number of columns for a half map
	private static final int Y_ELEMENTS = 4; // number of rows for a half map
	
	// public method that can be called to easily validate map -> calls several private methods that validate different criteria
	public static boolean isMapValid(Map map) {
		return (checkBorderWaterAmount(map) && checkNumberOfElementTypes(map) && checkCastleField(map) && checkFieldAccessibility(map));
	}
	
	// helper method that validates wheter the max amount of water is not exceeded
	// 1 water on short ends, 3 water on long ends
	private static boolean checkBorderWaterAmount(Map map) {
		// check North border
		int waterCount = 0;
		for(int x = 0; x < X_ELEMENTS; x++) {
			if(map.getElementByCoordinates(new Coordinates(x,0)).getMapElementType().equals(MapElementType.WATER)) { waterCount++; }
		}
		if(waterCount > 3) { return false; }
		waterCount = 0;
		
		//check South border
		for(int x = 0; x < X_ELEMENTS; x++) {
			if(map.getElementByCoordinates(new Coordinates(x,3)).getMapElementType().equals(MapElementType.WATER)) { waterCount++; }
		}
		if(waterCount > 3) { return false; }
		waterCount = 0;
		
		// check East border
		for(int y = 0; y < Y_ELEMENTS; y++) {
			if(map.getElementByCoordinates(new Coordinates(7,y)).getMapElementType().equals(MapElementType.WATER)) { waterCount++; }
		}
		if(waterCount > 1) { return false; }
		waterCount = 0;
		
		// check West border
		for(int y = 0; y < Y_ELEMENTS; y++) {
			if(map.getElementByCoordinates(new Coordinates(0,y)).getMapElementType().equals(MapElementType.WATER)) { waterCount++; }
		}
		if(waterCount > 1) { return false; }
		
		// not yet returned:
		return true;
	}
	
	// helper method that checks if there are enough elements of all types on the map
	// at least 3 mountain, 4 water and 15 grass
	private static boolean checkNumberOfElementTypes(Map map) {
		HashMap<MapElementType, Integer> elementCount = new HashMap<>();
		elementCount.put(MapElementType.WATER, 0);
		elementCount.put(MapElementType.GRASS, 0);
		elementCount.put(MapElementType.MOUNTAIN, 0);
		
		for(int x = 0; x < X_ELEMENTS; x++) {
			for(int y = 0; y < Y_ELEMENTS; y++) {
				int currentElementCounter = elementCount.get(map.getElementByCoordinates(new Coordinates(x,y)).getMapElementType()) + 1;
				elementCount.put(map.getElementByCoordinates(new Coordinates(x,y)).getMapElementType(), currentElementCounter);
			}
		}
		
		// check wheter all keys of hashmap (the different elementtypes) have counters that fit criteria
		return ((elementCount.get(MapElementType.WATER) >= 4) && (elementCount.get(MapElementType.MOUNTAIN) >= 3) 
					&& (elementCount.get(MapElementType.GRASS) >= 15));
	}
	
	// helper method that checks whether this clients castle is placed somewhere on the map
	private static boolean checkCastleField(Map map) {
		for(int x = 0; x < X_ELEMENTS; x++) {
			for(int y = 0; y < Y_ELEMENTS; y++) {
				MapElement currentElement = map.getElementByCoordinates(new Coordinates(x,y));
				if(currentElement.getOnField().size() > 0) {
					for(Figure figure : currentElement.getOnField()) {
						if(figure.getFigureType().equals(FigureType.MY_CASTLE)) {return (currentElement.getMapElementType().equals(MapElementType.GRASS));}
					}	
				}
			}
		}
		// if we reach here, there is no castle placed -> that shouldn't happen
		Logger logger = LoggerFactory.getLogger(MapValidator.class);
		logger.warn("My castle is not placed on map");
		return false; 
	}
	
	// check whether every field is accessible from every other field -> therefore implicitly also checks for islands
	// Reasoning method: if every field is accessible from first top left field that is !Water, we can also connect
	// all the other fields with each other by walking over the top left element we checked for
	private static boolean checkFieldAccessibility(Map map) { 
		HashMap<Coordinates, Integer> referenceHashMap = new HashMap<>();
		Coordinates startingPointFloodfillAlgorithm = new Coordinates(0,0);
		for(int x = 0; x < X_ELEMENTS; x++) {
			for(int y = 0; y < Y_ELEMENTS; y++) {
				if(!map.getElementByCoordinates(new Coordinates(x,y)).getMapElementType().equals(MapElementType.WATER)) {
					startingPointFloodfillAlgorithm = new Coordinates(x,y); // make sure that starting point is not in water
					referenceHashMap.put(new Coordinates(x,y), 1); // 1 means that this element should be visitable
				}
			}
		}
		HashMap<Coordinates, Integer> floodfilledHashMap = new HashMap<>();
		floodfillAlgorithm(startingPointFloodfillAlgorithm, map, floodfilledHashMap);
		// compare if floodfilled map reached all elements that are marked as eligible in referenceHashMap
		return (referenceHashMap.equals(floodfilledHashMap)); 
	}
	
	// executes floodfill algorithm recursively
	// marks on HashMap hm all coordinates that can be reached from a single starting point
	// by recursivly walking in all directions from points
	private static void floodfillAlgorithm(Coordinates c, Map m, HashMap<Coordinates, Integer> hm) {
		if(!m.getElementByCoordinates(c).getMapElementType().equals(MapElementType.WATER)) { 
			hm.put(c, 1);
			if(c.getX() + 1 <= 7 && !hm.containsKey(new Coordinates(c.getX()+1,c.getY()))) { // check right
				floodfillAlgorithm(new Coordinates(c.getX()+1, c.getY()), m, hm); 
			}
			if(c.getX() - 1 >= 0 && !hm.containsKey(new Coordinates(c.getX()-1,c.getY()))) { // check left
				floodfillAlgorithm(new Coordinates(c.getX()-1, c.getY()), m, hm); 
			}
			if(c.getY() + 1 <= 3 && !hm.containsKey(new Coordinates(c.getX(),c.getY()+1))) { // check up
				floodfillAlgorithm(new Coordinates(c.getX(), c.getY()+1), m, hm); 
			}
			if(c.getY() - 1 >= 0 && !hm.containsKey(new Coordinates(c.getX(),c.getY()-1))) { // check down
				floodfillAlgorithm(new Coordinates(c.getX(), c.getY()-1), m, hm); 
			}
		}
	}
}
