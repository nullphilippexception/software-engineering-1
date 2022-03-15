package server.halfmapvalidator;

import java.util.HashMap;

import MessagesBase.ETerrain;
import MessagesBase.HalfMap;
import MessagesBase.HalfMapNode;
import server.businessrules.IMapRule;
import server.exceptions.GenericExampleException;
import server.exceptions.InvalidMapException;

// This validator ensures that the received map contains exactly 32 fields and that it includes
// at least 15 grass fields, 3 mountain fields and 4 water fields
public class NumberOfFieldsValidator implements IMapRule {
	
	public void checkRule(HalfMap map) throws InvalidMapException {
		// create a hashmap that counts occurences for each ETerrain type
		HashMap<ETerrain, Integer> fieldTypeCounter = new HashMap<>();
		fieldTypeCounter.put(ETerrain.Grass, 0);
		fieldTypeCounter.put(ETerrain.Mountain, 0);
		fieldTypeCounter.put(ETerrain.Water, 0);
		
		// count field types and ensure that there are no unknown ETerrain types used
		for(HalfMapNode node : map.getMapNodes()) {
			if(node.getTerrain() == ETerrain.Grass || node.getTerrain() == ETerrain.Water || node.getTerrain() == ETerrain.Mountain) {
				int newValue = fieldTypeCounter.get(node.getTerrain()) + 1;
				fieldTypeCounter.put(node.getTerrain(), newValue);
			}
			else {
				throw new InvalidMapException("NumberOfFieldsValidator", "Unknown ETerrain type");
			}
		}
		
		// check if every terrain type occurs often enough
		if(fieldTypeCounter.get(ETerrain.Grass) < 15) {throw new GenericExampleException("NumberOfFieldsValidator", "Not enough Grass");}
		if(fieldTypeCounter.get(ETerrain.Mountain) < 3) {throw new GenericExampleException("NumberOfFieldsValidator", "Not enough Mountains");}
		if(fieldTypeCounter.get(ETerrain.Water) < 4) {throw new GenericExampleException("NumberOfFieldsValidator", "Not enough Water");}
	}
}
