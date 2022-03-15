package client.test.map;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.junit.jupiter.api.Test;
import client.mapcreation.MapValidator;
import client.model.*;

// this is a negativ clearance, tests provides lots of wrong maps
// validator is supposed to reject them all
class T_MapValidator {

	// test puts too much water on a border, validator needs to reject the map because it hurts the borderwater rule
	@Test
	void allWaterBorder_borderWater_returnFalse() {
		HashMap<Coordinates, MapElement> elements = new HashMap<>();
		// ARRANGE: create a map with one border all water
		for(int x = 0; x < 8; x++) {
			for(int y = 0; y < 4; y++) {
				MapElementType toPut = MapElementType.GRASS; // default
				if(y == 0) toPut = MapElementType.WATER; // this makes all whole border water -> illegal!
				else if(y == 3) toPut = MapElementType.MOUNTAIN;
				List<Figure> emptyList = new ArrayList<>();
				if(x == 0 && y == 1) emptyList.add(new Figure(FigureType.MY_CASTLE));
				elements.put(new Coordinates(x,y), new MapElement(toPut, emptyList));
			}
		}
		
		// ACT & ASSERT: check whether function returns false as expected
		Map map = new Map(elements);
		assertEquals(false, MapValidator.isMapValid(map));
	}
	
	// tests whether map contains enough of all elements
	// validator needs to reject tested map because it only contains grass
	@Test
	void allGrasMap_countElementTypes_returnFalse() {
		HashMap<Coordinates, MapElement> elements = new HashMap<>();
		// ARRANGE: create map that only contains grass (but still fullfills other criteria!)
		for(int x = 0; x < 8; x++) {
			for(int y = 0; y < 4; y++) {
				MapElementType toPut = MapElementType.GRASS; // all elements are grass -> illegal!
				List<Figure> emptyList = new ArrayList<>();
				if(x == 0 && y == 1) emptyList.add(new Figure(FigureType.MY_CASTLE));
				elements.put(new Coordinates(x,y), new MapElement(toPut, emptyList));
			}
		}
		
		// ACT & ASSERT: check whether function returns as expected false
		Map map = new Map(elements);
		assertEquals(false, MapValidator.isMapValid(map));
	}
	
	// tests whether a castle exists on the map to be validated, should return false
	@Test
	void mapWithoutCastle_checkCastleField_returnFalse() {
		// ARRANGE: create basic map without castle 
		HashMap<Coordinates, MapElement> elements = new HashMap<>();
		for(int x = 0; x < 8; x++) {
			for(int y = 0; y < 4; y++) {
				MapElementType toPut = MapElementType.GRASS; // default
				if(y == 0 && x < 3) toPut = MapElementType.WATER; 
				else if(y == 3) toPut = MapElementType.MOUNTAIN;
				List<Figure> emptyList = new ArrayList<>(); // there is no FigureType.MY_CASTLE on the map -> illegal!
				elements.put(new Coordinates(x,y), new MapElement(toPut, emptyList));
			}
		}
		
		// ACT & ASSERT: check whether function returns as expected false
		Map map = new Map(elements);
		assertEquals(false, MapValidator.isMapValid(map));
	}
	
	// check whether on this map all fields are accessible from all other fields, validator expected to return false
	@Test
	void splitMap_checkFieldAccessibility_returnFalse() {
		// ARRANGE: create basic map that has all water in column x == 3, therefore splitting map in two halfs
		HashMap<Coordinates, MapElement> elements = new HashMap<>();
		for(int x = 0; x < 8; x++) {
			for(int y = 0; y < 4; y++) {
				MapElementType toPut = MapElementType.GRASS; // default
				if(x == 3) toPut = MapElementType.WATER; // for x == 3 there will be a column of water, causing part of map to be unaccessible -> illegal!
				else if(y == 3 && x != 3) toPut = MapElementType.MOUNTAIN;
				List<Figure> emptyList = new ArrayList<>(); 
				if(x == 0 && y == 1) emptyList.add(new Figure(FigureType.MY_CASTLE));
				elements.put(new Coordinates(x,y), new MapElement(toPut, emptyList));
			}
		}
		
		// ACT & ASSERT: check whether function returns as expected
		Map map = new Map(elements);
		assertEquals(false, MapValidator.isMapValid(map));
	}

}
