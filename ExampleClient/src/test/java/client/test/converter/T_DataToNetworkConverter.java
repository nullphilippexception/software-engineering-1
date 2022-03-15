package client.test.converter;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import client.model.Coordinates;
import client.model.Figure;
import client.model.FigureType;
import client.model.Map;
import client.model.MapElement;
import client.model.MapElementType;
import client.model.MovementType;
import client.network.DataToNetworkConverter;

import org.junit.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import MessagesBase.EMove;
import MessagesBase.ETerrain;
import MessagesBase.HalfMap;
import MessagesBase.HalfMapNode;
import MessagesBase.PlayerMove;

class T_DataToNetworkConverter {
	
	// this test investigates whether the DataToNetworkConverter correctly converts a data class half of a map into a
	// network class HalfMap
	@Test
	void inputMap_mapToHalfMap_outputHalfMap() {
		HashMap<Coordinates, MapElement> inputMapElements = new HashMap<Coordinates, MapElement>();
		List<HalfMapNode> halfMapElements = new ArrayList<HalfMapNode>();
		String uniquePlayerIdentifier = "testIdentifier";
		
		// ARRANGE: create basic maps: an input map and a already finished HalfMap for reference
		boolean castlePlaced = false;
		for(int x = 0; x < 8; x++) {
			for(int y = 0; y < 4; y++) {
				MapElementType typeToPutMap;
				HalfMapNode halfMapNodeToPut;
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
					halfMapNodeToPut = new HalfMapNode(x,y,true,terrainToPut);
					castlePlaced = true;
				}
				else {
					halfMapNodeToPut = new HalfMapNode(x,y,terrainToPut);
				}
				MapElement currentElement = new MapElement(typeToPutMap, castleToPutMap);
				inputMapElements.put(new Coordinates(x,y),currentElement);
				halfMapElements.add(halfMapNodeToPut);
			}
		}
		
		// Act: Create the HalfMap for reference and execute the tested function and save result as the HalfMap to be tested
		Map inputMap = new Map(inputMapElements);
		HalfMap testedHalfMap = DataToNetworkConverter.mapToHalfMap(inputMap, uniquePlayerIdentifier);
		HalfMap referenceHalfMap = new HalfMap(uniquePlayerIdentifier, halfMapElements);
		
		// ASSERT: check wheter the expected HalfMap for reference and the computed HalfMap are the same
		assertEquals(referenceHalfMap, testedHalfMap);
	}
	
	// simple test that investigates whether data class MovementTypes are converted into network class EMoves properly
	@ParameterizedTest
	@CsvSource({"0","1","2","3"})
	void MovementType_convertMovementToPlayerMove_EMove(int index) {
		// ARRANGE: determine which MovementType is to be tested
		EMove expected = EMove.Right; // default
		MovementType input = MovementType.EAST; // default
		if(index == 0) {
			expected = EMove.Up;
			input = MovementType.NORTH;
		}
		if(index == 1) {
			expected = EMove.Right;
			input = MovementType.EAST;
		}
		if(index == 2) {
			expected = EMove.Down;
			input = MovementType.SOUTH;
		}
		if(index == 3) {
			expected = EMove.Left;
			input = MovementType.WEST;
		}
		
		// ACT: execute function to be tested
		PlayerMove testPlayerMove = DataToNetworkConverter.movementToPlayerMove(input, "testPlayerId");
		
		// ASSERT: check whether expected and actual result are the same
		assertEquals(expected, testPlayerMove.getMove());
	}
}

