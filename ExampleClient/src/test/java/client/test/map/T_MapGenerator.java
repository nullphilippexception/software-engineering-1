package client.test.map;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import client.mapcreation.MapGenerator;
import client.model.Coordinates;
import client.model.FigureType;
import client.model.Map;
import client.model.MapElement;
import client.model.MapElementType;

class T_MapGenerator {

	// tests criteria of maps that need to be true for all generated maps
	// this includes map size, placement of castle and avatar and a sufficient set of mountains (special case in MapGenerator)
	@Test
	void demandMap_createMap_returnMap() {
		// ARRANGE: create objects to be tested
		final int NO_OF_COLUMNS = 8;
		final int NO_OF_ROWS = 4;
		
		Map testMap = MapGenerator.generateMap();
		
		Coordinates castleCoordinates = new Coordinates(-1,-1); // default return for an element that is not on map
		Coordinates avatarCoordinates = new Coordinates(-1,-1);
		castleCoordinates = testMap.findThisFigure(FigureType.MY_CASTLE);
		avatarCoordinates = testMap.findThisFigure(FigureType.MY_AVATAR);
		int mountainCounter = 0;
		
		for(Coordinates coord : testMap.getElements().keySet()) {
			MapElement currentElement = testMap.getElementByCoordinates(coord);
			if(currentElement.getMapElementType().equals(MapElementType.MOUNTAIN)) mountainCounter++;
		}
		
		// ACT & ASSERT: check whether return of map getters is as expected
		assertEquals(NO_OF_COLUMNS, testMap.getNumberOfColumns());
		assertEquals(NO_OF_ROWS, testMap.getNumberOfRows());
		assertTrue(mountainCounter >= 6);
		assertNotEquals(new Coordinates(-1,-1), castleCoordinates);
		assertNotEquals(new Coordinates(-1,-1), avatarCoordinates);
	}

}
