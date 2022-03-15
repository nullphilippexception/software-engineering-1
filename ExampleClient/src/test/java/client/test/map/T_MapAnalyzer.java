package client.test.map;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import client.controller.MapAnalyzer;
import client.model.Coordinates;
import client.model.Figure;
import client.model.FigureType;
import client.model.Map;
import client.model.MapElement;
import client.model.MapElementType;
import client.model.MovementType;

// NOTE for grader: this class contains a data driven test
class T_MapAnalyzer {
	
	// this test checks whether the isOnMySite function is able to correctly identfity whether a given point is
	// on the clients side or not for all possible client sides
	@ParameterizedTest
	@CsvSource({"0,9,0,1", "0,4,0,0","1,0,5,1","1,0,1,0","2,1,0,1", "2,10,0,0","3,0,1,1","3,0,5,0"})
	void coordinates_isOnMySite_boolean(int siteChooser, int xcoord, int ycoord, int expectedResult) {
		// ARRANGE: select site of map to test based on parameters
		MovementType inputSite = MovementType.EAST; //default, if siteChooser == 0
		boolean expectedAnswer = (expectedResult == 1 ? true : false);
		
		if(siteChooser == 1) inputSite = MovementType.SOUTH;
		if(siteChooser == 2) inputSite = MovementType.WEST;
		if(siteChooser == 3) inputSite = MovementType.NORTH;
		
		// ACT: execute tested function
		boolean onMySite = MapAnalyzer.isOnMySite(inputSite, new Coordinates(xcoord, ycoord));
		
		// ASSERT: check whether the expected answer given by input parameters and the computed answer are the same
		assertEquals(expectedAnswer, onMySite);
	}
	
	// this test checks whether the getFieldsinMountainView correctly identifies all fields that can be seen from a mountain
	// on which the clients avatar is currently standing
	// it pays specific attention to the fact that no out of bounds fields are supposed to be seen from the mountain
	// it also pays attention to the fact that no fields can be seen if avatar is not standing on mountain
	@ParameterizedTest
	@CsvSource({"8,8,4,3","8,8,5,5","4,16,4,3","4,16,0,0"})
	void mountainCoordinates_getFieldsinMountainView_coordinatesAroundMountain(int rows, int cols, int myAvatarX, int myAvatarY) {
		// ARRANGE: create a map as the basis for the test with a mountain on (4|3)
		HashMap<Coordinates, MapElement> testElements = new HashMap<>();
		for(int x = 0; x < cols; x++) {
			for(int y = 0; y < rows; y++) {
				MapElementType typeToPut = MapElementType.GRASS;
				List<Figure> figureToPut = new ArrayList<>();
				if(x == 4 && y == 3) {
					typeToPut = MapElementType.MOUNTAIN;
				}
				if(x == myAvatarX && y == myAvatarY) {
					figureToPut.add(new Figure(FigureType.MY_AVATAR));
				}
				MapElement mapElementToPut = new MapElement(typeToPut, figureToPut);
				testElements.put(new Coordinates(x,y), mapElementToPut);
			}
		}
		Map testMap = new Map(testElements);
		Set<Coordinates> expectedSet = new HashSet<>();
		
		// fill the expectedSet with the coordinates expected to be seen from the mountain, but only if
		// the avatar actually stands on the mountain
		if(myAvatarX == 4) {
			expectedSet.add(new Coordinates(3,2));
			expectedSet.add(new Coordinates(3,3));
			expectedSet.add(new Coordinates(4,2));
			expectedSet.add(new Coordinates(5,2));
			expectedSet.add(new Coordinates(5,3));
			
			if(rows == 8) {
				expectedSet.add(new Coordinates(3,4));
				expectedSet.add(new Coordinates(4,4));
				expectedSet.add(new Coordinates(5,4));
			}
		}
		
		// ACT: execute tested function and convert result into set to make comparsion with expected result easier
		List<Coordinates> testList = MapAnalyzer.getFieldsInMountainView(testMap);
		Set<Coordinates> testSet = new HashSet<>();
		for(Coordinates coord : testList) {
			testSet.add(coord);
		}
		
		// ASSERT: check whether the expected set and the result of the computation are the same
		assertEquals(expectedSet,testSet);
	}

}
