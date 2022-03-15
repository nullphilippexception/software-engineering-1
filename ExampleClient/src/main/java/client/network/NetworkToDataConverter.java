package client.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import MessagesBase.ETerrain;
import MessagesGameState.EFortState;
import MessagesGameState.EPlayerGameState;
import MessagesGameState.EPlayerPositionState;
import MessagesGameState.ETreasureState;
import MessagesGameState.FullMap;
import MessagesGameState.FullMapNode;
import MessagesGameState.GameState;
import MessagesGameState.PlayerState;
import client.model.Coordinates;
import client.model.Figure;
import client.model.FigureType;
import client.model.Map;
import client.model.MapElement;
import client.model.MapElementType;

// Converter class that converts classes received by the network into data classes used by the client
public class NetworkToDataConverter {
	
	// extracts the current PlayerState of the player with Id playerId from the gameState handed to the method
	public static Optional<PlayerState> getPlayerGameState(GameState gameState, String playerId) {
		Set<PlayerState> currentPlayers = gameState.getPlayers();
		for(PlayerState playerState : currentPlayers) {
			if(playerState.getUniquePlayerID().equals(playerId)) { 
				return Optional.of(playerState);
			}
		}
		// if method hasnt returned yet: demanded player is not in set of players
		Logger logger = LoggerFactory.getLogger(NetworkToDataConverter.class);
		logger.warn("My player is not registered in set of players: " + playerId);
		return Optional.empty(); 
	}
	
	// checks the gamestate whether it is the turn of the player with Id playerId
	// returns true if its this player's turn to send an action
	public static boolean checkIfMyTurn(GameState gameState, String playerId) {
		EPlayerGameState currentState = getPlayerGameState(gameState, playerId).get().getState();
		if(currentState.equals(null)) {
			Logger logger = LoggerFactory.getLogger(NetworkToDataConverter.class);
			logger.warn("Current GameState is null");
			return false; // can't be my turn because state is null
		}
		if(currentState.equals(EPlayerGameState.MustAct)) { return true; }
		else { return false; }
	}
	
	// extracts FullMap from gameState and converts into a data class map
	public static  Optional<Map> getMapFromGameState(GameState gameState) {
		FullMap fullMap = null;
		if(!gameState.getMap().isEmpty()) { fullMap = gameState.getMap().get(); }
		else {
			Logger logger = LoggerFactory.getLogger(NetworkToDataConverter.class);
			logger.warn("The map contained in the GameState is empty");
			return Optional.empty();
		}
		HashMap<Coordinates, MapElement> mapElements = new HashMap<>(); // these will be the elements of the Map
		
		// convert all fullMapNodes into MapElements with corresponding Coordinates using a helper method
		for(FullMapNode fullMapNode : fullMap.getMapNodes()) {
			mapElements.put(new Coordinates(fullMapNode.getX(), fullMapNode.getY()), convertFullMapNodeToMapElement(fullMapNode));
		}
		Map result = new Map(mapElements);
		return Optional.of(result);
	}
	
	// helper method that summarizes other helper methods into converting a FullMapNode into a equivalent MapElement
	private static MapElement convertFullMapNodeToMapElement(FullMapNode fullMapNode) {
		return new MapElement(convertETerrainToMapElementType(fullMapNode.getTerrain()).get(), getFiguresOnNode(fullMapNode));
	}
	
	// helper method that converts ETerrain (network enum) into MapElementType (local enum)
	private static Optional<MapElementType> convertETerrainToMapElementType(ETerrain eterrain) {
		if(eterrain.equals(ETerrain.Water)) return Optional.of(MapElementType.WATER);
		if(eterrain.equals(ETerrain.Grass)) return Optional.of(MapElementType.GRASS);
		if(eterrain.equals(ETerrain.Mountain)) return Optional.of(MapElementType.MOUNTAIN);
		else { // input was an unknown ETerrain type
			Logger logger = LoggerFactory.getLogger(NetworkToDataConverter.class);
			logger.warn("Tried to convert an unknown ETerrain: " + eterrain);
			return Optional.empty(); 
		}
	}
	
	// helper method that converts the game figures found on a FullMapNode into Figures used by the data classes
	private static List<Figure> getFiguresOnNode(FullMapNode fullMapNode) {
		List<Figure> result = new ArrayList<>(); // list of figures on this node that will ultimately be returned
		
		// check for my treasure and both players forts
		if(fullMapNode.getTreasureState().equals(ETreasureState.MyTreasureIsPresent)) { result.add(new Figure(FigureType.MY_TREASURE)); }
		if(fullMapNode.getFortState().equals(EFortState.MyFortPresent)) { result.add(new Figure(FigureType.MY_CASTLE)); }
		if(fullMapNode.getFortState().equals(EFortState.EnemyFortPresent)) { result.add(new Figure(FigureType.OPPONENT_CASTLE)); }
		
		// check for player avatars
		if(fullMapNode.getPlayerPositionState().equals(EPlayerPositionState.BothPlayerPosition) ||
				fullMapNode.getPlayerPositionState().equals(EPlayerPositionState.MyPlayerPosition)) {
			result.add(new Figure(FigureType.MY_AVATAR));
		}
		if(fullMapNode.getPlayerPositionState().equals(EPlayerPositionState.BothPlayerPosition) ||
				fullMapNode.getPlayerPositionState().equals(EPlayerPositionState.EnemyPlayerPosition)) {
			result.add(new Figure(FigureType.OPPONENT_AVATAR));
		}
		return result;
	}
}
