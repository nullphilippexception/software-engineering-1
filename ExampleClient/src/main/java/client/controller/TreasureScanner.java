package client.controller;

import java.util.List;

import client.model.Coordinates;
import client.model.FigureType;
import client.model.Map;
import client.model.MovementType;

public class TreasureScanner {
	public static Coordinates scanForTreasure(Map map, MovementType mySite, List<Coordinates> viewPoints, List<Coordinates> unvisitedElements) {
		Coordinates bestNextMountain = new Coordinates(-1,-1); // default
		double minDistance = 5000.0; // will be larger than any real distance
		// if Dr Böhmer says AI too dependent on map -> initialize PathAnalyzer with empty mountainViewForTreasure list
		// the following line of code will then generalize AI to search my half for a unknown map
		if(viewPoints.size() == 0) viewPoints = unvisitedElements; 
		
		for(Coordinates viewPointCoord : viewPoints) {
			//ShortestPathAlgorithm algo = new ShortestPathAlgorithm(map, map.findThisFigure(FigureType.MY_AVATAR), viewPointCoord);
			double distance = MapAnalyzer.computeDistanceBetweenCoordinates(map.findThisFigure(FigureType.MY_AVATAR), viewPointCoord);
			if(distance < minDistance) {
				minDistance = distance;
				bestNextMountain = viewPointCoord;
			}
		}
		return bestNextMountain;
	}
}
