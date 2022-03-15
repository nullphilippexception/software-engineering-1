
package client.test.map;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;

import client.model.Coordinates;
import client.model.Figure;
import client.model.Map;
import client.model.MapElement;
import client.model.MapElementType;

import org.junit.jupiter.api.Test;


// these getters are non-trivial, therefore a test case is justified
class T_Map_getRowsAndColumns {
	
	// helper method to create a map for the test
	Map createTestMap(int rows, int columns) {
		HashMap<Coordinates, MapElement> testMapElements = new HashMap<>();
		for(int x = 0; x < columns; x++) {
			for(int y = 0; y < rows; y++) {
				testMapElements.put(new Coordinates(x,y), new MapElement(MapElementType.GRASS, new ArrayList<Figure>()));
			}
		}
		return new Map(testMapElements);
	}

	// this test investigates whether the map correctly compute its number of rows, both kinds of maps are tested
	@Test
	void testGetNumberOfRows() {
		Map testMap1 = createTestMap(8,8);
		Map testMap2 = createTestMap(16,4);
		
		assertEquals(8, testMap1.getNumberOfRows());
		assertEquals(16, testMap2.getNumberOfRows());
	}

	// this test investigates whether the map correctly compute its number of columns, both kinds of maps are tested
	@Test
	void testGetNumberOfColumns() {
		Map testMap1 = createTestMap(8,8);
		Map testMap2 = createTestMap(16,4);
		
		assertEquals(8, testMap1.getNumberOfColumns());
		assertEquals(4, testMap2.getNumberOfColumns());
	}
	

}

