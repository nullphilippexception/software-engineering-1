package client.controller;

import java.util.ArrayList;
import java.util.List;
import client.model.Coordinates;
import client.model.Figure;
import client.model.FigureType;
import client.model.Map;
import client.model.MapElementType;
import client.model.MovementType;

// one of the main classes of the project
// analyzes map and returns information like coordinates next to target etc.
public class MapAnalyzer {
	
	// method that marks fields that can be seen from the mountain the client is currently standing on
	public static List<Coordinates> getFieldsInMountainView(Map map) {
		int myPositionX = map.findThisFigure(FigureType.MY_AVATAR).getX();	
		int myPositionY = map.findThisFigure(FigureType.MY_AVATAR).getY();
		List<Coordinates> result = new ArrayList<>();
		
		// if the field this is checked for is not a mountain -> return empty list
		if(!map.getElementByCoordinates(map.findThisFigure(FigureType.MY_AVATAR)).getMapElementType().equals(MapElementType.MOUNTAIN)) {
			return result;
		}

		// add all coordinates to list that are directly adjacent to node avatar is standing on (including diagonal)
		// except for those who contain clients treasure or opponents castle
		for(int x = -1; x <= 1; x++) {
			for(int y = -1; y <= 1; y++) {
				int offsetX = x;
				int offsetY = y; 
				if(myPositionX + offsetX >= 0 && myPositionX + offsetX < map.getNumberOfColumns() && myPositionY + offsetY >= 0 
						&& myPositionY + offsetY < map.getNumberOfRows()
						&& map.getElementByCoordinates(new Coordinates(myPositionX+offsetX,myPositionY+offsetY))
							.getMapElementType().equals(MapElementType.GRASS) 
						&& !map.getElementByCoordinates(new Coordinates(myPositionX+offsetX,myPositionY+offsetY))
							.getOnField().contains(new Figure(FigureType.MY_TREASURE))
						&& !map.getElementByCoordinates(new Coordinates(myPositionX+offsetX,myPositionY+offsetY))
							.getOnField().contains(new Figure(FigureType.OPPONENT_CASTLE))) {
					result.add(new Coordinates(myPositionX+offsetX,myPositionY+offsetY));
				}
			}
		}
		return result;
	}
	
	// determines closest element to current player that is in computed area of opponents castle
	public static Coordinates closestOpponentCastleAreaElement(Map map, List<Coordinates> castleArea, MovementType mySite) {
		Coordinates avatarCoordinates = map.findThisFigure(FigureType.MY_AVATAR);
		
		// maybe treasure was found too soon? -> client wouldnt know correct castle area yet, therefore return random (opponent, eligible) element
		if(castleArea.isEmpty()) {
			return RandomOpponentMapElement.getRandomOpponentSideElement(map, mySite);
		}
		
		// find the one with closest distance
		double min = -10000; // make sure it is smaller than smallest possible distance
		int indexOfMin = 0;
		for(int i = 0; i < castleArea.size(); i++) {
			double distance = computeDistanceBetweenCoordinates(avatarCoordinates, castleArea.get(i));
			if(distance < min) {
				min = distance;
				indexOfMin = i;
			}
		}
		return castleArea.get(indexOfMin);
	}
	
	// helper method to determine whether a given Coordinate is on this clients site of map
	public static boolean isOnMySite(MovementType mySite, Coordinates coord) {
		// check four possibilities that coord is on mySite
		if(mySite.equals(MovementType.NORTH) && coord.getY() < 4) return true; 
		if(mySite.equals(MovementType.SOUTH) && coord.getY() > 3) return true; 
		if(mySite.equals(MovementType.WEST) && coord.getX() < 8) return true; 
		if(mySite.equals(MovementType.EAST) && coord.getX() > 7) return true; 
		return false; // if not returned yet -> coord is not on mySite
	}
	
	// helper method to compute distance between two coordinates
	// use double for possible later weighted distances dependent on MapElementTypes in between
	public static double computeDistanceBetweenCoordinates(Coordinates current, Coordinates target) {
		return (Math.abs(current.getX() - target.getX()) + Math.abs(current.getY() - target.getY()));
	}
}
