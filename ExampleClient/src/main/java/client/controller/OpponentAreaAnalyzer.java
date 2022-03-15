package client.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import client.model.Coordinates;
import client.model.FigureType;
import client.model.Map;
import client.model.MapElementType;
import client.model.MovementType;

// helper class that heuristically determines possible locatiosn for opponents castle as specified below
public class OpponentAreaAnalyzer {
	
	// based  on the current position of the opponents avatar, the possible positions of the opponents castle are computed
	// simplified method, obstacles like mountain/water are not taken into account in computing length of way
	// water fields are however removed from return list, as this list only includes desirable fields
	// method can be called independet on whether this client or opponent moves first, as it is called once this client makes six moves
	public static List<Coordinates> computeOpponentCastleArea(Map map, MovementType mySiteOfMap) {
		int opponentX = map.findThisFigure(FigureType.OPPONENT_AVATAR).getX();
		int opponentY = map.findThisFigure(FigureType.OPPONENT_AVATAR).getY();
		List<Coordinates> result = new ArrayList<>();
		
		// check and add fields that immediately surround opponent
		for(int x = -2; x <= 2; x++) {
			for(int y = -2; y <= 2; y++) {
				Coordinates currentCoord = new Coordinates(opponentX + x,opponentY + y);
				if(map.isCoordinatesValid(currentCoord) 
						&& !map.getElementByCoordinates(currentCoord).getMapElementType().equals(MapElementType.WATER) 
						&& !MapAnalyzer.isOnMySite(mySiteOfMap, currentCoord) 
						&& (2 != Math.abs(x) || 2 != Math.abs(y) )) {  // because the 2,2 distanced field cant be reached in that time
					result.add(currentCoord); 
				}
			}
		}
		
		// check and add horizontally and vertically three element distances -> can be reached if all grass
		Coordinates west = new Coordinates(opponentX - 3, opponentY);
		Coordinates east = new Coordinates(opponentX + 3, opponentY);
		Coordinates north = new Coordinates(opponentX, opponentY - 3);
		Coordinates south = new Coordinates(opponentX, opponentY + 3);
		if(map.isCoordinatesValid(west) && !map.getElementByCoordinates(west).getMapElementType().equals(MapElementType.WATER) 
				&& !MapAnalyzer.isOnMySite(mySiteOfMap, west)) { 
			result.add(west); 
		}
		if(map.isCoordinatesValid(east) && !map.getElementByCoordinates(east).getMapElementType().equals(MapElementType.WATER) 
				&& !MapAnalyzer.isOnMySite(mySiteOfMap, east)) { 
			result.add(east); 
		}
		if(map.isCoordinatesValid(north) && !map.getElementByCoordinates(north).getMapElementType().equals(MapElementType.WATER) 
				&& !MapAnalyzer.isOnMySite(mySiteOfMap, north)) { 
			result.add(north); 
		}
		if(map.isCoordinatesValid(south) && !map.getElementByCoordinates(south).getMapElementType().equals(MapElementType.WATER) 
				&& !MapAnalyzer.isOnMySite(mySiteOfMap, south)) { 
			result.add(south); 
		}
		
		// return list of desirable fields
		return result;         
	}
}
