package client.pathalgorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import client.model.Coordinates;

// helper class that stores paths that are evaluated by the ShortestPathAlgorithm
public class PathResult {
	private List<Coordinates> coordinatesList; // list of coordinates belonging to this path
	private int pathValue; // value of this path, computed by looking at MapElementTypes of Coordinates in this path (computation in SPA)
	
	// default ctor
	public PathResult(Coordinates startingPoint) { 
		this.coordinatesList = new ArrayList<>(); // start with empty path
		this.coordinatesList.add(startingPoint); // add the current (= the starting) point as first element to the path)
		this.pathValue = 0; // since the startingPoint is also last element of path there is an effort of 0 needed to walk path
	}
	
	// ctor if PathResult needs to be initialized with a different starting value than 0
	// for example when creating a PathResult for a water field
	public PathResult(Coordinates startingPoint, int value) {
		this.coordinatesList = new ArrayList<>();
		this.coordinatesList.add(startingPoint);
		this.pathValue = value;
	}
	
	// adds a coordinate to the current path
	public void addPathElement(Coordinates coord) {
		coordinatesList.add(coord);
	}
	
	// adds some value to the current path
	public void addValue(int value) {
		pathValue += value;
	}
	
	// adds a whole PathResult to this object
	public void addPathResultElement(PathResult addElement) {
		pathValue += addElement.getPathValue();
		coordinatesList.addAll(addElement.getCoordinatesList());
	}
	
	// returns path of this object
	public List<Coordinates> getCoordinatesList() {
		return this.coordinatesList;
	}
	
	// returns value of current path
	public int getPathValue() {
		return this.pathValue;
	}

	@Override
	public String toString() {
		return "PathResult: " + this.coordinatesList.toString() + ", " + this.pathValue; 
	}

	@Override
	public int hashCode() {
		return Objects.hash(coordinatesList, pathValue);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PathResult other = (PathResult) obj;
		return Objects.equals(coordinatesList, other.coordinatesList) && pathValue == other.pathValue;
	}
	
	
	
}
