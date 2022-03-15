package client.test.ai;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import client.controller.OpponentAreaAnalyzer;
import client.model.*;

class T_OpponentAreaAnalyzer {

	// this test investigates the computeOpponentCastleArea method
	// its inputs represent the direction of the map on which this clients castle is placed, the rows and columns of the map to be tested
	// and the location of the opponents avatar (named castleX and castleY to ensure that these coordinates are base for computation)
	// the test then computes a simplified map and checks whether the computeOpponentCastleArea method finds the correct possible
	// area of the opponents castle
	// it is ensured that some elements next to opponents avatar are water, because they are not supposed to be in the castleArea result
	@ParameterizedTest
	@CsvSource({"0,8,8,1,6","1,8,8,1,2","2,4,16,1,2","3,4,16,14,1"})
	void opponentLocation_computeOpponentCastleArea_opponentCastleArea(int testedDirection, int rows, int cols, int castleX, int castleY) {
		HashMap<Coordinates, MapElement> testElements = new HashMap<>();
		MovementType testMySite = MovementType.NORTH; // default for testedDriection == 0
		if(testedDirection == 1) testMySite = MovementType.SOUTH;
		if(testedDirection == 2) testMySite = MovementType.EAST;
		if(testedDirection == 3) testMySite = MovementType.WEST;
		
		// ARRANGE: create the simplified map
		for(int x = 0; x < cols; x++) {
			for(int y = 0; y < rows; y++) {
				MapElementType typeToPut = MapElementType.GRASS;
				List<Figure> figuresToPut = new ArrayList<>();
				
				if(x == castleX && y == castleY) figuresToPut.add(new Figure(FigureType.OPPONENT_AVATAR));
				
				if(x == castleX +1) typeToPut = MapElementType.WATER; // place water to check whether it is recognized by method
				MapElement putElement = new MapElement(typeToPut, figuresToPut);
				
				testElements.put(new Coordinates(x,y), putElement);
			}
		}
		Map testMap = new Map(testElements);
 		
		// create a list of the coordinates that are expected to be in the castleArea, depending on the given direction
		List<Coordinates> expectedCoord = new ArrayList<>();
		if(testMySite == MovementType.SOUTH) {
			int[] xFields = {0,1,0,1,0,0,1,3,3,3,4,1};
			int[] yFields = {0,0,1,1,2,3,3,1,2,3,2,2};
			for(int i = 0; i < xFields.length; i++) { // ABSTRACTION!
				expectedCoord.add(new Coordinates(xFields[i], yFields[i]));
			}
		}
		if(testMySite == MovementType.NORTH) {
			int[] xFields = {0,1,0,1,0,0,1,3,3,3,4,1};
			int[] yFields = {4,4,5,5,6,7,7,5,6,7,6,6};
			for(int i = 0; i < xFields.length; i++) { // ABSTRACTION!
				expectedCoord.add(new Coordinates(xFields[i], yFields[i]));
			}
		}
		if(testMySite == MovementType.EAST) {
			int[] xFields = {0,1,0,1,0,0,1,3,3,3,4,1};
			int[] yFields = {0,0,1,1,2,3,3,1,2,3,2,2};
			for(int i = 0; i < xFields.length; i++) { // ABSTRACTION!
				expectedCoord.add(new Coordinates(xFields[i], yFields[i]));
			}
		}
		if(testMySite == MovementType.WEST) {
			int[] xFields = {12,13,14,11,12,13,12,13,14,13,14,14};
			int[] yFields = {0,0,0,1,1,1,2,2,2,3,3,1};
			for(int i = 0; i < xFields.length; i++) { // ABSTRACTION!
				expectedCoord.add(new Coordinates(xFields[i], yFields[i]));
			}
		}
		
		// ACT: get output of tested method
		List<Coordinates> testedCoord = OpponentAreaAnalyzer.computeOpponentCastleArea(testMap, testMySite);
		
		// convert lists into sets to make comparison easier
		Set<Coordinates> testSet = new HashSet<>();
		Set<Coordinates> expectedSet = new HashSet<>();
		
		for(Coordinates coord : testedCoord) {
			testSet.add(coord);
		}
		for(Coordinates coord : expectedCoord) {
			expectedSet.add(coord);
		}
		
		// ASSERT: check whether sets are equal
		assertEquals(expectedSet, testSet);
	}

}
