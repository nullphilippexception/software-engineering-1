package client.model;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import client.exception.IllegalFigureRemoval;

// MapElements are the nodes that are placed on a map
// they consist of a MapElementType which indicates their respective terrain and
// of no, one or more figures that are currently on this element
public class MapElement {
	private MapElementType mapElementType;
	private List<Figure> onField;
	private Logger logger;
	
	// ctor
	public MapElement(MapElementType mapElementType, List<Figure> onField) { 
		this.mapElementType = mapElementType;
		this.onField = onField;
		this.logger = LoggerFactory.getLogger(MapElement.class);
	}

	// returns MapElementType of this MapElement
	public MapElementType getMapElementType() {
		return mapElementType;
	}

	// returns a list of figures that are currently on this element
	public List<Figure> getOnField() {
		return onField;
	}

	// adds a figure to the list of figures currently on this field
	public void addOnField(Figure onField) {
		this.onField.add(onField);
	}
	
	// checks if a given figure, defined by its figuretype is present on this mapelement
	public boolean containsFigure(FigureType figureType) {
		for(Figure f : onField) {
			if(f.getFigureType().equals(figureType)) return true; 
		}
		return false;
	}
	
	// removes a figure from this mapelement, returns true if figure was actually present on this element
	public boolean removeFigure(FigureType figuretype) {
		try {
			if(!this.containsFigure(figuretype)) { 
				throw new IllegalFigureRemoval("Tried to remove " + figuretype + " which is not on this element");
			}
			else {
				List<Figure> remainingFigures = new ArrayList<>();
				for(Figure figure : onField) {
					if(!figure.getFigureType().equals(figuretype)) { remainingFigures.add(figure); }
				}
				this.onField = new ArrayList<Figure>(remainingFigures);
				return true;
			}
		}
		catch(IllegalFigureRemoval e) { // checked exception
			logger.warn(e.toString());
			return false;
		}	
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mapElementType == null) ? 0 : mapElementType.hashCode());
		result = prime * result + ((onField == null) ? 0 : onField.hashCode());
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
		MapElement other = (MapElement) obj;
		if (mapElementType != other.mapElementType)
			return false;
		if (onField == null) {
			if (other.onField != null)
				return false;
		} else if (!onField.equals(other.onField))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MapElement [mapElementType=" + mapElementType + ", onField=" + onField + "]";
	}
	
	

}
