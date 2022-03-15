package client.pathalgorithm;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import client.model.Coordinates;
import client.model.Map;
import client.model.MapElementType;

// helper class that implements basic operations that are needed by the algorithms
public class AlgorithmHelper {
	
	// helper method that searches the minimum PathResult of List possiblePath and
	// adds the current point to the path, then returns this new path
	public static PathResult minimumOfPossiblePath(Map map, Coordinates thisPoint, List<PathResult> possiblePath) {
		int minIndex = -1;
		int minPathValue = Integer.MAX_VALUE;
		
		for(int i = 0; i < possiblePath.size(); i++) {
			if(possiblePath.get(i).getPathValue() < minPathValue) {
				minPathValue = possiblePath.get(i).getPathValue();
				minIndex = i;
			}
		}
		
		// add current (thisPoint) element to path
		MapElementType thisFieldType = map.getElementByCoordinates(thisPoint).getMapElementType();
		int fieldValue = valueForMapElementType(thisFieldType);
		PathResult result = new PathResult(thisPoint, fieldValue);
		result.addPathResultElement(possiblePath.get(minIndex));
		
		return result;
	}
	
	// helper method that returns the value (the smaller the better) for a handed MapElementType
	public static int valueForMapElementType(MapElementType inputType) {
		if(inputType.equals(MapElementType.GRASS)) return 1;
		if(inputType.equals(MapElementType.MOUNTAIN)) return 2;
		if(inputType.equals(MapElementType.WATER)) return 500;
		
		Logger logger = LoggerFactory.getLogger(AlgorithmHelper.class);
		logger.warn("Asked for illegal MapElementType: " + inputType);
		return 1000; // default return for unknown type -> worse then water/deadend so this track will never be reached
	}
}
