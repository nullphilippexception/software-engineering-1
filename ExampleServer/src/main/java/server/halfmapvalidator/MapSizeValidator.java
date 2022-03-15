package server.halfmapvalidator;

import MessagesBase.HalfMap;
import server.businessrules.IMapRule;
import server.exceptions.InvalidMapException;

// this validator ensures that the given map has exactly 32 nodes
public class MapSizeValidator implements IMapRule {

	public void checkRule(HalfMap halfMap) throws InvalidMapException {
		// check if map has right amount of total fields
		int EXPECTED_NO_OF_FIELDS = 32;
		if(halfMap.getMapNodes().size() != EXPECTED_NO_OF_FIELDS) {
			throw new InvalidMapException("MapSizeValidator", "Wrong number of fields");
		}
	}
}
