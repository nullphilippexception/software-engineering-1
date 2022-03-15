package server.halfmapvalidator;

import java.util.HashMap;

import MessagesBase.ETerrain;
import MessagesBase.HalfMap;
import MessagesBase.HalfMapNode;
import server.businessrules.IMapRule;
import server.exceptions.InvalidMapException;
import server.map.Coordinates;

// this validator checks whether the given halfmap fulfilles the rules of maximum water at its borders
// the map is supposed to have at most 1 water element on short ends and at most 3 water elements on long ends
public class BorderWaterValidator implements IMapRule {
	// main method that runs the test
	public void checkRule(HalfMap map) throws InvalidMapException {
		HashMap<Coordinates, HalfMapNode> sortedHalfMap = new HashMap<>();
		
		// sort the map by its coordinates to make checking the borders easier
		for(HalfMapNode node : map.getMapNodes()) {
			Coordinates nodeCoordinates = new Coordinates(node.getX(), node.getY());
			sortedHalfMap.put(nodeCoordinates, node);
		}
		
		// check borders through helper method
		if(countWaterLine(sortedHalfMap, 0, 8, 0, 1) > 3 // Top border
				|| countWaterLine(sortedHalfMap,0,1,0,4) > 1 // Left border
				|| countWaterLine(sortedHalfMap, 7, 1, 0, 4) > 1 // Right border
				|| countWaterLine(sortedHalfMap, 0, 8, 3, 1) > 3) { // Bottom border
			throw new InvalidMapException("BorderWaterValidator", "Too many water fields on a border");
		}
	}
	
	// helper method that checks a given border (specified through handed coordinates) on a given map
	// for the amount of water
	private int countWaterLine(HashMap<Coordinates, HalfMapNode> sortedMap, int xStart, int xFields, int yStart, int yFields) {
		int result = 0;
		for(int x = xStart; x < xStart + xFields; x++) {
			for(int y = yStart; y < yStart + yFields; y++) {
				if(sortedMap.get(new Coordinates(x,y)).getTerrain() == ETerrain.Water) { result++; }
			}
		}
		return result;
	}
}
