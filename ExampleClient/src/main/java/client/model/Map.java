package client.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import client.exception.MapNullPointerException;

// MODEL of the MVC model
// the map is the base of the whole game, it contains all the information
public class Map {
	private HashMap<Coordinates, MapElement> elements; // each MapElement can be reached by its unique identifier, its unique coordinates
	private final PropertyChangeSupport changes = new PropertyChangeSupport(this); // for MVC model to allow Views to listen
	
	// empty ctor solely used for MVC compliance in Main.java
	public Map() {
		this.elements = new HashMap<Coordinates, MapElement>();
	}
	
	// standard ctor that gets HashMap of elements
	public Map(HashMap<Coordinates, MapElement> elements) {
		this.elements = new HashMap<Coordinates, MapElement>();
		this.elements = elements;
	}
	
	// copy ctor
	public Map(Map otherMap) {
		this.elements = new HashMap<Coordinates, MapElement>();
		for(int x = 0; x < otherMap.getNumberOfColumns(); x++) { 
			for(int y = 0; y < otherMap.getNumberOfRows(); y++) {
				this.elements.put(new Coordinates(x,y), otherMap.getElementByCoordinates(new Coordinates(x,y)));
			}
		}
	}
	
	// allows View to register themselves as listeners
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		changes.addPropertyChangeListener(listener);
	}

	// sets this objects fields to the fields of the input map
	// throws MapNullPointerException if input map is null
	public void setMap(Map map) throws MapNullPointerException {
		try {
			Map oldValue = new Map(this);
			this.elements = map.getElements();
			changes.firePropertyChange("map", oldValue, this);
		}
		catch(Exception e) { 
			throw new MapNullPointerException("Tried to set map with NPE elements: " + e.getMessage()); //unchecked exception
		}
	}
	
	// notifies Views that a winner was determined and that game is over now
	public void setWinner(boolean thisPlayerWon) {
		changes.firePropertyChange("winner", 0, thisPlayerWon);
	}
	
	// returns the elements of this map
	public HashMap<Coordinates, MapElement> getElements() {
		return elements;
	}
	
	// returns an element identified by the input coordinates
	public MapElement getElementByCoordinates(Coordinates c) {
		return elements.get(c);
	}
	
	// adds a given figure to mapelement specified by input coordinates
	public void addFigureOnElement(Coordinates c, Figure f) {
		elements.get(c).addOnField(f);
	}
	
	// returns number of rows of this object
	public int getNumberOfRows() {
		Set<Coordinates> coordinates = this.elements.keySet(); 
		Vector<Integer> noOfDifferentY = new Vector<>();
		for(Coordinates coord : coordinates) {
			if(!noOfDifferentY.contains(coord.getY())) { noOfDifferentY.add(coord.getY()); }
		}
		return noOfDifferentY.size();
	}
	
	// returns number of columns of this object
	public int getNumberOfColumns() {
		Set<Coordinates> coordinates = this.elements.keySet();
		Vector<Integer> noOfDifferentX = new Vector<>();
		for(Coordinates coord : coordinates) {
			if(!noOfDifferentX.contains(coord.getX())) { noOfDifferentX.add(coord.getX()); }
		}
		return noOfDifferentX.size();
	}
	
	// checks if a input pair of Coordinates exists on this map
	public boolean isCoordinatesValid(Coordinates coord) {
		return this.elements.keySet().contains(coord);
	}
	
	// returns the coordinates of where a given figure is on the map
	// returns (-1,-1) if the figure does not exist on the map
	public Coordinates findThisFigure(FigureType figuretype) {
		try {
			for(int x = 0; x < this.getNumberOfColumns(); x++) {
				for(int y = 0; y < this.getNumberOfRows(); y++) {
					for(Figure figure :  this.elements.get(new Coordinates(x,y)).getOnField()) {
						if(figure.getFigureType().equals(figuretype)) { return new Coordinates(x,y); }
					}
				}
			}
			if(figuretype.equals(FigureType.MY_AVATAR) || figuretype.equals(FigureType.MY_CASTLE)) {
				throw new RuntimeException("Figure " + figuretype.toString() + " went missing!"); // because these two should always be there
			}
			return new Coordinates(-1,-1); // default return for unknown positon
		}
		catch(RuntimeException e) {
			Logger.getAnonymousLogger().log(Level.SEVERE, e.getMessage());
			return new Coordinates(-1,-1); // default return for unknown position
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((elements == null) ? 0 : elements.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Map other = (Map) obj;
		if (elements == null) {
			if (other.elements != null)
				return false;
		} else if (!elements.equals(other.elements))
			return false;
		return true;
	}
}
