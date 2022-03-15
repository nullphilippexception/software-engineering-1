package client.network;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import MessagesBase.EMove;
import MessagesBase.ETerrain;
import MessagesBase.HalfMap;
import MessagesBase.HalfMapNode;
import MessagesBase.PlayerMove;
import client.model.Coordinates;
import client.model.FigureType;
import client.model.Map;
import client.model.MapElementType;
import client.model.MovementType;

// Converter class that converts my data classes into classes that are accepted by the server
public class DataToNetworkConverter {
	
	// converts a MovementType (data class) to a PlayerMove (network class)
	public static PlayerMove movementToPlayerMove(MovementType movement, String playerId) {
		return PlayerMove.of(playerId, convertMovementTypeToEMove(movement).get());
	}
	
	// converts one half of a (data) map into a (network class) HalfMap
	// HalfMap is also handed the id of this client to identify who sent this half of the map
	public static HalfMap mapToHalfMap(Map map, String playerId) {
		List<HalfMapNode> listOfNodes = new ArrayList<>();
		for(int x = 0; x < map.getNumberOfColumns(); x++) {
			for(int y = 0; y < map.getNumberOfRows(); y++) {
				listOfNodes.add(mapElementToHalfMapNode(map,new Coordinates(x,y)));
			}
		}
		return new HalfMap(playerId, listOfNodes);
	}
	
	// helper method that converts a MapElement of a map at a given Coordinate into a HalfMapNode accepted by the server
	private static HalfMapNode mapElementToHalfMapNode(Map map, Coordinates coord) {
		boolean fortPresent = (map.findThisFigure(FigureType.MY_CASTLE).equals(coord));
		return new HalfMapNode(coord.getX(), coord.getY(), fortPresent , 
				convertMapElementTypeToETerrain(map.getElementByCoordinates(coord).getMapElementType()).get());
	}
	
	// helper method that converts a MapElementType (local enum) into a ETerrain (network enum)
	private static Optional<ETerrain> convertMapElementTypeToETerrain(MapElementType mapElementType) {
		if(mapElementType.equals(MapElementType.WATER)) return Optional.of(ETerrain.Water);
		if(mapElementType.equals(MapElementType.GRASS)) return Optional.of(ETerrain.Grass);
		if(mapElementType.equals(MapElementType.MOUNTAIN)) return Optional.of(ETerrain.Mountain);
		else {
			Logger logger = LoggerFactory.getLogger(DataToNetworkConverter.class);
			logger.warn("Tried to convert an unknown mapElementType: " + mapElementType);
			return Optional.empty();
		}
	}
	
	// helper method that converts a MovementType (local enum) into a EMove (network enum)
	private static Optional<EMove> convertMovementTypeToEMove(MovementType movementType) {
		if(movementType.equals(MovementType.NORTH)) return Optional.of(EMove.Up);
		else if(movementType.equals(MovementType.EAST)) return Optional.of(EMove.Right);
		else if(movementType.equals(MovementType.SOUTH)) return Optional.of(EMove.Down);
		else if(movementType.equals(MovementType.WEST)) return Optional.of(EMove.Left);
		else {
			Logger logger = LoggerFactory.getLogger(DataToNetworkConverter.class);
			logger.warn("Tried to convert an unknown movementType: " + movementType);
			return Optional.empty();
		}
	}
}
