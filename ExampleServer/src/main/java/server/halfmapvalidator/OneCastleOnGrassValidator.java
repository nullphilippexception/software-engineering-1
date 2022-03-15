package server.halfmapvalidator;

import MessagesBase.ETerrain;
import MessagesBase.HalfMap;
import MessagesBase.HalfMapNode;
import server.businessrules.IMapRule;
import server.exceptions.InvalidMapException;

//check whether the given halfmap contains exactly one castle and whether this castle is placed on grass
public class OneCastleOnGrassValidator implements IMapRule {
	
	public void checkRule(HalfMap map) throws InvalidMapException {
		int castleCount = 0;
		for(HalfMapNode node : map.getMapNodes()) {
			if(node.isFortPresent()) { 
				castleCount++; 
				if(node.getTerrain() != ETerrain.Grass) {
					throw new InvalidMapException("OneCastleOnGrassValidator", "Castle is not placed on grass!");
				}
			}
		}
		if(castleCount != 1) {
			throw new InvalidMapException("OneCastleOnGrassValidator", "Wrong number of castles placed on map");
		}
	}
}
