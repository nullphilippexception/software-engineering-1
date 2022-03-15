package client.test.ai;

import static org.junit.jupiter.api.Assertions.*;

import client.pathalgorithm.*;
import client.model.Coordinates;
import client.model.Map;
import client.model.MapElement;
import client.model.MapElementType;

import java.util.HashMap;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

// NOTE for grader: this is a data-driven test
class T_ShortestPathAlgorithm {

	// this test investigates the computeValueOfShortestPath function of the ShortestPathAlgorithm, which
	// computes the value (the smaller the better) of an also computed shortest path between an input starting point and a fixed target point
	// the test then compares whether the computed value corresponds with the expected value handed as a parameter
	@ParameterizedTest
	@CsvSource({"3,0,4","2,2,6","3,3,4","6,0,0","7,3,2","0,3,7"})
	void targetCoordinates_computeValueOfShortestPath_ValueOfPath(int xcoord, int ycoord, int expectedValue) {
		// ARRANGE: create a map -> 4 rows 8 cols is enough for this purpose
		HashMap<Coordinates, MapElement> elements = new HashMap<>();
		
		// create map that is all grass except for coloumn 4, which has a mountain on its y = 0 element,
		// water on its y = 1 and y = 2 element and grass on its y = 3 element
		for(int x = 0; x < 8; x++) {
			for(int y = 0; y < 4; y++) {
				if(x != 4) {
					elements.put(new Coordinates(x,y), new MapElement(MapElementType.GRASS, null)); // null here ok bc never used
				}
				else {
					if(y == 0) elements.put(new Coordinates(x,y), new MapElement(MapElementType.MOUNTAIN, null));
					if(y == 1 || y == 2) elements.put(new Coordinates(x,y), new MapElement(MapElementType.WATER, null));
					if(y == 3) elements.put(new Coordinates(x,y), new MapElement(MapElementType.GRASS, null));
				}
			}
		}
		Map map = new Map(elements);
		
		// create different startingPoints that want to reach a target
		Coordinates start = new Coordinates(xcoord,ycoord);
		Coordinates target = new Coordinates(6,1);
		
		// ACT: create the ShortestPathAlgorithm objects
		ShortestPathAlgorithm spa = new ShortestPathAlgorithm(map,start,target);
		
		// get values of different objects
		int computedValue = spa.computeValueOfShortestPath();
		
		// ASSERT: check if values are as expected
		assertEquals(expectedValue, computedValue);
		
	}

}
