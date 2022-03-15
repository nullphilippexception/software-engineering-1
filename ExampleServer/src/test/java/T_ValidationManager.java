import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.Test;

import MessagesBase.ETerrain;
import MessagesBase.HalfMap;
import MessagesBase.HalfMapNode;
import server.exceptions.GenericExampleException;
import server.halfmapvalidator.BorderWaterValidator;
import server.halfmapvalidator.MapFormatValidator;
import server.halfmapvalidator.MapSizeValidator;
import server.halfmapvalidator.NoIslandsValidator;
import server.halfmapvalidator.NumberOfFieldsValidator;
import server.halfmapvalidator.OneCastleOnGrassValidator;
import server.halfmapvalidator.ValidationManager;

// This test tests whether the six Business rules that are checked by the ValidationManager are actually
// correctly checked
class T_ValidationManager {

	@Test
	void differentHalfMaps_validateMaps_correctOrNot() {
		HashMap<Integer, Boolean> expectedException = new HashMap<>();
		
		// #001 - MapSizeValidator
		// should throw exception because 0 map nodes as input, expected 32
		try {
			expectedException.put(1, false);
			HalfMap testMap = new HalfMap();
			MapSizeValidator validator = new MapSizeValidator();
			validator.checkRule(testMap);
		}
		catch(GenericExampleException e) {
			expectedException.put(1, true);
		}
		
		// #002 - NumberOfFieldsValidator
		// should throws exception because only grass nodes and no mountains or water
		try {
			expectedException.put(2, false);
			List<HalfMapNode> nodesForTestMap = new ArrayList<>();
			for(int i = 0; i < 32; i++) {
				HalfMapNode tmpNode = new HalfMapNode(0,i, ETerrain.Grass);
				nodesForTestMap.add(tmpNode);
			}
			HalfMap testMap = new HalfMap("testplayerid", nodesForTestMap);
			NumberOfFieldsValidator validator = new NumberOfFieldsValidator();
			validator.checkRule(testMap);
		}
		catch(GenericExampleException e) {
			expectedException.put(2, true);
		}
		
		// #003 - MapFormatValidator
		// should throw exception because it is a 1x32 map instead of a 4x8 map
		try {
			expectedException.put(3, false);
			List<HalfMapNode> nodesForTestMap = new ArrayList<>();
			for(int i = 0; i < 32; i++) {
				HalfMapNode tmpNode = new HalfMapNode(0,i, ETerrain.Grass);
				nodesForTestMap.add(tmpNode);
			}
			HalfMap testMap = new HalfMap("testplayerid", nodesForTestMap);
			MapFormatValidator validator = new MapFormatValidator();
			validator.checkRule(testMap);
		}
		catch(GenericExampleException e) {
			expectedException.put(3, true);
		}
		
		// #004 - BorderWaterValidator
		// should throws exception because whole map and therefore also borders consist of water
		try {
			expectedException.put(4, false);
			List<HalfMapNode> nodesForTestMap = new ArrayList<>();
			for(int x = 0; x < 8; x++) {
				for(int y = 0; y < 4; y++) {
					HalfMapNode tmpNode = new HalfMapNode(x,y, ETerrain.Water);
					nodesForTestMap.add(tmpNode);
				}
			}
			HalfMap testMap = new HalfMap("testplayerid", nodesForTestMap);
			BorderWaterValidator validator = new BorderWaterValidator();
			validator.checkRule(testMap);
		}
		catch(GenericExampleException e) {
			expectedException.put(4, true);
		}
		
		// #005 OneCastleOnGrassValidator
		// should throw exception because every node has a castle
		try {
			expectedException.put(5, false);
			List<HalfMapNode> nodesForTestMap = new ArrayList<>();
			for(int x = 0; x < 8; x++) {
				for(int y = 0; y < 4; y++) {
					HalfMapNode tmpNode = new HalfMapNode(x,y, true, ETerrain.Grass); // forts everywhere
					nodesForTestMap.add(tmpNode);
				}
			}
			HalfMap testMap = new HalfMap("testplayerid", nodesForTestMap);
			OneCastleOnGrassValidator validator = new OneCastleOnGrassValidator();
			validator.checkRule(testMap);
		}
		catch(GenericExampleException e) {
			expectedException.put(5, true);
		}
		
		// #006 NoIslandsValidator
		// should throw exception because at x == 3 a water line seperates two halves of map
		try {
			expectedException.put(6, false);
			List<HalfMapNode> nodesForTestMap = new ArrayList<>();
			for(int x = 0; x < 8; x++) {
				for(int y = 0; y < 4; y++) {
					if(x == 3) {
						HalfMapNode tmpNode = new HalfMapNode(x,y, ETerrain.Water);
						nodesForTestMap.add(tmpNode);
					}
					else {
						HalfMapNode tmpNode = new HalfMapNode(x,y, ETerrain.Grass);
						nodesForTestMap.add(tmpNode);
					}
				}
			}
			HalfMap testMap = new HalfMap("testplayerid", nodesForTestMap);
			NoIslandsValidator validator = new NoIslandsValidator();
			validator.checkRule(testMap);
		}
		catch(GenericExampleException e) {
			expectedException.put(6, true);
		}
		
		// #007 ValidationManager
		// check for false negatives: this should not throw exception, because a correct map is created
		try {
			expectedException.put(7, false);
			List<HalfMapNode> nodesForTestMap = new ArrayList<>();
			for(int x = 0; x < 8; x++) {
				for(int y = 0; y < 4; y++) {
					if(y == 1) {
						if(x < 4) {
							HalfMapNode tmpNode = new HalfMapNode(x,y, ETerrain.Water);
							nodesForTestMap.add(tmpNode);
						}
						else {
							HalfMapNode tmpNode = new HalfMapNode(x,y, ETerrain.Mountain);
							nodesForTestMap.add(tmpNode);
						}
					}
					else {
						if(x == 0 && y == 0) {
							HalfMapNode tmpNode = new HalfMapNode(x,y, true, ETerrain.Grass);
							nodesForTestMap.add(tmpNode);
						}
						else {
							HalfMapNode tmpNode = new HalfMapNode(x,y, ETerrain.Grass);
							nodesForTestMap.add(tmpNode);
						}
					}
				}
			}
			HalfMap testMap = new HalfMap("testplayerid", nodesForTestMap);
			ValidationManager validator = new ValidationManager();
			validator.checkMapRules(testMap);
		}
		catch(GenericExampleException e) {
			expectedException.put(7, true);
		}
		
		// Assert whether all tests went as expected
		for(int i : expectedException.keySet()) {
			if(i < 7) {
				assertEquals(true, expectedException.get(i));
			}
			else {
				assertEquals(false, expectedException.get(i));
			}
		}
		
	}

}
