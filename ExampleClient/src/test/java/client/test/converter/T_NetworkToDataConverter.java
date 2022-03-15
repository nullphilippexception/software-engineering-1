package client.test.converter;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;

import client.model.Coordinates;
import client.model.Figure;
import client.model.FigureType;
import client.model.Map;
import client.model.MapElement;
import client.model.MapElementType;
import client.network.*;
import MessagesGameState.EFortState;
import MessagesGameState.EPlayerGameState;
import MessagesGameState.EPlayerPositionState;
import MessagesGameState.ETreasureState;
import MessagesGameState.FullMap;
import MessagesGameState.FullMapNode;
import MessagesGameState.GameState;
import MessagesGameState.PlayerState;
import MessagesBase.ETerrain;
import MessagesBase.UniquePlayerIdentifier;

// NOTE for grader: two of the tests in this class use Mockito and one is a data driven test
class T_NetworkToDataConverter {

	// this test investigates the getPlayerGameState function
	// it tests whether all four different EPlayerGameStates can be correctly extracted from an exemplary gameState
	@ParameterizedTest
	@CsvSource({"0","1","2","3"})
	void gameState_getPlayerGameState_PlayerState(int stateIndex) {
		// ARRANGE: create a simplified gameState and playerState and check which EPlayerGameState should be checked in this iteration of test
		String testPlayerId = "TestPlayerId";
		List<EPlayerGameState> playerStates = new ArrayList<>(); 
		playerStates.add(EPlayerGameState.MustAct);
		playerStates.add(EPlayerGameState.MustWait);
		playerStates.add(EPlayerGameState.Won);
		playerStates.add(EPlayerGameState.Lost);
		EPlayerGameState testPlayerGameState = playerStates.get(stateIndex);
		String testGameId = "TestGameId";
		UniquePlayerIdentifier mockedPI = Mockito.mock(UniquePlayerIdentifier.class);
		Mockito.when(mockedPI.getUniquePlayerID()).thenReturn(testPlayerId);
		// cant mock gameState and playerState because they are final
		PlayerState testPlayerState = new PlayerState("firstname", "lastname", "studentid", testPlayerGameState, mockedPI, false); 
		
		List<PlayerState> testPlayerList = new ArrayList<>();
		testPlayerList.add(testPlayerState);
		
		GameState gameState = new GameState(testPlayerList, testGameId);
		
		// ACT: use NetworkToDataConverter to compute PlayerState from created GameState
		PlayerState computedPlayerState = NetworkToDataConverter.getPlayerGameState(gameState, testPlayerId).get();
		
		// ASSERT: check whether expected and computed PlayerState are the same
		assertEquals(testPlayerState, computedPlayerState);
	}
	
	// this test investigates whether the NetworkToDataConverter correctly extracts the EPlayerGameState from a given GameState and
	// is able to identify when this client is allowed to act
	// if this client is asked to act, the tested function checkIfMyTurn should return true and false otherwise
	@ParameterizedTest
	@CsvSource({"0,1","1,0","2,0","3,0"})
	void booleanMyTurn_CheckIfMyTurn_booleanResult(int stateIndex, int myTurn) {
		String testPlayerId = "TestPlayerId";
		boolean expectedResult = (myTurn == 1 ? true : false);
		
		// ARRANGE: create PlayerState and GameState
		List<EPlayerGameState> playerStates = new ArrayList<>(); // STUPID solution
		playerStates.add(EPlayerGameState.MustAct);
		playerStates.add(EPlayerGameState.MustWait);
		playerStates.add(EPlayerGameState.Won);
		playerStates.add(EPlayerGameState.Lost);
		EPlayerGameState testPlayerGameState = playerStates.get(stateIndex);
		String testGameId = "TestGameId";
		UniquePlayerIdentifier mockedPI = Mockito.mock(UniquePlayerIdentifier.class);
		Mockito.when(mockedPI.getUniquePlayerID()).thenReturn(testPlayerId);
		PlayerState testPlayerState = new PlayerState("firstname", "lastname", "studentid", testPlayerGameState, mockedPI, false); // cant mock because final class
		
		List<PlayerState> testPlayerList = new ArrayList<>();
		testPlayerList.add(testPlayerState);
		
		GameState gameState = new GameState(testPlayerList, testGameId);
		
		// ACT: execute the tested function
		boolean testedResult = NetworkToDataConverter.checkIfMyTurn(gameState, testPlayerId);
		
		// ASSERT: compare the expected result, provided by the input parameters and the tested result
		assertEquals(expectedResult, testedResult);
	}
	
	// this test investigates whether the NetworkToDataConverter is able to correctly extract a data class map from a given GameState
	// to do so, the method getMapFromGameState is supposed to extract a network class FullMap from the game state and then
	// convert it into a data class map
	@Test
	void gameState_getMapFromGameState_Map() {
		HashMap<Coordinates, MapElement> inputMapElements = new HashMap<Coordinates, MapElement>();
		List<FullMapNode> fullMapElements = new ArrayList<FullMapNode>();
		
		// ARRANGE: create basic maps 
		boolean castlePlaced = false;
		for(int x = 0; x < 8; x++) {
			for(int y = 0; y < 8; y++) {
				MapElementType typeToPutMap;
				FullMapNode fullMapNodeToPut;
				ETerrain terrainToPut;
				List<Figure> castleToPutMap = new ArrayList<>();
				if(x < 4) {
					typeToPutMap = MapElementType.GRASS;
					terrainToPut = ETerrain.Grass;
				}
				else if(x >= 4 && x < 6) {
					typeToPutMap = MapElementType.MOUNTAIN;
					terrainToPut = ETerrain.Mountain;
				}
				else {
					typeToPutMap = MapElementType.WATER;
					terrainToPut = ETerrain.Water;
				}
				if(!castlePlaced) {
					castleToPutMap.add(new Figure(FigureType.MY_CASTLE));
					fullMapNodeToPut = new FullMapNode(terrainToPut,EPlayerPositionState.NoPlayerPresent,
							ETreasureState.NoOrUnknownTreasureState,EFortState.MyFortPresent,x,y);
					castlePlaced = true;
				}
				else {
					fullMapNodeToPut = new FullMapNode(terrainToPut,EPlayerPositionState.NoPlayerPresent,
							ETreasureState.NoOrUnknownTreasureState,EFortState.NoOrUnknownFortState,x,y);
				}
				MapElement currentElement = new MapElement(typeToPutMap, castleToPutMap);
				inputMapElements.put(new Coordinates(x,y),currentElement);
				fullMapElements.add(fullMapNodeToPut);
			}
		}
		
		FullMap inputFullMap = new FullMap(fullMapElements);
		Optional<FullMap> opFullMap = Optional.of(inputFullMap);
		
		// put created FullMap into gameState and save data class Map as reference
		GameState testState = new GameState(opFullMap, null, "gameStateId");
		Map referenceMap = new Map(inputMapElements);
		
		// ACT: exectute the method to extract and convert he map
		Map testedMap = NetworkToDataConverter.getMapFromGameState(testState).get();
		
		// ASSERT: check whether expected and computed result are the same
		assertEquals(referenceMap, testedMap);
	}

}
