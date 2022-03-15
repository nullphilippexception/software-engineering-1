package client.test.map;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import client.model.Coordinates;
import client.model.Figure;
import client.model.FigureType;
import client.model.Map;
import client.model.MapElement;
import client.model.MapElementType;

import org.junit.jupiter.api.Test;

import java.util.Random;

class T_MapDataIntegrity {
	
	// basic helper method
	// this map is not necessarily a valid map with regards to border restrictions, but that doesnt matter for these tests
	Map getBasicEmptyMap() {
		HashMap<Coordinates, MapElement> elements = new HashMap<>();
		Random rand = new Random();
		for(int x = 0; x < 8; x++) {
			for(int y = 0; y < 4; y++) {
				int chooseElement = rand.nextInt(30);
				MapElementType toPut = MapElementType.GRASS;
				if(chooseElement < 5) toPut = MapElementType.WATER;
				if(chooseElement >= 5 && chooseElement < 15) toPut = MapElementType.MOUNTAIN;
				if(chooseElement >= 15) toPut = MapElementType.GRASS;
				List<Figure> emptyList = new ArrayList<>();
				elements.put(new Coordinates(x,y), new MapElement(toPut, emptyList));
			}
		}
		return new Map(elements);
	}
	
	// tests the findThisFigure function of the map, whether the map will return the default bad coordinates for
	// a request of a figure that is not on the map
	@Test
	void figureType_findThisFigure_Coordinates() {
		Map testMap = getBasicEmptyMap(); // contains no figures!
		Coordinates testCoord = testMap.findThisFigure(FigureType.MY_AVATAR);
		assertEquals(new Coordinates(-1,-1), testCoord); // (-1,-1) is default return for unknown figures
	}
	
	// tests whether map knows which coordinates are valid for it and which are not
	// since (-1,-1) is the default return for bad coordinates, they are not valid
	// (1,1) is valid
	@Test
	void testValidCoordinates() {
		Map testMap = getBasicEmptyMap();
		assertEquals(false, testMap.isCoordinatesValid(new Coordinates(-1,-1)));
		assertEquals(true, testMap.isCoordinatesValid(new Coordinates(1,1)));
	}

}
