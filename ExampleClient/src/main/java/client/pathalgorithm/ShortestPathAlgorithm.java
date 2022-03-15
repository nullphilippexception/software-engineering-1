package client.pathalgorithm;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import client.model.Coordinates;
import client.model.Map;
import client.model.MapElementType;

// this class utilizes (a modified) depth search algorithm to find the fastest legal connection between
// two coordinates start and target on a given map
public class ShortestPathAlgorithm {	
	private Logger logger;
	private Coordinates start;
	private Coordinates target;
	private Map map;
	private int steps; // counts how many steps deep the pathfinding is already
	private int searchBoundary; // max amount of steps the algorithm is allowed to make for performance reasons
	private final int MAX_DEPTH_STEPS_BONUS; // a constant, chosen value that is added to the direct connection between two coordinates
											 // to allow the algorithm to make enough steps to walk around water fields
	
	public ShortestPathAlgorithm(Map map, Coordinates start, Coordinates target) {
		logger = LoggerFactory.getLogger(ShortestPathAlgorithm.class); // initialize logger for this class
		this.start = start;
		this.target = target;
		this.map = map;
		this.MAX_DEPTH_STEPS_BONUS = 10; // currently chosen step bonus -> change this value to change accuracy/time performance tradeoff
		this.steps = 1;
		// first part of formula computes most direct connection between two coordinates, but doesnt pay attention to potential obstacles
		this.searchBoundary = Math.abs(start.getX() - target.getX()) + Math.abs(start.getY() - target.getY()) + MAX_DEPTH_STEPS_BONUS;
	}
	
	// computes shortest path between fields start and target while paying respect to obstacles like water
	public List<Coordinates> computeShortestPath() {
		logger.info("Asked for path: " + start + " -> " + target);
		List<Coordinates> result = walkPath(start, new ArrayList<>(), steps).getCoordinatesList();
		result.remove(start); // because it is the current element, we want targets here
		return result;
	}
	
	// computes value of a shortest path between fields start and target
	// the lower the value the better/quicker is the path 
	// this method is mainly used for testing
	public int computeValueOfShortestPath() {
		return walkPath(start, new ArrayList<>(), steps).getPathValue() - 1; // -1 to remove impact of field i'm currently on
	}
	
	// helper method that recursively executes depth search
	// thisPoint is the currently investigated point, prevVisited contains the coordinates of fields already visited by the algorithm
	// steps counts how many steps deep the algorithm is already, once searchBoundary is reached a return is forced
	private PathResult walkPath(Coordinates thisPoint, List<Coordinates> prevVisited, int steps) {
		List<PathResult> possiblePath = new ArrayList<>();
		
		// check if target reached 
		if(thisPoint.equals(target)) { return new PathResult(thisPoint, 0); }
		
		// check if this field is water
		if(map.getElementByCoordinates(thisPoint).getMapElementType().equals(MapElementType.WATER)) {
			return new PathResult(thisPoint, 500); // 500 is value for water/deadends
		}
		
		// check if client already took so many steps that its on useless/performance-endangering route
		if(steps >= searchBoundary) { return new PathResult(thisPoint, 500); } // 500 is value for water/deadends
		
		// add element client is currently on to list of visited elements
		List<Coordinates> visitedElements = new ArrayList<>(prevVisited);
		visitedElements.add(thisPoint);
		
		// if not returned yet: make next recursion step and traverse paths in all possible directions
		if(map.isCoordinatesValid(new Coordinates(thisPoint.getX(), thisPoint.getY()-1)) // NORTH
				&& !visitedElements.contains(new Coordinates(thisPoint.getX(), thisPoint.getY()-1))) { 
			possiblePath.add(walkPath(new Coordinates(thisPoint.getX(), thisPoint.getY()-1), visitedElements, steps + 1));
		}
		
		if(map.isCoordinatesValid(new Coordinates(thisPoint.getX()+1, thisPoint.getY())) // EAST
				&& !visitedElements.contains(new Coordinates(thisPoint.getX()+1, thisPoint.getY()))) { 
			possiblePath.add(walkPath(new Coordinates(thisPoint.getX()+1, thisPoint.getY()), visitedElements, steps + 1));
		}
		if(map.isCoordinatesValid(new Coordinates(thisPoint.getX(), thisPoint.getY()+1)) // SOUTH
				&& !visitedElements.contains(new Coordinates(thisPoint.getX(), thisPoint.getY()+1))) { 
			possiblePath.add(walkPath(new Coordinates(thisPoint.getX(), thisPoint.getY()+1), visitedElements, steps + 1));
		}
		if(map.isCoordinatesValid(new Coordinates(thisPoint.getX()-1, thisPoint.getY())) // WEST
				&& !visitedElements.contains(new Coordinates(thisPoint.getX()-1, thisPoint.getY()))) { 
			possiblePath.add(walkPath(new Coordinates(thisPoint.getX()-1, thisPoint.getY()), visitedElements, steps + 1));
		}
		
		// return best possible path, bad value if this path is deadend (no further possible paths)
		if(possiblePath.isEmpty()) {
			return new PathResult(thisPoint, 500); // 500 is value for water/deadends
		}
		else {
			return AlgorithmHelper.minimumOfPossiblePath(map, thisPoint, possiblePath);
		}
	}
}
