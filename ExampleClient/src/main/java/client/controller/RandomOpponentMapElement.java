package client.controller;


import client.model.Coordinates;
import client.model.Map;
import client.model.MapElementType;
import client.model.MovementType;

// helper class that finds a non-water element on the far side of the opponents half of the map
// use case: if client finds its treasure within its first six moves the opponents location is yet unknown
// therefore opponents castle position can not be derived
// therefore this class is employed as a helper to determine a useful temporary target
public class RandomOpponentMapElement {
	
	// helper method that finds element on opponent side as specified above
	public static Coordinates getRandomOpponentSideElement(Map map, MovementType mySide) {
		for(int x = 0; x < map.getNumberOfColumns(); x++) {
			for(int y = 0; y < map.getNumberOfRows(); y++) {
				// NORTH
				if(mySide.equals(MovementType.NORTH) && y == map.getNumberOfRows()-1) { // get as far away as possible
					if(!map.getElementByCoordinates(new Coordinates(x,y)).getMapElementType().equals(MapElementType.WATER)) {
						return new Coordinates(x,y);
					}
				}
				// SOUTH
				if(mySide.equals(MovementType.SOUTH) && y == 0) { // get as far away as possible
					if(!map.getElementByCoordinates(new Coordinates(x,y)).getMapElementType().equals(MapElementType.WATER)) {
						return new Coordinates(x,y);
					}
				}
				// EAST
				if(mySide.equals(MovementType.EAST) && x == 0) { // get as far away as possible
					if(!map.getElementByCoordinates(new Coordinates(x,y)).getMapElementType().equals(MapElementType.WATER)) {
						return new Coordinates(x,y);
					}
				}
				// WEST
				if(mySide.equals(MovementType.WEST) && x == map.getNumberOfColumns()-1) { // get as far away as possible
					if(!map.getElementByCoordinates(new Coordinates(x,y)).getMapElementType().equals(MapElementType.WATER)) {
						return new Coordinates(x,y);
					}
				}
			}
		}
		return new Coordinates(-1,-1); // default return for bad coordinates
	}
}
