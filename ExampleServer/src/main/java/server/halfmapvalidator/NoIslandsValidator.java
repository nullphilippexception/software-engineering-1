package server.halfmapvalidator;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import MessagesBase.ETerrain;
import MessagesBase.HalfMap;
import MessagesBase.HalfMapNode;
import server.map.*;
import server.businessrules.IMapRule;
import server.exceptions.InvalidMapException;

// this validator uses the floodfill algorithm to check whether the given halfmap contains any islands
// an island occurs if a part of a map is cut off from the rest through a line of water
public class NoIslandsValidator implements IMapRule{
	// main method that runs the test
	public void checkRule(HalfMap halfMap) throws InvalidMapException {
		HashMap<Coordinates, Integer> referenceHashMap = new HashMap<>();
		Coordinates startingPointFloodfillAlgorithm = new Coordinates(0,0);
		for(int x = 0; x < 8; x++) {
			for(int y = 0; y < 4; y++) {
				if(!this.getHalfMapElementByCoordinates(halfMap, new Coordinates(x,y)).getTerrain().equals(ETerrain.Water)) {
					startingPointFloodfillAlgorithm = new Coordinates(x,y); // make sure that starting point is not in water
					referenceHashMap.put(new Coordinates(x,y), 1); // 1 means that this element should be visitable
				}
			}
		}
		HashMap<Coordinates, Integer> floodfilledHashMap = new HashMap<>();
		floodfillAlgorithm(startingPointFloodfillAlgorithm, halfMap, floodfilledHashMap);
		// compare if floodfilled map reached all elements that are marked as eligible in referenceHashMap
		if (!referenceHashMap.equals(floodfilledHashMap)) {
			throw new InvalidMapException("NoIslandsValidator", "There are islands on this map");
		}
	}
	
	// implementation of floodfill algorithm
	private void floodfillAlgorithm(Coordinates coord, HalfMap halfMap, HashMap<Coordinates, Integer> checkMap) {
		if(!this.getHalfMapElementByCoordinates(halfMap, coord).getTerrain().equals(ETerrain.Water)) { 
			checkMap.put(coord, 1);
			if(coord.getX() + 1 <= 7 && !checkMap.containsKey(new Coordinates(coord.getX()+1,coord.getY()))) { // check right
				floodfillAlgorithm(new Coordinates(coord.getX()+1, coord.getY()), halfMap, checkMap); 
			}
			if(coord.getX() - 1 >= 0 && !checkMap.containsKey(new Coordinates(coord.getX()-1,coord.getY()))) { // check left
				floodfillAlgorithm(new Coordinates(coord.getX()-1, coord.getY()), halfMap, checkMap); 
			}
			if(coord.getY() + 1 <= 3 && !checkMap.containsKey(new Coordinates(coord.getX(),coord.getY()+1))) { // check up
				floodfillAlgorithm(new Coordinates(coord.getX(), coord.getY()+1), halfMap, checkMap); 
			}
			if(coord.getY() - 1 >= 0 && !checkMap.containsKey(new Coordinates(coord.getX(),coord.getY()-1))) { // check down
				floodfillAlgorithm(new Coordinates(coord.getX(), coord.getY()-1), halfMap, checkMap); 
			}
		}
	}
	
	// helper method to find a halfmapelement by its coordinates
	private HalfMapNode getHalfMapElementByCoordinates(HalfMap halfMap, Coordinates targetCoord) {
		for(HalfMapNode halfMapNode : halfMap.getMapNodes()) {
			Coordinates currentCoord = new Coordinates(halfMapNode.getX(), halfMapNode.getY());
			if(currentCoord.equals(targetCoord)) return halfMapNode;
		}
		Logger.getAnonymousLogger().log(Level.WARNING, "Asked for non existing HalfMapNode");
		return new HalfMapNode(); // BAD EXCEPTION HANDLING!
	}

}
