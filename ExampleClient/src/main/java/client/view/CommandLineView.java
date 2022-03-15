package client.view;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import client.model.Coordinates;
import client.model.Figure;
import client.model.FigureType;
import client.model.Map;
import client.model.MapElement;
import client.model.MapElementType;

// this class is the VIEW of the MVC pattern
// it prints the some basic information about the displayed map, the map itself and, if the game is over, the users
// result (win/loss) to the CLI of the user
public class CommandLineView {
	
	private Map map;
	private int printRound; // counts the number of maps that were printed to user
	private Logger logger;
	
	// listens to model, when model fires event that change has occured -> act accordingly
	final PropertyChangeListener mapChangeListener = event -> {
		Object data = event.getNewValue();
		Object key = event.getPropertyName(); 
		
		if(data instanceof Map) {
			printMapToUser((Map) data);
		}
		
		if(key.equals("winner")) {
			printEndOfGame((boolean) data); // data will always be boolean in this case
		}
	};
	
	// ctor, hands map to this object and initializes round counter
	// also tells model (map) that its wants to listen to it
	public CommandLineView(Map map) {
		this.map = map;
		printRound = 1;
		logger = LoggerFactory.getLogger(CommandLineView.class);
		
		map.addPropertyChangeListener(mapChangeListener);
	}

	// prints map to user by using several private helper methods
	public void printMapToUser(Map map) {
		printMapInfo();
		for(int y = 0; y < map.getNumberOfRows(); y++) {
			printMapRowToCommandLine(getRowAsList(y));
		}
	}
	
	// prints the end of game message to user
	public void printEndOfGame(boolean thisPlayerWon) {
		if(thisPlayerWon) { System.out.println("You won the game!"); }
		else { System.out.println("You lost the game!"); }
	}
	
	// helper method that takes a list of elements that represent one row of a map
	// seperates data into row of figures and row terrain that are then formatted with seperate method
	private void printMapRowToCommandLine(List<MapElement> printRow) {
		StringBuffer figureRow = new StringBuffer("");
		StringBuffer terrainRow = new StringBuffer("");
		
		for(MapElement currElement : printRow) {
			figureRow.append(printFigureOnMapElement(currElement.getOnField()));
			terrainRow.append(printMapElement(currElement));
		}
		System.out.println(figureRow.toString());
		System.out.println(terrainRow.toString());
	}
	
	// helper method that returns a "visual" representation as a String of the figures handed to it
	private String printFigureOnMapElement(List<Figure> figures) {
		StringBuffer result = new StringBuffer();
		final int spacePerMapElement = 5;
		for(Figure fig : figures) {
			if(fig.getFigureType().equals(FigureType.MY_AVATAR)) { result.append("P1"); }
			if(fig.getFigureType().equals(FigureType.MY_CASTLE)) { result.append("C1"); }
			if(fig.getFigureType().equals(FigureType.MY_TREASURE)) { result.append("T1"); }
			if(fig.getFigureType().equals(FigureType.OPPONENT_AVATAR)) { result.append("P2"); }
			if(fig.getFigureType().equals(FigureType.OPPONENT_CASTLE)) { result.append("C2"); }
			if(fig.getFigureType().equals(FigureType.OPPONENT_TREASURE)) { result.append("T2"); }
		}
		// fill remaining space with spaces
		int placeholdersToBeAdded = spacePerMapElement - result.length();
		for(int i = 0; i < placeholdersToBeAdded; i++) { 
			result.append(" "); 
		}
		return result.toString();
	}
	
	// helper method that returns a "visual" representation as a String of the terrains of the mapelements handed to it
	private String printMapElement(MapElement mapElement) {
			if(mapElement.getMapElementType().equals(MapElementType.GRASS)) return "____|";
			if(mapElement.getMapElementType().equals(MapElementType.MOUNTAIN)) return "AAAA|";
			if(mapElement.getMapElementType().equals(MapElementType.WATER)) return "~~~~|";
			// still didn't return -> there is a problem with map
			logger.warn("map contains an element that is not valid");
			return "????"; // default return for unknown elements
	}
	
	// helper method that prints info map (each symbols meaning)
	// also prints number of map -> does not correspond with number of moves because map doesnt change every move
	private void printMapInfo() {
		System.out.println("[Map #"+ printRound + "]" + " A - Mountain, _ - Grass, ~ - Water, C1 - my Castle, T1 - my Treasure, P1 - my Avatar, C2 - opponents Castle, T2 - opponents Treasure, P2 - opponents Avatar");
		printRound++;
	}
	
	// helper method that transforms the map row with the selectedRow index into a list of MapElements
	private List<MapElement> getRowAsList(int selectedRow) {
		List<MapElement> result = new ArrayList<MapElement>();
		for(int x = 0; x < map.getNumberOfColumns(); x++) {
			result.add(map.getElementByCoordinates(new Coordinates(x,selectedRow)));
		}
		return result;
	}

}
