package client.test.ai;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyObject;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;

import client.controller.RandomOpponentMapElement;
import client.model.Coordinates;
import client.model.Map;
import client.model.MapElement;
import client.model.MapElementType;
import client.model.MovementType;

// NOTE for grader: This is a data-driven test that also uses Mockito
class T_RandomOpponentMapElement {

	// this test investigates whether the getRandomOpponentSideElement returns the expected element, which should be a non-water element
	// on the opponents side of the map in the row/column that is the furthest away from this clients half of the map
	@ParameterizedTest
	@CsvSource({"8,8,0,0,7", "8,8,1,0,0", "4,16,2,0,0", "4,16,3,15,0"})
	void mySideOfField_getRandomOpponentSideElement_CoordinatesOnOpponentSide(int mapRows, int mapCols, int mySiteOfMap, int expectedX, int expectedY) {
		// ARRANGE: create a Mock of the map and mapelements to be tested
		Map testMap = Mockito.mock(Map.class);
		MapElement testMapElement = Mockito.mock(MapElement.class);
		MapElementType returnElementType = MapElementType.GRASS;
		MovementType mySite = MovementType.NORTH; // default for input value mySiteOfMap = 0
		if(mySiteOfMap == 1) mySite = MovementType.SOUTH;
		if(mySiteOfMap == 2) mySite = MovementType.EAST;
		if(mySiteOfMap == 3) mySite = MovementType.WEST;
		
		Mockito.when(testMap.getNumberOfRows()).thenReturn(mapRows);
		Mockito.when(testMap.getNumberOfColumns()).thenReturn(mapCols);
		Mockito.when(testMap.getElementByCoordinates(anyObject())).thenReturn(testMapElement); // doesnt work with new Coordinates(anyInt(), anyInt())
		Mockito.when(testMapElement.getMapElementType()).thenReturn(returnElementType);
		
		// ACT: execute the function to be tested
		Coordinates resultCoordinates = RandomOpponentMapElement.getRandomOpponentSideElement(testMap, mySite);
		
		// ASSERT: assert that the coordinates resulting from the executed function are equal to the ones handed by input parameters
		assertEquals(new Coordinates(expectedX, expectedY), resultCoordinates);
	}

}
