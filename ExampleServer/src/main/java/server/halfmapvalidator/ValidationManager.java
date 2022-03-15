package server.halfmapvalidator;

import java.util.ArrayList;
import java.util.List;

import MessagesBase.HalfMap;
import server.businessrules.IMapRule;
import server.exceptions.InvalidMapException;

// main class for validation of halfmap rules
public class ValidationManager {
	
	// this method creates a list of rules that implement the IMapRule interface
	// then checks for all added rules whether they are hurt
	// if a rule is hurt it throws and InvalidMapException
	public static void checkMapRules(HalfMap halfMap) throws InvalidMapException{
		List<IMapRule> listOfMapRules = new ArrayList<>();
		
		// ADD NEW RULES FOR MAP VALIDATION HERE!
		MapSizeValidator rule1 = new MapSizeValidator(); // ensures map has 32 total fields
		listOfMapRules.add(rule1);
		NumberOfFieldsValidator rule2 = new NumberOfFieldsValidator(); // ensures at least 15 grass, 4 water, 3 mountain
		listOfMapRules.add(rule2);
		MapFormatValidator rule3 = new MapFormatValidator(); // ensures cohesive 4x8 map with top left start at (0|0)
		listOfMapRules.add(rule3);
		BorderWaterValidator rule4 = new BorderWaterValidator(); // ensures that amount of water fields <= 1 on short and <=3 on long borders
		listOfMapRules.add(rule4);
		OneCastleOnGrassValidator rule5 = new OneCastleOnGrassValidator(); // ensures that only one castle is placed and that it is on grass
		listOfMapRules.add(rule5);
		NoIslandsValidator rule6 = new NoIslandsValidator(); // ensures that there are no islands cause by water on map
		listOfMapRules.add(rule6);
		
		// goes through all the rules
		for(IMapRule currentRule : listOfMapRules) {
			currentRule.checkRule(halfMap);
		}
	}
}
