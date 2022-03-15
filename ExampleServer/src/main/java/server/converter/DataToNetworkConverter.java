package server.converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import MessagesBase.EMove;
import MessagesBase.ETerrain;
import MessagesBase.PlayerMove;
import MessagesBase.UniquePlayerIdentifier;
import MessagesGameState.EFortState;
import MessagesGameState.EPlayerPositionState;
import MessagesGameState.ETreasureState;
import MessagesGameState.FullMap;
import MessagesGameState.FullMapNode;
import MessagesGameState.PlayerState;
import server.game.EAchievements;
import server.map.*;

// Converter class that converts my data classes into classes that are accepted by the network to send to the client
public class DataToNetworkConverter {
	
	// converts a data class map into a network class fullmap
	public static FullMap mapToFullMap(Map map, UniquePlayerIdentifier player, HashMap<String, List<EAchievements>> achievements, int moveCount) {
		List<FullMapNode> listOfNodes = new ArrayList<>();
		Random random = new Random();
		int elementForRandomOpponentPosition = random.nextInt(64);
		int iterationCounter = 0;
		int NO_OF_MOVES_TO_HIDE_OPP = 12; // number of the moves where the opponent is supposed to be hidden
		
		// create fullmap
		for(int x = 0; x < map.getNumberOfColumns(); x++) {
			for(int y = 0; y < map.getNumberOfRows(); y++) {
				if(iterationCounter == elementForRandomOpponentPosition && moveCount < NO_OF_MOVES_TO_HIDE_OPP) { // hides true opp position, fakes different one
					listOfNodes.add(mapElementToFullMapNode(map,new Coordinates(x,y), player, achievements, false, true));
				}
				else if(iterationCounter != elementForRandomOpponentPosition && moveCount < NO_OF_MOVES_TO_HIDE_OPP) { // hides true opp position, doesnt fake one
					listOfNodes.add(mapElementToFullMapNode(map,new Coordinates(x,y), player, achievements, false, false));
				}
				else { // shows true opponent position
					listOfNodes.add(mapElementToFullMapNode(map,new Coordinates(x,y), player, achievements, true, false));
				}
				iterationCounter++;
			}
		}
		return new FullMap(listOfNodes);
	}
	
	// helper method that converts a MapElement of a map at a given Coordinate into a FullMapNode accepted by the network
	private static FullMapNode mapElementToFullMapNode(Map map, Coordinates coord, UniquePlayerIdentifier player, 
														HashMap<String, List<EAchievements>> achievements, boolean showOpponent, boolean fakeOpponent) {
		MapElement currentElement = map.getElementByCoordinates(coord);
		List<Figure> currentFigures = currentElement.getOnField();
		EFortState fortState = EFortState.NoOrUnknownFortState;
		EPlayerPositionState playerPositionState = EPlayerPositionState.NoPlayerPresent;
		ETreasureState treasureState = ETreasureState.NoOrUnknownTreasureState;
		ETerrain terrainState = convertMapElementTypeToETerrain(currentElement.getMapElementType()).get();
		
		// iterate through all figures on this node and take appropriate action
		for(Figure fig : currentFigures) {
			 // check whether it is one of gamestate requesting player's figures
			if(fig.getFigureOwnerPlayerId().equals(player.getUniquePlayerID())) {
				if(fig.getFigureType().equals(FigureType.AVATAR)) {
					// check if other players avatar is already here 
					if(playerPositionState.equals(EPlayerPositionState.EnemyPlayerPosition)) playerPositionState = EPlayerPositionState.BothPlayerPosition;
					else playerPositionState = EPlayerPositionState.MyPlayerPosition;
				} // Fort
				else if(fig.getFigureType().equals(FigureType.CASTLE)) {
					fortState = EFortState.MyFortPresent;
				}
				else { // Treasure
					if(achievements.get(player.getUniquePlayerID()).contains(EAchievements.DISCOVER_TREASURE)
					   && !achievements.get(player.getUniquePlayerID()).contains(EAchievements.COLLECT_TREASURE)) {
						treasureState = ETreasureState.MyTreasureIsPresent;
					}
				}
			}
			else { // it is one of opponents figures
				if(fig.getFigureType().equals(FigureType.AVATAR) && showOpponent) {
					// check if other players avatar is already here 
					if(playerPositionState.equals(EPlayerPositionState.MyPlayerPosition)) playerPositionState = EPlayerPositionState.BothPlayerPosition;
					else playerPositionState = EPlayerPositionState.EnemyPlayerPosition;
				} // Fort
				else if(fig.getFigureType().equals(FigureType.CASTLE)) {
					if(achievements.get(player.getUniquePlayerID()).contains(EAchievements.FIND_CASTLE)) {
						fortState = EFortState.EnemyFortPresent;
					}
				}
				else { // Treasure
					// Player can never see opponents treasure so this case doesn't matter
				}
			}
		}
		
		// check if game is still within number of rounds where opponents position is supposed to be hidden
		// if yes, hide opponents position
		if(fakeOpponent) {
			if(playerPositionState.equals(EPlayerPositionState.MyPlayerPosition)) playerPositionState = EPlayerPositionState.BothPlayerPosition;
			else playerPositionState = EPlayerPositionState.EnemyPlayerPosition;
		}
		
		return new FullMapNode(terrainState, playerPositionState, treasureState, fortState, coord.getX(), coord.getY());
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
	
	// converts a MovementType (data class) to a PlayerMove (network class)
	private static PlayerMove movementToPlayerMove(MovementType movement, String playerId) {
		return PlayerMove.of(playerId, convertMovementTypeToEMove(movement).get());
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
